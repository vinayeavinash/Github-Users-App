package com.vinaye.githubuserapplication.data.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SearchUsersResponse(
    @SerializedName("total_count") @Expose val totalResults: Long,
    @SerializedName("items") @Expose val users: List<UsersResponse>
)