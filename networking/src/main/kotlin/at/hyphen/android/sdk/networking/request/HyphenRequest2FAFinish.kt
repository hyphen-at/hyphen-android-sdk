package at.hyphen.android.sdk.networking.request

import kotlinx.serialization.Serializable

@Serializable
data class HyphenRequest2FAFinish(
    val twoFactorAuthRequestId: String,
)
