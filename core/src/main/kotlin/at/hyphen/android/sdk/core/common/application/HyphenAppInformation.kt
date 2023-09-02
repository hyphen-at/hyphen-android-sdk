package at.hyphen.android.sdk.core.common.application

import kotlinx.serialization.Serializable

@Serializable
data class HyphenAppInformation(
    val appId: String,
    val appName: String
)
