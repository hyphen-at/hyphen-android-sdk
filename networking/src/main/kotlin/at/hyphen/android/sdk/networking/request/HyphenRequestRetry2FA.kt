package at.hyphen.android.sdk.networking.request

import kotlinx.serialization.Serializable

@Serializable
data class HyphenRequestRetry2FA(
    val destDeviceId: String,
)
