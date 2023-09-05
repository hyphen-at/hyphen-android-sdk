package at.hyphen.android.sdk.networking.request

import at.hyphen.android.sdk.core.common.key.HyphenPublicKey
import kotlinx.serialization.Serializable

@Serializable
data class HyphenRequestSignInChallenge(
    val challengeType: String,
    val request: Request,
    val publicKey: HyphenPublicKey,
) {

    @Serializable
    data class Request(
        val method: String,
        val token: String,
        val chainName: String,
    )
}
