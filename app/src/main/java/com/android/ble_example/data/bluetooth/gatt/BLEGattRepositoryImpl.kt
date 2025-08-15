package com.android.ble_example.data.bluetooth.gatt

import android.annotation.SuppressLint
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import com.android.ble_example.domain.bluetooth.entity.BLEGattCharacteristic
import com.android.ble_example.domain.bluetooth.entity.BLEGattService
import com.android.ble_example.domain.bluetooth.repository.BLEGattRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BLEGattRepositoryImpl @Inject constructor() : BLEGattRepository {

    private var bluetoothGatt: BluetoothGatt? = null
    private val _discoveredServices = MutableStateFlow<List<BLEGattService>>(emptyList())
    
    fun setBluetoothGatt(gatt: BluetoothGatt?) {
        bluetoothGatt = gatt
    }

    override fun getDiscoveredServices(): StateFlow<List<BLEGattService>> = _discoveredServices.asStateFlow()

    @SuppressLint("MissingPermission")
    override suspend fun discoverServices(): Result<Unit> {
        return try {
            bluetoothGatt?.discoverServices()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    @SuppressLint("MissingPermission")
    override suspend fun readCharacteristic(serviceUuid: String, characteristicUuid: String): Result<ByteArray?> {
        return try {
            val service = bluetoothGatt?.getService(UUID.fromString(serviceUuid))
            val characteristic = service?.getCharacteristic(UUID.fromString(characteristicUuid))
            
            if (characteristic != null) {
                bluetoothGatt?.readCharacteristic(characteristic)
                Result.success(characteristic.value)
            } else {
                Result.failure(Exception("Characteristic not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    @SuppressLint("MissingPermission")
    override suspend fun writeCharacteristic(serviceUuid: String, characteristicUuid: String, value: ByteArray): Result<Unit> {
        return try {
            val service = bluetoothGatt?.getService(UUID.fromString(serviceUuid))
            val characteristic = service?.getCharacteristic(UUID.fromString(characteristicUuid))
            
            if (characteristic != null) {
                characteristic.value = value
                bluetoothGatt?.writeCharacteristic(characteristic)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Characteristic not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
        if (status == BluetoothGatt.GATT_SUCCESS) {
            val services = gatt.services.map { service ->
                BLEGattService(
                    service = service,
                    uuid = service.uuid,
                    type = service.type,
                    characteristics = service.characteristics.map { characteristic ->
                        BLEGattCharacteristic(
                            characteristic = characteristic,
                            uuid = characteristic.uuid,
                            properties = characteristic.properties,
                            permissions = characteristic.permissions
                        )
                    }
                )
            }
            _discoveredServices.value = services
        }
    }
}
