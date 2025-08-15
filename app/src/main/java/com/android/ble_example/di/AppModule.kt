package com.android.ble_example.di

import com.android.ble_example.domain.repository.BLERepository
import com.android.ble_example.data.repository.BLERepositoryImpl
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
    abstract fun bindBLERepository(
        bleRepositoryImpl: BLERepositoryImpl
    ): BLERepository
}
