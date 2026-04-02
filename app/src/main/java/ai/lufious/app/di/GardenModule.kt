package ai.lufious.app.di

import ai.lufious.app.presentation.garden.data.repository.PlantRepository
import ai.lufious.app.presentation.garden.data.repository.PlantRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GardenModule {

    @Provides
    @Singleton
    fun providePlantRepository(impl: PlantRepositoryImpl): PlantRepository = impl
}
