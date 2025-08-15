package com.android.ble_example.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.ble_example.domain.model.BLEDevice
import com.android.ble_example.domain.model.BLEService
import com.android.ble_example.domain.model.BLEState
import com.android.ble_example.domain.usecase.ConnectToDeviceUseCase
import com.android.ble_example.domain.usecase.DisconnectDeviceUseCase
import com.android.ble_example.domain.usecase.DiscoverServicesUseCase
import com.android.ble_example.domain.usecase.ScanForDevicesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BLEViewModel @Inject constructor(
    private val scanForDevicesUseCase: ScanForDevicesUseCase,
    private val connectToDeviceUseCase: ConnectToDeviceUseCase,
    private val disconnectDeviceUseCase: DisconnectDeviceUseCase,
    private val discoverServicesUseCase: DiscoverServicesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(BLEUIState())
    val uiState: StateFlow<BLEUIState> = _uiState.asStateFlow()

    init {
        observeBLEState()
        observeDiscoveredDevices()
        observeDiscoveredServices()
    }

    private fun observeBLEState() {
        viewModelScope.launch {
            // This would be connected to the repository state
            // For now, we'll manage it through the use cases
        }
    }

    private fun observeDiscoveredDevices() {
        viewModelScope.launch {
            scanForDevicesUseCase().collect { devices ->
                _uiState.value = _uiState.value.copy(
                    discoveredDevices = devices
                )
            }
        }
    }

    private fun observeDiscoveredServices() {
        viewModelScope.launch {
            discoverServicesUseCase().collect { services ->
                _uiState.value = _uiState.value.copy(
                    discoveredServices = services
                )
            }
        }
    }

    fun startScan() {
        _uiState.value = _uiState.value.copy(
            bleState = BLEState.Scanning
        )
        scanForDevicesUseCase.startScan()
    }

    fun stopScan() {
        _uiState.value = _uiState.value.copy(
            bleState = BLEState.Idle
        )
        scanForDevicesUseCase.stopScan()
    }

    fun connectToDevice(device: BLEDevice) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                bleState = BLEState.Connecting
            )
            
            connectToDeviceUseCase(device).onSuccess {
                _uiState.value = _uiState.value.copy(
                    bleState = BLEState.Connected,
                    connectedDevice = device
                )
            }.onFailure { exception ->
                _uiState.value = _uiState.value.copy(
                    bleState = BLEState.ErrorWithMessage(exception.message ?: "Connection failed")
                )
            }
        }
    }

    fun disconnectDevice() {
        viewModelScope.launch {
            disconnectDeviceUseCase().onSuccess {
                _uiState.value = _uiState.value.copy(
                    bleState = BLEState.Disconnected,
                    connectedDevice = null
                )
            }.onFailure { exception ->
                _uiState.value = _uiState.value.copy(
                    bleState = BLEState.ErrorWithMessage(exception.message ?: "Disconnection failed")
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(
            bleState = BLEState.Idle
        )
    }
}

data class BLEUIState(
    val bleState: BLEState = BLEState.Idle,
    val discoveredDevices: List<BLEDevice> = emptyList(),
    val discoveredServices: List<BLEService> = emptyList(),
    val connectedDevice: BLEDevice? = null
)
