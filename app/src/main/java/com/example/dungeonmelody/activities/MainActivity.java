package com.example.dungeonmelody.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import com.example.dungeonmelody.R;
import com.example.dungeonmelody.backgroundTasks.PlayerSeekBarRefreshTask;
import com.example.dungeonmelody.configuration.YouTubeConfig;
import com.example.dungeonmelody.listeners.PlayerSeekBarChangeListener;
import com.example.dungeonmelody.listeners.PlayerStateChangeListener;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

public class MainActivity extends YouTubeBaseActivity
{
    private YouTubePlayer _youTubePlayer;
    private YouTubePlayerView _youTubePlayerView;
    private Button _playButton;
    private Button _closeButton;
    private SeekBar _seekBar;
    private PlayerSeekBarRefreshTask _seekBarRefreshTask;

    @Override
    protected void onDestroy() {
        _seekBarRefreshTask.cancel(true);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _playButton = findViewById(R.id.playButton);
        _closeButton = findViewById(R.id.closeButton);
        _youTubePlayerView = findViewById(R.id.youtubePlayer);
        _seekBar = findViewById(R.id.seekBar);

        _playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _youTubePlayerView.initialize(YouTubeConfig.GetApiKey(), GetPlayerOnInitListener());
            }
        });

        _closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private YouTubePlayer.OnInitializedListener GetPlayerOnInitListener(){
        return new YouTubePlayer.OnInitializedListener(){
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                if(_youTubePlayer != null)
                {
                    return;
                }

                _youTubePlayer = youTubePlayer;

                _seekBarRefreshTask = new PlayerSeekBarRefreshTask(_seekBar, _youTubePlayer);
                _seekBarRefreshTask.execute();

                PlayerStateChangeListener playerStateChangeListener = new PlayerStateChangeListener(_seekBar, _youTubePlayer);
                _youTubePlayer.setPlayerStateChangeListener(playerStateChangeListener);

                _seekBar.setOnSeekBarChangeListener(new PlayerSeekBarChangeListener(_youTubePlayer));

                youTubePlayer.cueVideo("XI8l7rThpn8");
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        };
    }
}
