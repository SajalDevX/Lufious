package ai.lufious.app.di

import ai.lufious.app.BuildConfig
import ai.lufious.app.presentation.auth.data.datasource.FirebaseAuthDataSource
import ai.lufious.app.presentation.auth.data.repository.AuthRepository
import ai.lufious.app.presentation.auth.data.repository.AuthRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder().build()

    @Singleton
    @Provides
    fun provideApiService(
        client: OkHttpClient, json: Json
    ): ApiRetrofit {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
            .create(ApiRetrofit::class.java)
    }

    @Provides @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides @Singleton
    fun provideAuthDataSource(auth: FirebaseAuth) =
        FirebaseAuthDataSource(auth)

    @Provides @Singleton
    fun provideAuthRepository(ds: FirebaseAuthDataSource): AuthRepository =
        AuthRepositoryImpl(ds)

    @Provides
    fun provideSignupUseCase(repo: AuthRepository) = SignupUseCase(repo)

    @Provides
    fun provideLoginUseCase(repo: AuthRepository) = LoginUseCase(repo)
}