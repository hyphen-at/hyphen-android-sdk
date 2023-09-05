package at.hyphen.android.sdk.networking.api

import at.hyphen.android.sdk.core.common.key.HyphenPublicKey
import at.hyphen.android.sdk.networking.request.HyphenRequest2FAApprove
import at.hyphen.android.sdk.networking.request.HyphenRequestEditDevice
import at.hyphen.android.sdk.networking.request.HyphenRequestRetry2FA
import com.skydoves.sandwich.ApiResponse
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

internal interface DeviceAPI {

    @PUT("device/v1/devices/{publicKey}")
    suspend fun editDevice(
        @Path("publicKey") publicKey: HyphenPublicKey,
        requestPayload: HyphenRequestEditDevice
    ): ApiResponse<Unit>

    @PUT("device/v1/2fa/{id}")
    suspend fun retry2FA(@Path("id") id: String, requestPayload: HyphenRequestRetry2FA): ApiResponse<Unit>

    @DELETE("device/v1/2fa/{id}")
    suspend fun deny2FA(@Path("id") id: String): ApiResponse<Unit>

    @POST("/device/v1/2fa/{id}/approve")
    suspend fun approve2FA(@Path("id") id: String, requestPayload: HyphenRequest2FAApprove): ApiResponse<Unit>
}
