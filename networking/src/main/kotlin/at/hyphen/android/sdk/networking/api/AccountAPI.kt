package at.hyphen.android.sdk.networking.api

import at.hyphen.android.sdk.networking.response.HyphenResponseMyAccount
import com.skydoves.sandwich.ApiResponse
import retrofit2.http.GET

internal interface AccountAPI {

    @GET("account/v1/me")
    suspend fun getMyAccount(): ApiResponse<HyphenResponseMyAccount>
}
