package edu.festu.ivan.kuznetsov.myapplication.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import edu.festu.ivan.kuznetsov.myapplication.database.lesson.Lesson
import edu.festu.ivan.kuznetsov.myapplication.databinding.LessonItemBinding
import edu.festu.ivan.kuznetsov.myapplication.util.LessonDiffUtils
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter

class TimetableAdapter(private var onItemClickListener: OnItemClickListener): RecyclerView.Adapter<TimetableAdapter.ViewHolder>() {
    private var lessons: MutableList<Lesson> = mutableListOf()
    companion object {
        private val TAG = TimetableAdapter::class.java.simpleName
    }
    init {
        lessons = mutableListOf()
    }
    fun getLessons():MutableList<Lesson> = lessons

    inner class ViewHolder(val binding: LessonItemBinding) : RecyclerView.ViewHolder(binding.root){
        override fun toString(): String {
            return super.toString() + " '" + binding.lessonTitle.text.toString() + "'"
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
        Log.d(TAG, lessons[position].toString())
        holder.binding.lessonTitle.text = lessons[position].title
        holder.binding.startDate2.text = lessons[position].dateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
        holder.binding.startTime2.text = lessons[position].dateTime.format(DateTimeFormatter.ofPattern("hh:mm"))
        holder.binding.endDate.text = lessons[position].dateTimeEnd.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
        holder.binding.endTime.text =lessons[position].dateTimeEnd.format(DateTimeFormatter.ofPattern("hh:mm"))
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