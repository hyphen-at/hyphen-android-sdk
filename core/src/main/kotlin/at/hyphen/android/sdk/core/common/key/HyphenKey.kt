package at.hyphen.android.sdk.core.common.key

import kotlinx.serialization.Serializable

@Serializable
data class HyphenKey(
    val publicKey: HyphenPublicKey,
    val type: HyphenKeyType,
    val name: String,
    val keyIndex: Int,
    val userKey: HyphenUserKey? = null,
    val recoverKey: HyphenRecoverKey? = null,
    val lastUsedAt: String
)
