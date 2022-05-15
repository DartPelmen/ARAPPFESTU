package edu.festu.ivan.kuznetsov.myapplication.database.teacher

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TeacherDAO {
    @Insert
    fun add(vararg student: Teacher)
    @Update
    fun update(student: Teacher)
    @Delete
    fun delete(student: Teacher)
    @Query("SELECT * FROM TEACHER")
    fun getAll(): MutableList<Teacher>
    @Query("SELECT * FROM Teacher WHERE id_teacher = :idTeacher")
    fun getById(idTeacher: String): Teacher?
    @Query("SELECT * FROM TEACHER WHERE first_name = :firstName")
    fun getAllByFirstName(firstName:String): MutableList<Teacher>
    @Query("SELECT * FROM TEACHER WHERE patronymic = :patronymic")
    fun getAllByPatronymic(patronymic:String): MutableList<Teacher>
    @Query("SELECT * FROM TEACHER WHERE last_name = :lastName")
    fun getAllByLastName(lastName:String): MutableList<Teacher>
}