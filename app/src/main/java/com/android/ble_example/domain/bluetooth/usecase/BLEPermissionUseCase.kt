package com.android.ble_example.domain.bluetooth.usecase

import com.android.ble_example.domain.bluetooth.repository.BLEPermissionRepository
import javax.inject.Inject

class BLEPermissionUseCase @Inject constructor(
    private val permissionRepository: BLEPermissionRepository
) {
    fun hasRequiredPermissions(): Boolean = permissionRepository.hasRequiredPermissions()
    
    fun getRequiredPermissions(): Array<String> = permissionRepository.getRequiredPermissions()
    
    fun shouldShowPermissionRationale(): Boolean = permissionRepository.shouldShowPermissionRationale()
    
    fun openAppSettings() = permissionRepository.openAppSettings()
}
