package com.trendingrepos.trimvideo

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler
import com.github.hiteshsondhi88.libffmpeg.FFmpeg
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException
import com.trendingrepos.trimvideo.databinding.FragmentSelectVideoBinding
import com.trendingrepos.trimvideo.databinding.FragmentSelectVideoBinding.*
import com.trendingrepos.trimvideo.databinding.FragmentTrimVideoBinding
import java.io.File


class TrimVideoFragment : Fragment() {

    private lateinit var binding: FragmentTrimVideoBinding
    private lateinit var viewModel: TrimVideoViewModel
    private val args: TrimVideoFragmentArgs by navArgs()
    private var duration : Int = 0
    private var dest : File? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
         binding = FragmentTrimVideoBinding.inflate(inflater, container, false)
        val uri = Uri.parse(args.uriString)
        binding.videoView.setVideoURI(uri)
        binding.videoView.requestFocus()
        binding.videoView.start()
        binding.videoView.setOnPreparedListener {
            duration = it.duration
        }
        binding.trimBt.setOnClickListener{executeCutVideoCommand(1, 1)}
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(TrimVideoViewModel::class.java)
    }

    private fun executeCutVideoCommand(startMs: Int, endMs: Int) {
        val dir = File(requireContext().filesDir, "cut_videos")
        if(!dir.exists()){
            dir.mkdir()
        }

        val filePrefix = "cut_video"
        val fileExtn = ".mp4"

        dest = File(dir, filePrefix + fileExtn)

        var fileNo = 0
        while (dest!!.exists()) {
            fileNo++
            dest = File(dir, filePrefix + fileNo + fileExtn)
        }
        val filePath = dest!!.absolutePath
        val complexCommand = arrayOf( "-ss", "" + 10, "-y", "-i", args.path, "-t",
            "" + (duration-10), "-vcodec", "mpeg4", "-b:v", "2097152", "-b:a", "48000",
            "-ac", "2", "-ar", "22050", filePath
        )
        execFFmpegBinary(complexCommand)
    }

    private fun execFFmpegBinary(command: Array<String>) {
        val ffmpeg = FFmpeg.getInstance(requireContext())
        ffmpeg.loadBinary(object : LoadBinaryResponseHandler() {
            override fun onFailure() {}
            override fun onSuccess() {
                try {
                    ffmpeg.execute(command, object : ExecuteBinaryResponseHandler() {
                        override fun onFailure(s: String) {}
                        override fun onSuccess(s: String) {
                            navigateToVideoPreview()
                        }
                        override fun onProgress(s: String) {
                           Log.d("Progress", s)
                        }
                        override fun onStart() {}

                        override fun onFinish() {}
                    })
                } catch (e: FFmpegCommandAlreadyRunningException) {
                }
            }
        })
    }

    private fun navigateToVideoPreview() {
        val direction =
            TrimVideoFragmentDirections.actionTrimVideoFragmentToPlayVideoFragment(Uri.fromFile(dest).toString())
        binding.root.findNavController().navigate(direction)
    }
}
