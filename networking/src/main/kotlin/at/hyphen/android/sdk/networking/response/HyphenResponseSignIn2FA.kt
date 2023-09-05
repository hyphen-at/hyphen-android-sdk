package at.hyphen.android.sdk.networking.response

import at.hyphen.android.sdk.core.common.twofactor.Hyphen2FAStatus
import kotlinx.serialization.Serializable

@Serializable
data class HyphenResponseSignIn2FA(
    val twoFactorAuth: Hyphen2FAStatus,
    val ephemeralAccessToken: String?,
)
