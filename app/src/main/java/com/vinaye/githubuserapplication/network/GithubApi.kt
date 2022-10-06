package com.vinaye.githubuserapplication.network

import com.vinaye.githubuserapplication.data.response.SearchUsersResponse
import com.vinaye.githubuserapplication.data.response.UserDetailResponse
import com.vinaye.githubuserapplication.data.response.UsersResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface GithubApi {

    companion object {
        // github base url and  token
        const val BASE_URL = "https://api.github.com/"
        const val TOKEN = "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"
    }

    // get users info
    @GET("users")
    @Headers("Authorization: token $TOKEN")
    suspend fun getUsers(): Response<List<UsersResponse>>

    // get user info
    @GET("users/{username}")
    @Headers("Authorization: token $TOKEN")
    suspend fun getUserDetailOf(@Path("username") username: String): Response<UserDetailResponse>

    // get following  info
    @GET("users/{username}/following")
    @Headers("Authorization: token $TOKEN")
    suspend fun getFollowingOfUser(@Path("username") username: String): Response<List<UsersResponse>>

    // get followers  info
    @GET("users/{username}/followers")
    @Headers("Authorization: token $TOKEN")
    suspend fun getFollowersOfUser(@Path("username") username: String): Response<List<UsersResponse>>

    // get search user   info
    @GET("search/users")
    @Headers("Authorization: token $TOKEN")
    suspend fun searchUsers(@Query("q") query: String): Response<SearchUsersResponse>
}