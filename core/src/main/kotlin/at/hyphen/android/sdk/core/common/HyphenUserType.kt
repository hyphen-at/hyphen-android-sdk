import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class HyphenUserType {
    @SerialName("device")
    DEVICE,

    @SerialName("passkey")
    PASSKEY,

    @SerialName("wallet")
    WALLET
}
