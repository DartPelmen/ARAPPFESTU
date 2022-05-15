package edu.festu.ivan.kuznetsov.myapplication.database.route

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.RESTRICT
import androidx.room.PrimaryKey
import edu.festu.ivan.kuznetsov.myapplication.database.auditorium.Auditorium
import java.util.*

@Entity(foreignKeys = [ForeignKey(entity = Auditorium::class, parentColumns = ["id_auditorium"], childColumns = ["id_auditorium_start"], onDelete = RESTRICT, onUpdate = RESTRICT),
    ForeignKey(entity = Auditorium::class, parentColumns = ["id_auditorium"], childColumns = ["id_auditorium_end"], onDelete = RESTRICT, onUpdate = RESTRICT)])
class Route(

    @ColumnInfo(name = "title")
    var title: String,
    @ColumnInfo(name = "id_auditorium_start")
    var idAuditoriumStart: String,
    @ColumnInfo(name = "id_auditorium_end")
    var idAuditoriumEnd: String)
{
    @PrimaryKey
    @ColumnInfo(name = "id_route")
    var idRoute: String= UUID.randomUUID().toString()
    override fun equals(other: Any?): Boolean {

        return this.idRoute==(other as Route).idRoute &&
                this.idAuditoriumEnd==other.idAuditoriumEnd &&
                this.idAuditoriumStart==other.idAuditoriumStart &&
                this.title == other.title
    }

    override fun toString(): String {
        return title
    }
}

