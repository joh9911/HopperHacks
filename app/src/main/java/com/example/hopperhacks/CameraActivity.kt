package com.example.hopperhacks

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Insets.add
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.hopperhacks.databinding.ActivityCameraBinding
import com.example.hopperhacks.databinding.ActivityMainBinding
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Retrofit

import java.security.Permission
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@ExperimentalGetImage
class CameraActivity : FragmentActivity() {
    var mBinding: ActivityCameraBinding? = null
    val binding get() = mBinding!!

    private var imageCapture: ImageCapture? = null

    private lateinit var cameraExecutor: ExecutorService

    private lateinit var retrofit2: Retrofit
    private lateinit var myApiService: MyApiService


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)
        cameraExecutor = Executors.newSingleThreadExecutor()
        retrofit2 = Retrofit2().initRetrofit()
        myApiService = retrofit2.create(MyApiService::class.java)
//        initializeGPT3()

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }


    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(
                    this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    @ExperimentalGetImage
    private inner class YourImageAnalyzer(private val scanner: BarcodeScanner, private val imageAnalysis: ImageAnalysis) :
        ImageAnalysis.Analyzer {

        override fun analyze(imageProxy: ImageProxy) {
            Log.d("실행 ", "analyze")
            val mediaImage = imageProxy.image
            if (mediaImage != null) {
                val image =
                    InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                val result = scanner.process(image)
                    .addOnSuccessListener { barcodes ->
                        for (barcode in barcodes) {
                            val bounds = barcode.boundingBox
                            val corners = barcode.cornerPoints

                            val rawValue = barcode.rawValue

                            val valueType = barcode.valueType


                            // See API reference for complete list of supported types
                            Log.d("실행", "success! $bounds ${corners} ${rawValue} $valueType")
                            when (valueType) {
                                Barcode.TYPE_PRODUCT -> {
                                    val productCode = barcode.rawValue!!
                                    var API_KEY = "5dgwzskwy4uwokf3191pgdm2byrd87"
                                    CoroutineScope(Dispatchers.IO).launch {
                                        withContext(Dispatchers.Main){
                                            binding.progressBar.visibility = View.VISIBLE
                                        }
                                        val result = myApiService.getInfoFromBarcode(
                                            productCode,
                                            "y",
                                            API_KEY
                                        ).body()
                                        val myDataModel =
                                            MyDataModel(
                                                result?.products?.get(0)?.title,
                                                result?.products?.get(0)?.nutritionFacts,
                                                result?.products?.get(0)?.images?.get(0)
                                            )


                                        val client = OkHttpClient()
                                        val jsonMediaType = "application/json; charset=utf-8".toMediaType()
                                        val body = """
        {
            "model": "gpt-3.5-turbo",
            "messages": [
                {
                    "role": "user",
                    "content": "Give me a list of healthier alternatives of ${title}. No other text, just list the product names."
                }
            ]
        }
        """.trimIndent().toRequestBody(jsonMediaType)

                                        val request = Request.Builder()
                                            .url("https://api.openai.com/v1/chat/completions")
                                            .addHeader("Authorization", "Bearer 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")
                                            .post(body)
                                            .build()

                                        val response = client.newCall(request).execute()

                                        val responseBody = response.body?.string()


                                        withContext(Dispatchers.Main){
                                            val intent = Intent(this@CameraActivity,ActivityResult::class.java).apply{
                                                putExtra("title", "${myDataModel.name}")
                                                putExtra("nutrition", "${myDataModel.nutritionFacts}")
                                                putExtra("image", "${myDataModel.imageUrl}")
                                                putExtra("list", "${responseBody}")
                                            }

                                            startActivity(intent)

                                        }
                                    }

                                    imageAnalysis.clearAnalyzer()
                                }

                                Barcode.TYPE_WIFI -> {
                                    val ssid = barcode.wifi!!.ssid
                                    val password = barcode.wifi!!.password
                                    val type = barcode.wifi!!.encryptionType
                                    Log.d("성공 시", "$ssid $password $type")
                                }

                                Barcode.TYPE_URL -> {
                                    val title = barcode.url!!.title
                                    val url = barcode.url!!.url
                                }
                            }
                        }
                        // Task completed successfully
                        // ...
                    }
                    .addOnFailureListener {
                        // Task failed with an exception
                        // ...
                    }
                imageProxy.close()
                // Pass image to an ML Kit Vision API
                // ...
            } else {
                Log.d("실행", "실해오디고asdfs 있음")
            }
        }
    }



    fun fetchGPT3Response(title: String?) {
        CoroutineScope(Dispatchers.IO).launch {
            val client = OkHttpClient()
            val jsonMediaType = "application/json; charset=utf-8".toMediaType()
            val body = """
        {
            "model": "gpt-3.5-turbo",
            "messages": [
                {
                    "role": "user",
                    "content": "Give me a list of healthier alternatives of ${title}. No other text, just list the product names."
                }
            ]
        }
        """.trimIndent().toRequestBody(jsonMediaType)

            val request = Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .addHeader("Authorization", "Bearer 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")
                .post(body)
                .build()

            val response = client.newCall(request).execute()

            val responseBody = response.body?.string()

            withContext(Dispatchers.Main) {
                Log.d("응답","${extractContentToList(responseBody)}")
            }
        }
    }

    fun extractContentToList(responseBody: String?): List<String> {
        return responseBody?.let {
            val gson = Gson()
            val responseObj = gson.fromJson(it, Response::class.java)

            // 첫 번째 choice의 message -> content 값을 추출
            if (responseObj.choices.isNotEmpty()) {
                val content = responseObj.choices.first().message.content
                // content를 줄바꿈으로 분리하여 리스트로 변환
                content.split("\n")
            } else {
                emptyList()
            }
        } ?: emptyList() // responseBody가 null인 경우 빈 리스트 반환
    }

    // 사용할 데이터 클래스 정의
    data class Response(
        val choices: List<Choice>
    )

    data class Choice(
        val message: Message
    )

    data class Message(
        val content: String
    )


    private fun startCamera() {
        val options = BarcodeScannerOptions.Builder()

            .enableAllPotentialBarcodes() // Optional
            .build()

        val imageAnalyzer = ImageAnalysis.Builder()
            .build()
            .also {
                it.setAnalyzer(
                    cameraExecutor,
                    YourImageAnalyzer(BarcodeScanning.getClient(options), it)
                )
            }

        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }
            imageCapture = ImageCapture.Builder()
                .build()

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture, imageAnalyzer
                )

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "CameraXApp"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf(
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.RECORD_AUDIO
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }
}