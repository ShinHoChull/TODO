package com.leaf.android_mvvm_example.di.modules

import com.example.todo.di.app.MyApplication

import org.koin.dsl.module


//private val httpLoggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
//
//private val provideOkHttpClient = OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor).build()
//
//val networkModule = module {
//
//    single {
//        Retrofit.Builder()
//            .baseUrl(MyApplication.BASE_URL)
//            .client(provideOkHttpClient)
//            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//    }
//}