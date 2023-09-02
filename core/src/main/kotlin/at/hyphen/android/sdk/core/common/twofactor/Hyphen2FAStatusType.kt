package at.hyphen.android.sdk.core.common.twofactor

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class Hyphen2FAStatusType {
    @SerialName("pending")
    PENDING,

    @SerialName("approved")
    APPROVED,

    @SerialName("denied")
    DENIED
}
