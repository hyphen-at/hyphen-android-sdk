package at.hyphen.android.sdk.networking.response

import at.hyphen.android.sdk.core.common.account.HyphenAccount
import kotlinx.serialization.Serializable

@Serializable
data class HyphenResponseMyAccount(
    val account: HyphenAccount,
)
