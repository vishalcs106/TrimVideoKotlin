package com.trendingrepos.trimvideo

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import com.trendingrepos.trimvideo.databinding.FragmentPlayVideoBinding


class PlayVideoFragment : Fragment() {

    private lateinit var viewModel: PlayVideoViewModel
    private lateinit var binding: FragmentPlayVideoBinding
    private val args: PlayVideoFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlayVideoBinding.inflate(inflater, container, false)
        binding.previewVv.setVideoURI(Uri.parse(args.uriString))
        binding.previewVv.start()
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(PlayVideoViewModel::class.java)
        // TODO: Use the ViewModel
    }


}
