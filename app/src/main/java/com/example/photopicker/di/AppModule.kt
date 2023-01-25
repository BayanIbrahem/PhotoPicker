package com.example.photopicker.di

import android.app.Application
import com.example.photopicker.data.repository.StorageManager_Impl
import com.example.photopicker.data.storage.shared_storage.SharedStorageManager
import com.example.photopicker.data.storage.private_storage.PrivateStorageManager
import com.example.photopicker.domain.repository.StorageManagerRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(
    SingletonComponent::class, //       `as long as the app stays every instance will not be destroyed`
//    ActivityComponent::class,         `as long as the activity lives`
//    ViewModelComponent::class,        `as long the view model lives`
//    ActivityRetainedComponent::class, `as long as the activity lives and it stay when recreating activity`
)
object AppModule {

    @Provides
    @Singleton // this singleton, means that we need only one instance from this repo,
               // and without this annotation every time this function will be called, a new Singleton
               // instance will be created
    fun providePrivateStorageManager(app: Application): PrivateStorageManager {
        return PrivateStorageManager(app)
    }

    @Provides
    @Singleton
    fun provideSharedStorageManager(app: Application): SharedStorageManager {
        return SharedStorageManager(app)
    }

    @Provides
    @Singleton
    fun provideStorageManager(
        privateStorageManager: PrivateStorageManager,
        sharedStorageManager: SharedStorageManager,
    ): StorageManagerRepo {
        return StorageManager_Impl(privateStorageManager, sharedStorageManager)
    }
}