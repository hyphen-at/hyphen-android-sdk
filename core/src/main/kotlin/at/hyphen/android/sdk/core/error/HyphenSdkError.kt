package at.hyphen.android.sdk.core.error

sealed class HyphenSdkError : Exception() {

    data object NotInitialized : HyphenSdkError() {
        private fun readResolve(): Any = NotInitialized
        override val message: String get() = "SDK has not been properly initialized."
    }

    data object ActivityDelegateRegistrationFailed : HyphenSdkError() {
        private fun readResolve(): Any = ActivityDelegateRegistrationFailed
        override val message: String get() = "Activity does not implement HyphenAuthenticateDelegate."
    }

    data object GoogleAuthError : HyphenSdkError() {
        private fun readResolve(): Any = GoogleAuthError
        override val message: String get() = "Error in Google authentication process."
    }

    data object Unauthorized : HyphenSdkError() {
        private fun readResolve(): Any = Unauthorized
        override val message: String get() = "User is not authorized to perform this operation."
    }

    data object TwoFactorDenied : HyphenSdkError() {
        private fun readResolve(): Any = TwoFactorDenied
        override val message: String get() = "User denied the two-factor authentication request."
    }

    data object InternalSdkError : HyphenSdkError() {
        private fun readResolve(): Any = InternalSdkError
        override val message: String get() = "An internal SDK error occurred."
    }
}
