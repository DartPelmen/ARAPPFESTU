package edu.festu.ivan.kuznetsov.myapplication.util

import androidx.recyclerview.widget.DiffUtil
import edu.festu.ivan.kuznetsov.myapplication.database.lesson.Lesson
import edu.festu.ivan.kuznetsov.myapplication.database.route.Route


class RouteDiffUtils(private val oldList: List<Route>, private val newList: List<Route>) : DiffUtil.Callback() {
    override fun getOldListSize(): Int =oldList.size

    override fun getNewListSize(): Int = newList.size
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].idRoute=== newList[newItemPosition].idRoute
    }
    override fun areContentsTheSame(oldCourse: Int, newPosition: Int): Boolean {

        return oldList[oldCourse] == newList[newPosition]
    }

}

class LessonDiffUtils(private val oldList: List<Lesson>, private val newList: List<Lesson>) : DiffUtil.Callback() {
    override fun getOldListSize(): Int =oldList.size

    override fun getNewListSize(): Int = newList.size
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id=== newList[newItemPosition].id
    }
    override fun areContentsTheSame(oldCourse: Int, newPosition: Int): Boolean {

        return oldList[oldCourse] == newList[newPosition]
    }

}