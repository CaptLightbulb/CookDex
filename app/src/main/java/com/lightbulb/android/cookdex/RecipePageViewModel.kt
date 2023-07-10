package com.lightbulb.android.cookdex

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import java.util.*

class RecipePageViewModel: ViewModel() {

    private val repo = RecipeRepo.fetch()
    private val recipeIdData = MutableLiveData<UUID>()

    var recipeData: LiveData<Recipe> = Transformations.switchMap(recipeIdData){recipeId ->
        repo.getRecipe(recipeId)
    }

    fun loadRecipe(recipeId: UUID){
        recipeIdData.value = recipeId
    }

    fun saveRecipe(recipe: Recipe){
        repo.updateRecipe(recipe)
    }

    fun deleteRecipe(recipe: Recipe){
        repo.deleteRecipe(recipe)
    }
}