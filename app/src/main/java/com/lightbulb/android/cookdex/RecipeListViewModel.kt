package com.lightbulb.android.cookdex

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RecipeListViewModel : ViewModel() {

    private val repo = RecipeRepo.fetch()
    var recipes = repo.getRecipes()

    fun add(recipe: Recipe){
        repo.addRecipe(recipe)
    }
}