package at.hyphen.android.sdk.networking.response

import at.hyphen.android.sdk.core.common.key.HyphenKey
import kotlinx.serialization.Serializable

@Serializable
data class HyphenResponseKeys(
    val keys: List<HyphenKey>,
)
