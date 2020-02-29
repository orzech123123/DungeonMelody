package com.example.dungeonmelody.actions;


import com.google.android.youtube.player.YouTubePlayer;

public class ConfigurePlayerAction implements YouTubePlayer.PlayerStateChangeListener {

    private final YouTubePlayer _youTubePlayer;

    public ConfigurePlayerAction(YouTubePlayer youTubePlayer){
        _youTubePlayer = youTubePlayer;
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
    }

    @Override
    public void onVideoEnded() {

    }

    @Override
    public void onError(YouTubePlayer.ErrorReason errorReason) {

    }
}
