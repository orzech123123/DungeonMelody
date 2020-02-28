package com.example.dungeonmelody;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
    YouTubePlayer.OnInitializedListener _youtubePlayerOnInit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _playButton = findViewById(R.id.playButton);
        _xButton = findViewById(R.id.xButton);
        _youTubePlayerView = findViewById(R.id.youtubePlayer);

        _youtubePlayerOnInit = new YouTubePlayer.OnInitializedListener(){

            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                youTubePlayer.loadVideo("agCof0o43c8");
                _youTubePlayer = youTubePlayer;
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
    }
}
