package at.hyphen.android.sdk.core.common.key

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HyphenPassKey(
    val platform: HyphenPassKeyPlatform
) {
    @Serializable
    enum class HyphenPassKeyPlatform {

        @SerialName("android")
        ANDROID,

        @SerialName("iOS")
        IOS
    }
}
