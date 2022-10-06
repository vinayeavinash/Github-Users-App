package com.vinaye.githubuserapplication.data.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class UserDetailResponse(
    val id: Long,
    @SerializedName("login") @Expose val username: String,
    @SerializedName("avatar_url") @Expose val avatarUrl: String,
    @SerializedName("name") @Expose val fullName: String?,
    @SerializedName("company") @Expose val companyName: String?,
    @SerializedName("blog") @Expose val blogUrl: String?,
    val location: String?,
    val email: String?,
    @SerializedName("twitter_username") @Expose val twitterUsername: String?,
    val bio: String?,
    @SerializedName("public_repos") @Expose val repositoriesCount: Long,
    @SerializedName("public_gists") @Expose val gistsCount: Long,
    @SerializedName("followers") @Expose val followersCount: Long,
    @SerializedName("following") @Expose val followingCount: Long
)