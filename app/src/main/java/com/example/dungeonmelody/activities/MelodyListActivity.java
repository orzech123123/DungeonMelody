package com.example.dungeonmelody.activities;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.dungeonmelody.R;
import com.example.dungeonmelody.backgroundTasks.RunAsyncTask;
import com.example.dungeonmelody.configuration.ApisConfig;
import com.example.dungeonmelody.data.PlayMelodyData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MelodyListActivity extends ListActivity {
    private String[] _melodyIds;
    private String[] _titles;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new RunAsyncTask(() -> DownloadAndSetMelodiesOnList(), false).execute();

        ListView listView = getListView();
        listView.setOnItemClickListener((parent, view, position, id) -> {
            PlayMelodyData.MelodyId = _melodyIds[position];

            Intent intent = new Intent(MelodyListActivity.this, PlayMelodyActivity.class);
            startActivity(intent);
        });
    }

    //TODO ugly long method to refactor
    private void DownloadAndSetMelodiesOnList() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://dungeonmelody-0441.restdb.io/rest/tabs")
                .header("x-apikey", ApisConfig.GetRestdbioApiKey())
                .header("Content-Type", "application/json")
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            String json = response.body().string();
            runOnUiThread(() -> {
                try {
                    JSONArray jsonArray = new JSONArray(json);

                    ArrayList<JSONObject> jsonObjects = new ArrayList<>();
                    for (int i=0;i<jsonArray.length();i++){
                        jsonObjects.add((JSONObject) jsonArray.get(i));
                    }

                    _melodyIds = jsonObjects.stream()
                            .map(j -> {
                                try {
                                    return j.getString("melodyId");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                return null;
                            })
                            .distinct()
                            .toArray(String[]::new);
                    _titles = jsonObjects.stream()
                            .map(j -> {
                                try {
                                    return j.getString("title");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                return null;
                            })
                            .distinct()
                            .toArray(String[]::new);

                    setListAdapter(new ArrayAdapter<>(this, R.layout.activity_melody_list, _titles));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}