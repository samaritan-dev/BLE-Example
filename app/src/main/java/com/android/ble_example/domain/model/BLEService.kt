package com.android.ble_example.domain.model

import android.bluetooth.BluetoothGattService
import java.util.UUID

data class BLEService(
    val service: BluetoothGattService,
    val uuid: UUID,
    val type: Int,
    val characteristics: List<BLECharacteristic> = emptyList()
)
