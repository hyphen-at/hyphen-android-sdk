package at.hyphen.android.sdk.core.eventbus

sealed class HyphenEventBusType {

    data class Show2FAWaitingProgressModal(val show: Boolean) : HyphenEventBusType()

    data object TwoFactorAuthDenied : HyphenEventBusType()

    data class TwoFactorAuthApproved(val requestId: String) : HyphenEventBusType()
}
