package edu.festu.ivan.kuznetsov.myapplication.database.auditorium

import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import androidx.room.ForeignKey.RESTRICT
import edu.festu.ivan.kuznetsov.myapplication.database.teacher.Teacher
import java.util.*

@Entity(foreignKeys = [ForeignKey(entity = Teacher::class, parentColumns = ["id_teacher"], childColumns = ["auditorium_lead"], onUpdate = CASCADE, onDelete = RESTRICT)])
data class Auditorium(
    @ColumnInfo(name = "name")
    var name: String,
    @ColumnInfo(name = "description")
    var description: String,
    @ColumnInfo(name = "x_cord")
    var xCord: Float,
    @ColumnInfo(name = "y_cord")
    var yCord: Float,
    @ColumnInfo(name = "auditorium_lead")
    var leadId: String) {
    @PrimaryKey
    @ColumnInfo(name = "id_auditorium")
    var id: String=UUID.randomUUID().toString()

    override fun toString(): String {
        return name
    }
}
