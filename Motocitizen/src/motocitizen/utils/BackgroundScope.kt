package motocitizen.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlin.coroutines.CoroutineContext

class BackgroundScope(dispatcher: CoroutineDispatcher) : CoroutineScope, AutoCloseable {
    private val handler = CoroutineExceptionHandler { _, exception ->
    }

    private val context = SupervisorJob() + dispatcher + handler

    override val coroutineContext: CoroutineContext
        get() = context

    override fun close() {
        context.cancel()
    }

    @Suppress("FunctionName")
    companion object {
        fun IO() = BackgroundScope(Dispatchers.IO)
        fun Unconfined() = BackgroundScope(Dispatchers.Unconfined)
        fun Default() = BackgroundScope(Dispatchers.Default)
    }
}