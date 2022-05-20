package edu.festu.ivan.kuznetsov.myapplication.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.*
import edu.festu.ivan.kuznetsov.myapplication.R
import edu.festu.ivan.kuznetsov.myapplication.database.lesson.Lesson
import edu.festu.ivan.kuznetsov.myapplication.databinding.FragmentTimetableBinding
import edu.festu.ivan.kuznetsov.myapplication.util.AddLessonFragment
import edu.festu.ivan.kuznetsov.myapplication.util.LessonDiffUtils
import edu.festu.ivan.kuznetsov.myapplication.viewmodel.LessonModel
import java.lang.ref.WeakReference
import java.time.LocalDateTime


class TimetableFragment : Fragment() {
    companion object{
        private val TAG = TimetableAdapter::class.java.simpleName
    }
    private val adapter = TimetableAdapter(object : TimetableAdapter.OnItemClickListener {


        override fun onItemClicked(position: Int, item: Lesson) {
            Log.d(TAG, "Timetable item clicked")
            binding.lessinList.findNavController().navigate(R.id.action_timetableFragment_to_lessonFragment,
                bundleOf("lesson_name" to item.title,
                    "lesson_start" to item.dateTime.toString(),
                    "lesson_end" to item.dateTimeEnd.toString(),
                    "teacher_id" to item.idTeacher)
            )
        }

    })

    private lateinit var binding: FragmentTimetableBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentTimetableBinding.inflate(inflater,container,false)
        Log.d(TAG, "observer method started")
        binding.lessinList.adapter = adapter
        binding.lessinList.addItemDecoration(
            DividerItemDecoration(binding.lessinList.context,
                DividerItemDecoration.VERTICAL)
        )
        binding.lessinList.layoutManager = LinearLayoutManager(requireActivity())

        val lessonModel: LessonModel by viewModels()
        lessonModel.context = WeakReference(requireActivity())
        lessonModel.getRoutes().observe(this.viewLifecycleOwner) { routes ->
            Log.d(TAG, "observer method started")
            val productDiffUtilCallback =
                LessonDiffUtils(adapter.getLessons(), routes)
            val productDiffResult =
                DiffUtil.calculateDiff(productDiffUtilCallback)
            adapter.setLesson(routes)


            Log.d(TAG, "observer method started")

            if (adapter.getLessons().isEmpty())
            {
                Log.d(TAG, "observer method started true")
                binding.lessinList.visibility = View.GONE
                binding.nothingElements.visibility = View.VISIBLE
            }
            else {
                Log.d(TAG, "observer method started false")
                binding.lessinList.visibility = View.VISIBLE
                binding.nothingElements.visibility = View.GONE
            }
            Log.d(TAG, "observer method calculated")
            productDiffResult.dispatchUpdatesTo(adapter)
        }


        val itemTouchHelper = ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
                val position = viewHolder.layoutPosition
                lessonModel.remove(position)
            }
        })
        itemTouchHelper.attachToRecyclerView(binding.lessinList)
        lessonModel.getRoutesFromDB()
        requireActivity().supportFragmentManager.setFragmentResultListener("addLesson",this) {
                _: String?, result: Bundle ->

            val lesson = Lesson(result.getString("lessonTitle").toString(),
                LocalDateTime.parse(result.getString("startDate").toString()),
            LocalDateTime.parse(result.getString("endDate").toString()),
            result.getString("auditorium").toString(),
                result.getString("idTeacher").toString())
            lesson.id = result.getString("idLesson").toString()
            lessonModel.addRoute(lesson)
            Log.d(TAG, "ADDED")
        }
        binding.addLesson.setOnClickListener {
            AddLessonFragment.display(requireActivity().supportFragmentManager)
            }
        return binding.root
    }
}

