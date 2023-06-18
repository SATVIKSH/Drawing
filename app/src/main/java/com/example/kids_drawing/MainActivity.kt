package com.example.kids_drawing

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.app.DownloadManager.Request
import android.app.Instrumentation.ActivityResult
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import java.nio.file.attribute.AclEntry.Builder

class MainActivity : AppCompatActivity() {
    private var drawingView: DrawingView? = null
    private var mCurrentColor:ImageButton?=null
    val requestPermission:ActivityResultLauncher<Array<String>> =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
                permissions->
                permissions.entries.forEach() {
                    val permissionName = it.key
                    val isGranted = it.value
                    if (isGranted) {
                        Toast.makeText(this, "PERMISSION GRANTED", Toast.LENGTH_LONG).show()
                        val pickIntent=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        openGalleryLauncher.launch(pickIntent)
                    } else {
                        if (permissionName == Manifest.permission.READ_EXTERNAL_STORAGE) {
                            Toast.makeText(this, "STORAGE PERMISSION DENIED", Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                }
            }

     val openGalleryLauncher:ActivityResultLauncher<Intent> =
             registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
                 result->
                 if(result.resultCode== RESULT_OK && result.data!=null)
                 {
                     val imageBackground:ImageView=findViewById(R.id.iv_background)
                     imageBackground.setImageURI(result.data?.data)
                 }
             }





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        drawingView = findViewById(R.id.drawing_view)
        val brushBtn=findViewById<ImageButton>(R.id.ib_brush)
        brushBtn.setOnClickListener {
            showBrushSizeChooserDialog()
        }
        val linearLayoutColorPicker=findViewById<LinearLayout>(R.id.ll_paint_colors)
       mCurrentColor=linearLayoutColorPicker[1] as ImageButton
        mCurrentColor!!.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.pallete_pressed))
        val ib_Gallery:ImageButton=findViewById(R.id.ib_gallery)
        ib_Gallery.setOnClickListener {
            requestStoragePermission()
        }
        val ib_Undo=findViewById<ImageButton>(R.id.ib_Undo)
             ib_Undo.setOnClickListener {
                drawingView?.UndoPath()

            }

    }
    fun setColorPicker(view: View)
    {
        val mColor=view as ImageButton
        if(mColor!=mCurrentColor)
        {
            drawingView?.colorChange(mColor.tag.toString())
            mColor.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.pallete_pressed))
            mCurrentColor!!.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.pallete_normal))
        }
        mCurrentColor=view
    }
    private fun requestStoragePermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(
                this,Manifest.permission.READ_EXTERNAL_STORAGE )
        ){

            showRationaleDialog("KIDS DRAWING APP","Kids Drawing App needs to access your  External Storage")

        }
        else{
            requestPermission.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))
        }
    }

    private fun showBrushSizeChooserDialog() {
    val brushDialog= Dialog(this)

           brushDialog.setContentView(R.layout.dialog_brush_size)
        brushDialog.setTitle("Brush Size:")
        val smallbtn=brushDialog.findViewById<ImageButton>(R.id.ib_small_brush)
        smallbtn.setOnClickListener {
            drawingView?.setSizeForBrush(10.toFloat())
            brushDialog.dismiss()
        }
        val largebtn=brushDialog.findViewById<ImageButton>(R.id.ib_large_brush)
        largebtn.setOnClickListener {
            drawingView?.setSizeForBrush(30.toFloat())
            brushDialog.dismiss()
        }
        val mediumbtn=brushDialog.findViewById<ImageButton>(R.id.ib_medium_brush)
        mediumbtn.setOnClickListener {
            drawingView?.setSizeForBrush(20.toFloat())
            brushDialog.dismiss()
        }
        brushDialog.show()
    }
    private fun showRationaleDialog(title:String,message: String)
    {
        val builder=AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("CANCEL"){
            dialog,_->dialog.dismiss()
        }
        builder.create().show()
    }
}