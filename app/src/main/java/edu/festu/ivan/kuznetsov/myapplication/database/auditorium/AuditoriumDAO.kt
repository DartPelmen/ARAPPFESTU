package edu.festu.ivan.kuznetsov.myapplication.database.auditorium

import androidx.room.*
import edu.festu.ivan.kuznetsov.myapplication.database.teacher.Teacher

@Dao
interface AuditoriumDAO {
    @Insert
    fun add(vararg auditorium: Auditorium)
    @Update
    fun update(auditorium: Auditorium)
    @Delete
    fun delete(auditorium: Auditorium)
    @Query("SELECT * FROM AUDITORIUM")
    fun getAll(): MutableList<Auditorium>
    @Query("SELECT * FROM AUDITORIUM WHERE id_auditorium = :idAuditorium")
    fun getById(idAuditorium: String): Auditorium?
    @Query("SELECT * FROM AUDITORIUM WHERE name = :name")
    fun getByName(name:String): Auditorium?
    @Query("SELECT * FROM AUDITORIUM WHERE description like '%'||:patronymic||'%'")
    fun getAllByPatronymic(patronymic:String): MutableList<Auditorium>
    @Query("SELECT * FROM TEACHER WHERE id_teacher = :teacher_id")
    fun getAllByLead(teacher_id:String): MutableList<Teacher>
}