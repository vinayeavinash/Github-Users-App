package com.vinaye.githubuserapplication.di

import android.app.Application
import com.vinaye.githubuserapplication.network.GithubApi
import com.vinaye.githubuserapplication.repository.MainRepository
import com.vinaye.githubuserapplication.util.DispacherProvider

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // di provides  retrofit instance
    @Provides
    @Singleton
    fun provideRetrofitInstance(): Retrofit =
        Retrofit.Builder()
            .baseUrl(GithubApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    // di provides  github api
    @Provides
    @Singleton
    fun provideGithubApi(retrofit: Retrofit): GithubApi =
        retrofit.create(GithubApi::class.java)

    // di provides main repo
    @Provides
    @Singleton
    fun provideMainRepository(
        githubApi: GithubApi,
        application: Application
    ): MainRepository =
        MainRepository(githubApi, application)

    // di provides   coroutines  dispatchers
    @Provides
    @Singleton
    fun provideDispatchers(): DispacherProvider = object : DispacherProvider {
        override val main: CoroutineDispatcher
            get() = Dispatchers.Main
        override val io: CoroutineDispatcher
            get() = Dispatchers.IO
        override val default: CoroutineDispatcher
            get() = Dispatchers.Default
        override val unconfined: CoroutineDispatcher
            get() = Dispatchers.Unconfined
    }
}