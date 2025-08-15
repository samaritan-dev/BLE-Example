package com.android.ble_example.presentation.bluetooth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.ble_example.domain.bluetooth.entity.BLEScanResult
import com.android.ble_example.domain.bluetooth.usecase.BLEScanUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BLEScannerViewModel @Inject constructor(
    private val scanUseCase: BLEScanUseCase
) : ViewModel() {

    private val _scanResults = MutableStateFlow<List<BLEScanResult>>(emptyList())
    val scanResults: StateFlow<List<BLEScanResult>> = _scanResults.asStateFlow()
    
    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    init {
        observeScanResults()
    }

    private fun observeScanResults() {
        viewModelScope.launch {
            scanUseCase().collect { results ->
                _scanResults.value = results
            }
        }
    }

    fun startScan() {
        scanUseCase.startScan()
        _isScanning.value = true
    }

    fun stopScan() {
        scanUseCase.stopScan()
        _isScanning.value = false
    }

    fun clearScanResults() {
        _scanResults.value = emptyList()
    }
}
