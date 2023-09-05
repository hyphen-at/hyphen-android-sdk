package at.hyphen.android.sdk.networking.response

import kotlinx.serialization.Serializable

@Serializable
data class HyphenSignResult(
    val signature: Signature,
) {

    @Serializable
    data class Signature(
        val addr: String,
        val keyId: Long,
        val signature: String,
    )
}
