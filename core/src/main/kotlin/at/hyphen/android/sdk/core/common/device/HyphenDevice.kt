package at.hyphen.android.sdk.core.common.device

import at.hyphen.android.sdk.core.common.HyphenOSName
import kotlinx.serialization.Serializable

@Serializable
data class HyphenDevice(
    val id: String?,
    val publicKey: String,
    val pushToken: String,
    val name: String,
    val osName: HyphenOSName,
    val osVersion: String,
    val deviceManufacturer: String,
    val deviceModel: String,
    val lang: String,
    val type: HyphenDeviceType
)

