package at.hyphen.android.sdk.core.common.account

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class HyphenChainType {
    @SerialName("evm")
    EVM,

    @SerialName("cadence")
    CADENCE,

    @SerialName("sealevel")
    SEALEVEL,

    @SerialName("movevm")
    MOVEVM
}
