package com.example.photopicker.di

import android.app.Application
import androidx.navigation.ActivityNavigatorExtras
import com.example.photopicker.data.repository.InternalStorageManager_Impl
import com.example.photopicker.domain.repository.InternalStorageManagerRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
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
    fun provideInternalStorageManagerRepo(app: Application): InternalStorageManagerRepo {
        return InternalStorageManager_Impl(app)
    }
}