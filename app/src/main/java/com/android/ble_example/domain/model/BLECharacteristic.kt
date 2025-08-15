package com.android.ble_example.domain.model

import android.bluetooth.BluetoothGattCharacteristic
import java.util.UUID

data class BLECharacteristic(
    val characteristic: BluetoothGattCharacteristic,
    val uuid: UUID,
    val properties: Int,
    val permissions: Int,
    val value: ByteArray? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BLECharacteristic

        if (uuid != other.uuid) return false
        if (properties != other.properties) return false
        if (permissions != other.permissions) return false
        if (value != null) {
            if (other.value == null) return false
            if (!value.contentEquals(other.value)) return false
        } else if (other.value != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = uuid.hashCode()
        result = 31 * result + properties
        result = 31 * result + permissions
        result = 31 * result + (value?.contentHashCode() ?: 0)
        return result
    }
}
