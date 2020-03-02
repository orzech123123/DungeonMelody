package com.example.dungeonmelody.activities;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.dungeonmelody.R;
import com.example.dungeonmelody.backgroundTasks.UpdateSeekBarProgressTask;
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

public class CreateMelodyComposeActivity extends YouTubeBaseActivity
{
    private YouTubePlayer _youTubePlayer;
    private YouTubePlayerView _youTubePlayerView;
    private Button _markerButton;
    private Button _startButton;
    private Button _breakButton;
    private SeekBar _seekBar;
    private UpdateSeekBarProgressTask _seekBarRefreshTask;
    private MelodyComposerService _melodyComposerService;

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
        setContentView(R.layout.activity_create_melody_compose);

        _youTubePlayerView = findViewById(R.id.youtubePlayer);
        _markerButton = findViewById(R.id.markerButton);
        _startButton = findViewById(R.id.startButton);
        _breakButton = findViewById(R.id.breakButton);
        _seekBar = findViewById(R.id.seekBar);

        _startButton.setOnClickListener(GetStartButtonOnClickListener());
        _markerButton.setOnClickListener(GetMarkerButtonOnClickListener());
        _breakButton.setOnClickListener(GetBreakButtonOnClickListener());

        _seekBar.setEnabled(false);
        _markerButton.setEnabled(false);
        _breakButton.setEnabled(false);

        _melodyComposerService = new MelodyComposerService(CreateMelodyData.TabsText);

        _youTubePlayerView.initialize(YouTubeConfig.GetApiKey(), GetPlayerOnInitListener());

        UpdateTabsOnView();
    }

    private void UpdateTabsOnView() {
        //TODO to bedzie zastapione innym rysowaniem/wyswietlaniem
       String text = "";
        for (TabPart tabPart: _melodyComposerService.GetTabParts()) {
            String tabPartText = tabPart.Tabs + "<br />";
            String color = "";
            if(tabPart.ProgressEnd != null)
            {
                color = "green";
            }
            else if(tabPart.ProgressStart != null)
            {
                color = "blue";
            }

            text = text + "<font color='"+color+"'>" + tabPartText + "</font>";
        }
        ((TextView) findViewById(R.id.textView)).setText(Html.fromHtml(text), TextView.BufferType.SPANNABLE);
    }

    private View.OnClickListener GetStartButtonOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _youTubePlayer.play();
                _seekBar.setEnabled(true);
                _markerButton.setEnabled(true);
                _breakButton.setEnabled(true);
            }
        };
    }

    private View.OnClickListener GetBreakButtonOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _melodyComposerService.SetBreak(_youTubePlayer.getCurrentTimeMillis());
                UpdateTabsOnView();
            }
        };
    }

    private View.OnClickListener GetMarkerButtonOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _melodyComposerService.SetMarker(_youTubePlayer.getCurrentTimeMillis());
                UpdateTabsOnView();
            }
        };
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

                _seekBarRefreshTask = new UpdateSeekBarProgressTask(_seekBar, _youTubePlayer);
                _seekBarRefreshTask.execute();
                _seekBar.setOnSeekBarChangeListener(new MultipleOnSeekBarChangeListener(Arrays.asList(
                        new UpdatePlayerProgressOnSeekBarChangeAction(_youTubePlayer),
                        _melodyComposerService.GetClearTabProgressesOnProgresRewindBackListener(),
                        _melodyComposerService.GetFireActionOnRewindBackListener(() -> UpdateTabsOnView())
                )));

                _youTubePlayer.cueVideo(CreateMelodyData.VideoUrl);
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        };
    }
}
