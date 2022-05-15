package edu.festu.ivan.kuznetsov.myapplication.database.lesson

import androidx.room.*
import edu.festu.ivan.kuznetsov.myapplication.database.auditorium.Auditorium
import edu.festu.ivan.kuznetsov.myapplication.database.teacher.Teacher
import java.time.LocalDateTime
@Dao
interface LessonDAO {
    @Insert
    fun add(vararg lesson: Lesson)
    @Update
    fun update(lesson: Lesson)
    @Delete
    fun delete(lesson: Lesson)
    @Query("SELECT * FROM LESSON")
    fun getAll(): MutableList<Lesson>
    @Query("SELECT * FROM Lesson WHERE id_lesson = :idLesson")
    fun getById(idLesson: String): Lesson?

    @Query("SELECT * FROM Lesson WHERE id_teacher = :idTeacher")
    fun getAllByTeacherId(idTeacher: String): MutableList<Lesson>?
    @Query("SELECT * FROM TEACHER WHERE id_teacher = :idTeacher")
    fun getByFirstName(idTeacher:String): Teacher?
    @Query("SELECT * FROM AUDITORIUM WHERE id_auditorium = :idAuditorium")
    fun getByAuditoriumId(idAuditorium:String):Auditorium?
    @Query("SELECT * FROM LESSON WHERE title like '%'||:lastName||'%'")
    fun getAllByTitleLike(lastName:String): MutableList<Lesson>

    @Query("SELECT * FROM LESSON WHERE date_time_start = :dateTimeStart")
    fun getByDateTimeStartLike(dateTimeStart:LocalDateTime): Lesson?
}