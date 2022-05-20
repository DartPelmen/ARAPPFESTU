package edu.festu.ivan.kuznetsov.myapplication.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import edu.festu.ivan.kuznetsov.myapplication.database.MyDatabase
import edu.festu.ivan.kuznetsov.myapplication.database.lesson.Lesson
import edu.festu.ivan.kuznetsov.myapplication.ui.TimetableFragment
import java.lang.ref.WeakReference
import java.util.concurrent.Executors

class LessonModel : ViewModel() {
    companion object {
        private val TAG = LessonModel::class.java.simpleName
    }
    lateinit var context: WeakReference<Context>
    private val routeList = MutableLiveData<MutableList<Lesson>>()
    init {
        routeList.value = mutableListOf()
    }
    fun getRoutes(): LiveData<MutableList<Lesson>> = routeList

    val errorMessage = MutableLiveData<String>()
    fun getRoutesFromDB() {
        context.get()?.let {
            Log.d(TAG, "getting lessons from db")
            Executors.newSingleThreadExecutor().execute {
                routeList.value?.clear()
                routeList.value?.addAll(MyDatabase.getInstance(it).getLessonDAO().getAll())
                routeList.postValue(routeList.value)
            }
        }
    }
    fun addRoute(route: Lesson) {
        Log.d(TAG, "ADD ROUTE")
        context.get()?.let {
            Log.d(TAG, "ADD ROUTE TRUE")
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