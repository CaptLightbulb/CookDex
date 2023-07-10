package com.lightbulb.android.cookdex.persistence

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.Dao
import com.lightbulb.android.cookdex.Recipe
import java.util.*

@Dao
interface RecipeDao {

    @Query("SELECT * FROM recipe")
    fun getRecipes(): LiveData<List<Recipe>>

    @Query("SELECT * FROM recipe WHERE id=(:id)")
    fun getRecipe(id: UUID): LiveData<Recipe?>

    @Update
    fun updateRecipe(recipe: Recipe)

    @Insert
    fun addRecipe(recipe: Recipe)

    @Delete
    fun deleteRecipe(recipe: Recipe)
}