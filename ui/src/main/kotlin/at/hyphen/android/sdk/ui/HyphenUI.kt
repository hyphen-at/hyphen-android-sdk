@file:Suppress("JSON_FORMAT_REDUNDANT")

package at.hyphen.android.sdk.ui

import android.content.Context
import android.content.Intent
import at.hyphen.android.sdk.core.common.twofactor.Hyphen2FAStatus
import at.hyphen.android.sdk.core.common.twofactor.Hyphen2FAStatusType
import at.hyphen.android.sdk.core.eventbus.HyphenEventBus
import at.hyphen.android.sdk.core.eventbus.HyphenEventBusType
import at.hyphen.android.sdk.networking.response.HyphenResponseSignIn2FA
import at.hyphen.android.sdk.ui.twofactor.Hyphen2FAActivity
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

object HyphenUI {
    internal var pendingTwoFactorStatus: Hyphen2FAStatus? = null

    fun handleHyphenIntent(context: Context, intent: Intent) {
        val hyphenNotificationType = intent.getStringExtra("hyphen:type")
        val hyphenData = intent.getStringExtra("hyphen:data")

        if (hyphenNotificationType != null && hyphenData != null) {
            handle(context, hyphenNotificationType, hyphenData)
        }
    }

    fun onFirebaseMessageReceived(context: Context, message: RemoteMessage) {
        val data = message.data

        val hyphenNotificationType = data["hyphen:type"]
        val hyphenData = data["hyphen:data"]

        handle(context, hyphenNotificationType.orEmpty(), hyphenData.orEmpty())
    }

    private fun handle(context: Context, hyphenNotificationType: String, hyphenData: String) {
        if (hyphenNotificationType == "2fa-request") {
            val twoFactorRequest =
                Json {
                    prettyPrint = true
                    ignoreUnknownKeys = true
                    explicitNulls = false
                }.decodeFromString<HyphenResponseSignIn2FA>(hyphenData)
            pendingTwoFactorStatus = twoFactorRequest.twoFactorAuth

            val intent = Intent(context, Hyphen2FAActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        } else if (hyphenNotificationType == "2fa-status-change") {
            val twoFactorRequest =
                Json {
                    prettyPrint = true
                    ignoreUnknownKeys = true
                    explicitNulls = false
                }.decodeFromString<HyphenResponseSignIn2FA>(hyphenData)
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
        }
    }
}
