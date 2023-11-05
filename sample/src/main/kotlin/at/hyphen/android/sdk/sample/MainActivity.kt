package at.hyphen.android.sdk.sample

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import at.hyphen.android.sdk.authenticate.HyphenAuthenticate
import at.hyphen.android.sdk.authenticate.HyphenAuthenticateDelegate
import at.hyphen.android.sdk.core.Hyphen
import at.hyphen.android.sdk.sample.ui.theme.HyphenSampleAppTheme
import at.hyphen.android.sdk.ui.twofactor.Hyphen2FAActivity
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class MainActivity : AppCompatActivity(), HyphenAuthenticateDelegate {
    private var networkState by mutableStateOf("Testnet")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Timber.plant(Timber.DebugTree())
        initializeHyphenSdk()

        val intent = Intent(this, Hyphen2FAActivity::class.java)
        startActivity(intent)

        setContent {
            HyphenSampleAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(
                                rememberScrollState()
                            )
                            .padding(16.dp),
                    ) {
                        Text(
                            text = "Hyphen SDK Demo",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            fontSize = 32.sp,
                        )
                        Text(
                            text = "This is the sample code for the Hyphen iOS SDK. " +
                                "Please refer to the app and implement it in the app.\n\n" +
                                "The Hyphen SDK already includes 2FA verification feature. After installing demo app on another device, you can test it by logging in using the same account.",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(top = 12.dp)
                        )
                        Button(
                            onClick = {
                                changeNetwork()
                            },
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Text(text = networkState)
                        }

                        Text(
                            text = "Login with Google",
                            style = MaterialTheme.typography.titleMedium,
                            fontSize = 24.sp,
                            modifier = Modifier.padding(top = 32.dp)
                        )

                        Button(
                            onClick = {
                                loginWithGoogle()
                            },
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Text(text = "Login with Google")
                        }
                    }
                }
            }
        }
    }

    private fun initializeHyphenSdk() {
        Hyphen.initialize(context = this)
        Hyphen.appSecret = "lH2rU/P0gdv5+zJfgQzITluGHBNtX2jG1JiBZNbKHPQ"
        Hyphen.network = Hyphen.NetworkType.MAINNET
    }

    private fun changeNetwork() {
        if (Hyphen.network == Hyphen.NetworkType.TESTNET) {
            Hyphen.network = Hyphen.NetworkType.MAINNET
            networkState = "Mainnet"
        } else {
            Hyphen.network = Hyphen.NetworkType.TESTNET
            networkState = "Testnet"
        }
    }

    private fun loginWithGoogle() {
        CoroutineScope(
            context = Dispatchers.IO
        ).launch(
            CoroutineExceptionHandler { coroutineContext, throwable ->
                Timber.e(throwable)
            }
        ) {
            HyphenAuthenticate.authenticate(
                activity = this@MainActivity,
                webClientId = "201778913659-dn4bo82q6hce3kfp7vstp04b22nh5hbi.apps.googleusercontent.com"
            )

            val hyphenAccount = HyphenAuthenticate.getAccount(context = this@MainActivity)
            Timber.tag("HyphenSample").i(hyphenAccount.toString())
        }
    }

    override var hyphenAuthenticateActivityResultLauncher: ActivityResultLauncher<Intent> =
        (this as ComponentActivity).registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            hyphenActivityResultCallback?.invoke(it)
        }

    override var hyphenActivityResultCallback: ((ActivityResult) -> Unit)? = null
}
