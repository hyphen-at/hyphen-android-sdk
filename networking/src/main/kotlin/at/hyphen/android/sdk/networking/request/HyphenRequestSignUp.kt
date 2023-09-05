package at.hyphen.android.sdk.networking.request

import at.hyphen.android.sdk.core.common.key.HyphenUserKey
import kotlinx.serialization.Serializable

@Serializable
data class HyphenRequestSignUp(
    val method: String,
    val token: String,
    val chainName: String,
    val userKey: HyphenUserKey,
)
