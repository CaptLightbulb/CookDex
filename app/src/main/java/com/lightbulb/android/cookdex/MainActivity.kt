package com.lightbulb.android.cookdex

import android.location.Criteria
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class MainActivity : AppCompatActivity(),  RecipeListFragment.Callbacks {

    //This will initialize the UI to display the fragment which shows the master list of recipes
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)

        if(currentFragment == null){
            val fragment = RecipeListFragment.newInstance()
            supportFragmentManager.beginTransaction().add(R.id.fragment_container, fragment).commit()
        }
    }

    //This will switch the list fragment over to a fragment showing details about the given recipe
    override fun onRecipeSelected(recipeId: UUID){
        val fragment = RecipeFragment.newInstance(recipeId)
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment)
            .addToBackStack(null).commit()
    }

}