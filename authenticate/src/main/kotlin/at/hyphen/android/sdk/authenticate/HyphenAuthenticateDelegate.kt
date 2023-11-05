package at.hyphen.android.sdk.authenticate

import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher

interface HyphenAuthenticateDelegate {

    val hyphenAuthenticateActivityResultLauncher: ActivityResultLauncher<Intent>

    var hyphenActivityResultCallback: ((ActivityResult) -> Unit)?
}
