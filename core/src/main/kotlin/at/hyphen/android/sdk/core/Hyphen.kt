package at.hyphen.android.sdk.core

import android.content.Context
import androidx.annotation.RestrictTo
import androidx.fragment.app.FragmentActivity
import at.hyphen.android.sdk.core.common.credential.HyphenCredential
import at.hyphen.android.sdk.core.eventbus.HyphenEventBus
import at.hyphen.android.sdk.core.preferences.HyphenSecurePreferences
import java.lang.ref.WeakReference
import timber.log.Timber


object Hyphen {

    enum class NetworkType {
        TESTNET,
        MAINNET
    }

    private var _appSecret: String = ""

    var network: NetworkType = NetworkType.TESTNET

    var currentActivity = WeakReference<FragmentActivity?>(null)

    var appSecret: String
        set(newValue) {
            if (_appSecret.isEmpty()) {
                _appSecret = newValue
                Timber.tag("HyphenSDK").i("Hyphen appSecret set successfully.")
            } else {
                Timber.tag("HyphenSDK").w("Hyphen appSecret already setted.")
            }
        }
        get() {
            return _appSecret
        }

    fun initialize(context: Context) {
        HyphenEventBus.initialize()
        HyphenSecurePreferences.initialize(context)
    }

    fun saveCredential(credential: HyphenCredential) {
        val editor = HyphenSecurePreferences.getSharedPreferences().edit()
        editor.putString("at.hyphen.sdk.credential.accessToken", credential.accessToken)
        editor.putString("at.hyphen.sdk.credential.refreshToken", credential.refreshToken)
        editor.apply()
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    fun saveEphemeralAccessToken(ephemeralAccessToken: String) {
        val editor = HyphenSecurePreferences.getSharedPreferences().edit()
        editor.putString("at.hyphen.sdk.credential.ephemeralAccessToken", ephemeralAccessToken)
        editor.apply()
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    fun getEphemeralAccessToken(): String? {
        return HyphenSecurePreferences.getSharedPreferences()
            .getString("at.hyphen.sdk.credential.ephemeralAccessToken", null)
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    fun clearEphemeralAccessToken() {
        val editor = HyphenSecurePreferences.getSharedPreferences().edit()
        editor.remove("at.hyphen.sdk.credential.ephemeralAccessToken")
        editor.apply()
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    fun getCredential(): HyphenCredential {
        val accessToken = HyphenSecurePreferences.getSharedPreferences()
            .getString("at.hyphen.sdk.credential.accessToken", "") ?: ""
        val refreshToken = HyphenSecurePreferences.getSharedPreferences()
            .getString("at.hyphen.sdk.credential.refreshToken", "") ?: ""

        return HyphenCredential(accessToken = accessToken, refreshToken = refreshToken)
    }
}
