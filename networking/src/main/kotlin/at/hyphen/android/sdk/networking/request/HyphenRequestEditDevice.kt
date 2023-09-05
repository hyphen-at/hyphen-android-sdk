package at.hyphen.android.sdk.networking.request

import kotlinx.serialization.Serializable

@Serializable
data class HyphenRequestEditDevice(
    val id: String?,
    val pushToken: String?,
    val name: String?,
    val osName: String?,
    val osVersion: String?,
    val deviceManufacturer: String?,
    val deviceModel: String?,
    val lang: String?,
)
