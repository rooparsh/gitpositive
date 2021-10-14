package org.acmvit.gitpositive.remote

import org.acmvit.gitpositive.remote.model.Follower
import org.acmvit.gitpositive.remote.model.Following
import org.acmvit.gitpositive.remote.model.UserData
import org.acmvit.gitpositive.remote.model.RepositoryResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiInterface {
    @GET("users/{username}")
    fun getData(@Path("username") username: String?): Call<UserData>

    @GET("users/{username}/repos")
    suspend fun getReposForUser(
        @Path("username") username: String?,
        @Query("per_page") perPage: Int = 100
    ): RepositoryResponse

    @GET("users/{userName}/following")
    suspend fun getUserFollowing(
        @Path("userName") username: String,
        @Query("per_page") perPage: Int = 100
    ): Response<List<Following>>

    @GET("users/{userName}/followers")
    suspend fun getUserFollowers(
        @Path("userName") username: String,
        @Query("per_page") perPage: Int = 100
    ): Response<List<Follower>>
}
