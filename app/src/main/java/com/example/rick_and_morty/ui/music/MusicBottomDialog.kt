package com.example.rick_and_morty.ui.music

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.rick_and_morty.databinding.MusicBottomDialogBinding
import com.example.rick_and_morty.exoplayer.MusicService
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class MusicBottomDialog() : BottomSheetDialogFragment() {
    private lateinit var binding: MusicBottomDialogBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = MusicBottomDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.exoPlayer.player = MusicService.player
        binding.exoPlayer.showController()
        binding.exoPlayer.controllerShowTimeoutMs = -1
    }
}