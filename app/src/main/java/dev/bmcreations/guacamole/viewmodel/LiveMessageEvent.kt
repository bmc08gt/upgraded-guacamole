package dev.bmcreations.guacamole.viewmodel

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

class LiveMessageEvent<T> : MutableLiveData<(T.() -> Unit)?>() {

    fun setEventReceiver(owner: LifecycleOwner, receiver: T) {
        observe(owner, Observer { event ->
            if (event != null) {
                receiver.event()
            }
        })
    }

    fun sendEvent(event: (T.() -> Unit)?) {
        value = event
    }
}
