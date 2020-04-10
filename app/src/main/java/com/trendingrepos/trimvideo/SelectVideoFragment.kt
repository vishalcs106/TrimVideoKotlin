package com.trendingrepos.trimvideo

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.trendingrepos.trimvideo.databinding.FragmentSelectVideoBinding


class SelectVideoFragment : Fragment() {

    private lateinit var binding: FragmentSelectVideoBinding

    companion object {
        fun newInstance() = SelectVideoFragment()
    }

    private lateinit var viewModel: SelectVideoViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSelectVideoBinding.inflate(inflater, container, false)
        binding.selectVideo.setOnClickListener{
            checkPermissionForImage()
        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //viewModel = ViewModelProviders.of(this).get(SelectVideoViewModel::class.java)
    }

    private fun checkPermissionForImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_DENIED)
                && (checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_DENIED)) {
                val permission = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                val permissionCoarse = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

                requestPermissions(permission, 111)
                requestPermissions(permissionCoarse, 111)
            } else {
                videoChooser()
            }
        }
    }

    private fun videoChooser(){
        val intent = Intent()
        intent.type = "video/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, "Select Video"), 101
        )
    }

     @SuppressLint("NewApi")
     override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
         super.onActivityResult(requestCode, resultCode, data)
         if (requestCode == 101 && resultCode == RESULT_OK) {
            if (data != null) {
                val uri = data!!.data
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                val selection = "_id=?"
                val selectionArgs = arrayOf(
                    split[1]
                )
                val uriString = getFilePath(contentUri, selection, selectionArgs, requireContext())
                uri?.let { uriString?.let { it1 -> navigateToVideoView(it1, it.toString()) } }
            }
        }
    }




    private fun navigateToVideoView( path : String, uriString : String) {
        val direction =
            SelectVideoFragmentDirections.actionViewPagerFragmentToPlantDetailFragment(uriString, path)
        binding.root.findNavController().navigate(direction)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray) {
        when (requestCode) {
            111 -> {
                if (!grantResults.isEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    videoChooser()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
