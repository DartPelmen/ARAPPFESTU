package edu.festu.ivan.kuznetsov.myapplication.database.event

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Event(

    @ColumnInfo(name = "title")
    var title: String,
    @ColumnInfo(name = "description")
    var description: String){
    @ColumnInfo(name = "id_event")
    @PrimaryKey
    var id: String= UUID.randomUUID().toString()
}
