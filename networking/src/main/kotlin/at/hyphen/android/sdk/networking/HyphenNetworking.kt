package at.hyphen.android.sdk.networking

import at.hyphen.android.sdk.core.Hyphen
import at.hyphen.android.sdk.core.common.account.HyphenAccount
import at.hyphen.android.sdk.core.common.key.HyphenKey
import at.hyphen.android.sdk.core.common.key.HyphenPublicKey
import at.hyphen.android.sdk.networking.api.AccountAPI
import at.hyphen.android.sdk.networking.api.AuthAPI
import at.hyphen.android.sdk.networking.api.DeviceAPI
import at.hyphen.android.sdk.networking.api.KeyAPI
import at.hyphen.android.sdk.networking.api.SignAPI
import at.hyphen.android.sdk.networking.interceptor.HyphenHeaderInterceptor
import at.hyphen.android.sdk.networking.request.HyphenRequest2FAApprove
import at.hyphen.android.sdk.networking.request.HyphenRequest2FAFinish
import at.hyphen.android.sdk.networking.request.HyphenRequestEditDevice
import at.hyphen.android.sdk.networking.request.HyphenRequestRetry2FA
import at.hyphen.android.sdk.networking.request.HyphenRequestSign
import at.hyphen.android.sdk.networking.request.HyphenRequestSignIn2FA
import at.hyphen.android.sdk.networking.request.HyphenRequestSignInChallenge
import at.hyphen.android.sdk.networking.request.HyphenRequestSignInChallengeRespond
import at.hyphen.android.sdk.networking.request.HyphenRequestSignUp
import at.hyphen.android.sdk.networking.response.HyphenResponseSignIn
import at.hyphen.android.sdk.networking.response.HyphenResponseSignIn2FA
import at.hyphen.android.sdk.networking.response.HyphenResponseSignInChallenge
import at.hyphen.android.sdk.networking.response.HyphenSignResult
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.skydoves.sandwich.ApiResponse
import com.skydoves.sandwich.adapters.ApiResponseCallAdapterFactory
import com.skydoves.sandwich.mapSuccess
import com.skydoves.sandwich.onSuccess
import java.time.Duration
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

@Suppress("JSON_FORMAT_REDUNDANT")
object HyphenNetworking {

    @OptIn(ExperimentalSerializationApi::class)
    private val retrofit = Retrofit.Builder().apply {
        client(
            OkHttpClient.Builder().apply {
                connectTimeout(Duration.ZERO)
                writeTimeout(Duration.ZERO)
                readTimeout(Duration.ZERO)

                addInterceptor(
                    HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    }
                )
                addInterceptor(HyphenHeaderInterceptor())
            }.build()
        )

        baseUrl("https://api.dev.hyphen.at/")
        addConverterFactory(
            Json {
                prettyPrint = true
                ignoreUnknownKeys = true
                explicitNulls = false
            }.asConverterFactory("application/json".toMediaType())
        )
        addCallAdapterFactory(ApiResponseCallAdapterFactory.create())
    }.build()

    private val authApiService = retrofit.create(AuthAPI::class.java)

    private val accountApiService = retrofit.create(AccountAPI::class.java)

    private val deviceApiService = retrofit.create(DeviceAPI::class.java)

    private val keyApiService = retrofit.create(KeyAPI::class.java)

    private val signApiService = retrofit.create(SignAPI::class.java)

    object Account {
        suspend fun getAccount(): ApiResponse<HyphenAccount> =
            accountApiService.getMyAccount().mapSuccess { account }
    }

    object Auth {
        suspend fun signIn2FA(payload: HyphenRequestSignIn2FA): ApiResponse<HyphenResponseSignIn2FA> =
            authApiService.signIn2FA(requestPayload = payload).onSuccess {
                data.ephemeralAccessToken?.let { token ->
                    Hyphen.saveEphemeralAccessToken(token)
                }
            }

        suspend fun signInChallenge(payload: HyphenRequestSignInChallenge): ApiResponse<HyphenResponseSignInChallenge> =
            authApiService.signInChallenge(requestPayload = payload)

        suspend fun signInChallengeRespond(payload: HyphenRequestSignInChallengeRespond): ApiResponse<HyphenResponseSignIn> =
            authApiService.signInChallengeRespond(requestPayload = payload)

        suspend fun signUp(payload: HyphenRequestSignUp): ApiResponse<HyphenResponseSignIn> =
            authApiService.signUp(requestPayload = payload)

        suspend fun twoFactorFinish(payload: HyphenRequest2FAFinish): ApiResponse<HyphenResponseSignIn> =
            authApiService.twoFactorFinish(requestPayload = payload)
    }

    object Device {
        suspend fun editDevice(publicKey: HyphenPublicKey, payload: HyphenRequestEditDevice): ApiResponse<Unit> =
            deviceApiService.editDevice(publicKey = publicKey, requestPayload = payload)

        suspend fun retry2FA(id: String, payload: HyphenRequestRetry2FA): ApiResponse<Unit> =
            deviceApiService.retry2FA(id = id, requestPayload = payload)

        suspend fun deny2FA(id: String): ApiResponse<Unit> =
            deviceApiService.deny2FA(id = id)

        suspend fun approve2FA(id: String, payload: HyphenRequest2FAApprove): ApiResponse<Unit> =
            deviceApiService.approve2FA(id = id, requestPayload = payload)
    }

    object Key {
        suspend fun getKeys(): ApiResponse<List<HyphenKey>> =
            keyApiService.getKeys().mapSuccess { keys }
    }

    object Sign {
        suspend fun signTransactionWithServerKey(message: String): ApiResponse<HyphenSignResult> =
            signApiService.signTransactionWithServerKey(requestPayload = HyphenRequestSign(message = message))

        suspend fun signTransactionWithPayMasterKey(message: String): ApiResponse<HyphenSignResult> =
            signApiService.signTransactionWithPayMasterKey(requestPayload = HyphenRequestSign(message = message))
    }
}
