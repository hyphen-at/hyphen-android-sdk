package at.hyphen.android.sdk.core.common.key.registration

import kotlinx.serialization.Serializable

@Serializable
data class HyphenRecoverKeyRegistration(
    val publicKey: String,
    val cloudKey: CloudKey?
) {
    @Serializable
    data class CloudKey(
        val accountName: String
    )
}
