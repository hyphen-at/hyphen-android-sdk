@file:Suppress("JSON_FORMAT_REDUNDANT")

package at.hyphen.android.sdk.ui

import at.hyphen.android.sdk.core.common.twofactor.Hyphen2FAStatusType
import at.hyphen.android.sdk.core.eventbus.HyphenEventBus
import at.hyphen.android.sdk.core.eventbus.HyphenEventBusType
import at.hyphen.android.sdk.networking.response.HyphenResponseSignIn2FA
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import timber.log.Timber

object HyphenUI {
    fun onFirebaseMessageReceived(message: RemoteMessage) {
        val data = message.data

        val hyphenNotificationType = data["hyphen:type"]
        val hyphenData = data["hyphen:data"]

        if (hyphenNotificationType == "2fa-status-change") {
            val twoFactorRequest =
                Json {
                    prettyPrint = true
                    ignoreUnknownKeys = true
                    explicitNulls = false
                }.decodeFromString<HyphenResponseSignIn2FA>(hyphenData.orEmpty())
            HyphenEventBus.post(HyphenEventBusType.Show2FAWaitingProgressModal(show = false))

            if (twoFactorRequest.twoFactorAuth.status == Hyphen2FAStatusType.APPROVED) {
                CoroutineScope(Dispatchers.IO).launch {
                    delay(3_000)
                    HyphenEventBus.post(
                        HyphenEventBusType.TwoFactorAuthApproved(
                            requestId = twoFactorRequest.twoFactorAuth.request.id
                        )
                    )
                }
            } else if (twoFactorRequest.twoFactorAuth.status == Hyphen2FAStatusType.DENIED) {
                HyphenEventBus.post(HyphenEventBusType.TwoFactorAuthDenied)
            }
        } else if (hyphenNotificationType == "")
            Timber.tag("HyphenUI").e(message.data.toString())
    }
}
