package at.hyphen.android.sdk.core.common.device

import at.hyphen.android.sdk.core.common.HyphenOSName
import kotlinx.serialization.Serializable

@Serializable
data class HyphenDeviceRegistration(
    val osName: HyphenOSName,
    val osVersion: String,
    val deviceManufacturer: String,
    val deviceModel: String,
    val lang: String
)
