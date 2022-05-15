package edu.festu.ivan.kuznetsov.myapplication.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import edu.festu.ivan.kuznetsov.myapplication.R
import edu.festu.ivan.kuznetsov.myapplication.database.MyDatabase
import edu.festu.ivan.kuznetsov.myapplication.database.route.Route
import edu.festu.ivan.kuznetsov.myapplication.databinding.FragmentRouteListBinding
import edu.festu.ivan.kuznetsov.myapplication.util.DijkstraShortestPath
import edu.festu.ivan.kuznetsov.myapplication.util.RouteDiffUtils
import edu.festu.ivan.kuznetsov.myapplication.viewmodel.RouteViewModel
import java.lang.ref.WeakReference
import java.util.concurrent.Callable
import java.util.concurrent.Executors


class RouteListFragment : Fragment() {
    companion object{
        private val TAG = RouteListFragment::class.java.simpleName
    }
    private lateinit var binding: FragmentRouteListBinding
    private val adapter = RouteListAdapter(object : RouteListAdapter.OnItemClickListener {
        override fun onItemClicked(position: Int, item: Route) {
            binding.routes.findNavController().navigate(R.id.action_routeListFragment_to_routeFragment,
                bundleOf("route_name" to item.toString(),
                "route_start" to item.idAuditoriumStart,
                "route_end" to item.idAuditoriumEnd,
                "route_id" to item.idRoute))
        }

    })
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentRouteListBinding.inflate(inflater,container,false)
        binding.routes.adapter = adapter
        binding.routes.addItemDecoration(
            DividerItemDecoration(binding.routes.context,
                DividerItemDecoration.VERTICAL)
        )
        binding.routes.layoutManager = LinearLayoutManager(requireContext())


        val routeModel:RouteViewModel by viewModels()
        routeModel.context = WeakReference(requireContext())
        routeModel.getRoutes().observe(this.viewLifecycleOwner) { routes ->

            val productDiffUtilCallback =
                RouteDiffUtils(adapter.getRoutes(), routes)
            val productDiffResult =
                DiffUtil.calculateDiff(productDiffUtilCallback)

            adapter.setRoutes(routes)
           // adapter.notifyDataSetChanged()
            if (adapter.getRoutes().isEmpty())
            {
                binding.routes.visibility = View.GONE
                binding.nothing.visibility = View.VISIBLE
            }
            else {
                binding.routes.visibility = View.VISIBLE
                binding.nothing.visibility = View.GONE
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
                routeModel.remove(position)
            }
        })
        itemTouchHelper.attachToRecyclerView(binding.routes)
        routeModel.getRoutesFromDB()

        binding.add.setOnClickListener {

            val future = Executors.newSingleThreadExecutor().submit(Callable { MyDatabase.getInstance(requireContext()).getAuditoriumDAO().getAll() })
            while (!future.isDone){
                Log.d(TAG, "GETTING DATA")
            }
            val singleItems = future.get()

            var checkedItemFirst = 1
            var checkedItemSecond = 2

            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Выберите начальную аудиторию")
                .setNeutralButton("Отмена") { _, _ ->
                }
                .setPositiveButton("Подтвердить") { _, _ ->
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Выберите конечную аудиторию")
                        .setNeutralButton("Отмена") { _, _ ->
                        }
                        .setPositiveButton("Подтвердить") { _, _ ->
                            Toast.makeText(requireContext(),DijkstraShortestPath.getPath(singleItems[checkedItemFirst].name,singleItems[checkedItemSecond].name).toString(),Toast.LENGTH_LONG).show()
                            routeModel.addRoute(Route("Из ${singleItems[checkedItemFirst].name} в ${singleItems[checkedItemSecond].name}",singleItems[checkedItemFirst].id,singleItems[checkedItemSecond].id))
                        }
                        .setSingleChoiceItems(singleItems.map{ it.name}.toTypedArray(), checkedItemSecond) { _, which ->
                            checkedItemSecond = which
                        }
                        .show()
                }
                .setSingleChoiceItems(singleItems.map{ it.name}.toTypedArray(), checkedItemFirst) { _, which ->
                    checkedItemFirst = which
                }

                // Single-choice items (initialized with checked item)
                .show()

        }
        return binding.root
    }

}