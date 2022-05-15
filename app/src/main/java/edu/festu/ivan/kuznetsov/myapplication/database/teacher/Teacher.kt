package edu.festu.ivan.kuznetsov.myapplication.database.teacher

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Teacher(

    @ColumnInfo(name = "first_name")
    var firstName: String,
    @ColumnInfo(name = "patronymic")
    var patronymic: String,
    @ColumnInfo(name = "last_name")
    var lastName: String
){
    @PrimaryKey
    @ColumnInfo(name = "id_teacher")
    var idTeacher: String= UUID.randomUUID().toString()
    override fun toString(): String {
        return "$lastName $firstName $patronymic"
    }
}
