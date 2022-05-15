package edu.festu.ivan.kuznetsov.myapplication.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import edu.festu.ivan.kuznetsov.myapplication.database.route.Route
import edu.festu.ivan.kuznetsov.myapplication.databinding.RouteItemBinding
import edu.festu.ivan.kuznetsov.myapplication.util.RouteDiffUtils


class RouteListAdapter(private var onItemClickListener: OnItemClickListener): RecyclerView.Adapter<RouteListAdapter.ViewHolder>() {
    private var routes: MutableList<Route> = mutableListOf()

   init {
       routes = mutableListOf()
   }
    fun getRoutes():MutableList<Route> = routes

        inner class ViewHolder(val binding: RouteItemBinding) : RecyclerView.ViewHolder(binding.root){
        override fun toString(): String {
            return super.toString() + " '" + binding.titleRoute + "'"
        }




        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val holder =  ViewHolder(
            RouteItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
        holder.binding.root.setOnClickListener {
            val position = holder.adapterPosition
            // если холдер соответсвует какой-либо позиции в адаптере
            // если холдер соответсвует какой-либо позиции в адаптере
            if (position != RecyclerView.NO_POSITION) {
                // уведомляем слушателя о нажатии
                fireItemClicked(position, routes[position])
            } }
        return holder
    }
    private fun fireItemClicked(position: Int, item: Route) {
        if (onItemClickListener != null) {
            onItemClickListener.onItemClicked(position, item)
        }
    }

    // суда подписываемся активитей, фрагментом, перезнтером или чем нибудь еще
    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }

    // реализуем подписчиком
    interface OnItemClickListener {
        fun onItemClicked(position: Int, item: Route)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.titleRoute.text = routes[position].title
    }

    override fun getItemCount(): Int = routes.size

    fun setRoutes(facts: MutableList<Route>) {
        val diffCallback = RouteDiffUtils(routes, facts)
        val diffCourses = DiffUtil.calculateDiff(diffCallback)
        this.routes.clear()
        this.routes.addAll(facts)


        diffCourses.dispatchUpdatesTo(this)

    }
}