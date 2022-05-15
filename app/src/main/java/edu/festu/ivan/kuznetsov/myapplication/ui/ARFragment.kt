package edu.festu.ivan.kuznetsov.myapplication

//import edu.festu.ivan.kuznetsov.myapplication.classification.MLKitObjectDetector
import android.graphics.Bitmap
import android.media.Image
import android.opengl.Matrix
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.ar.core.*
import com.google.ar.core.exceptions.*
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import edu.festu.ivan.kuznetsov.myapplication.ar.*
import edu.festu.ivan.kuznetsov.myapplication.ar.render.BackgroundRenderer
import edu.festu.ivan.kuznetsov.myapplication.ar.render.SampleRender
import edu.festu.ivan.kuznetsov.myapplication.ar.render.helpers.CameraPermissionHelper
import edu.festu.ivan.kuznetsov.myapplication.ar.render.helpers.DisplayRotationHelper
import edu.festu.ivan.kuznetsov.myapplication.ar.third_party.YuvToRgbConverter
import edu.festu.ivan.kuznetsov.myapplication.database.MyDatabase
import edu.festu.ivan.kuznetsov.myapplication.database.route.Route
import edu.festu.ivan.kuznetsov.myapplication.databinding.FragmentARBinding
import edu.festu.ivan.kuznetsov.myapplication.ui.RouteListFragment
import edu.festu.ivan.kuznetsov.myapplication.util.DijkstraShortestPath
import edu.festu.ivan.kuznetsov.myapplication.viewmodel.RouteViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.tasks.asDeferred
import java.io.*
import java.lang.ref.WeakReference
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.Executors


/**
 * A simple [Fragment] subclass.
 * Use the [ARFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ARFragment : Fragment() {
    private val coroutineScope = MainScope()
    var objectResults: List<Text.Line>? = null
    private var session: Session? = null
    private lateinit var displayRotationHelper: DisplayRotationHelper
    private lateinit var  yuvConverter: YuvToRgbConverter
    // Rendering components
    lateinit var backgroundRenderer: BackgroundRenderer
    val pointCloudRender = PointCloudRender()
    val labelRenderer = LabelRender()
    private var installRequested = false
    private var shouldConfigureSession = false

    // Matrices for reuse in order to prevent reallocations every frame.
    val viewMatrix = FloatArray(16)
    val projectionMatrix = FloatArray(16)
    val viewProjectionMatrix = FloatArray(16)
    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    val arLabeledAnchors = Collections.synchronizedList(mutableListOf<ARLabeledAnchor>())
    var scanButtonWasPressed = false
    //private var recognizer:TextRecognizer? = null

    companion object {
        val TAG = ARFragment::class.java.simpleName
    }
    private lateinit var binding: FragmentARBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "GO TO CREATE FRAGMENT")

        yuvConverter = YuvToRgbConverter(requireContext())


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        Log.d(TAG, "GO TO CREATE FRAGMENT VIEW")

        binding = FragmentARBinding.inflate(layoutInflater,container,false)
        displayRotationHelper = DisplayRotationHelper( /*context=*/requireContext())
 //       currentAnalyzer =  MLKitObjectDetector(requireContext())

        val routeModel: RouteViewModel by viewModels()
        routeModel.context = WeakReference(requireContext())

        binding.floatingActionButton.visibility = View.GONE

        binding.floatingActionButton.setOnClickListener{

            val future = Executors.newSingleThreadExecutor().submit(Callable { MyDatabase.getInstance(requireContext()).getAuditoriumDAO().getAll() })
            while (!future.isDone){
                Log.d(TAG, "GETTING DATA")
            }
            val singleItems = future.get()

            var checkedItemSecond = 2

                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Выберите конечную аудиторию")
                        .setNeutralButton("Отмена") { _, _ ->
                        }
                        .setPositiveButton("Подтвердить") { _, _ ->
                            Toast.makeText(requireContext(),
                                DijkstraShortestPath.getPath(binding.status.text.toString(),singleItems[checkedItemSecond].name).toString(),
                                Toast.LENGTH_LONG).show()
                            routeModel.addRoute(Route("Из ${binding.status.text} в ${singleItems[checkedItemSecond].name}",singleItems.first { it.name==binding.status.text}.id,singleItems[checkedItemSecond].id))
                        }
                        .setSingleChoiceItems(singleItems.map{ it.name}.toTypedArray(), checkedItemSecond) { _, which ->
                            checkedItemSecond = which
                        }
                        .show()
                }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "FRAGMENT VIEW CREATED")

        binding.surfaceView.apply {
            SampleRender(this, Renderer(), requireActivity().assets)
        }
        binding.scanButton.setOnClickListener {
            setScan()
        }
        binding.resetButton.setOnClickListener {
            clearAnchors()
        }
        resumeSession()
    }


    override fun onResume() {
        super.onResume()
        Log.d(TAG, "GO TO CREATE FRAGMENT RESUME")
        resumeSession()
    }

    private fun resumeSession() {
        if (session == null) {
            var exception: Exception? = null
            var message: String? = null
            try {
                when (ArCoreApk.getInstance()
                    .requestInstall(requireActivity(), !installRequested)) {
                    ArCoreApk.InstallStatus.INSTALL_REQUESTED -> {
                        installRequested = true
                        return
                    }
                    ArCoreApk.InstallStatus.INSTALLED -> {
                        Log.e(TAG, "ArCoreApk Installed!")
                    }
                    null -> {
                        Log.e(TAG, "ArCoreApk is Null!")
                        return
                    }
                }

                // ARCore requires camera permissions to operate. If we did not yet obtain runtime
                // permission on Android M and above, now is a good time to ask the user for it.
                if (!CameraPermissionHelper.hasCameraPermission(requireActivity())) {
                    CameraPermissionHelper.requestCameraPermission(requireActivity())

                    return
                }
                session = Session(requireContext())
            } catch (e: UnavailableArcoreNotInstalledException) {
                message = "Please install ARCore"
                exception = e
            } catch (e: UnavailableUserDeclinedInstallationException) {
                message = "Please install ARCore"
                exception = e
            } catch (e: UnavailableApkTooOldException) {
                message = "Please update ARCore"
                exception = e
            } catch (e: UnavailableSdkTooOldException) {
                message = "Please update this app"
                exception = e
            } catch (e: Exception) {
                message = "This device does not support AR"
                exception = e
            }
            message?.let {
                Log.e(TAG, "Exception creating session", exception)
                return
            }
            shouldConfigureSession = true
        }
        if (shouldConfigureSession) {
            configureSession()
            shouldConfigureSession = false
        }

        // Note that order matters - see the note in onPause(), the reverse applies here.
        try {
            session!!.resume()
        } catch (e: CameraNotAvailableException) {
            session = null
            return
        }
        binding.surfaceView.onResume()
        displayRotationHelper.onResume()
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "GO TO CREATE FRAGMENT PAUSE")
        session?.let {
            displayRotationHelper.onPause()
            it.pause()
        }
        if (session != null) {
            // Note that the order matters - GLSurfaceView is paused first so that it does not try
            // to query the session. If Session is paused before GLSurfaceView, GLSurfaceView may
            // still call session.update() and get a SessionPausedException.
            displayRotationHelper.onPause()
            binding.surfaceView.onPause()
            session!!.pause()
        }
    }
    override fun onDestroy() {
        Log.d(TAG, "GO TO CREATE FRAGMENT DESTROY")
        session?.close()
        session = null
        super.onDestroy()
    }

    private fun configureSession() {
        val config = Config(session)

        config.focusMode = Config.FocusMode.AUTO
        config.planeFindingMode = Config.PlaneFindingMode.HORIZONTAL_AND_VERTICAL
        if (session!!.isDepthModeSupported(Config.DepthMode.AUTOMATIC)) {
            config.depthMode = Config.DepthMode.AUTOMATIC
        }
        session!!.configure(config)
        val filter =
            CameraConfigFilter(session).setFacingDirection(CameraConfig.FacingDirection.BACK)
        val configs = session!!.getSupportedCameraConfigs(filter)
        val sort =
            compareByDescending<CameraConfig> { it.imageSize.width }.thenByDescending {
                it.imageSize.height
            }
        session!!.cameraConfig = configs.sortedWith(sort)[0]
    }

    fun setScanningActive(active: Boolean) =
requireActivity().runOnUiThread{
    when (active) {
        true -> {
            binding.scanButton.isEnabled = false
            binding.scanButton.text = "найти информацию"
        }
        false -> {
            binding.scanButton.isEnabled = true
            binding.scanButton.text = "сканировать"
        }
    }
}




    private val convertFloats = FloatArray(4)
    private val convertFloatsOut = FloatArray(4)
    fun createAnchor(xImage: Float, yImage: Float, frame: Frame): Anchor? {
        // IMAGE_PIXELS -> VIEW
        convertFloats[0] = xImage
        convertFloats[1] = yImage
        frame.transformCoordinates2d(
            Coordinates2d.IMAGE_PIXELS,
            convertFloats,
            Coordinates2d.VIEW,
            convertFloatsOut
        )

        // Conduct a hit test using the VIEW coordinates
        val hits = frame.hitTest(convertFloatsOut[0], convertFloatsOut[1])

        val result = hits.getOrNull(0) ?: return null
        Log.d(TAG, "HIT RESULT ${result.distance}")
        return result.trackable.createAnchor(result.hitPose)
    }

    fun Frame.tryAcquireCameraImage() =
        try {
            acquireCameraImage()
        } catch (e: NotYetAvailableException) {
            null
        } catch (e: Throwable) {
            throw e
        }
    private fun setScan(){

        scanButtonWasPressed = true
    }
    private fun clearAnchors(){
        synchronized(arLabeledAnchors) { arLabeledAnchors.clear() }
    }

    inner class Renderer: SampleRender.Renderer{
        override fun onSurfaceCreated(render: SampleRender) {
            Log.d(TAG, "CREATED!")
            backgroundRenderer =
                BackgroundRenderer(render).apply { setUseDepthVisualization(render, false) }
            pointCloudRender.onSurfaceCreated(render)
            labelRenderer.onSurfaceCreated(render)
        }

        override fun onSurfaceChanged(render: SampleRender?, width: Int, height: Int) {
            displayRotationHelper.onSurfaceChanged(width, height)
        }





        override fun onDrawFrame(render: SampleRender) {
            Log.d(TAG, "GO TO DRAW!")

            if (session==null)
            {
                Log.d(TAG, "SESSION IS NULL ")
                return
            }
            session!!.setCameraTextureNames(intArrayOf(backgroundRenderer.cameraColorTexture.textureId))

            // Notify ARCore session that the view size changed so that the perspective matrix and
            // the video background can be properly adjusted.
            displayRotationHelper.updateSessionIfNeeded(session)
            val frame =
                try {
                    session!!.update()
                } catch (e: CameraNotAvailableException) {
                    Log.e(TAG, "Camera not available during onDrawFrame", e)

                    return
                } catch (e: SessionPausedException) {
                    Log.e(TAG, "Session Still Not Resumed!", e)

                    return
                }
            backgroundRenderer.updateDisplayGeometry(frame)
            backgroundRenderer.drawBackground(render)

            // Get camera and projection matrices.
            val camera = frame.camera
            camera.getViewMatrix(viewMatrix, 0)
            camera.getProjectionMatrix(projectionMatrix, 0, 0.01f, 100.0f)
            Matrix.multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0)

            // Handle tracking failures.
            if (camera.trackingState != TrackingState.TRACKING) {
                return
            }
            // Draw point cloud.
            frame.acquirePointCloud().use { pointCloud ->
                pointCloudRender.drawPointCloud(render, pointCloud, viewProjectionMatrix)
            }

            // Frame.acquireCameraImage must be used on the GL thread.
            // Check if the button was pressed last frame to start processing the camera image.
            if (scanButtonWasPressed) {
                scanButtonWasPressed = false
                val cameraImage = frame.tryAcquireCameraImage()
                if (cameraImage != null) {
                    // Call our ML model on an IO thread.
                    coroutineScope.launch(Dispatchers.IO) {
                        val cameraId = session!!.cameraConfig.cameraId
                        val imageRotation = displayRotationHelper.getCameraSensorToDisplayRotation(cameraId)
                        val image = InputImage.fromBitmap(convertYuv(cameraImage), imageRotation)
                        objectResults = recognizer.process(image).continueWith{
                            Log.d(TAG, "LISt OF ${it.result.textBlocks.size}")
                            it.result.textBlocks
                        }.continueWith { it.result.flatMap { x-> x.lines } }
                            .asDeferred().await()


                        cameraImage.close()
                    }
                }
            }

            /** If results were completed this frame, create [Anchor]s from model results. */
            val objects = objectResults
            if (objects != null) {
                objectResults = null
//                Log.i(TAG, "$currentAnalyzer got objects: $objects")

                val anchors =
                    objects.mapNotNull { obj ->
                        val re = Regex("[A-Za-z ]")
                        val text = re.replace(obj.text,"")
                        val anchor =
                            createAnchor(obj.boundingBox!!.exactCenterX(), obj.boundingBox!!.exactCenterY(), frame)
                                ?: return@mapNotNull null

                            val labelText = when (text.trim()) {
                                "3430" -> {
                                    requireActivity().runOnUiThread {
                                        binding.status.text = "3430"
                                        binding.floatingActionButton.visibility = View.VISIBLE
                                    }
                                    "This is IoT Laboratory!\nCurrent Lesson: Java-Programming with I.V. Kuznetsov!\nLab Manager - I.V. Kuznetsov"
                                }
                                "428" -> {
                                    requireActivity().runOnUiThread {
                                        binding.status.text = "428"
                                        binding.floatingActionButton.visibility = View.VISIBLE

                                    }
                                    "This is VR Laboratory!\nCurrent Lesson: Mobile Development with I.V. Kuznetsov!\nLab Manager - A.V. Rud"
                                }
                                "236" -> {
                                    requireActivity().runOnUiThread {
                                        binding.status.text = "236"
                                        binding.floatingActionButton.visibility = View.VISIBLE

                                    }
                                    "This is 3D-Printing Laboratory!\nCurrent Lesson: Mobile Development with A.A. Kholodilov!\nLab Manager - A.A. Kholodilov"
                                }
                                else -> {
                                    ""
                                }
                            }

                        ARLabeledAnchor(anchor, labelText)

                    }
                anchors.forEach{
                    if(it.label.isNotBlank() and it.label.isNotEmpty())
                        arLabeledAnchors.add(it)
                }
//                arLabeledAnchors.addAll(anchors)
                    Log.e(TAG, Thread.currentThread().name)

                    requireActivity().runOnUiThread { binding.resetButton.isEnabled = arLabeledAnchors.isNotEmpty()}
                    setScanningActive(false)
                    when {
                        objects.isEmpty() -> {
                            requireActivity().runOnUiThread {
                                binding.floatingActionButton.visibility = View.GONE
                                binding.status.text = "не удалось просканировать."
                            }
                            Log.i(TAG, "Classification model returned no results.")
                        }
                        anchors.size != objects.size -> {
                            requireActivity().runOnUiThread {
                                binding.floatingActionButton.visibility = View.GONE
                                binding.status.text = "Не удалось показать информацию об аудитории. Попробуйте провести сканирование из другой позиции."
                            }
                            Log.i(
                                TAG,
                                "Objects were classified, but could not be attached to an anchor. " +
                                        "Try moving your device around to obtain a better understanding of the environment."
                            )
                        }

                    }

            }
            // Draw labels at their anchor position.
            synchronized(arLabeledAnchors) {
                Log.d(TAG, "DRAWING ANCHOR LABELS FOR ${arLabeledAnchors.size} ANCHORS")
                for (arDetectedObject in arLabeledAnchors) {

                    val anchor = arDetectedObject.anchor
                    if (anchor.trackingState != TrackingState.TRACKING) {
                        Log.d(TAG, arDetectedObject.label + "does not tracking!!")
                        continue
                    }
                    labelRenderer.draw(
                        render,
                        viewProjectionMatrix,
                        anchor.pose,
                        camera.pose,
                        arDetectedObject.label
                    )
                }
            }
        }
    }
    fun convertYuv(image: Image): Bitmap {
        return Bitmap.createBitmap(image.width, image.height, Bitmap.Config.ARGB_8888).apply {
            yuvConverter.yuvToRgb(image, this)
        }
    }
}

data class ARLabeledAnchor(val anchor: Anchor, val label: String)

