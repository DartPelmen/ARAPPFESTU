package edu.festu.ivan.kuznetsov.myapplication.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import edu.festu.ivan.kuznetsov.myapplication.database.MyDatabase
import edu.festu.ivan.kuznetsov.myapplication.database.lesson.Lesson
import java.lang.ref.WeakReference
import java.util.concurrent.Executors

class LessonModel : ViewModel() {
    lateinit var context: WeakReference<Context>
    private val routeList = MutableLiveData<MutableList<Lesson>>()
    init {
        routeList.value = mutableListOf()
    }
    fun getRoutes(): LiveData<MutableList<Lesson>> = routeList

    val errorMessage = MutableLiveData<String>()
    fun getRoutesFromDB() {
        context.get()?.let {

            Executors.newSingleThreadExecutor().execute {
                routeList.value?.clear()
                routeList.value?.addAll(MyDatabase.getInstance(it).getLessonDAO().getAll())
                routeList.postValue(routeList.value)
            }


        }
    }
    fun addRoute(route: Lesson) {
        context.get()?.let {
            Executors.newSingleThreadExecutor().execute{
                MyDatabase.getInstance(it).getLessonDAO().add(route)
                routeList.value?.add(route)
                routeList.postValue(routeList.value)
            }
        }
    }
    fun remove(position: Int){
        context.get()?.let {ctx->
            Executors.newSingleThreadExecutor().execute{
                routeList.value?.let {
                    MyDatabase.getInstance(ctx).getLessonDAO().delete(it[position])
                    routeList.value?.removeAt(position)
                    routeList.postValue(routeList.value)
                }
            }
        }
    }
}