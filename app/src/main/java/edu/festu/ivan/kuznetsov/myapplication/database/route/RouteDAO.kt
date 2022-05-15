package edu.festu.ivan.kuznetsov.myapplication.database.route

import androidx.room.*

@Dao
interface RouteDAO {
    @Insert
    fun add(vararg route: Route)
    @Update
    fun update(route: Route)
    @Delete
    fun delete(route: Route)
    @Query("SELECT * FROM ROUTE")
    fun getAll(): MutableList<Route>
    @Query("SELECT * FROM ROUTE WHERE id_route = :idRoute")
    fun getById(idRoute: String): Route?
    @Query("SELECT * FROM ROUTE WHERE id_auditorium_end = :idAuditoriumEnd")
    fun getAllByEndAuditory(idAuditoriumEnd:String): MutableList<Route>
    @Query("SELECT * FROM ROUTE WHERE id_auditorium_start = :idAuditoriumStart")
    fun getAllByStartAuditory(idAuditoriumStart:String): MutableList<Route>

}