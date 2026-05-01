package ai.lufious.app.di

import ai.lufious.app.presentation.shop.data.repository.ListingRepository
import ai.lufious.app.presentation.shop.data.repository.ListingRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ShopModule {

    @Provides
    @Singleton
    fun provideListingRepository(impl: ListingRepositoryImpl): ListingRepository = impl
}
