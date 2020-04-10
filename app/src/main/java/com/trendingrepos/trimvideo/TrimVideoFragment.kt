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
import com.trendingrepos.trimvideo.databinding.FragmentTrimVideoBinding
import java.io.File


class TrimVideoFragment : Fragment() {

    companion object {
        fun newInstance() = TrimVideoFragment()
    }

    private lateinit var viewModel: TrimVideoViewModel
    private val args: TrimVideoFragmentArgs by navArgs()
    private var duration : Int = 0
    private var destString : String = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentTrimVideoBinding>(
            inflater, R.layout.fragment_trim_video, container, false)
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

//        val ffmpegDir = File(requireContext().filesDir, "ffmpeg")
//        if(!ffmpegDir.exists()){
//            ffmpegDir.mkdir()
//        }

        val filePrefix = "cut_video"
        val fileExtn = ".mp4"

        var dest = File(dir, filePrefix + fileExtn)
        destString = dest.absolutePath
        var fileNo = 0
        while (dest.exists()) {
            fileNo++
            dest = File(dir, filePrefix + fileNo + fileExtn)
        }
        Log.d("MainActivity.TAG", "startTrim: src: ${args.path}")
        Log.d("MainActivity.TAG", "startTrim: dest: " + dest.absolutePath)
        Log.d("MainActivity.TAG", "startTrim: startMs: $startMs")
        Log.d("MainActivity.TAG", "startTrim: endMs: $endMs")
        val filePath = dest.absolutePath
        val complexCommand = arrayOf( "-ss", "" + 10, "-y", "-i", args.path, "-t",
            "" + (duration-10), "-vcodec", "mpeg4", "-b:v", "2097152", "-b:a", "48000",
            "-ac", "2", "-ar", "22050", filePath
        )
        execFFmpegBinary(complexCommand)
    }

    private fun execFFmpegBinary(command: Array<String>) {
        val ffmpeg = FFmpeg.getInstance(requireContext())

        ffmpeg.loadBinary(object : LoadBinaryResponseHandler() {
            override fun onFailure() {
                System.out.println("")
            }

            override fun onSuccess() {
                try {
                    ffmpeg.execute(command, object : ExecuteBinaryResponseHandler() {
                        override fun onFailure(s: String) {
                            System.out.println("")
                        }
                        override fun onSuccess(s: String) {
                            navigateToVideoPreview()
                        }
                        override fun onProgress(s: String) {
                           Log.d("Progress", s)
                        }
                        override fun onStart() {
                            System.out.println("")
                        }

                        override fun onFinish() {
                            System.out.println("")
                        }
                    })
                } catch (e: FFmpegCommandAlreadyRunningException) {
                }
            }
        })




    }

    private fun navigateToVideoPreview() {
        val direction =
            TrimVideoFragmentDirections.actionTrimVideoFragmentToPlayVideoFragment(uriString)
        binding.root.findNavController().navigate(direction)
    }

}
