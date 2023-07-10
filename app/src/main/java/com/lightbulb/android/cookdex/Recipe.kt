package com.lightbulb.android.cookdex

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Recipe (@PrimaryKey val id: UUID = UUID.randomUUID(),
                   var title: String = "",
                   var url: String = "",
                   var imagePath: String = ""){
}

