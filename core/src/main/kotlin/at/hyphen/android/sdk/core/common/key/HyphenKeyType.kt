package at.hyphen.android.sdk.core.common.key

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class HyphenKeyType {
    @SerialName("user-key")
    USER_KEY,

    @SerialName("recover-key")
    RECOVER_KEY,

    @SerialName("server-key")
    SERVER_KEY,
}
