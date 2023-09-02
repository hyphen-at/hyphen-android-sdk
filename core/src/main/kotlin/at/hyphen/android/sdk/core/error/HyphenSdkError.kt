package at.hyphen.android.sdk.core.error

sealed class HyphenSdkError : Exception() {

    data object NotInitialized : HyphenSdkError() {
        override val message: String get() = "SDK has not been properly initialized."
    }

    data object GoogleAuthError : HyphenSdkError() {
        override val message: String get() = "Error in Google authentication process."
    }

    data object Unauthorized : HyphenSdkError() {
        override val message: String get() = "User is not authorized to perform this operation."
    }

    data object TwoFactorDenied : HyphenSdkError() {
        override val message: String get() = "User denied the two-factor authentication request."
    }

    data object InternalSdkError : HyphenSdkError() {
        override val message: String get() = "An internal SDK error occurred."
    }
}
