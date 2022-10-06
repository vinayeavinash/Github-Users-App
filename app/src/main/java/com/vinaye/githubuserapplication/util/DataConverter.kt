package com.vinaye.githubuserapplication.util

import com.vinaye.githubuserapplication.data.model.User
import com.vinaye.githubuserapplication.data.response.SearchUsersResponse
import com.vinaye.githubuserapplication.data.response.UserDetailResponse
import com.vinaye.githubuserapplication.data.response.UsersResponse


object DataConverter {
    const val STRING_NULL = "-"


    // convert   user resp to users model
    fun usersResponseToUsersModel(usersResponse: List<UsersResponse>): List<User> {
        val allUsers = mutableListOf<User>()

        for (user in usersResponse) {
            user.apply {
                allUsers.add(
                    User(
                        id = id,
                        username = username,
                        avatarUrl = avatar_url
                    )
                )
            }
        }

        return allUsers
    }

    //conv  search  users  to users model
    fun searchUsersToUserModels(searchUsersResponse: SearchUsersResponse): List<User> {
        val allUsers = mutableListOf<User>()

        if (searchUsersResponse.totalResults <= 0)
            return allUsers

        val usersResponse = searchUsersResponse.users

        for (user in usersResponse) {
            user.apply {
                allUsers.add(
                    User(
                        id = id,
                        username = username,
                        avatarUrl = avatar_url
                    )
                )
            }
        }

        return allUsers
    }

    //  conv users details   to users model
    fun userDetailResponseToUserModel(userResponse: UserDetailResponse): User =
        User(
            userResponse.id,
            userResponse.username,
            userResponse.avatarUrl,
            userResponse.fullName ?: STRING_NULL,
            userResponse.companyName ?: STRING_NULL,
            userResponse.blogUrl ?: STRING_NULL,
            userResponse.location ?: STRING_NULL,
            userResponse.email ?: STRING_NULL,
            userResponse.twitterUsername ?: STRING_NULL,
            userResponse.bio ?: STRING_NULL,
            userResponse.repositoriesCount,
            userResponse.gistsCount,
            userResponse.followersCount,
            userResponse.followingCount,
        )


}