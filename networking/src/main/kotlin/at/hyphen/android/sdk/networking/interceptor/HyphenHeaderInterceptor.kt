package at.hyphen.android.sdk.networking.interceptor

import at.hyphen.android.sdk.core.Hyphen
import okhttp3.Interceptor
import okhttp3.Response

internal class HyphenHeaderInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response =
        chain.proceed(
            chain.request()
                .newBuilder()
                .addHeader(
                    name = "X-Hyphen-App-Secret",
                    value = Hyphen.appSecret,
                )
                .addHeader(
                    name = "X-Hyphen-SDK-Platform",
                    value = "Android",
                )
                .addHeader(
                    name = "X-Hyphen-SDK-Version",
                    value = "0.1.0",
                )
                .apply {
                    Hyphen.getEphemeralAccessToken()?.let { tempAccessToken ->
                        if (tempAccessToken.isNotBlank()) {
                            this.addHeader("Authorization", "Bearer $tempAccessToken")
                        }
                        Hyphen.clearEphemeralAccessToken()
                    } ?: run {
                        Hyphen.getCredential().accessToken.takeIf { it.isNotBlank() }?.let { accessToken ->
                            this.addHeader("Authorization", "Bearer $accessToken")
                        }
                    }
                }
                .build()
        )
}
