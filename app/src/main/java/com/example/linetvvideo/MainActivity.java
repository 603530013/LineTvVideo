package com.example.linetvvideo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;


import static com.example.linetvvideo.VideoInfoActivity.EXTRA_VIDEO_INFO_BACKGROUND;
import static com.example.linetvvideo.VideoInfoActivity.EXTRA_VIDEO_INFO_CREATE;
import static com.example.linetvvideo.VideoInfoActivity.EXTRA_VIDEO_INFO_ID;
import static com.example.linetvvideo.VideoInfoActivity.EXTRA_VIDEO_INFO_RATING;
import static com.example.linetvvideo.VideoInfoActivity.EXTRA_VIDEO_INFO_TITLE;
import static com.example.linetvvideo.VideoInfoActivity.EXTRA_VIDEO_INFO_TOTAL_VIEWS;

public class MainActivity extends AppCompatActivity {
    public final String SHARE_PREFERENCE_SEARCH = "share.preference.search";
    public final String SHARE_PREFERENCE_SEARCH_TEXT = "share.preference.search.text";
    private RecyclerView recyclerView;
    private EditText editText;
    private Button btn_search;
    private Button btn_cancel;

    private List<Video> mVideoList;
    private VideoListAdapter mVideoListAdapter;
    private Intent intent;
    private VideoDao videoDao;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.video_view);
        editText = findViewById(R.id.search_text);
        btn_search = findViewById(R.id.search_button);
        btn_cancel = findViewById(R.id.search_cancel);

        intent = new Intent(this, VideoInfoActivity.class);
        videoDao = VideoDb.getInstance(getApplicationContext()).getVideoDao();

        sharedPreferences = getSharedPreferences(SHARE_PREFERENCE_SEARCH, MODE_PRIVATE);
        editText.setText(sharedPreferences.getString(SHARE_PREFERENCE_SEARCH_TEXT, ""));

        new HttpAsyncTask().execute("https://static.linetv.tw/interview/dramas-sample.json");
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        videoDao.getAllVideos().observe(this, new Observer<List<Video>>() {
            @Override
            public void onChanged(List<Video> videos) {
                mVideoList = videos;
                if (TextUtils.isEmpty(sharedPreferences.getString(SHARE_PREFERENCE_SEARCH_TEXT, ""))) {
                    mVideoListAdapter = new VideoListAdapter(mVideoList);
                    recyclerView.setAdapter(mVideoListAdapter);
                } else {
                    search();
                }
            }
        });
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search();
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mVideoListAdapter = new VideoListAdapter(mVideoList);
                recyclerView.setAdapter(mVideoListAdapter);
                editText.setText("");
                btn_cancel.setVisibility(View.GONE);
                sharedPreferences.edit().remove(SHARE_PREFERENCE_SEARCH_TEXT).commit();
            }
        });
    }

    private void search() {
        final String searchContext = editText.getText().toString();
        new AsyncTask<Void, Void, List<Video>>() {
            @Override
            protected List<Video> doInBackground(Void... voids) {
                return videoDao.getVideoByContext(searchContext);
            }

            @Override
            protected void onPostExecute(List<Video> videos) {
                super.onPostExecute(videos);
                mVideoListAdapter = new VideoListAdapter(videos);
                recyclerView.setAdapter(mVideoListAdapter);
                btn_cancel.setVisibility(View.VISIBLE);
                sharedPreferences.edit().putString(SHARE_PREFERENCE_SEARCH_TEXT, searchContext).apply();
            }
        }.execute();
    }

    public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.ViewHolder> {

        private List<Video> mVideoList;

        public VideoListAdapter(List<Video> photoList) {
            this.mVideoList = photoList;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private LinearLayout videoLayout;
            private ImageView image;
            private TextView imageName;
            private TextView imageRating;
            private TextView imageCreate;

            ViewHolder(View itemView) {
                super(itemView);
                videoLayout = (LinearLayout) itemView.findViewById(R.id.video_layout);
                image = (ImageView) itemView.findViewById(R.id.image);
                imageName = (TextView) itemView.findViewById(R.id.image_name);
                imageRating = (TextView) itemView.findViewById(R.id.image_rating);
                imageCreate = (TextView) itemView.findViewById(R.id.image_create);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.viewholder_video, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            final String thumb = mVideoList.get(position).getThumb();
            final String name = mVideoList.get(position).getName();
            final double rating = mVideoList.get(position).getRating();
            final String create = mVideoList.get(position).getCreated_at();
            final int totalViews = mVideoList.get(position).getTotal_views();
            final int id = mVideoList.get(position).getDrama_id();
            Picasso.get().load(thumb).into(holder.image);
            holder.imageName.setText(name);
            holder.imageRating.setText("Rating: " + String.valueOf(rating));
            holder.imageCreate.setText("Create time: " + create);
            holder.videoLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent.putExtra(EXTRA_VIDEO_INFO_ID, id);
                    intent.putExtra(EXTRA_VIDEO_INFO_BACKGROUND, thumb);
                    intent.putExtra(EXTRA_VIDEO_INFO_TITLE, name);
                    intent.putExtra(EXTRA_VIDEO_INFO_RATING, rating);
                    intent.putExtra(EXTRA_VIDEO_INFO_CREATE, create);
                    intent.putExtra(EXTRA_VIDEO_INFO_TOTAL_VIEWS, totalViews);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mVideoList.size();
        }
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            URL url = null;
            try {
                url = new URL(urls[0]);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream is = connection.getInputStream();
                BufferedReader in = new BufferedReader(new InputStreamReader(is));
                String line = in.readLine();
                StringBuffer json = new StringBuffer();
                while (line != null) {
                    json.append(line);
                    line = in.readLine();
                }
                JSONObject jsonObj = new JSONObject(String.valueOf(json));
                JSONArray jsonArray = jsonObj.getJSONArray("data");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject videoInfoObject = jsonArray.getJSONObject(i);
                    int dramaId = videoInfoObject.getInt("drama_id");
                    String name = videoInfoObject.getString("name");
                    int totalViews = videoInfoObject.getInt("total_views");
                    String createdAt = videoInfoObject.getString("created_at");
                    String thumb = videoInfoObject.getString("thumb");
                    double rating = videoInfoObject.getDouble("rating");
                    Video video = new Video(dramaId, name, totalViews, createdAt, thumb, rating);
                    if (VideoDb.getInstance(getApplicationContext()).getVideoDao().getVideoById(dramaId) == null) {
                        VideoDb.getInstance(getApplicationContext()).getVideoDao().insert(video);
                    }
                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
            return "";
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
        }
    }
}
