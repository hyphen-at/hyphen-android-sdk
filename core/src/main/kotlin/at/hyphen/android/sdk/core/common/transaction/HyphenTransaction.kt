package at.hyphen.android.sdk.core.common.transaction

import kotlinx.serialization.Serializable

@Serializable
data class HyphenTransaction(
    val id: String,
    val chainName: String,
    val refUrl: String
)
