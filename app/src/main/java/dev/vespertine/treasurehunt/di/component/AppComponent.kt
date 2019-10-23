package dev.vespertine.treasurehunt.di.component

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import dev.vespertine.treasurehunt.TreasureApp
import dev.vespertine.treasurehunt.di.module.AppModule
import dev.vespertine.treasurehunt.di.module.BuildersModule
import dev.vespertine.treasurehunt.di.module.NetworkModule
import javax.inject.Singleton

@Singleton
@Component(modules = [(AndroidSupportInjectionModule::class),

    (BuildersModule::class),
    (NetworkModule::class)])
interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application) : Builder
        @BindsInstance
        fun baseUrl(url: String) : Builder
        fun build(): AppComponent
    }

    fun inject(instance: TreasureApp)
}