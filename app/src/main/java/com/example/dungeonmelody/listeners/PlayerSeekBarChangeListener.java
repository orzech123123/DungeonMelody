package com.example.dungeonmelody.listeners;

import android.widget.SeekBar;

import com.google.android.youtube.player.YouTubePlayer;

public class PlayerSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

    private final YouTubePlayer _youTubePlayer;

    public PlayerSeekBarChangeListener(YouTubePlayer youTubePlayer){

        _youTubePlayer = youTubePlayer;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (!fromUser) {
            return;
        }

        _youTubePlayer.seekToMillis(progress);
        seekBar.setProgress(progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }
}