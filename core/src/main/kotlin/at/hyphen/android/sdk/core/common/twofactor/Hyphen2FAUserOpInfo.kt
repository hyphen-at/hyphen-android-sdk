package at.hyphen.android.sdk.core.common.twofactor

import kotlinx.serialization.Serializable

@Serializable
data class Hyphen2FAUserOpInfo(
    val type: String,
    val signIn: SignIn
) {

    @Serializable
    data class SignIn(
        val location: String,
        val ip: String,
        val email: String
    )
}
