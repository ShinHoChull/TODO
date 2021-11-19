package com.example.todo.di.modules


import net.daum.mf.map.api.MapView
import org.koin.dsl.module
import retrofit2.Retrofit


val apiModule = module {
    //single { get<Retrofit>().create(GitHubApi::class.java) }
}