package com.android.ble_example.domain.usecase

import com.android.ble_example.domain.model.BLEService
import com.android.ble_example.domain.repository.BLERepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DiscoverServicesUseCase @Inject constructor(
    private val bleRepository: BLERepository
) {
    operator fun invoke(): Flow<List<BLEService>> = bleRepository.discoverServices()
}
