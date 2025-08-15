package com.android.ble_example.domain.bluetooth.repository

interface BLEPermissionRepository {
    fun hasRequiredPermissions(): Boolean
    fun getRequiredPermissions(): Array<String>
    fun shouldShowPermissionRationale(): Boolean
    fun openAppSettings()
}
