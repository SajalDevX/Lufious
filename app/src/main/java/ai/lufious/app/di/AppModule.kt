package ai.lufious.app.di

import ai.lufious.app.BuildConfig
import ai.lufious.app.core.firebase.FirestoreManager
import ai.lufious.app.core.local_cache.LocalCacheManager
import ai.lufious.app.core.local_cache.LocalCacheManagerImpl
import ai.lufious.app.core.utils.DispatcherProvider
import ai.lufious.app.presentation.auth.data.datasource.FirebaseAuthDataSource
import ai.lufious.app.presentation.auth.data.repository.AuthRepository
import ai.lufious.app.presentation.auth.data.repository.AuthRepositoryImpl
import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
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

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore =
        FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideAuthDataSource(auth: FirebaseAuth, firestoreManager: FirestoreManager) =
        FirebaseAuthDataSource(auth, firestoreManager)

    @Provides
    @Singleton
    fun provideAuthRepository(ds: FirebaseAuthDataSource): AuthRepository =
        AuthRepositoryImpl(ds)

    @Provides
    @Singleton
    fun provideLocalCacheManager(
        @ApplicationContext context: Context
    ): LocalCacheManager = LocalCacheManagerImpl(context)
}