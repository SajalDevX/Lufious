package ai.lufious.app.di

import ai.lufious.app.BuildConfig
import ai.lufious.app.core.db.LufiousDatabase
import ai.lufious.app.core.db.dao.PlantDao
import ai.lufious.app.core.local_cache.LocalCacheManager
import ai.lufious.app.core.local_cache.LocalCacheManagerImpl
import ai.lufious.app.core.network.AuthInterceptor
import ai.lufious.app.core.network.LufiousApi
import ai.lufious.app.presentation.auth.data.datasource.FirebaseAuthDataSource
import ai.lufious.app.presentation.auth.data.repository.AuthRepository
import ai.lufious.app.presentation.auth.data.repository.AuthRepositoryImpl
import android.content.Context
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
        encodeDefaults = true
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(authInterceptor)
            .apply {
                if (BuildConfig.DEBUG) {
                    addInterceptor(
                        HttpLoggingInterceptor().apply {
                            level = HttpLoggingInterceptor.Level.BASIC
                        }
                    )
                }
            }
            .build()

    @Singleton
    @Provides
    fun provideRetrofit(client: OkHttpClient, json: Json): Retrofit {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    @Singleton
    @Provides
    fun provideLufiousApi(retrofit: Retrofit): LufiousApi =
        retrofit.create(LufiousApi::class.java)

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideAuthDataSource(auth: FirebaseAuth) = FirebaseAuthDataSource(auth)

    @Provides
    @Singleton
    fun provideAuthRepository(ds: FirebaseAuthDataSource): AuthRepository =
        AuthRepositoryImpl(ds)

    @Provides
    @Singleton
    fun provideLocalCacheManager(
        @ApplicationContext context: Context
    ): LocalCacheManager = LocalCacheManagerImpl(context)

    @Provides
    @Singleton
    fun provideLufiousDatabase(
        @ApplicationContext context: Context
    ): LufiousDatabase =
        Room.databaseBuilder(context, LufiousDatabase::class.java, "lufious.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun providePlantDao(db: LufiousDatabase): PlantDao = db.plantDao()
}
