package at.hyphen.android.sdk.networking.request

import kotlinx.serialization.Serializable

@Serializable
data class HyphenRequestSign(
    val message: String,
)
