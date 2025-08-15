package com.android.ble_example.domain.usecase

import com.android.ble_example.domain.model.BLEDevice
import com.android.ble_example.domain.repository.BLERepository
import javax.inject.Inject

class ConnectToDeviceUseCase @Inject constructor(
    private val bleRepository: BLERepository
) {
    suspend operator fun invoke(device: BLEDevice): Result<Unit> {
        return bleRepository.connectToDevice(device)
    }
}
