package com.lightbulb.android.cookdex

import android.app.Application

class CookDexApplication: Application() {

    //This class is just needed to initialize a singleton instance of my database repository class
    override fun onCreate() {
        super.onCreate()
        RecipeRepo.build(this)
    }
}