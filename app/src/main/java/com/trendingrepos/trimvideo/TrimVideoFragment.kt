package com.trendingrepos.trimvideo

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler
import com.github.hiteshsondhi88.libffmpeg.FFmpeg
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException
import com.trendingrepos.trimvideo.databinding.FragmentTrimVideoBinding
import java.io.File


class TrimVideoFragment : Fragment() {

    private lateinit var binding: FragmentTrimVideoBinding
    private val args: TrimVideoFragmentArgs by navArgs()
    private var duration: Int = 0
    private var dest: File? = null
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
        binding.trimBt.setOnClickListener {
            try {
                executeCutVideoCommand(
                    binding.trimStartEt.text.toString().toInt(),
                    binding.trimEndEt.text.toString().toInt()
                )
            } catch (e: NumberFormatException) {
               showToast(getString(R.string.invalidInput))
            }
        }
        return binding.root
    }

    private fun executeCutVideoCommand(startS: Int, endS: Int) {
        val dir = File(requireContext().filesDir, "cut_videos")
        if (!dir.exists()) {
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
        val complexCommand = arrayOf(
            "-ss", "" + startS, "-y", "-i", args.path, "-t",
            "" + (duration - endS), "-vcodec", "mpeg4", "-b:v", "2097152", "-b:a", "48000",
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

                        override fun onStart() {
                            binding.trimBt.visibility = View.GONE
                            binding.progressbar.visibility = View.VISIBLE
                        }

                        override fun onFinish() {
                            binding.trimBt.visibility = View.VISIBLE
                            binding.progressbar.visibility = View.INVISIBLE
                            showToast(getString(R.string.finished))
                        }
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
