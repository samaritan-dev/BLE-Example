package com.android.ble_example.data.bluetooth.connection

import android.annotation.SuppressLint
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.content.Context
import com.android.ble_example.domain.bluetooth.entity.BLEConnectionState
import com.android.ble_example.domain.bluetooth.entity.BLEScanResult
import com.android.ble_example.domain.bluetooth.repository.BLEConnectionRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BLEConnectionRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : BLEConnectionRepository {

    private var bluetoothGatt: BluetoothGatt? = null
    private val _connectionState = MutableStateFlow<BLEConnectionState>(BLEConnectionState.Idle)
    
    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            
            when (newState) {
                BluetoothGatt.STATE_CONNECTED -> {
                    _connectionState.value = BLEConnectionState.Connected
                }
                BluetoothGatt.STATE_DISCONNECTED -> {
                    _connectionState.value = BLEConnectionState.Disconnected
                    bluetoothGatt = null
                }
            }
        }
    }

    override fun getConnectionState(): StateFlow<BLEConnectionState> = _connectionState.asStateFlow()

    @SuppressLint("MissingPermission")
    override suspend fun connectToDevice(device: BLEScanResult): Result<Unit> {
        return try {
            _connectionState.value = BLEConnectionState.Connecting
            
            bluetoothGatt = device.device.connectGatt(context, false, gattCallback)
            
            if (bluetoothGatt?.connect() == true) {
                Result.success(Unit)
            } else {
                _connectionState.value = BLEConnectionState.ErrorWithMessage("Failed to initiate connection")
                Result.failure(Exception("Failed to initiate connection"))
            }
        } catch (e: Exception) {
            _connectionState.value = BLEConnectionState.ErrorWithMessage("Connection failed: ${e.message}")
            Result.failure(e)
        }
    }

    @SuppressLint("MissingPermission")
    override suspend fun disconnectDevice(): Result<Unit> {
        return try {
            bluetoothGatt?.disconnect()
            bluetoothGatt = null
            _connectionState.value = BLEConnectionState.Disconnected
            Result.success(Unit)
        } catch (e: Exception) {
            _connectionState.value = BLEConnectionState.ErrorWithMessage("Disconnection failed: ${e.message}")
            Result.failure(e)
        }
    }

    override fun isConnected(): Boolean {
        return _connectionState.value == BLEConnectionState.Connected
    }
}
