package at.hyphen.android.sdk.networking.response

import kotlinx.serialization.Serializable

@Serializable
data class HyphenResponseSignInChallenge(
    val challengeData: String,
    val expiresAt: String,
)
