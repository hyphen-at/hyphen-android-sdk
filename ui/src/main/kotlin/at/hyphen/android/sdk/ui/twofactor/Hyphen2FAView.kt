package at.hyphen.android.sdk.ui.twofactor

import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import at.hyphen.android.sdk.core.common.HyphenOSName
import at.hyphen.android.sdk.core.common.application.HyphenAppInformation
import at.hyphen.android.sdk.core.common.device.HyphenDevice
import at.hyphen.android.sdk.core.common.device.HyphenDeviceType
import at.hyphen.android.sdk.core.common.twofactor.Hyphen2FARequest
import at.hyphen.android.sdk.core.common.twofactor.Hyphen2FAStatus
import at.hyphen.android.sdk.core.common.twofactor.Hyphen2FAStatusType
import at.hyphen.android.sdk.core.common.twofactor.Hyphen2FAUserOpInfo
import at.hyphen.android.sdk.ui.foundation.rememberDrawablePainter
import kotlinx.coroutines.delay

@Composable
internal fun Hyphen2FAView(
    status: Hyphen2FAStatus,
    onApproveButtonClick: () -> Unit,
    onDenyButtonClick: () -> Unit,
) {
    var remainingTimeSeconds by remember { mutableIntStateOf(3 * 60) }
    val remainingTimeText by remember(remainingTimeSeconds) {
        derivedStateOf {
            if (remainingTimeSeconds == 0) {
                return@derivedStateOf ""
            }

            return@derivedStateOf " (${(remainingTimeSeconds / 60)}:${
                String.format(
                    "%02d",
                    remainingTimeSeconds % 60
                )
            })"
        }
    }

    LaunchedEffect(Unit) {
        while (remainingTimeSeconds != 0) {
            delay(1000)
            remainingTimeSeconds--
        }
    }

    Scaffold {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                val icon: Drawable =
                    LocalContext.current.packageManager.getApplicationIcon(LocalContext.current.packageName)
                Image(
                    painter = rememberDrawablePainter(drawable = icon),
                    contentDescription = "AppIcon",
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .padding(top = 28.dp)
                        .size(48.dp)
                )
                Text(
                    text = "${status.request.app.appName} requires to\nSign-In",
                    textAlign = TextAlign.Start,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 32.sp,
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .padding(horizontal = 20.dp)
                )
                Text(
                    text = "${status.request.srcDevice.deviceModel} is trying to sign-in into ${status.request.app.appName} in the ${status.request.userOpInfo.signIn.email} account.",
                    textAlign = TextAlign.Start,
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    modifier = Modifier
                        .padding(top = 14.dp)
                        .padding(horizontal = 20.dp)
                )
            }

            Column {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 28.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .border(
                            width = 1.dp,
                            brush = SolidColor(MaterialTheme.colorScheme.outline),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .background(color = MaterialTheme.colorScheme.primaryContainer)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Device",
                        textAlign = TextAlign.Start,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = status.request.srcDevice.deviceModel,
                        textAlign = TextAlign.Start,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .padding(top = 4.dp)
                    )

                    Text(
                        text = "App Name",
                        textAlign = TextAlign.Start,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 28.dp)
                    )
                    Text(
                        text = status.request.app.appName,
                        textAlign = TextAlign.Start,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .padding(top = 4.dp)
                    )

                    Text(
                        text = "Near",
                        textAlign = TextAlign.Start,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 28.dp)
                    )
                    Text(
                        text = status.request.userOpInfo.signIn.location,
                        textAlign = TextAlign.Start,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .padding(top = 4.dp)
                    )

                    Text(
                        text = "Time",
                        textAlign = TextAlign.Start,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 28.dp)
                    )
                    Text(
                        text = "Just now",
                        textAlign = TextAlign.Start,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .padding(top = 4.dp)
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 18.dp)
                        .navigationBarsPadding()
                        .fillMaxWidth()
                ) {
                    Button(
                        onClick = onDenyButtonClick,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "Deny")
                    }
                    Button(
                        onClick = onApproveButtonClick,
                        enabled = remainingTimeSeconds > 0,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "Approve$remainingTimeText")
                    }
                }
            }
        }
    }
}

@Preview(name = "Hyphen 2FA View")
@Composable
internal fun Hyphen2FAViewPreview() {
    Hyphen2FAView(
        status = Hyphen2FAStatus(
            id = "0000000000000000",
            accountId = "000000000",
            request = Hyphen2FARequest(
                id = "000000",
                app = HyphenAppInformation(appId = "000000", appName = "Hyphen SDK Sample"),
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
        onApproveButtonClick = {},
        onDenyButtonClick = {},
    )
}
