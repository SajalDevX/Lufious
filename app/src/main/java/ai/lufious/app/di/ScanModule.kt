package ai.lufious.app.di

import ai.lufious.app.presentation.scan.data.repository.ScanRepository
import ai.lufious.app.presentation.scan.data.repository.ScanRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ScanModule {

    @Provides
    @Singleton
    fun provideScanRepository(impl: ScanRepositoryImpl): ScanRepository = impl
}
