package ai.lufious.app.core.utils

sealed class Result<T>(
    val data: T? = null, val message: String? = null
) {
    class Error<T>( message: String) : Result<T>(null, message)
    class Success<T>(data: T) : Result<T>(data)
}