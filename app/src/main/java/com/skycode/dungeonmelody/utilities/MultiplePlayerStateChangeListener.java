package com.skycode.dungeonmelody.utilities;

import com.google.android.youtube.player.YouTubePlayer;
import java.util.List;

public class MultiplePlayerStateChangeListener implements YouTubePlayer.PlayerStateChangeListener {

    private final List<YouTubePlayer.PlayerStateChangeListener> _listeners;

    public MultiplePlayerStateChangeListener(List<YouTubePlayer.PlayerStateChangeListener> listeners){

        _listeners = listeners;
    }

    @Override
    public void onLoading() {

    }

    @Override
    public void onLoaded(String s) {
        for (YouTubePlayer.PlayerStateChangeListener listener: _listeners) {
            listener.onLoaded(s);
        }
    }

    @Override
    public void onAdStarted() {

    }

    @Override
    public void onVideoStarted() {
        for (YouTubePlayer.PlayerStateChangeListener listener: _listeners) {
            listener.onVideoStarted();
        }
    }

    @Override
    public void onVideoEnded() {

    }

    @Override
    public void onError(YouTubePlayer.ErrorReason errorReason) {

    }
}
