package com.android.ble_example.presentation.bluetooth.coordinator

import com.android.ble_example.domain.bluetooth.entity.BLEScanResult
import com.android.ble_example.domain.bluetooth.usecase.BLEConnectionUseCase
import com.android.ble_example.domain.bluetooth.usecase.BLEGattUseCase
import com.android.ble_example.domain.bluetooth.usecase.BLEPermissionUseCase
import com.android.ble_example.domain.bluetooth.usecase.BLEScanUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BLECoordinator @Inject constructor(
    private val scanUseCase: BLEScanUseCase,
    private val connectionUseCase: BLEConnectionUseCase,
    private val gattUseCase: BLEGattUseCase,
    private val permissionUseCase: BLEPermissionUseCase
) {
    
    // Scanner operations
    fun getScanResults(): Flow<List<BLEScanResult>> = scanUseCase()
    fun startScan() = scanUseCase.startScan()
    fun stopScan() = scanUseCase.stopScan()
    fun isScanning(): Boolean = scanUseCase.isScanning()
    
    // Connection operations
    fun getConnectionState() = connectionUseCase.getConnectionState()
    suspend fun connectToDevice(device: BLEScanResult) = connectionUseCase.connectToDevice(device)
    suspend fun disconnectDevice() = connectionUseCase.disconnectDevice()
    fun isConnected(): Boolean = connectionUseCase.isConnected()
    
    // GATT operations
    fun getDiscoveredServices() = gattUseCase.getDiscoveredServices()
    suspend fun discoverServices() = gattUseCase.discoverServices()
    suspend fun readCharacteristic(serviceUuid: String, characteristicUuid: String) = 
        gattUseCase.readCharacteristic(serviceUuid, characteristicUuid)
    suspend fun writeCharacteristic(serviceUuid: String, characteristicUuid: String, value: ByteArray) = 
        gattUseCase.writeCharacteristic(serviceUuid, characteristicUuid, value)
    
    // Permission operations
    fun hasRequiredPermissions(): Boolean = permissionUseCase.hasRequiredPermissions()
    fun getRequiredPermissions(): Array<String> = permissionUseCase.getRequiredPermissions()
    fun shouldShowPermissionRationale(): Boolean = permissionUseCase.shouldShowPermissionRationale()
    fun openAppSettings() = permissionUseCase.openAppSettings()
}
