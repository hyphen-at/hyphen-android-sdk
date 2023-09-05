package at.hyphen.android.sdk.networking.request

import kotlinx.serialization.Serializable

@Serializable
data class HyphenRequestSignInChallengeRespond(
    val challengeType: String,
    val challengeData: String,
    val deviceKey: DeviceKey,
) {

    @Serializable
    data class DeviceKey(
        val signature: String,
    )
}
