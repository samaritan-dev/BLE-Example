package com.android.ble_example.data.bluetooth.scanner

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import com.android.ble_example.domain.bluetooth.entity.BLEScanResult
import com.android.ble_example.domain.bluetooth.repository.BLEScannerRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BLEScannerRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : BLEScannerRepository {

    private val bluetoothManager: BluetoothManager by lazy {
        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    }
    
    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        bluetoothManager.adapter
    }
    
    private val bluetoothLeScanner: BluetoothLeScanner? by lazy {
        bluetoothAdapter?.bluetoothLeScanner
    }
    
    private var isScanning = false
    private val _scanResults = MutableStateFlow<List<BLEScanResult>>(emptyList())
    
    private val scanCallback = object : ScanCallback() {
        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            
            val scanResult = BLEScanResult(
                device = result.device,
                name = result.device.name,
                address = result.device.address,
                rssi = result.rssi
            )
            
            val currentResults = _scanResults.value.toMutableList()
            val existingIndex = currentResults.indexOfFirst { it.address == scanResult.address }
            
            if (existingIndex != -1) {
                currentResults[existingIndex] = scanResult
            } else {
                currentResults.add(scanResult)
            }
            
            _scanResults.value = currentResults
        }
        
        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            isScanning = false
        }
    }

    override fun getScanResults(): StateFlow<List<BLEScanResult>> = _scanResults.asStateFlow()

    @SuppressLint("MissingPermission")
    override fun startScan() {
        if (isScanning) return
        
        try {
            val scanSettings = ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build()
            
            val scanFilters = listOf<ScanFilter>()
            
            bluetoothLeScanner?.startScan(scanFilters, scanSettings, scanCallback)
            isScanning = true
            _scanResults.value = emptyList()
        } catch (e: Exception) {
            isScanning = false
        }
    }

    @SuppressLint("MissingPermission")
    override fun stopScan() {
        if (!isScanning) return
        
        try {
            bluetoothLeScanner?.stopScan(scanCallback)
            isScanning = false
        } catch (e: Exception) {
            isScanning = false
        }
    }

    override fun isScanning(): Boolean = isScanning
}
