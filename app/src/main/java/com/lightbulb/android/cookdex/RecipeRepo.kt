package com.lightbulb.android.cookdex

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.lightbulb.android.cookdex.persistence.RecipeDatabase
import java.util.*
import java.util.concurrent.Executors

class RecipeRepo private constructor(context: Context){

    //Build/retrieve recipe database
    private val recipeDatabase : RecipeDatabase = Room.databaseBuilder(context.applicationContext, RecipeDatabase::class.java, "cookDexDatabase")
        .build()

    private val dao = recipeDatabase.recipeDao()
    private val thread = Executors.newSingleThreadExecutor()

    fun getRecipes(): LiveData<List<Recipe>> = dao.getRecipes()
    fun getRecipe(id: UUID): LiveData<Recipe?> = dao.getRecipe(id)

    //commits any recipe changes to the database on a separate thread
    fun updateRecipe(recipe: Recipe){
        thread.execute{
            dao.updateRecipe(recipe)
        }
    }

    //adds a given recipe to the database on a separate thread
    fun addRecipe(recipe: Recipe){
        thread.execute{
            dao.addRecipe(recipe)
        }
    }

    //deletes a given recipe from the database
    fun deleteRecipe(recipe: Recipe){
        thread.execute{
            dao.deleteRecipe(recipe)
        }
    }

    //used to instantiate or retrieve a singleton of this class
    companion object{
        private var repoObject: RecipeRepo? = null

        fun build(context: Context){
            if (repoObject == null){
                repoObject = RecipeRepo(context)
            }
        }

        fun fetch(): RecipeRepo{
            return repoObject ?: throw Exception("The repo has not been built yet")
        }
    }
}