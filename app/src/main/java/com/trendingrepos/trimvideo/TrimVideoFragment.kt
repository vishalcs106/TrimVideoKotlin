package com.trendingrepos.trimvideo

import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler
import com.github.hiteshsondhi88.libffmpeg.FFmpeg
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException
import com.trendingrepos.trimvideo.databinding.FragmentTrimVideoBinding
import java.io.File


class TrimVideoFragment : Fragment() {

    companion object {
        fun newInstance() = TrimVideoFragment()
    }

    private lateinit var viewModel: TrimVideoViewModel
    private val args: TrimVideoFragmentArgs by navArgs()

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
        binding.trimBt.setOnClickListener{executeCutVideoCommand(1, 1)}
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(TrimVideoViewModel::class.java)
    }


    private fun executeCutVideoCommand(startMs: Int, endMs: Int) {
        val dir = File(requireContext().getFilesDir(), "cut_videos")
        if(!dir.exists()){
            dir.mkdir();
        }
        val filePrefix = "cut_video"
        val fileExtn = ".mp4"

        var dest = File(dir, filePrefix + fileExtn)
        var fileNo = 0
        while (dest.exists()) {
            fileNo++
            dest = File(dir, filePrefix + fileNo + fileExtn)
        }
//        Log.d(MainActivity.TAG, "startTrim: src: $yourRealPath")
//        Log.d(MainActivity.TAG, "startTrim: dest: " + dest.absolutePath)
//        Log.d(MainActivity.TAG, "startTrim: startMs: $startMs")
//        Log.d(MainActivity.TAG, "startTrim: endMs: $endMs")
        val filePath = dest.absolutePath
        //String[] complexCommand = {"-i", yourRealPath, "-ss", "" + startMs / 1000, "-t", "" + endMs / 1000, dest.getAbsolutePath()};
        val complexCommand = arrayOf( "-ss", "" + startMs / 1000, "-y", "-i", args.path, "-t",
            "" + (endMs - startMs) / 1000, "-vcodec", "mpeg4", "-b:v", "2097152", "-b:a", "48000",
            "-ac", "2", "-ar", "22050", filePath
        )
        execFFmpegBinary(complexCommand)
    }

    private fun execFFmpegBinary(command: Array<String>) {
        val ffmpeg = FFmpeg.getInstance(requireContext())
        try {
            ffmpeg.execute(command, object : ExecuteBinaryResponseHandler() {
                override fun onFailure(s: String) {
                    System.out.println("")
                }
                override fun onSuccess(s: String) {
                    System.out.println("")
                }
                override fun onProgress(s: String) {
                    System.out.println("")
                }
                override fun onStart() {
                    System.out.println("")
                }

                override fun onFinish() {}
            })
        } catch (e: FFmpegCommandAlreadyRunningException) {
        }
    }

}
