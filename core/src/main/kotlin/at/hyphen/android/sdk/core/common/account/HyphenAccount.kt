package at.hyphen.android.sdk.core.common.account

import kotlinx.serialization.Serializable

@Serializable
data class HyphenAccount(
    val id: String,
    val addresses: List<HyphenAccountAddress>,
    val parent: List<HyphenAccountAddress>,
    val createdAt: String
)
