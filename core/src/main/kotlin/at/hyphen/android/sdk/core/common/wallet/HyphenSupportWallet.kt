package at.hyphen.android.sdk.core.common.wallet

import kotlinx.serialization.Serializable

@Serializable
data class HyphenSupportWallet(
    val name: String,
    val kind: String
)
