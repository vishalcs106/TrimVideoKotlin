package com.trendingrepos.trimvideo

import android.net.Uri
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.navArgs
import com.trendingrepos.trimvideo.databinding.FragmentTrimVideoBinding


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
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(TrimVideoViewModel::class.java)
    }
}
