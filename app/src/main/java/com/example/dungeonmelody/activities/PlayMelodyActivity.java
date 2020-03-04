package com.example.dungeonmelody.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dungeonmelody.R;
import com.example.dungeonmelody.actions.RunOnSeekProgressRewindBackAction;
import com.example.dungeonmelody.backgroundTasks.RunAsyncTask;
import com.example.dungeonmelody.configuration.YouTubeConfig;
import com.example.dungeonmelody.actions.SetSeekBarMaxProgressValueFromPlayerAction;
import com.example.dungeonmelody.actions.UpdatePlayerProgressOnSeekBarChangeAction;
import com.example.dungeonmelody.data.CreateMelodyData;
import com.example.dungeonmelody.data.TabPart;
import com.example.dungeonmelody.services.MelodyComposerService;
import com.example.dungeonmelody.utilities.MultipleOnSeekBarChangeListener;
import com.example.dungeonmelody.utilities.MultiplePlayerStateChangeListener;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import java.util.Arrays;

public class PlayMelodyActivity extends YouTubeBaseActivity
{
    private YouTubePlayer _youTubePlayer;
    private YouTubePlayerView _youTubePlayerView;
    private SeekBar _seekBar;
    private RunAsyncTask _uiRefreshTask;

    @Override
    protected void onDestroy() {
        _uiRefreshTask.cancel(true);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_melody);

        _youTubePlayerView = findViewById(R.id.youtubePlayer);
        _seekBar = findViewById(R.id.seekBar);

        ((TextView) findViewById(R.id.textView)).setMovementMethod(new ScrollingMovementMethod());

        _youTubePlayerView.initialize(YouTubeConfig.GetApiKey(), GetPlayerOnInitListener());

        UpdateTabsOnView();

        _uiRefreshTask = new RunAsyncTask(() -> {
            UpdateSeekBarProgress();
        }, true);
        _uiRefreshTask.execute();
        _seekBar.setEnabled(false);
    }

    private void UpdateTabsOnView() {
        /*//TODO to bedzie zastapione innym rysowaniem/wyswietlaniem
        String text = "";
        for (TabPart tabPart: _melodyComposerService.GetTabParts()) {
            String tabPartText = tabPart.Tabs + "<br />";
            String color = "";
            if(tabPart.ProgressEnd != null)
            {
                color = "blue";
            }
            else if(tabPart.ProgressStart != null)
            {
                color = "green";
            }

            text = text + "<font color='"+color+"'>" + tabPartText + "</font>";
        }
        ((TextView) findViewById(R.id.textView)).setText(Html.fromHtml(text), TextView.BufferType.SPANNABLE);*/
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
                        new SetSeekBarMaxProgressValueFromPlayerAction(_seekBar, _youTubePlayer)
                )));

                _seekBar.setOnSeekBarChangeListener(new MultipleOnSeekBarChangeListener(Arrays.asList(
                        new UpdatePlayerProgressOnSeekBarChangeAction(_youTubePlayer),
                        new RunOnSeekProgressRewindBackAction((progress) -> UpdateTabsOnView())
                )));

                _youTubePlayer.cueVideo(/*CreateMelodyData.VideoUrl*/"j-fWDrZSiZs");
                _seekBar.setEnabled(true);
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        };
    }

    private void UpdateSeekBarProgress(){
        if(_seekBar == null || _youTubePlayer == null)
        {
            return;
        }

        runOnUiThread(() -> {
            try{
                _seekBar.setProgress(_youTubePlayer.getCurrentTimeMillis());
            }
            catch (IllegalStateException e){}//youtube released
        });

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
