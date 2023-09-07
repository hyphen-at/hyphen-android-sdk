package at.hyphen.android.sdk.authenticate

import android.app.Instrumentation
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher

interface HyphenAuthenticateDelegate {

    val hyphenAuthenticateActivityResultLauncher: ActivityResultLauncher<Intent>

    var hyphenActivityResultCallback: ((Instrumentation.ActivityResult) -> Unit)?
}
