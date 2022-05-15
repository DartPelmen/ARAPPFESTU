package edu.festu.ivan.kuznetsov.myapplication.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import edu.festu.ivan.kuznetsov.myapplication.database.lesson.Lesson
import edu.festu.ivan.kuznetsov.myapplication.database.route.Route
import edu.festu.ivan.kuznetsov.myapplication.databinding.LessonItemBinding
import edu.festu.ivan.kuznetsov.myapplication.databinding.RouteItemBinding
import edu.festu.ivan.kuznetsov.myapplication.util.LessonDiffUtils
import edu.festu.ivan.kuznetsov.myapplication.util.RouteDiffUtils

class TimetableAdapter(private var onItemClickListener: OnItemClickListener): RecyclerView.Adapter<TimetableAdapter.ViewHolder>() {
    private var lessons: MutableList<Lesson> = mutableListOf()

    init {
        lessons = mutableListOf()
    }
    fun getLessons():MutableList<Lesson> = lessons

    inner class ViewHolder(val binding: LessonItemBinding) : RecyclerView.ViewHolder(binding.root){
        override fun toString(): String {
            return super.toString() + " '" + binding.lessonTitle + "'"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val holder =  ViewHolder(
            LessonItemBinding.inflate(
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
                fireItemClicked(position, lessons[position])
            } }
        return holder
    }
    private fun fireItemClicked(position: Int, item: Lesson) {
        onItemClickListener.onItemClicked(position, item)
    }

    // суда подписываемся активитей, фрагментом, перезнтером или чем нибудь еще
    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }

    // реализуем подписчиком
    interface OnItemClickListener {
        fun onItemClicked(position: Int, item: Lesson)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.lessonTitle.text = lessons[position].title
        holder.binding.startDate2.text = buildString {
        append(lessons[position].dateTime.dayOfMonth)
        append(" ")
        append(lessons[position].dateTime.month.value)
        append(" ")
        append(lessons[position].dateTime.year)
    }
        holder.binding.startTime2.text = buildString {
        append(lessons[position].dateTime.hour)
        append(":")
        append(lessons[position].dateTime.minute)
    }
        holder.binding.endDate.text = buildString {
        append(lessons[position].dateTimeEnd.dayOfMonth)
        append(" ")
        append(lessons[position].dateTimeEnd.month.name)
        append(" ")
        append(lessons[position].dateTimeEnd.year)
    }
        holder.binding.endTime.text =
            buildString {
        append(lessons[position].dateTimeEnd.hour)
        append(":")
        append(lessons[position].dateTimeEnd.minute)
    }
    }

    override fun getItemCount(): Int = lessons.size

    fun setLesson(facts: MutableList<Lesson>) {
        val diffCallback = LessonDiffUtils(lessons, facts)
        val diffCourses = DiffUtil.calculateDiff(diffCallback)
        this.lessons.clear()
        this.lessons.addAll(facts)

        diffCourses.dispatchUpdatesTo(this)

    }
}