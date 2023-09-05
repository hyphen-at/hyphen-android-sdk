package at.hyphen.android.sdk.networking.api

import at.hyphen.android.sdk.networking.request.HyphenRequestSign
import at.hyphen.android.sdk.networking.response.HyphenSignResult
import com.skydoves.sandwich.ApiResponse
import retrofit2.http.Body
import retrofit2.http.POST

internal interface SignAPI {

    @POST("sign/v1/cadence/transaction")
    suspend fun signTransactionWithServerKey(@Body requestPayload: HyphenRequestSign): ApiResponse<HyphenSignResult>

    @POST("sign/v1/cadence/paymaster")
    suspend fun signTransactionWithPayMasterKey(@Body requestPayload: HyphenRequestSign): ApiResponse<HyphenSignResult>
}
