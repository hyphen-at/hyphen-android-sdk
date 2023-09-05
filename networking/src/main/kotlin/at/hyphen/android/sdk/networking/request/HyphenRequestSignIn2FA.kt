package at.hyphen.android.sdk.networking.request

import at.hyphen.android.sdk.core.common.key.HyphenUserKey
import kotlinx.serialization.Serializable

@Serializable
data class HyphenRequestSignIn2FA(
    val request: Request,
    val userKey: HyphenUserKey,
) {

    @Serializable
    data class Request(
        val method: String,
        val token: String,
        val chainName: String,
    )
}
