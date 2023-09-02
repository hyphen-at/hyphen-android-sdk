package at.hyphen.android.sdk.core.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.RestrictTo
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey


@RestrictTo(RestrictTo.Scope.LIBRARY)
object HyphenSecurePreferences {

    private var hyphenSharedPreferences: SharedPreferences? = null

    private var isInitialized = false

    fun initialize(context: Context) {
        val masterKeyAlias = MasterKey
            .Builder(context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        hyphenSharedPreferences = EncryptedSharedPreferences.create(
            context,
            "hyphen_sdk_preferences",
            masterKeyAlias,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        isInitialized = true
    }

    fun getSharedPreferences(): SharedPreferences {
        if (!isInitialized) {
            throw IllegalStateException("HyphenSecurePreferences is not initialized")
        }

        return hyphenSharedPreferences!!
    }
}
