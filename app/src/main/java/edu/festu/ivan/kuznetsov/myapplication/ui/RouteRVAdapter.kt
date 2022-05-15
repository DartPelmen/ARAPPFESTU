package edu.festu.ivan.kuznetsov.myapplication.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.festu.ivan.kuznetsov.myapplication.databinding.RouteItemBinding
import edu.festu.ivan.kuznetsov.myapplication.util.RouteDiffUtils

class RouteRVAdapter: RecyclerView.Adapter<RouteRVAdapter.ViewHolder>() {
    private var routes: MutableList<String> = mutableListOf()

    init {
        routes = mutableListOf()
    }

    fun getRoutes():MutableList<String> = routes

    inner class ViewHolder(val binding: RouteItemBinding) : RecyclerView.ViewHolder(binding.root){
        override fun toString(): String {
            return super.toString() + " '" + binding.titleRoute + "'"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            RouteItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.titleRoute.text = routes[position]
    }

    override fun getItemCount(): Int = routes.size

    fun setRoutes(facts: MutableList<String>) {
        this.routes =facts
    }
}