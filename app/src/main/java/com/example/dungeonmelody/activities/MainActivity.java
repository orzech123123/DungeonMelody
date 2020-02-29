package com.example.dungeonmelody.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import com.example.dungeonmelody.R;
import com.example.dungeonmelody.backgroundTasks.UpdateSeekBarProgressTask;
import com.example.dungeonmelody.configuration.YouTubeConfig;
import com.example.dungeonmelody.actions.SetSeekBarMaxProgressValueFromPlayerAction;
import com.example.dungeonmelody.actions.UpdatePlayerProgressOnSeekBarChangeAction;
import com.example.dungeonmelody.utilities.MultipleOnSeekBarChangeListener;
import com.example.dungeonmelody.utilities.MultiplePlayerStateChangeListener;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import java.util.Arrays;

public class MainActivity extends YouTubeBaseActivity
{
    private YouTubePlayer _youTubePlayer;
    private YouTubePlayerView _youTubePlayerView;
    private Button _playButton;
    private Button _closeButton;
    private SeekBar _seekBar;
    private UpdateSeekBarProgressTask _seekBarRefreshTask;

    @Override
    protected void onDestroy() {
        if(_seekBarRefreshTask != null)
        {
            _seekBarRefreshTask.cancel(true);
        }
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _youTubePlayerView = findViewById(R.id.youtubePlayer);
        _playButton = findViewById(R.id.playButton);
        _closeButton = findViewById(R.id.closeButton);
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

                _youTubePlayer.setPlayerStateChangeListener(new MultiplePlayerStateChangeListener(Arrays.asList(
                        (YouTubePlayer.PlayerStateChangeListener) new SetSeekBarMaxProgressValueFromPlayerAction(_seekBar, _youTubePlayer)
                )));

                _seekBarRefreshTask = new UpdateSeekBarProgressTask(_seekBar, _youTubePlayer);
                _seekBarRefreshTask.execute();
                _seekBar.setOnSeekBarChangeListener(new MultipleOnSeekBarChangeListener(Arrays.asList(
                        (SeekBar.OnSeekBarChangeListener)new UpdatePlayerProgressOnSeekBarChangeAction(_youTubePlayer)
                )));

                _youTubePlayer.loadVideo("XI8l7rThpn8");
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        };
    }
}
