package com.kanthi.githubrepoexplorer.data.remote.api

import com.kanthi.githubrepoexplorer.data.remote.dto.RepositoryDto
import com.kanthi.githubrepoexplorer.data.remote.dto.SearchRepositoriesResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GithubApiService {

    @GET("search/repositories")
    suspend fun searchRepositories(
        @Query("q") query: String,
        @Query("sort") sort: String?,
        @Query("order") order: String? = "desc",
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
    ): SearchRepositoriesResponseDto

    @GET("repos/{owner}/{repo}")
    suspend fun getRepository(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
    ): RepositoryDto

    companion object {
        const val BASE_URL = "https://api.github.com/"
        const val PAGE_SIZE = 20
    }
}
