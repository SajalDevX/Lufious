package ai.lufious.app.presentation.auth.data.usecases

import ai.lufious.app.core.local_cache.LocalCacheManager
import ai.lufious.app.presentation.auth.data.repository.AuthRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val authRepo: AuthRepository,
    private val localCache: LocalCacheManager
) {
    operator fun invoke() {
        authRepo.signOut()
        localCache.clearAll()
    }
}
