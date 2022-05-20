package edu.festu.ivan.kuznetsov.myapplication.database.lesson

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.util.*

@Entity
data class Lesson(

    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "date_time_start")
    val dateTime: LocalDateTime,
    @ColumnInfo(name = "date_time_end")
    val dateTimeEnd: LocalDateTime,
    @ColumnInfo(name = "id_auditorium")
    val idAuditorium: String,
    @ColumnInfo(name = "id_teacher")
    val idTeacher: String){
    @PrimaryKey
    @ColumnInfo(name = "id_lesson")
    var id: String = UUID.randomUUID().toString()
    override fun toString(): String {
        return "$title $dateTime $dateTimeEnd"
    }
}