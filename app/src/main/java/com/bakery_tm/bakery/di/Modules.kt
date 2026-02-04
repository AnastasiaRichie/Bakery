package com.bakery_tm.bakery.di

import android.content.Context
import androidx.room.Room
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.bakery_tm.bakery.common.AuthManager
import com.bakery_tm.bakery.data.FoodRepositoryImpl
import com.bakery_tm.bakery.data.OrderRepositoryImpl
import com.bakery_tm.bakery.data.ShoppingCartRepositoryImpl
import com.bakery_tm.bakery.data.UserRepositoryImpl
import com.bakery_tm.bakery.data.api.AuthInterceptor
import com.bakery_tm.bakery.data.api.ErrorHandler
import com.bakery_tm.bakery.data.api.FoodApi
import com.bakery_tm.bakery.data.api.OrderApi
import com.bakery_tm.bakery.data.api.UserApi
import com.bakery_tm.bakery.data.api.WebSocketManager
import com.bakery_tm.bakery.data.database.BakeryDatabase
import com.bakery_tm.bakery.domain.AvatarPreferences
import com.bakery_tm.bakery.domain.FoodRepository
import com.bakery_tm.bakery.domain.OrderRepository
import com.bakery_tm.bakery.domain.ShoppingCartRepository
import com.bakery_tm.bakery.domain.UserRepository
import com.bakery_tm.bakery.view_model.FoodViewModel
import com.bakery_tm.bakery.view_model.ForgotPasswordViewModel
import com.bakery_tm.bakery.view_model.MainViewModel
import com.bakery_tm.bakery.view_model.OrderViewModel
import com.bakery_tm.bakery.view_model.RegistrationViewModel
import com.bakery_tm.bakery.view_model.ShoppingCartViewModel
import com.bakery_tm.bakery.view_model.UserViewModel
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {
    single {
        Room.databaseBuilder(androidContext(), BakeryDatabase::class.java, "bakery_database").build()
    }
    single { GsonBuilder().setLenient().create() }
    single { CoroutineScope(SupervisorJob() + Dispatchers.IO) }
    single { get<Retrofit>().create(UserApi::class.java) }
    single { get<Retrofit>().create(OrderApi::class.java) }
    single { get<Retrofit>().create(FoodApi::class.java) }
    single { get<BakeryDatabase>().userDao() }
    single { get<BakeryDatabase>().orderDao() }
    single { get<BakeryDatabase>().productDao() }
    single { get<BakeryDatabase>().cartDao() }
    single<UserRepository> { UserRepositoryImpl(get(), get(), get(), get(), get()) }
    single<FoodRepository> { FoodRepositoryImpl(get(), get()) }
    single<OrderRepository> { OrderRepositoryImpl(get(), get(), get()) }
    single<ShoppingCartRepository> { ShoppingCartRepositoryImpl(get(), get()) }
    single { AvatarPreferences(androidContext()) }
    single { provideEncryptedPrefs(get()) }
    single { ErrorHandler(get()) }
    single { AuthManager(get(), get()) }
    single { AuthInterceptor(get()) }
    single {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }
    single(named("authClient")) {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        OkHttpClient.Builder()
            .addInterceptor(get<AuthInterceptor>())
            .addInterceptor(logging)
            .build()
    }
    single<FoodApi> {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FoodApi::class.java)
    }

    single<UserApi> {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UserApi::class.java)
    }
    single<OrderApi> {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(get(named("authClient")))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OrderApi::class.java)
    }
    single {
        WebSocketManager(get(named("authClient")))
    }
    viewModel { RegistrationViewModel(get(), get()) }
    viewModel { FoodViewModel(get()) }
    viewModel { UserViewModel(get(), get(), get()) }
    viewModel { OrderViewModel(get(), get(), get()) }
    viewModel { ShoppingCartViewModel(get(), get()) }
    viewModel { ForgotPasswordViewModel(get()) }
    viewModel { MainViewModel(userRepository = get(), webSocketManager = get()) }
}

fun provideEncryptedPrefs(context: Context) =
    EncryptedSharedPreferences.create(
        context,
        "prefs",
        MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

private const val BASE_URL = "http://192.168.144.158:8080/api/"