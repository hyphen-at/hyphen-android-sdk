package at.hyphen.android.sdk.core.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class HyphenOSName {
    @SerialName("iOS")
    IOS,

    @SerialName("iPadOS")
    IPADOS,

    @SerialName("Android")
    ANDROID
}
