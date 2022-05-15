package edu.festu.ivan.kuznetsov.myapplication.util

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import edu.festu.ivan.kuznetsov.myapplication.R
import edu.festu.ivan.kuznetsov.myapplication.database.MyDatabase
import edu.festu.ivan.kuznetsov.myapplication.database.auditorium.Auditorium
import edu.festu.ivan.kuznetsov.myapplication.database.lesson.Lesson
import edu.festu.ivan.kuznetsov.myapplication.database.teacher.Teacher
import edu.festu.ivan.kuznetsov.myapplication.databinding.LessonDialogBinding
import java.time.LocalDateTime
import java.util.concurrent.Executors


class AddLessonFragment: DialogFragment(){
    companion object{
        const val TAG = "example_dialog"
        fun display(fragmentManager: FragmentManager): AddLessonFragment {
            val exampleDialog = AddLessonFragment()
            exampleDialog.show(fragmentManager, TAG)
            return exampleDialog
        }
    }
    private lateinit var teachers: List<Teacher>
    private lateinit var auditoriums: List<Auditorium>

    private var toolbar: Toolbar? = null
    private lateinit var binding: LessonDialogBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = LessonDialogBinding.inflate(inflater,container,false)
        Executors.newSingleThreadExecutor().execute{
            teachers = MyDatabase.getInstance(requireContext()).getTeacherDAO().getAll()
            auditoriums = MyDatabase.getInstance(requireContext()).getAuditoriumDAO().getAll()
            Log.d(TAG, teachers.toString())
            val adapter = ArrayAdapter(requireContext(), R.layout.list_item, teachers)
            val adapterAuditorium = ArrayAdapter(requireContext(), R.layout.list_item, auditoriums)
            requireActivity().runOnUiThread {
                (binding.textInputLayout.editText as? AutoCompleteTextView)?.setAdapter(adapter)

                (binding.auditoriumLayout.editText as? AutoCompleteTextView)?.setAdapter(adapterAuditorium)

            }
        }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setNavigationOnClickListener { dismiss() }
        binding.toolbar.title = "Добавить занятие"
        binding.toolbar.inflateMenu(R.menu.example_dialog)
        binding.toolbar.setOnMenuItemClickListener { _ ->
            val start = LocalDateTime.of(binding.dateLesson.year,binding.dateLesson.month,
                binding.dateLesson.dayOfMonth, binding.startTime.hour, binding.startTime.minute,0,0)
            val end = LocalDateTime.of(binding.dateLesson.year,binding.dateLesson.month,
                binding.dateLesson.dayOfMonth, binding.endTime.hour, binding.endTime.minute,0,0)
            val l = Lesson(binding.lessonTitle.toString(),start,end,
                auditoriums.first { it.toString()==binding.auditorium.text.toString()}.id,
                teachers.first{it.toString()==binding.teacher.text.toString()}.idTeacher)
            Log.d(TAG, l.toString())
            Executors.newSingleThreadExecutor().execute{
                MyDatabase.getInstance(requireContext()).getLessonDAO().add(l)
                requireActivity().runOnUiThread {
                    dismiss()
                }
            }
            true
        }
    }

    override fun onStart() {
        super.onStart()
        val dialog: Dialog? = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window?.setLayout(width, height)
        }
    }
}