package com.example.batikcapstone

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.content.res.AssetFileDescriptor
import android.os.Environment
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.example.batikcapstone.data.local.PredictionResultDao
import com.example.batikcapstone.data.local.PredictionResultDatabase
import com.example.batikcapstone.data.model.Batik
import com.example.batikcapstone.data.model.PredictionResult
import com.example.batikcapstone.ui.batik.DetailBatikActivity
import com.example.batikcapstone.ui.home.HomeFragment
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.tensorflow.lite.Interpreter
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.text.SimpleDateFormat
import java.util.*

class CameraResultActivity : AppCompatActivity() {
    private val CAMERA_PERMISSION_REQUEST_CODE = 1001
    private val CAMERA_REQUEST_CODE = 1002

    private lateinit var firestore: FirebaseFirestore
    private lateinit var imageView: ImageView
    private lateinit var tvprediction: TextView
    private lateinit var batikList: MutableList<Batik>
    private lateinit var tflite: Interpreter
    private lateinit var predictionResultDatabase: PredictionResultDatabase
    private lateinit var predictionResultDao: PredictionResultDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_result)

        imageView = findViewById(R.id.iv_recentImage)
        firestore = FirebaseFirestore.getInstance()
        batikList = mutableListOf()

        predictionResultDatabase = PredictionResultDatabase.getDatabase(this)
        predictionResultDao = predictionResultDatabase.predictionResultDao()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        } else {
            startCamera()
        }
        // Load the TensorFlow Lite model
        tflite = Interpreter(loadModelFile())

        fetchDataFromDatabase()
    }

    private fun startCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA_REQUEST_CODE)
    }

    private fun loadModelFile(): MappedByteBuffer {
        // TODO: Replace with the path to your TFLite model file
        val modelFileDescriptor: AssetFileDescriptor = assets.openFd("trial_model.tflite")
        val inputStream = FileInputStream(modelFileDescriptor.fileDescriptor)
        val startOffset = modelFileDescriptor.startOffset
        val declaredLength = modelFileDescriptor.declaredLength
        return inputStream.channel.map(
            FileChannel.MapMode.READ_ONLY,
            startOffset,
            declaredLength
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            imageView.setImageBitmap(imageBitmap)

            val inputBuffer = preprocessImage(imageBitmap)

            val outputShape = tflite.getOutputTensor(0).shape()
            val outputSize = outputShape[1]

            val outputBuffer = ByteBuffer.allocateDirect(outputSize * 4)
                .order(ByteOrder.nativeOrder())

            tflite.run(inputBuffer, outputBuffer)

            // Process the outputBuffer and show the prediction results
            // You can access the predicted values using `outputBuffer.getFloat(index)`

            outputBuffer.rewind() // Reset the buffer's position to read from the beginning

            val predictionResults = getPredictionResults(outputBuffer)
            val resultText = getResultText(predictionResults)

            // Save the picture to the local storage
            val imagePath = saveImageToStorage(imageBitmap)

            // Save the prediction result to the database with the image path
            val predictionResult = PredictionResult(result = resultText, imagePath = imagePath, timestamp = System.currentTimeMillis())
            savePredictionResult(predictionResult)

            getBatikInformation(resultText)
        } else if (resultCode == Activity.RESULT_CANCELED) {
        // Camera canceled, navigate back to HomeFragment
            finish()
    }
    }
    private fun savePredictionResult(result: PredictionResult) {
        if (result.result != "Random Images") {
            GlobalScope.launch(Dispatchers.IO) {
                predictionResultDao.insertPredictionResult(result)
            }
        }
    }

    private fun saveImageToStorage(imageBitmap: Bitmap): String {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_${timeStamp}_"

        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imageFile = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",         /* suffix */
            storageDir      /* directory */
        )

        val fos = FileOutputStream(imageFile)
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        fos.flush()
        fos.close()

        return imageFile.absolutePath
    }

    private fun getBatikInformation(result: String) {
        val predictedClass = result

        if (predictedClass == "Random Images") {
            showRecognitionErrorDialog()
            return
        }

        val desiredBatik = batikList.find { batik ->
            batik.name.equals(predictedClass, ignoreCase = true)
        }

        desiredBatik?.let {
            val intent = Intent(this, DetailBatikActivity::class.java)
            intent.putExtra("batik", desiredBatik)
            startActivity(intent)
            finish()
        }
    }

    private fun getPredictionResults(outputBuffer: ByteBuffer): FloatArray {
        val results = FloatArray(outputBuffer.remaining() / 4)
        outputBuffer.asFloatBuffer().get(results)
        return results
    }

    private fun getResultText(predictionResults: FloatArray): String {
        val classLabels = arrayOf(
            "Batik Megamendung",
            "Random Images",
            "Batik Cendrawasih",
            "Batik Ceplok",
            "Batik Lasem",
            "Batik Merak Abyorhokokai",
            "Batik Parang",
            "Batik Sekar Jagad",
            "Batik Sido Asih",
            "Batik Sido Luhur",
            "Batik Singa Barong",
            "Batik Tambal",
            "Batik Tujuh Rupa"
        )

        val resultText = StringBuilder()

        var maxScore = -1f
        var predictedClass = ""

        for (i in classLabels.indices) {
            val score = predictionResults[i]
            if (score > maxScore) {
                maxScore = score
                predictedClass = classLabels[i]
            }
            resultText.append(classLabels[i])
                .append(": ")
                .append(score)
                .append("\n")
        }

        resultText.append("\nPredicted: ")
            .append(predictedClass)
            .append("\nScore: ")
            .append(maxScore)

        return predictedClass
    }

    private fun showRecognitionErrorDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.camera_alert_title)
        builder.setMessage(R.string.camera_alert_message)
        builder.setCancelable(false) // Set cancelable to false
        builder.setNegativeButton(R.string.camera_alert_no) { dialog, _ ->
            dialog.dismiss()
            finish()
        }
        builder.setPositiveButton(getString(R.string.camera_alert_yes)) { dialog, _ ->
            dialog.dismiss()
            startCamera()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun preprocessImage(bitmap: Bitmap): ByteBuffer {
        val inputShape = tflite.getInputTensor(0).shape()
        val inputSize = inputShape[1]

        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, true)
        val inputBuffer = ByteBuffer.allocateDirect(3 * inputSize * inputSize * 4)
            .order(ByteOrder.nativeOrder())

        val pixels = IntArray(inputSize * inputSize)
        resizedBitmap.getPixels(pixels, 0, inputSize, 0, 0, inputSize, inputSize)

        for (pixelValue in pixels) {
            inputBuffer.putFloat(((pixelValue shr 16 and 0xFF) - 127) / 128.0f)
            inputBuffer.putFloat(((pixelValue shr 8 and 0xFF) - 127) / 128.0f)
            inputBuffer.putFloat(((pixelValue and 0xFF) - 127) / 128.0f)
        }

        inputBuffer.rewind()
        return inputBuffer
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera()
            } else {
                Toast.makeText(
                    this,
                    "Camera permission required",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun fetchDataFromDatabase() {
        firestore.collection("batikjenis").get().addOnSuccessListener { snapshot ->
            val tempList = mutableListOf<Batik>()
            for (document in snapshot) {
                val batik = document.toObject(Batik::class.java)
                tempList.add(batik)
            }
            batikList.clear()
            batikList.addAll(tempList)
        }.addOnFailureListener { exception ->
            // Handle the failure here
        }
    }

    override fun onBackPressed() {
        // Call the finish() method to close the activity
        val homeFragmentIntent = Intent(this, HomeFragment::class.java)
        startActivity(homeFragmentIntent)
        finish()
    }
}