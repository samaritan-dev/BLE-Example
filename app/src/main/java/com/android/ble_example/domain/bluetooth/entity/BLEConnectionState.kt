package com.android.ble_example.domain.bluetooth.entity

sealed class BLEConnectionState {
    object Idle : BLEConnectionState()
    object Scanning : BLEConnectionState()
    object Connecting : BLEConnectionState()
    object Connected : BLEConnectionState()
    object Disconnected : BLEConnectionState()
    object Error : BLEConnectionState()
    
    data class ErrorWithMessage(val message: String) : BLEConnectionState()
}
