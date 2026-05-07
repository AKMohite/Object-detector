package app.mak.objectdetector.di

import app.mak.objectdetector.core.ml.ImageDetector
import app.mak.objectdetector.core.ml.MLImageDetector
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
internal abstract class MLModule {

    @Binds
    abstract fun bindObjectDetector(detector: MLImageDetector): ImageDetector
}