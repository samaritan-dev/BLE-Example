package com.android.ble_example.domain.bluetooth.entity

import android.bluetooth.BluetoothGattService
import java.util.UUID

data class BLEGattService(
    val service: BluetoothGattService,
    val uuid: UUID,
    val type: Int,
    val characteristics: List<BLEGattCharacteristic> = emptyList()
)
