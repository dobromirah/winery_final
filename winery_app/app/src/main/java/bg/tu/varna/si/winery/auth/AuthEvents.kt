package bg.tu.varna.si.winery.auth

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object AuthEvents {
    private val _logout = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val logout = _logout.asSharedFlow()

    fun emitLogout() {
        _logout.tryEmit(Unit)
    }
}
