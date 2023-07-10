package com.lightbulb.android.cookdex.persistence

import android.net.Uri
import androidx.core.net.toUri
import androidx.room.TypeConverter
import java.util.*

class TypeConversions {

    @TypeConverter
    fun toUUID(uuid: String?): UUID?{
        return UUID.fromString(uuid)
    }

    @TypeConverter
    fun fromUUID(uuid: UUID?): String?{
        return uuid?.toString()
    }
}