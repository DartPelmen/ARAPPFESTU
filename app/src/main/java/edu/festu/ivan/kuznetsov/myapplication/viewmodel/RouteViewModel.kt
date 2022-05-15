package edu.festu.ivan.kuznetsov.myapplication.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import edu.festu.ivan.kuznetsov.myapplication.database.MyDatabase
import edu.festu.ivan.kuznetsov.myapplication.database.route.Route
import java.lang.ref.WeakReference
import java.util.concurrent.Executors

class RouteViewModel(): ViewModel() {
    lateinit var context: WeakReference<Context>
    private val routeList = MutableLiveData<MutableList<Route>>()
    init {
        routeList.value = mutableListOf()
    }
    fun getRoutes(): LiveData<MutableList<Route>> = routeList

    val errorMessage = MutableLiveData<String>()
    fun getRoutesFromDB() {
        context.get()?.let {

            Executors.newSingleThreadExecutor().execute {
                routeList.value?.clear()
                routeList.value?.addAll(MyDatabase.getInstance(it).getRouteDAO().getAll())
                routeList.postValue(routeList.value)
            }


        }
    }
    fun addRoute(route: Route) {
        context.get()?.let {
            Executors.newSingleThreadExecutor().execute{
                MyDatabase.getInstance(it).getRouteDAO().add(route)
                routeList.value?.add(route)
                routeList.postValue(routeList.value)
            }
        }
    }
    fun remove(position: Int){
        context.get()?.let {ctx->
            Executors.newSingleThreadExecutor().execute{
                routeList.value?.let {
                    MyDatabase.getInstance(ctx).getRouteDAO().delete(it[position])
                    routeList.value?.removeAt(position)
                    routeList.postValue(routeList.value)
                }
            }
        }
    }
}