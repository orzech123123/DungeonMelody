package com.example.dungeonmelody;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

public class MainActivity extends YouTubeBaseActivity
{
    private YouTubePlayer _youTubePlayer;
    private YouTubePlayerView _youTubePlayerView;
    private Button _playButton;
    private Button _xButton;
    private SeekBar _seekBar;
    YouTubePlayer.OnInitializedListener _youtubePlayerOnInit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _playButton = findViewById(R.id.playButton);
        _xButton = findViewById(R.id.xButton);
        _youTubePlayerView = findViewById(R.id.youtubePlayer);
        _seekBar = findViewById(R.id.seekBar2);

        _youtubePlayerOnInit = new YouTubePlayer.OnInitializedListener(){

            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                _youTubePlayer = youTubePlayer;
//                youTubePlayer.loadVideo("agCof0o43c8");
//                int duration = youTubePlayer.getDurationMillis();
//                _seekBar.setMax(duration);

                _youTubePlayer.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {
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
                });

                youTubePlayer.cueVideo("XI8l7rThpn8");
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        };

        _playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _youTubePlayerView.initialize(YouTubeConfig.GetApiKey(), _youtubePlayerOnInit);
            }
        });

        _xButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _youTubePlayer.seekToMillis(12000);
            }
        });

        _seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(_youTubePlayer != null)
                {
                    _youTubePlayer.seekToMillis(progress);
                }
                seekBar.setProgress(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }
}
