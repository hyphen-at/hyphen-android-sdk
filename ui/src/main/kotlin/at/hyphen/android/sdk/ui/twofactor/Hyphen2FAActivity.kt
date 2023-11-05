package at.hyphen.android.sdk.ui.twofactor

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import at.hyphen.android.sdk.core.common.HyphenOSName
import at.hyphen.android.sdk.core.common.application.HyphenAppInformation
import at.hyphen.android.sdk.core.common.device.HyphenDevice
import at.hyphen.android.sdk.core.common.device.HyphenDeviceType
import at.hyphen.android.sdk.core.common.twofactor.Hyphen2FARequest
import at.hyphen.android.sdk.core.common.twofactor.Hyphen2FAStatus
import at.hyphen.android.sdk.core.common.twofactor.Hyphen2FAStatusType
import at.hyphen.android.sdk.core.common.twofactor.Hyphen2FAUserOpInfo
import at.hyphen.android.sdk.flow.HyphenFlow
import at.hyphen.android.sdk.networking.HyphenNetworking
import at.hyphen.android.sdk.ui.theme.HyphenUITheme
import kotlinx.coroutines.launch
import timber.log.Timber

class Hyphen2FAActivity : AppCompatActivity() {

    private var twoFactorAuth: Hyphen2FAStatus? by mutableStateOf(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val coroutineScope = rememberCoroutineScope()

            HyphenUITheme {
                Hyphen2FAView(
                    status = Hyphen2FAStatus(
                        id = "0000000000000000",
                        accountId = "000000000",
                        request = Hyphen2FARequest(
                            id = "000000",
                            app = HyphenAppInformation(
                                appId = "000000",
                                appName = "Hyphen SDK Sample"
                            ),
                            userOpInfo = Hyphen2FAUserOpInfo(
                                type = "sign-in",
                                signIn = Hyphen2FAUserOpInfo.SignIn(
                                    location = "Seoul, KR",
                                    ip = "127.0.0.1",
                                    email = "test@hyphen.at"
                                )
                            ),
                            srcDevice = HyphenDevice(
                                id = "000000",
                                publicKey = "000000",
                                pushToken = "000000",
                                name = "iPhone 15 Pro Max",
                                osName = HyphenOSName.ANDROID,
                                osVersion = "17.1.0",
                                deviceManufacturer = "Apple",
                                deviceModel = "iPhone16,2",
                                lang = "KR",
                                type = HyphenDeviceType.MOBILE,
                            ),
                            destDevice = HyphenDevice(
                                id = "000000",
                                publicKey = "000000",
                                pushToken = "000000",
                                name = "Google Pixel 6a",
                                osName = HyphenOSName.ANDROID,
                                osVersion = "12.0.1",
                                deviceManufacturer = "Google",
                                deviceModel = "pixel6a",
                                lang = "United States",
                                type = HyphenDeviceType.MOBILE,
                            ),
                            requestedAt = "2023-06-21T00:00:00:000",
                            message = "<cadence script>",
                        ),
                        status = Hyphen2FAStatusType.PENDING,
                        expiresAt = "2023-06-21T00:00:00:000",
                    ),
                    onApproveButtonClick = {
                        Timber.tag("HyphenUI")
                            .i("Generate and signing 'source device add public key' transaction...")
                        coroutineScope.launch {
                            HyphenFlow.signAndSendTransaction(
                                cadenceScript = """
                                    transaction {
                                        execute {
                                            log("Hello World")
                                        }
                                    }
                                """.trimIndent(),
                                arguments = emptyList()
                            )
                        }
                    },
                    onDenyButtonClick = {
                        coroutineScope.launch {
                            twoFactorAuth?.let {
                                HyphenNetworking.Device.deny2FA(id = it.id)
                                finish()
                            }
                        }
                    }
                )
            }
        }
    }
}
