package at.hyphen.android.sdk.core.crypto

import android.annotation.SuppressLint
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import at.hyphen.android.sdk.core.Hyphen
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PublicKey
import java.security.Signature
import java.security.interfaces.ECPublicKey
import java.security.spec.ECGenParameterSpec
import java.util.concurrent.Executor
import javax.crypto.Cipher
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@SuppressLint("StaticFieldLeak")
object HyphenCryptography {

    private const val ANDROID_KEYSTORE_NAME = "AndroidKeyStore"
    private const val KEY_ALIAS = "hyphen-device-key"

    private var executor: Executor? = null
    private var biometricPrompt: BiometricPrompt? = null
    private var promptInfo: BiometricPrompt.PromptInfo? = null

    private suspend fun initializeBiometricPrompt(signature: Signature): BiometricPrompt.CryptoObject? {
        return suspendCoroutine { continuation ->
            Hyphen.currentActivity.get()?.let { activity ->
                executor = ContextCompat.getMainExecutor(activity)
                biometricPrompt = BiometricPrompt(activity, executor!!,
                    object : BiometricPrompt.AuthenticationCallback() {
                        override fun onAuthenticationError(
                            errorCode: Int,
                            errString: CharSequence,
                        ) {
                            super.onAuthenticationError(errorCode, errString)
                            Toast.makeText(
                                activity,
                                "Authentication error: $errString", Toast.LENGTH_SHORT
                            ).show()
                        }

                        override fun onAuthenticationSucceeded(
                            result: BiometricPrompt.AuthenticationResult,
                        ) {
                            super.onAuthenticationSucceeded(result)
                            Toast.makeText(
                                activity,
                                "Authentication succeeded!", Toast.LENGTH_SHORT
                            ).show()

                            continuation.resume(result.cryptoObject)
                        }

                        override fun onAuthenticationFailed() {
                            super.onAuthenticationFailed()
                            Toast.makeText(
                                activity, "Authentication failed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })

                promptInfo = BiometricPrompt.PromptInfo.Builder()
                    .setTitle("Hyphen Authenticate")
                    .setSubtitle("Signing transaction with your device key.")
                    .setNegativeButtonText("Use device password")
                    .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
                    .build()

                biometricPrompt!!.authenticate(
                    promptInfo!!,
                    BiometricPrompt.CryptoObject(signature)
                )
            }
        }
    }

    fun isDeviceKeyExist(): Boolean {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE_NAME)
        keyStore.load(null)
        return keyStore.containsAlias(KEY_ALIAS)
    }

    fun generateKey() {
        val keyPairGenerator = KeyPairGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_EC, ANDROID_KEYSTORE_NAME
        )
        keyPairGenerator.initialize(
            KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY
            )
                .setDigests(KeyProperties.DIGEST_SHA256)
                .setAlgorithmParameterSpec(ECGenParameterSpec("secp256r1"))
                .setUserAuthenticationRequired(true)
                .build()
        )
        keyPairGenerator.generateKeyPair()
    }

    fun getPublicKey(): PublicKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE_NAME)
        keyStore.load(null)
        return keyStore.getCertificate(KEY_ALIAS).publicKey
    }

    fun getPublicKeyHex(): String {
        val pubKey = getPublicKey()
        val ecPubKey = pubKey as ECPublicKey

        val x = ecPubKey.params.generator.affineX.toString(16)
        val y = ecPubKey.params.generator.affineY.toString(16)

        return "$x$y"
    }

    suspend fun signData(data: ByteArray): ByteArray? {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE_NAME)
        keyStore.load(null)

        val privateKeyEntry = keyStore.getEntry(KEY_ALIAS, null) as? KeyStore.PrivateKeyEntry
        val privateKey = privateKeyEntry?.privateKey ?: return null

        val signature = Signature.getInstance("SHA256withECDSA")
        signature.initSign(privateKey)
        signature.update(data)

        val cryptObject = initializeBiometricPrompt(signature)

        return cryptObject!!.signature!!.sign()
    }

    fun encrypt(data: ByteArray): ByteArray? {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE_NAME)
        keyStore.load(null)
        val publicKey = keyStore.getCertificate(KEY_ALIAS).publicKey

        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)

        return cipher.doFinal(data)
    }

    fun decrypt(data: ByteArray): ByteArray? {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE_NAME)
        keyStore.load(null)

        val privateKeyEntry = keyStore.getEntry(KEY_ALIAS, null) as? KeyStore.PrivateKeyEntry
        val privateKey = privateKeyEntry?.privateKey ?: return null

        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(Cipher.DECRYPT_MODE, privateKey)

        return cipher.doFinal(data)
    }
}
