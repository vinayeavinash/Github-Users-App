package com.vinaye.githubuserapplication.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class User(
    val id: Long,
    val username: String,
    val avatarUrl: String,
    var fullName: String = "",
    val companyName: String = "",
    val blogUrl: String = "",
    val location: String = "",
    val email: String = "",
    val twitterUsername: String = "",
    val bio: String = "",
    val repositoriesCount: Long = 0,
    val gistsCount: Long = 0,
    val followersCount: Long = 0,
    val followingCount: Long = 0
) : Parcelable