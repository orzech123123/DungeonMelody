package com.example.dungeonmelody.listeners;

import android.widget.SeekBar;

import com.google.android.youtube.player.YouTubePlayer;

public class PlayerStateChangeListener implements YouTubePlayer.PlayerStateChangeListener {

    private final SeekBar _seekBar;
    private final YouTubePlayer _youTubePlayer;

    public PlayerStateChangeListener(SeekBar seekBar, YouTubePlayer youTubePlayer){
        _seekBar = seekBar;

        this._youTubePlayer = youTubePlayer;
    }

    @Override
    public void onLoading() {

    }

    @Override
    public void onLoaded(String s) {
        _youTubePlayer.play();
    }

    @Override
    public void onAdStarted() {

    }

    @Override
    public void onVideoStarted() {
        _youTubePlayer.play();
        int duration = _youTubePlayer.getDurationMillis();
        _seekBar.setMax(duration);
    }

    @Override
    public void onVideoEnded() {

    }

    @Override
    public void onError(YouTubePlayer.ErrorReason errorReason) {

    }
}
