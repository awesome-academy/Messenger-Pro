package com.example.finalapplication.screen

import android.app.Application
import com.example.finalapplication.data.repository.ContactRepository
import com.example.finalapplication.data.repository.ContactRepositoryImpl
import com.example.finalapplication.data.repository.MessageRepository
import com.example.finalapplication.data.repository.MessageRepositoryImpl
import com.example.finalapplication.data.repository.NotificationRepository
import com.example.finalapplication.data.repository.NotificationRepositoryImpl
import com.example.finalapplication.data.repository.UserRepository
import com.example.finalapplication.data.repository.UserRepositoryIpml
import com.example.finalapplication.data.repository.resource.ContactDataSource
import com.example.finalapplication.data.repository.resource.MessageDataSource
import com.example.finalapplication.data.repository.resource.UserDataSource
import com.example.finalapplication.data.repository.resource.remote.RemoteContact
import com.example.finalapplication.data.repository.resource.remote.RemoteMessage
import com.example.finalapplication.data.repository.resource.remote.RemoteUser
import com.example.finalapplication.screen.chatroom.ChatViewModel
import com.example.finalapplication.screen.createaccount.CreateAccountViewModel
import com.example.finalapplication.screen.historycontact.HistoryContactViewModel
import com.example.finalapplication.screen.incoming.IncomingViewModel
import com.example.finalapplication.screen.launch.LaunchViewModel
import com.example.finalapplication.screen.login.LoginViewModel
import com.example.finalapplication.screen.main.MainViewModel
import com.example.finalapplication.screen.outgoing.OutgoingViewModel
import com.example.finalapplication.screen.profile.ProfileViewModel
import com.example.finalapplication.screen.search.SearchViewModel
import com.example.finalapplication.utils.ApiFcmService
import com.example.finalapplication.utils.FcmConstant
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {
    viewModel {
        LaunchViewModel(get())
    }
    viewModel {
        CreateAccountViewModel(get())
    }
    viewModel {
        LoginViewModel(get())
    }
    viewModel {
        ProfileViewModel(get())
    }
    viewModel {
        SearchViewModel(get())
    }
    viewModel {
        MainViewModel(get())
    }
    fun provideRetrofit(factory: Gson): Retrofit {
        val httpLogging = HttpLoggingInterceptor()
        httpLogging.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder()
            .addInterceptor {
                val request = it.request().newBuilder()
                return@addInterceptor it.proceed(request.build())
            }
            .addInterceptor(httpLogging)
            .build()
        return Retrofit.Builder()
            .baseUrl(FcmConstant.BASE_URL_FCM)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(factory))
            .build()
    }

    fun provideGson() = GsonBuilder()
        .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
        .create()

    fun provideApiService(retrofit: Retrofit) = retrofit.create(ApiFcmService::class.java)
    single { provideApiService(get()) }
    single { provideGson() }
    single { provideRetrofit(get()) }
    single<UserDataSource.Remote> { RemoteUser() }
    single<UserRepository> { UserRepositoryIpml(get()) }
}

val chatModule = module {
    viewModel {
        ChatViewModel(get(), get(), get())
    }
    viewModel {
        HistoryContactViewModel(get())
    }
    viewModel {
        OutgoingViewModel(get(), get())
    }
    viewModel {
        IncomingViewModel(get(), get())
    }
    single<ContactRepository> { ContactRepositoryImpl(get()) }
    single<ContactDataSource.Remote> { RemoteContact() }
    single<MessageDataSource.Remote> { RemoteMessage(get()) }
    single<MessageRepository> { MessageRepositoryImpl(get()) }
    single<NotificationRepository> { NotificationRepositoryImpl(get()) }
}

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MyApplication)
            modules(appModule, chatModule)
        }
    }
}
