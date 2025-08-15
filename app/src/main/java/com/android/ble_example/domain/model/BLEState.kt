package com.android.ble_example.domain.model

sealed class BLEState {
    object Idle : BLEState()
    object Scanning : BLEState()
    object Connecting : BLEState()
    object Connected : BLEState()
    object Disconnected : BLEState()
    object Error : BLEState()
    
    data class ErrorWithMessage(val message: String) : BLEState()
}
