package at.hyphen.android.sdk.core.eventbus

import androidx.annotation.RestrictTo
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
object HyphenEventBus {
    private val eventBus = EventBus.builder().build()
    private var onEventReceived: ((HyphenEventBusType) -> Unit)? = null

    internal fun initialize() {
        if (!eventBus.isRegistered(this)) {
            eventBus.register(this)
        }
    }

    fun post(event: HyphenEventBusType) {
        eventBus.post(event)
    }

    @Suppress("unused")
    @Subscribe
    internal fun onHyphenEventReceived(event: HyphenEventBusType) {
        onEventReceived?.invoke(event)
    }

    fun observe(callback: (HyphenEventBusType) -> Unit) {
        onEventReceived = callback
    }
}
