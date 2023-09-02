package at.hyphen.android.sdk.core.common.key

import HyphenUserType
import at.hyphen.android.sdk.core.common.device.HyphenDevice
import at.hyphen.android.sdk.core.common.wallet.HyphenSupportWallet
import kotlinx.serialization.Serializable

@Serializable
data class HyphenUserKey(
    val type: HyphenUserType,
    val publicKey: String?,
    val device: HyphenDevice?,
    val wallet: HyphenSupportWallet?,
)
