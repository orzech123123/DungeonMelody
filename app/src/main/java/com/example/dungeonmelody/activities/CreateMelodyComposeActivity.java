package com.example.dungeonmelody.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.dungeonmelody.R;
import com.example.dungeonmelody.actions.RunOnSeekProgressRewindBackAction;
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

import java.io.IOException;
import java.util.Arrays;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CreateMelodyComposeActivity extends YouTubeBaseActivity
{
    private YouTubePlayer _youTubePlayer;
    private YouTubePlayerView _youTubePlayerView;
    private Button _markerButton;
    private Button _breakButton;
    private Button _saveButton;
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
        _breakButton = findViewById(R.id.breakButton);
        _saveButton = findViewById(R.id.saveButton);
        _seekBar = findViewById(R.id.seekBar);

        _markerButton.setOnClickListener(GetMarkerButtonOnClickListener());
        _breakButton.setOnClickListener(GetBreakButtonOnClickListener());
        _saveButton.setOnClickListener(GetSaveButtonOnClickListener());

        SetUiEnabled(false);

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

    private View.OnClickListener GetBreakButtonOnClickListener() {
        return v -> {
            _melodyComposerService.SetBreak(_youTubePlayer.getCurrentTimeMillis());
            UpdateTabsOnView();
        };
    }
    private View.OnClickListener GetSaveButtonOnClickListener() {
        return v -> {
            if(_melodyComposerService.IsReadyToSave())
            {
                _melodyComposerService.SaveMelody();

                Intent intent = new Intent(CreateMelodyComposeActivity.this, MenuActivity.class);
                startActivity(intent);
            }
        };
    }

    private View.OnClickListener GetMarkerButtonOnClickListener() {
        return v -> {
            _melodyComposerService.SetMarker(_youTubePlayer.getCurrentTimeMillis());
            UpdateTabsOnView();
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
                        new SetSeekBarMaxProgressValueFromPlayerAction(_seekBar, _youTubePlayer),
                        GetEnableButtonsOnPlayerPlay()
                )));

                _seekBarRefreshTask = new UpdateSeekBarProgressTask(_seekBar, _youTubePlayer);
                _seekBarRefreshTask.execute();
                _seekBar.setOnSeekBarChangeListener(new MultipleOnSeekBarChangeListener(Arrays.asList(
                        new UpdatePlayerProgressOnSeekBarChangeAction(_youTubePlayer),
                        new RunOnSeekProgressRewindBackAction((progress) -> _melodyComposerService.HandleRewindBack(progress)),
                        new RunOnSeekProgressRewindBackAction((progress) -> _melodyComposerService.ClearTabProgressesAfterProgress(progress)),
                        new RunOnSeekProgressRewindBackAction((progress) -> UpdateTabsOnView())
                )));

                _youTubePlayer.cueVideo(CreateMelodyData.VideoUrl);
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        };
    }

    private YouTubePlayer.PlayerStateChangeListener GetEnableButtonsOnPlayerPlay()
    {
        return new YouTubePlayer.PlayerStateChangeListener() {
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
                SetUiEnabled(true);
            }

            @Override
            public void onVideoEnded() {

            }

            @Override
            public void onError(YouTubePlayer.ErrorReason errorReason) {

            }
        };
    }

    private void SetUiEnabled(boolean enable)
    {
        _seekBar.setEnabled(enable);
        _markerButton.setEnabled(enable);
        _breakButton.setEnabled(enable);
        _saveButton.setEnabled(enable);
    }
}
