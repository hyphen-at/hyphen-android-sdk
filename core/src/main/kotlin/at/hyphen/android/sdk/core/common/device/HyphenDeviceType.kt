package at.hyphen.android.sdk.core.common.device

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class HyphenDeviceType {
    @SerialName("mobile")
    MOBILE,

    @SerialName("tablet")
    TABLET
}
