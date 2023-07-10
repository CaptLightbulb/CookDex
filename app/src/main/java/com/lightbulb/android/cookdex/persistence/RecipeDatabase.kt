package com.lightbulb.android.cookdex.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.lightbulb.android.cookdex.Recipe


@Database(entities = [Recipe::class], version = 1)
@TypeConverters(TypeConversions::class)
abstract class RecipeDatabase: RoomDatabase(){
    abstract fun recipeDao(): RecipeDao
}