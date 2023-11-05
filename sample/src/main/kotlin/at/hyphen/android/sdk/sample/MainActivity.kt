package at.hyphen.android.sdk.sample

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import at.hyphen.android.sdk.authenticate.HyphenAuthenticate
import at.hyphen.android.sdk.authenticate.HyphenAuthenticateDelegate
import at.hyphen.android.sdk.core.Hyphen
import at.hyphen.android.sdk.flow.HyphenFlow
import at.hyphen.android.sdk.sample.ui.theme.HyphenSampleAppTheme
import at.hyphen.android.sdk.ui.HyphenUI
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class MainActivity : AppCompatActivity(), HyphenAuthenticateDelegate {
    private var networkState by mutableStateOf("Testnet")
    private var authText by mutableStateOf("")
    private var txText by mutableStateOf("")
    private var isLoading by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intent?.let {
            HyphenUI.handleHyphenIntent(this, it)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1)
        }

        Timber.plant(Timber.DebugTree())
        initializeHyphenSdk()

        setContent {
            HyphenSampleAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
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
                                text = "This is the sample code for the Hyphen Android SDK. " +
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
                            Text(
                                text = authText,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(top = 12.dp)
                            )

                            Text(
                                text = "Sign & Send Transaction ",
                                style = MaterialTheme.typography.titleMedium,
                                fontSize = 24.sp,
                                modifier = Modifier.padding(top = 32.dp)
                            )

                            Button(
                                onClick = {
                                    signAndSendTransaction()
                                },
                                modifier = Modifier.padding(top = 16.dp)
                            ) {
                                Text(text = "Signing & Send Transaction")
                            }
                            Text(
                                text = txText,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(top = 12.dp)
                            )
                        }

                        if (isLoading) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f)
                                    )
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        intent?.let {
            HyphenUI.handleHyphenIntent(this, it)
        }
    }

    private fun initializeHyphenSdk() {
        Hyphen.initialize(context = this)
        Hyphen.appSecret = "lH2rU/P0gdv5+zJfgQzITluGHBNtX2jG1JiBZNbKHPQ"
        Hyphen.network = Hyphen.NetworkType.TESTNET
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

            authText = "Authenticate Result : $hyphenAccount"
        }
    }

    private fun signAndSendTransaction() {
        isLoading = true
        CoroutineScope(
            context = Dispatchers.IO
        ).launch {
            val txId = HyphenFlow.signAndSendTransaction(
                cadenceScript = """
                transaction {
                    execute {
                        log("Hello World!!!")
                    }
                }
            """.trimIndent(),
                arguments = emptyList(),
                withAuthorizer = false,
            )
            txText = txId

            val urlIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://${if (Hyphen.network == Hyphen.NetworkType.TESTNET) "testnet." else ""}flowdiver.io/tx/${txId}?tab=overview")
            )
            runOnUiThread {
                startActivity(urlIntent)
            }

            isLoading = false
        }
    }

    override var hyphenAuthenticateActivityResultLauncher: ActivityResultLauncher<Intent> =
        (this as ComponentActivity).registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            hyphenActivityResultCallback?.invoke(it)
        }

    override var hyphenActivityResultCallback: ((ActivityResult) -> Unit)? = null
}
