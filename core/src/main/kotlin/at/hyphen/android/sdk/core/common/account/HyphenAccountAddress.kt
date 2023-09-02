package at.hyphen.android.sdk.core.common.account

import kotlinx.serialization.Serializable

@Serializable
data class HyphenAccountAddress(
    val chainName: String,
    val chainId: Int? = null,
    val chainType: HyphenChainType,
    val address: String,
    val domainName: String? = null
)
