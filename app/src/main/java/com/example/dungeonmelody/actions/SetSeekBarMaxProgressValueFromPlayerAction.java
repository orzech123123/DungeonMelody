package com.example.dungeonmelody.actions;

import android.widget.SeekBar;

import com.google.android.youtube.player.YouTubePlayer;

public class SetSeekBarMaxProgressValueFromPlayerAction implements YouTubePlayer.PlayerStateChangeListener {

    private final SeekBar _seekBar;
    private final YouTubePlayer _youTubePlayer;

    public SetSeekBarMaxProgressValueFromPlayerAction(SeekBar seekBar, YouTubePlayer youTubePlayer){
        _seekBar = seekBar;
        _youTubePlayer = youTubePlayer;
    }

    @Override
    public void onLoading() {

    }

    @Override
    public void onLoaded(String s) {
    }

    @Override
    public void onAdStarted() {

    }

    @Override
    public void onVideoStarted() {
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
