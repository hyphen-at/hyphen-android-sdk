package at.hyphen.android.sdk.ui.twofactor

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import at.hyphen.android.sdk.flow.HyphenFlow
import at.hyphen.android.sdk.flow.HyphenFlowCadence
import at.hyphen.android.sdk.networking.HyphenNetworking
import at.hyphen.android.sdk.networking.request.HyphenRequest2FAApprove
import at.hyphen.android.sdk.ui.HyphenUI
import at.hyphen.android.sdk.ui.theme.HyphenUITheme
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import timber.log.Timber

class Hyphen2FAActivity : AppCompatActivity() {

    private var isProcessing by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val coroutineScope = rememberCoroutineScope()

            HyphenUI.pendingTwoFactorStatus?.let {
                HyphenUITheme {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        Hyphen2FAView(
                            status = it,
                            onApproveButtonClick = {
                                coroutineScope.launch {
                                    approve2FA()
                                }
                            },
                            onDenyButtonClick = {
                                coroutineScope.launch {
                                    reject2FA()
                                }
                            }
                        )

                        if (isProcessing) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f)
                                    )
                                    .pointerInput(Unit) {}
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.align(Alignment.Center),
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private suspend fun reject2FA() {
        val twoFactorAuth = HyphenUI.pendingTwoFactorStatus
            ?: error("HyphenUI SDK internal error. UI state 'twoFactorAuth' is nil.")

        isProcessing = true

        HyphenNetworking.Device.deny2FA(twoFactorAuth.id)
        HyphenUI.pendingTwoFactorStatus = null
        finish()
    }

    private suspend fun approve2FA() {
        isProcessing = true

        Timber.tag("HyphenUI")
            .i("Generate and signing 'source device add public key' transaction...")

        val twoFactorAuth = HyphenUI.pendingTwoFactorStatus
            ?: error("HyphenUI SDK internal error. UI state 'twoFactorAuth' is nil.")

        val json = Json {
            prettyPrint = true
            ignoreUnknownKeys = true
            explicitNulls = false
        }
        val cadenceScript: HyphenFlowCadence = json.decodeFromString(twoFactorAuth.request.message)
        val tx = HyphenFlow.signAndSendTransaction(cadenceScript.cadence, arguments = emptyList())

        HyphenNetworking.Device.approve2FA(
            id = twoFactorAuth.id,
            payload = HyphenRequest2FAApprove(txId = tx)
        )

        HyphenUI.pendingTwoFactorStatus = null
        Timber.tag("HyphenUI").i("Approve 2FA done!")

        finish()
    }
}
