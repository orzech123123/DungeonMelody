package com.example.dungeonmelody.activities;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.example.dungeonmelody.R;
import com.example.dungeonmelody.backgroundTasks.RunAsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MelodyListActivity extends ListActivity {

    static final String[] FRUITS = new String[]{"Apple", "Avocado", "Banana",
            "Blueberry", "Coconut", "Durian", "Guava", "Kiwifruit",
            "Jackfruit", "Mango", "Olive", "Pear", "Sugar-apple"};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new RunAsyncTask(() -> DownloadAndSetMelodiesOnList(), false).execute();

//        ListView listView = getListView();
//        listView.setTextFilterEnabled(true);
//
//        listView.setOnItemClickListener(new OnItemClickListener() {
//            public void onItemClick(AdapterView<?> parent, View view,
//                                    int position, long id) {
//                // When clicked, show a toast with the TextView text
//                Toast.makeText(getApplicationContext(),
//                        ((TextView) view).getText(), Toast.LENGTH_SHORT).show();
//            }
//        });

    }

    //TODO ugly long method to refactor
    private void DownloadAndSetMelodiesOnList() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://dungeonmelody-0441.restdb.io/rest/tabs")
                .header("x-apikey", "8733ef5f451ad34dbda6155cb2142c01bb423")
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

                    String[] melodyIds = jsonObjects.stream()
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

                    setListAdapter(new ArrayAdapter<String>(this, R.layout.activity_melody_list, melodyIds));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}