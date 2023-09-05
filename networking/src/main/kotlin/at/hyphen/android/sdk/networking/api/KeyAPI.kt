package at.hyphen.android.sdk.networking.api

import at.hyphen.android.sdk.networking.response.HyphenResponseKeys
import com.skydoves.sandwich.ApiResponse
import retrofit2.http.GET

internal interface KeyAPI {

    @GET("key/v1/keys")
    suspend fun getKeys(): ApiResponse<HyphenResponseKeys>
}
