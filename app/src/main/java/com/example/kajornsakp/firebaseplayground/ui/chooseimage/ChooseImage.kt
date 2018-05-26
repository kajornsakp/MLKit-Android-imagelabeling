package com.example.kajornsakp.firebaseplayground.ui.chooseimage

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.example.kajornsakp.firebaseplayground.R
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import kotlinx.android.synthetic.main.choose_image_fragment.*
import java.io.IOException

class ChooseImage : Fragment() {

    companion object {
        fun newInstance() = ChooseImage()
        const val CAMERA = 0;
        const val LIBRARY = 1;
        const val CAMERA_PERMISSION = android.Manifest.permission.CAMERA
        const val READ_EXTERNAL_STORAGE_PERMISSION = android.Manifest.permission.READ_EXTERNAL_STORAGE
        const val WRITE_EXTERNAL_STORAGE_PERMISSION = android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    }

    private lateinit var viewModel: ChooseImageViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.choose_image_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ChooseImageViewModel::class.java)

        camera_btn.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(CAMERA_PERMISSION), 201)
            } else {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, CAMERA)
            }

        }
        library_btn.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(READ_EXTERNAL_STORAGE_PERMISSION), 202)
            } else {
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_PICK
                startActivityForResult(intent, LIBRARY)
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            Log.d("DATA", data.toString())
            onImageSelected(data!!.data)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            201 -> openCamera()
            202 -> openLibrary()
        }
    }

    private fun openLibrary() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_PICK
        startActivityForResult(intent, LIBRARY)
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA)
    }

    private fun onImageSelected(uri: Uri) {
        Log.d("IMAGE", uri.toString())
        var image: FirebaseVisionImage
        try {
            image = FirebaseVisionImage.fromFilePath(context!!, uri)
            val detector = FirebaseVision.getInstance().getVisionLabelDetector()
            detector.detectInImage(image)
                    .addOnSuccessListener {
                        var output = ""
                        it.forEach {
                            output += it.label + " " + it.confidence + "\n"
                        }
//                        Toast.makeText(context!!,output,Toast.LENGTH_SHORT).show()
                        showResultScreen(uri, output)
                    }.addOnFailureListener {
                        Log.e("ERROR", it.localizedMessage)
                    }

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun showResultScreen(uri: Uri, label: String) {
        val direction = ChooseImageDirections.action_chooseImageFragment_to_resultFragment(uri.toString(), label)
        Navigation.findNavController(activity!!, R.id.my_nav_host_fragment).navigate(direction)
    }
}
