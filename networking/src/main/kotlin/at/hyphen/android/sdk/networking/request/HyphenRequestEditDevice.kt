package at.hyphen.android.sdk.networking.request

import kotlinx.serialization.Serializable

@Serializable
data class HyphenRequestEditDevice(
    val id: String? = null,
    val pushToken: String? = null,
    val name: String? = null,
    val osName: String? = null,
    val osVersion: String? = null,
    val deviceManufacturer: String? = null,
    val deviceModel: String? = null,
    val lang: String? = null,
)
