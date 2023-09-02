package at.hyphen.android.sdk.core.crypto

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PublicKey
import java.security.Signature
import java.security.spec.ECGenParameterSpec
import java.util.Base64
import javax.crypto.Cipher

object HyphenCryptography {

    private const val ANDROID_KEYSTORE_NAME = "AndroidKeyStore"
    private const val KEY_ALIAS = "hyphen-device-key"

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
        val publicKey = keyStore.getCertificate(KEY_ALIAS).publicKey
        return publicKey
    }

    fun getPublicKeyHex(): String {
        val pubKey = getPublicKey()
        return Base64.getEncoder().encodeToString(pubKey.encoded)
    }

    fun signData(data: ByteArray): ByteArray? {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE_NAME)
        keyStore.load(null)

        val privateKeyEntry = keyStore.getEntry(KEY_ALIAS, null) as? KeyStore.PrivateKeyEntry
        val privateKey = privateKeyEntry?.privateKey ?: return null

        val signature = Signature.getInstance("SHA256withECDSA")
        signature.initSign(privateKey)
        signature.update(data)

        return signature.sign()
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
