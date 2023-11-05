package at.hyphen.android.sdk.sample

import at.hyphen.android.sdk.ui.HyphenUI
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class SampleMessagingService: FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        HyphenUI.onFirebaseMessageReceived(message)
    }
}
