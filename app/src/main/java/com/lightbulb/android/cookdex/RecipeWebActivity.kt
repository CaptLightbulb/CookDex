package com.lightbulb.android.cookdex

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class RecipeWebActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_web)

        val fragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)

        //Creates a new RecipeWebFragment using the intent data to retrieve the Url for the webview
        if (fragment == null){
            val thisFragment = RecipeWebFragment.newInstance(intent.data!!)
            supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainer, thisFragment)
                .commit()
        }
    }

    //Used by other classes to retrieve an intent containing this activity class
    companion object{
        fun newIntent(context: Context, recipeWebUri: Uri) : Intent {
            return Intent(context, RecipeWebActivity::class.java).apply {
                data = recipeWebUri
            }
        }
    }
}