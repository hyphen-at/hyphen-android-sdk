package at.hyphen.android.sdk.networking.api

import at.hyphen.android.sdk.networking.request.*
import at.hyphen.android.sdk.networking.response.HyphenResponseSignIn
import at.hyphen.android.sdk.networking.response.HyphenResponseSignIn2FA
import at.hyphen.android.sdk.networking.response.HyphenResponseSignInChallenge
import com.skydoves.sandwich.ApiResponse
import retrofit2.http.Body
import retrofit2.http.POST

internal interface AuthAPI {

    @POST("auth/v1/signin/2fa")
    suspend fun signIn2FA(@Body requestPayload: HyphenRequestSignIn2FA): ApiResponse<HyphenResponseSignIn2FA>

    @POST("auth/v1/signin/challenge")
    suspend fun signInChallenge(@Body requestPayload: HyphenRequestSignInChallenge): ApiResponse<HyphenResponseSignInChallenge>

    @POST("auth/v1/signin/challenge/respond")
    suspend fun signInChallengeRespond(@Body requestPayload: HyphenRequestSignInChallengeRespond): ApiResponse<HyphenResponseSignIn>

    @POST("auth/v1/signup")
    suspend fun signUp(@Body requestPayload: HyphenRequestSignUp): ApiResponse<HyphenResponseSignIn>

    @POST("auth/v1/signin/2fa/finish")
    suspend fun twoFactorFinish(@Body requestPayload: HyphenRequest2FAFinish): ApiResponse<HyphenResponseSignIn>
}
