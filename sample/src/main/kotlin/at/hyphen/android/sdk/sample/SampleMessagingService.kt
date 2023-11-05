package at.hyphen.android.sdk.sample

import android.annotation.SuppressLint
import at.hyphen.android.sdk.ui.HyphenUI
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class SampleMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        HyphenUI.onFirebaseMessageReceived(applicationContext, message)
    }
}
