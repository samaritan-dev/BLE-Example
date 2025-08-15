package com.android.ble_example.domain.usecase

import com.android.ble_example.domain.repository.BLERepository
import javax.inject.Inject

class DisconnectDeviceUseCase @Inject constructor(
    private val bleRepository: BLERepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return bleRepository.disconnectDevice()
    }
}
