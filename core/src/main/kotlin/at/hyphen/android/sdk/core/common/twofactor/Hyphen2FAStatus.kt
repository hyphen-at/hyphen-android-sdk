package at.hyphen.android.sdk.core.common.twofactor

import kotlinx.serialization.Serializable

@Serializable
data class Hyphen2FAStatus(
    val id: String,
    val accountId: String,
    val request: Hyphen2FARequest,
    val status: Hyphen2FAStatusType,
    val expiresAt: String,
    val result: Result? = null
) {
    @Serializable
    data class Result(
        val txId: String
    )
}
