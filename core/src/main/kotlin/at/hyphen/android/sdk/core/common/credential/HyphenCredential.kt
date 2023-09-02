package at.hyphen.android.sdk.core.common.credential

import kotlinx.serialization.Serializable

@Serializable
data class HyphenCredential(
    val accessToken: String,
    val refreshToken: String
)
