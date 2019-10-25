package dev.vespertine.treasurehunt

import android.app.Activity
import android.app.Application
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import dev.vespertine.treasurehunt.di.component.DaggerAppComponent
import javax.inject.Inject


class TreasureApp : Application(), HasActivityInjector {

    @Inject
    lateinit var activityInjector: DispatchingAndroidInjector<Activity>



    override fun onCreate() {
        super.onCreate()

        DaggerAppComponent.builder()
            .application(this)
            .baseUrl("https://lambda-treasure-hunt.herokuapp.com/")
            .build().inject(this)
    }

    override fun activityInjector(): AndroidInjector<Activity> = activityInjector



}