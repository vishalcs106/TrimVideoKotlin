package com.trendingrepos.trimvideo

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.trendingrepos.trimvideo.databinding.FragmentSelectVideoBinding


class SelectVideoFragment : Fragment() {

    private lateinit var binding: FragmentSelectVideoBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSelectVideoBinding.inflate(inflater, container, false)
        binding.selectVideo.setOnClickListener {
            if (checkPermissionForVideo(PERMISSIONS_REQUEST_CODE)) {
                videoChooser()
            }
        }
        return binding.root
    }

    private fun videoChooser() {
        val intent = Intent()
        intent.type = "video/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, "Select Video"), VIDEO_CHOOSER_REQUEST_CODE
        )
    }

    @SuppressLint("NewApi")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == VIDEO_CHOOSER_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                val uri = data.data
                val uriString = uri?.let { getFilePath(it, requireContext()) }
                uriString?.let { it -> navigateToVideoView(it, uri.toString()) }
            }
        }
    }

    private fun navigateToVideoView(path: String, uriString: String) {
        val direction =
            SelectVideoFragmentDirections.actionSelectVideoFragmentToTrimVideoFragment(
                uriString,
                path
            )
        binding.root.findNavController().navigate(direction)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    videoChooser()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    companion object {
        const val PERMISSIONS_REQUEST_CODE: Int = 111
        const val VIDEO_CHOOSER_REQUEST_CODE: Int = 101
    }
}
