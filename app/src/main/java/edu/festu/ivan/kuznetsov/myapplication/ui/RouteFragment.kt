package edu.festu.ivan.kuznetsov.myapplication.ui

import android.graphics.*
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import edu.festu.ivan.kuznetsov.myapplication.R
import edu.festu.ivan.kuznetsov.myapplication.database.MyDatabase
import edu.festu.ivan.kuznetsov.myapplication.database.auditorium.Auditorium
import edu.festu.ivan.kuznetsov.myapplication.database.route.Route
import edu.festu.ivan.kuznetsov.myapplication.databinding.FragmentRouteBinding
import edu.festu.ivan.kuznetsov.myapplication.util.DijkstraShortestPath
import java.util.concurrent.Executors


class RouteFragment : Fragment() {
    companion object{
        private val TAG = RouteFragment::class.java.simpleName
    }
    private lateinit var binding: FragmentRouteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private val adapter = RouteRVAdapter()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentRouteBinding.inflate(inflater,container,false)
        val start = arguments?.getString("route_start").toString()
        val end = arguments?.getString("route_end").toString()
        binding.routeview.adapter = adapter
        binding.routeview.addItemDecoration(
            DividerItemDecoration(binding.routeview.context,
                DividerItemDecoration.VERTICAL)
        )
        binding.routeview.layoutManager = LinearLayoutManager(requireContext())


        binding.routemap.holder.addCallback(object :SurfaceHolder.Callback{
            override fun surfaceCreated(p0: SurfaceHolder) {
                Executors.newSingleThreadExecutor().execute {
                    val audStart = MyDatabase.getInstance(requireContext()).getAuditoriumDAO().getById(start)
                    val audEnd = MyDatabase.getInstance(requireContext()).getAuditoriumDAO().getById(end)
                    val audList: MutableList<Auditorium> = mutableListOf()
                    audStart?.let {
                            st ->
                        audEnd?.let { en ->
                            DijkstraShortestPath.getPath(st.name,en.name).forEach { vertex ->
                                audList.add(MyDatabase.getInstance(requireContext()).getAuditoriumDAO().getByName(vertex.name)!!)
                            }
                            draw(audList)

                            requireActivity().runOnUiThread {
                                val destinations:MutableList<String> = audList.map { if(!it.name.contains("Лестница")) "Дойдите до аудитории ${it.name}" else "Дойдите до лестницы"}.toMutableList()
                                destinations[0] = "Начало маршрута: ${audList[0].name}"
                                destinations[destinations.size-1] = "Конец маршрута:  ${audList[audList.size - 1].name}"
                                adapter.setRoutes(destinations)
                                adapter.notifyDataSetChanged()
                            }
                        }
                    }

                }
            }

            override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {}
            override fun surfaceDestroyed(p0: SurfaceHolder) {}
        })

        return binding.root
    }

    private fun draw(audList: MutableList<Auditorium>) {

        val canvas = binding.routemap.holder.lockCanvas()
        val dr  = ResourcesCompat.getDrawable(resources, R.drawable.ic_map,null)?.toBitmap(canvas.width,canvas.height)!!

        val array: MutableList<Float> = mutableListOf()
        for (i in 0 until audList.size - 1) {
            array.add(audList[i].xCord / 100 * dr.width)
            array.add(audList[i].yCord / 100 * dr.height)
            array.add(audList[i + 1].xCord / 100 * dr.width)
            array.add(audList[i + 1].yCord / 100 * dr.height)
            Log.d(
                TAG,
                "POINT COORD IS \${it.xCord/100*canvas.width};\${it.yCord/100*canvas.height}"
            )
        }
        val p = Paint()
        p.isAntiAlias =true
        p.color = Color.RED
        p.strokeWidth = 10f
        p.strokeCap = Paint.Cap.ROUND
        p.strokeJoin = Paint.Join.ROUND
        p.style = Paint.Style.FILL_AND_STROKE
        canvas.drawBitmap(dr,0f,0f,null)
        canvas.drawLines(array.toFloatArray(), p)
        binding.routemap.holder.unlockCanvasAndPost(canvas)
        Log.d(TAG,dr.width.toString() + " " + dr.height.toString())
    }


}