package at.hyphen.android.sdk.core.common.key

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HyphenRecoverKey(
    val type: RecoveryType,
    val cloudKey: CloudKey?
) {
    @Serializable
    enum class RecoveryType {
        @SerialName("type")
        TYPE,

        @SerialName("cloudKey")
        CLOUD_KEY
    }

    @Serializable
    data class CloudKey(
        val accountName: String
    )
}
