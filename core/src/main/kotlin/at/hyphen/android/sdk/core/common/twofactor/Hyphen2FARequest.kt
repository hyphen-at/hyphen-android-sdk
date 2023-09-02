package at.hyphen.android.sdk.core.common.twofactor

import at.hyphen.android.sdk.core.common.application.HyphenAppInformation
import at.hyphen.android.sdk.core.common.device.HyphenDevice
import kotlinx.serialization.Serializable

@Serializable
data class Hyphen2FARequest(
    val id: String,
    val app: HyphenAppInformation,
    val userOpInfo: Hyphen2FAUserOpInfo,
    val srcDevice: HyphenDevice,
    val destDevice: HyphenDevice,
    val requestedAt: String,
    val message: String
)
