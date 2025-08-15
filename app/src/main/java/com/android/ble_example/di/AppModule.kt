package com.android.ble_example.di

import com.android.ble_example.domain.bluetooth.repository.BLEConnectionRepository
import com.android.ble_example.domain.bluetooth.repository.BLEGattRepository
import com.android.ble_example.domain.bluetooth.repository.BLEPermissionRepository
import com.android.ble_example.domain.bluetooth.repository.BLEScannerRepository
import com.android.ble_example.data.bluetooth.connection.BLEConnectionRepositoryImpl
import com.android.ble_example.data.bluetooth.gatt.BLEGattRepositoryImpl
import com.android.ble_example.data.bluetooth.permission.BLEPermissionRepositoryImpl
import com.android.ble_example.data.bluetooth.scanner.BLEScannerRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {
    
    @Binds
    @Singleton
    abstract fun bindBLEScannerRepository(
        bleScannerRepositoryImpl: BLEScannerRepositoryImpl
    ): BLEScannerRepository
    
    @Binds
    @Singleton
    abstract fun bindBLEConnectionRepository(
        bleConnectionRepositoryImpl: BLEConnectionRepositoryImpl
    ): BLEConnectionRepository
    
    @Binds
    @Singleton
    abstract fun bindBLEGattRepository(
        bleGattRepositoryImpl: BLEGattRepositoryImpl
    ): BLEGattRepository
    
    @Binds
    @Singleton
    abstract fun bindBLEPermissionRepository(
        blePermissionRepositoryImpl: BLEPermissionRepositoryImpl
    ): BLEPermissionRepository
}
