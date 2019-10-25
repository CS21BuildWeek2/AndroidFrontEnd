package dev.vespertine.treasurehunt.di.module

import dagger.Module
import dagger.android.ContributesAndroidInjector
import dev.vespertine.treasurehunt.activity.MainActivity

@Module
abstract class BuildersModule {

    @ContributesAndroidInjector
    internal abstract fun contributeMainActivity(): MainActivity

}