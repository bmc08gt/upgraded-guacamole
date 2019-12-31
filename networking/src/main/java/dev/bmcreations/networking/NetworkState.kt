package dev.bmcreations.networking

class NetworkState(val status: Status, val msg: String) {

    enum class Status {
        RUNNING,
        SUCCESS,
        FAILED
    }

    companion object {

        val LOADED: NetworkState =
            NetworkState(
                Status.SUCCESS,
                "Success"
            )
        val LOADING: NetworkState =
            NetworkState(
                Status.RUNNING,
                "Running"
            )
    }
}
