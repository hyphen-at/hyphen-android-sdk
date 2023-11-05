package at.hyphen.android.sdk.authenticate

import android.app.Activity
import at.hyphen.android.sdk.core.error.HyphenSdkError
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await

object HyphenGoogleAuthenticate {

    suspend fun authenticate(activity: Activity, webClientId: String): AuthResult {
        if (FirebaseApp.getInstance().options.projectId == null) {
            throw HyphenSdkError.NotInitialized
        }

        if (activity as? HyphenAuthenticateDelegate == null) {
            throw HyphenSdkError.ActivityDelegateRegistrationFailed
        }

        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(webClientId)
            .build()

        val googleSignInClient = GoogleSignIn.getClient(activity, options)

        return suspendCancellableCoroutine { continuation ->
            val delegator = activity as HyphenAuthenticateDelegate

            delegator.hyphenActivityResultCallback = { result ->
                CoroutineScope(Dispatchers.IO).launch {
                    val rst = try {
                        GoogleSignIn.getSignedInAccountFromIntent(result.data).await()
                    } catch (e: ApiException) {
                        if (e.statusCode == GoogleSignInStatusCodes.CANCELED ||
                            e.statusCode == GoogleSignInStatusCodes.SIGN_IN_CANCELLED
                        ) {
                            continuation.resumeWithException(HyphenSdkError.GoogleAuthError)
                        } else {
                            continuation.resumeWithException(e)
                        }
                        null
                    } ?: return@launch

                    val idToken = rst.idToken.let {
                        if (it == null) {
                            continuation.resumeWithException(NullPointerException("idToken == null"))
                        }

                        it
                    } ?: return@launch

                    val credential = GoogleAuthProvider.getCredential(idToken, null)
                    val firebaseGoogleLoginResult =
                        Firebase.auth.signInWithCredential(credential).await()

                    continuation.resume(firebaseGoogleLoginResult)
                }
            }

            delegator.hyphenAuthenticateActivityResultLauncher.launch(googleSignInClient.signInIntent)
        }
    }
}
