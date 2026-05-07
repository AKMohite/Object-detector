package app.mak.objectdetector.di

import app.mak.objectdetector.core.ml.MLObjectDetector
import app.mak.objectdetector.core.ml.ObjectDetector
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
internal abstract class MLModule {

    @Binds
    abstract fun bindObjectDetector(detector: MLObjectDetector): ObjectDetector
}