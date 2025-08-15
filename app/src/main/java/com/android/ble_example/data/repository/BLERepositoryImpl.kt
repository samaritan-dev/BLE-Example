package com.android.ble_example.data.repository

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import com.android.ble_example.domain.model.BLECharacteristic
import com.android.ble_example.domain.model.BLEDevice
import com.android.ble_example.domain.model.BLEService
import com.android.ble_example.domain.model.BLEState
import com.android.ble_example.domain.repository.BLERepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BLERepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : BLERepository {

    private val bluetoothManager: BluetoothManager by lazy {
        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    }
    
    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        bluetoothManager.adapter
    }
    
    private val bluetoothLeScanner: BluetoothLeScanner? by lazy {
        bluetoothAdapter?.bluetoothLeScanner
    }
    
    private var bluetoothGatt: BluetoothGatt? = null
    private var isScanning = false
    
    private val _bleState = MutableStateFlow<BLEState>(BLEState.Idle)
    private val _discoveredDevices = MutableStateFlow<List<BLEDevice>>(emptyList())
    private val _discoveredServices = MutableStateFlow<List<BLEService>>(emptyList())
    
    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            
            val device = BLEDevice(
                device = result.device,
                name = result.device.name,
                address = result.device.address,
                rssi = result.rssi
            )
            
            val currentDevices = _discoveredDevices.value.toMutableList()
            val existingIndex = currentDevices.indexOfFirst { it.address == device.address }
            
            if (existingIndex != -1) {
                currentDevices[existingIndex] = device
            } else {
                currentDevices.add(device)
            }
            
            _discoveredDevices.value = currentDevices
        }
        
        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            _bleState.value = BLEState.ErrorWithMessage("Scan failed with error code: $errorCode")
        }
    }
    
    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            
            when (newState) {
                BluetoothGatt.STATE_CONNECTED -> {
                    _bleState.value = BLEState.Connected
                    gatt.discoverServices()
                }
                BluetoothGatt.STATE_DISCONNECTED -> {
                    _bleState.value = BLEState.Disconnected
                    bluetoothGatt = null
                }
            }
        }
        
        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            super.onServicesDiscovered(gatt, status)
            
            if (status == BluetoothGatt.GATT_SUCCESS) {
                val services = gatt.services.map { service ->
                    BLEService(
                        service = service,
                        uuid = service.uuid,
                        type = service.type,
                        characteristics = service.characteristics.map { characteristic ->
                            BLECharacteristic(
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

    override fun getBLEState(): StateFlow<BLEState> = _bleState.asStateFlow()

    override fun scanForDevices(): StateFlow<List<BLEDevice>> = _discoveredDevices.asStateFlow()

    override fun startScan() {
        if (isScanning) return
        
        if (!isBluetoothEnabled()) {
            _bleState.value = BLEState.ErrorWithMessage("Bluetooth is not enabled")
            return
        }
        
        if (!hasRequiredPermissions()) {
            _bleState.value = BLEState.ErrorWithMessage("Required permissions not granted")
            return
        }
        
        try {
            val scanSettings = ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build()
            
            val scanFilters = listOf<ScanFilter>()
            
            bluetoothLeScanner?.startScan(scanFilters, scanSettings, scanCallback)
            isScanning = true
            _bleState.value = BLEState.Scanning
            _discoveredDevices.value = emptyList()
        } catch (e: Exception) {
            _bleState.value = BLEState.ErrorWithMessage("Failed to start scan: ${e.message}")
        }
    }

    override fun stopScan() {
        if (!isScanning) return
        
        try {
            bluetoothLeScanner?.stopScan(scanCallback)
            isScanning = false
            _bleState.value = BLEState.Idle
        } catch (e: Exception) {
            _bleState.value = BLEState.ErrorWithMessage("Failed to stop scan: ${e.message}")
        }
    }

    override suspend fun connectToDevice(device: BLEDevice): Result<Unit> {
        return try {
            _bleState.value = BLEState.Connecting
            
            bluetoothGatt = device.device.connectGatt(context, false, gattCallback)
            
            if (bluetoothGatt?.connect() == true) {
                Result.success(Unit)
            } else {
                _bleState.value = BLEState.ErrorWithMessage("Failed to initiate connection")
                Result.failure(Exception("Failed to initiate connection"))
            }
        } catch (e: Exception) {
            _bleState.value = BLEState.ErrorWithMessage("Connection failed: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun disconnectDevice(): Result<Unit> {
        return try {
            bluetoothGatt?.disconnect()
            bluetoothGatt = null
            _bleState.value = BLEState.Disconnected
            Result.success(Unit)
        } catch (e: Exception) {
            _bleState.value = BLEState.ErrorWithMessage("Disconnection failed: ${e.message}")
            Result.failure(e)
        }
    }

    override fun discoverServices(): StateFlow<List<BLEService>> = _discoveredServices.asStateFlow()

    override fun isBluetoothEnabled(): Boolean {
        return bluetoothAdapter?.isEnabled == true
    }

    override fun requestBluetoothPermissions() {
        // This will be handled by the UI layer
    }
    
    private fun hasRequiredPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
        } else {
            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        }
    }
}
