package at.hyphen.android.sdk.networking.response

import at.hyphen.android.sdk.core.common.account.HyphenAccount
import at.hyphen.android.sdk.core.common.credential.HyphenCredential
import at.hyphen.android.sdk.core.common.transaction.HyphenTransaction
import kotlinx.serialization.Serializable

@Serializable
data class HyphenResponseSignIn(
    val account: HyphenAccount,
    val credentials: HyphenCredential,
    val transaction: HyphenTransaction?,
)
