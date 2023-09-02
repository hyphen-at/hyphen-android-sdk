package at.hyphen.android.sdk.core.common.key.registration

import HyphenUserType
import at.hyphen.android.sdk.core.common.device.HyphenDeviceRegistration
import at.hyphen.android.sdk.core.common.key.HyphenPassKey
import at.hyphen.android.sdk.core.common.key.HyphenPublicKey
import at.hyphen.android.sdk.core.common.wallet.HyphenSupportWallet
import kotlinx.serialization.Serializable

@Serializable
data class HyphenUserKeyRegistration(
    val publicKey: HyphenPublicKey,
    val type: HyphenUserType,
    val device: HyphenDeviceRegistration? = null,
    val passKey: HyphenPassKey? = null,
    val wallet: HyphenSupportWallet? = null
)
