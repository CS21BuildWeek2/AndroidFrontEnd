package dev.vespertine.treasurehunt.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import dev.vespertine.treasurehunt.R
import io.reactivex.disposables.CompositeDisposable

class MainActivity : AppCompatActivity() {

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
