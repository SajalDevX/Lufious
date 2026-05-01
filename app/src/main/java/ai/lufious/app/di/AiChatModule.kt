package ai.lufious.app.di

import ai.lufious.app.presentation.scan.data.repository.AiChatRepository
import ai.lufious.app.presentation.scan.data.repository.AiChatRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AiChatModule {
    @Binds
    @Singleton
    abstract fun bindAiChatRepository(impl: AiChatRepositoryImpl): AiChatRepository
}
