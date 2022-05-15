package edu.festu.ivan.kuznetsov.myapplication.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import edu.festu.ivan.kuznetsov.myapplication.R
import edu.festu.ivan.kuznetsov.myapplication.database.MyDatabase
import edu.festu.ivan.kuznetsov.myapplication.database.lesson.Lesson
import edu.festu.ivan.kuznetsov.myapplication.database.route.Route
import edu.festu.ivan.kuznetsov.myapplication.databinding.FragmentTimetableBinding
import edu.festu.ivan.kuznetsov.myapplication.util.AddLessonFragment
import edu.festu.ivan.kuznetsov.myapplication.util.DijkstraShortestPath
import edu.festu.ivan.kuznetsov.myapplication.util.LessonDiffUtils
import edu.festu.ivan.kuznetsov.myapplication.util.RouteDiffUtils
import edu.festu.ivan.kuznetsov.myapplication.viewmodel.LessonModel
import edu.festu.ivan.kuznetsov.myapplication.viewmodel.RouteViewModel
import java.lang.ref.WeakReference
import java.util.concurrent.Callable
import java.util.concurrent.Executors


class TimetableFragment : Fragment() {
    companion object{
        private val TAG = TimetableAdapter::class.java.simpleName
    }
    private val adapter = TimetableAdapter(object : TimetableAdapter.OnItemClickListener {


        override fun onItemClicked(position: Int, item: Lesson) {
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

        val lessonModel: LessonModel by viewModels()
        lessonModel.context = WeakReference(requireContext())
        lessonModel.getRoutes().observe(this.viewLifecycleOwner) { routes ->

            val productDiffUtilCallback =
                LessonDiffUtils(adapter.getLessons(), routes)
            val productDiffResult =
                DiffUtil.calculateDiff(productDiffUtilCallback)

            adapter.setLesson(routes)
            // adapter.notifyDataSetChanged()
            if (adapter.getLessons().isEmpty())
            {
                binding.lessinList.visibility = View.GONE
                binding.nothingElements.visibility = View.VISIBLE
            }
            else {
                binding.lessinList.visibility = View.VISIBLE
                binding.nothingElements.visibility = View.GONE
            }
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

        binding.addLesson.setOnClickListener {
            AddLessonFragment.display(requireActivity().supportFragmentManager)

        }






        return binding.root
    }

}