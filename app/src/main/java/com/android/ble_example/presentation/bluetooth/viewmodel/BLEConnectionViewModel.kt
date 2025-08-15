package com.android.ble_example.presentation.bluetooth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.ble_example.domain.bluetooth.entity.BLEConnectionState
import com.android.ble_example.domain.bluetooth.entity.BLEScanResult
import com.android.ble_example.domain.bluetooth.usecase.BLEConnectionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BLEConnectionViewModel @Inject constructor(
    private val connectionUseCase: BLEConnectionUseCase
) : ViewModel() {

    private val _connectionState = MutableStateFlow<BLEConnectionState>(BLEConnectionState.Idle)
    val connectionState: StateFlow<BLEConnectionState> = _connectionState.asStateFlow()
    
    private val _connectedDevice = MutableStateFlow<BLEScanResult?>(null)
    val connectedDevice: StateFlow<BLEScanResult?> = _connectedDevice.asStateFlow()

    init {
        observeConnectionState()
    }

    private fun observeConnectionState() {
        viewModelScope.launch {
            connectionUseCase.getConnectionState().collect { state ->
                _connectionState.value = state
            }
        }
    }

    suspend fun connectToDevice(device: BLEScanResult) {
        _connectedDevice.value = device
        connectionUseCase.connectToDevice(device)
    }

    suspend fun disconnectDevice() {
        connectionUseCase.disconnectDevice()
        _connectedDevice.value = null
    }

    fun isConnected(): Boolean = connectionUseCase.isConnected()
}
