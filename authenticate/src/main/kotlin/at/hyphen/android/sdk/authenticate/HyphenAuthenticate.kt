package at.hyphen.android.sdk.authenticate

import HyphenUserType
import android.app.Activity
import android.content.Context
import android.os.Build
import at.hyphen.android.sdk.core.Hyphen
import at.hyphen.android.sdk.core.common.HyphenOSName
import at.hyphen.android.sdk.core.common.account.HyphenAccount
import at.hyphen.android.sdk.core.common.device.HyphenDevice
import at.hyphen.android.sdk.core.common.device.HyphenDeviceType
import at.hyphen.android.sdk.core.common.key.HyphenUserKey
import at.hyphen.android.sdk.core.crypto.HyphenCryptography
import at.hyphen.android.sdk.core.error.HyphenSdkError
import at.hyphen.android.sdk.core.eventbus.HyphenEventBus
import at.hyphen.android.sdk.core.eventbus.HyphenEventBusType
import at.hyphen.android.sdk.deviceinfo.DeviceName
import at.hyphen.android.sdk.networking.HyphenNetworking
import at.hyphen.android.sdk.networking.request.HyphenRequest2FAFinish
import at.hyphen.android.sdk.networking.request.HyphenRequestEditDevice
import at.hyphen.android.sdk.networking.request.HyphenRequestSignIn2FA
import at.hyphen.android.sdk.networking.request.HyphenRequestSignInChallenge
import at.hyphen.android.sdk.networking.request.HyphenRequestSignInChallengeRespond
import at.hyphen.android.sdk.networking.request.HyphenRequestSignUp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.skydoves.sandwich.getOrThrow
import com.skydoves.sandwich.suspendOnError
import com.skydoves.sandwich.suspendOnSuccess
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlinx.coroutines.tasks.await
import timber.log.Timber

object HyphenAuthenticate {

    private var account: HyphenAccount? = null

    suspend fun getAccount(context: Context): HyphenAccount {
        if (account != null) {
            return account!!
        }

        val accountResult = HyphenNetworking.Account.getAccount().getOrThrow()
        updateDeviceInformation(context)

        account = accountResult

        return accountResult
    }

    @OptIn(ExperimentalStdlibApi::class)
    suspend fun authenticate(activity: Activity, webClientId: String) {
        val authCredential = HyphenGoogleAuthenticate.authenticate(activity, webClientId)
        val authResult = Firebase.auth.signInWithCredential(authCredential.credential!!).await()

        Timber.tag("HyphenSDK").i("Add firebase user...")
        Timber.tag("HyphenSDK")
            .d("Firebase authenticate successfully. User -> ${authResult.user?.displayName} (${authResult.user?.email})")
        val idToken = authResult.user?.getIdToken(false)?.await()?.token.orEmpty()
        Timber.tag("HyphenSDK").d("FIDToken -> $idToken")

        if (!HyphenCryptography.isDeviceKeyExist()) {
            Timber.tag("HyphenSDK").i("Hyphen device key not found! Generate new device key...")

            HyphenCryptography.generateKey()

            requestSignIn2FA(context = activity, idToken = idToken, userKey = getHyphenUserKey(activity))
        } else {
            Timber.tag("HyphenSDK").i("Request authenticate challenge...")
            val userKey = getHyphenUserKey(activity)

            try {
                val challengeRequest = HyphenNetworking.Auth.signInChallenge(
                    payload = HyphenRequestSignInChallenge(
                        challengeType = "deviceKey",
                        request = HyphenRequestSignInChallenge.Request(
                            method = "firebase",
                            token = idToken,
                            chainName = "flow-testnet",
                        ),
                        publicKey = userKey.publicKey.orEmpty(),
                    )
                ).getOrThrow()
                val challengeData = challengeRequest.challengeData
                val challengeSignature = HyphenCryptography.signData(challengeData.toByteArray())?.toHexString()
                val challengeRespondRequest = HyphenNetworking.Auth.signInChallengeRespond(
                    payload = HyphenRequestSignInChallengeRespond(
                        challengeType = "deviceKey",
                        challengeData = challengeData,
                        deviceKey = HyphenRequestSignInChallengeRespond.DeviceKey(
                            signature = "0x${challengeSignature.orEmpty()}"
                        )
                    )
                ).getOrThrow()

                account = challengeRespondRequest.account
                Hyphen.saveCredential(challengeRespondRequest.credentials)
            } catch (e: Exception) {
                Timber.tag("HyphenSDK").e("Request challenge failed. Attempting 2FA request for another device...")
                requestSignIn2FA(context = activity, idToken = idToken, userKey = userKey)
            }
        }
    }

    private suspend fun updateDeviceInformation(context: Context) {
        val userKey = getHyphenUserKey(context)

        HyphenNetworking.Device.editDevice(
            publicKey = userKey.publicKey.orEmpty(),
            payload = HyphenRequestEditDevice(
                pushToken = userKey.device?.pushToken.orEmpty(),
            ),
        )

        Timber.tag("HyphenSDK").i("Update device information successfully.")
    }

    private suspend fun requestSignIn2FA(context: Context, idToken: String, userKey: HyphenUserKey) {
        Timber.tag("HyphenSDK").i("Request Hyphen 2FA authenticate...")

        HyphenNetworking.Auth.signIn2FA(
            payload = HyphenRequestSignIn2FA(
                request = HyphenRequestSignIn2FA.Request(
                    method = "firebase",
                    token = idToken,
                    chainName = "flow-testnet",
                ),
                userKey = userKey,
            )
        ).suspendOnSuccess {
            Timber.tag("HyphenSDK").i("Request Hyphen 2FA authenticate successfully. Please check your another device.")

            HyphenEventBus.post(HyphenEventBusType.Show2FAWaitingProgressModal(show = true))

            val requestId = suspendCoroutine { continuation ->
                HyphenEventBus.observe<HyphenEventBusType> {
                    when (it) {
                        is HyphenEventBusType.TwoFactorAuthDenied -> {
                            Timber.tag("HyphenSDK").i("2FA denied")
                            HyphenEventBus.removeObserver(HyphenEventBusType::class)
                            continuation.resumeWithException(HyphenSdkError.TwoFactorDenied)
                        }

                        is HyphenEventBusType.TwoFactorAuthApproved -> {
                            Timber.tag("HyphenSDK").i("2FA approved")
                            HyphenEventBus.removeObserver(HyphenEventBusType::class)
                            continuation.resume(it.requestId)
                        }

                        else -> {}
                    }
                }
            }

            val result = HyphenNetworking.Auth.twoFactorFinish(
                payload = HyphenRequest2FAFinish(
                    twoFactorAuthRequestId = requestId,
                )
            ).getOrThrow()

            account = result.account
            Hyphen.saveCredential(result.credentials)
        }.suspendOnError {
            if (errorBody?.string()?.contains("please sign up") == true) {
                Timber.tag("HyphenSDK").i("Request Hyphen 2FA authenticate... - Failed -> Sign up needed")
                Timber.tag("HyphenSDK").i("Request Hyphen Sign up...")

                val result = HyphenNetworking.Auth.signUp(
                    payload = HyphenRequestSignUp(
                        method = "firebase",
                        token = idToken,
                        chainName = "flow-testnet",
                        userKey = userKey,
                    )
                ).getOrThrow()

                account = result.account
                Hyphen.saveCredential(result.credentials)
            }
        }
    }

    private suspend fun getHyphenUserKey(context: Context): HyphenUserKey {
        val fcmToken = Firebase.messaging.token.await()
        val publicKey = HyphenCryptography.getPublicKeyHex()

        val deviceInformation = suspendCoroutine<DeviceName.DeviceInfo> { continuation ->
            DeviceName.with(context).request(object : DeviceName.Callback {
                override fun onFinished(info: DeviceName.DeviceInfo?, error: Exception?) {
                    error?.let {
                        continuation.resumeWithException(it)
                        return
                    }

                    info?.let {
                        continuation.resume(it)
                    }
                }
            })
        }

        return HyphenUserKey(
            type = HyphenUserType.DEVICE,
            device = HyphenDevice(
                id = null,
                publicKey = publicKey,
                pushToken = fcmToken,
                name = deviceInformation.marketName.orEmpty(),
                osName = HyphenOSName.ANDROID,
                deviceManufacturer = deviceInformation.manufacturer.orEmpty(),
                deviceModel = deviceInformation.model.orEmpty(),
                lang = context.resources.configuration.locales.get(0).displayCountry,
                osVersion = Build.VERSION.RELEASE,
                type = HyphenDeviceType.MOBILE,
            ),
            publicKey = publicKey,
            wallet = null,
        )
    }
}