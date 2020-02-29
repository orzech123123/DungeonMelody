package com.example.dungeonmelody.activities;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.RequiresApi;

import com.example.dungeonmelody.R;
import com.example.dungeonmelody.configuration.YouTubeConfig;
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

public class CreateMelodyChooseVideoActivity extends YouTubeBaseActivity {

    private YouTubePlayerView _youTubePlayerView;
    private YouTubePlayer _youTubePlayer;
    private EditText _videoUrlText;
    private Button _nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_melody_choose_video);

        _youTubePlayerView = findViewById(R.id.youtubePlayer);
        _videoUrlText = findViewById(R.id.videoUrlText);
        _nextButton = findViewById(R.id.nextButton);

        _youTubePlayerView.initialize(YouTubeConfig.GetApiKey(), GetPlayerOnInitListener());
        _videoUrlText.addTextChangedListener(GetVideoUrlChangeListener());

        _nextButton.setEnabled(false);
    }

    private TextWatcher GetVideoUrlChangeListener(){
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(_youTubePlayer == null)
                {
                    return;
                }

                String url = _videoUrlText.getText().toString();
                _youTubePlayer.loadVideo(url);

                TurnOffOnNextButtonIfVideoExist().execute();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
    }

    private AsyncTask TurnOffOnNextButtonIfVideoExist()
    {
        return new AsyncTask() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            protected Object doInBackground(Object[] objects) {

                OkHttpClient client = new OkHttpClient();
                @SuppressLint("WrongThread") String url = _videoUrlText.getText().toString();
                Request request = new Request.Builder()
                        .url("https://www.youtube.com/oembed?url=https://www.youtube.com/watch?v=" + url)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            _nextButton.setEnabled(response.code() == 200);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }
        };
    }

    private YouTubePlayer.OnInitializedListener GetPlayerOnInitListener(){
        return new YouTubePlayer.OnInitializedListener(){
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                _youTubePlayer = youTubePlayer;
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
            }
        };
    }
}
