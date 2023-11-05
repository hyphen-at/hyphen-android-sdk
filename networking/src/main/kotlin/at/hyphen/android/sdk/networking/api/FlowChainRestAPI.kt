package at.hyphen.android.sdk.networking.api

import at.hyphen.android.sdk.networking.request.flowchain.FlowTransactionRequest
import at.hyphen.android.sdk.networking.response.flowchain.FlowBlock
import at.hyphen.android.sdk.networking.response.flowchain.FlowTransactionResult
import com.skydoves.sandwich.ApiResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

internal interface FlowChainRestAPI {

    @GET("v1/blocks")
    suspend fun getLatestBlocks(
        @Query("expand") expand: String = "payload",
        @Query("height") height: String = "sealed"
    ): ApiResponse<List<FlowBlock>>

    @POST("v1/transactions")
    suspend fun postTransactions(
        @Body payload: FlowTransactionRequest,
    ): ApiResponse<FlowTransactionResult>
}
