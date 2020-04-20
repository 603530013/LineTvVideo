package com.example.linetvvideo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class VideoInfoActivity extends AppCompatActivity {
    public static final String EXTRA_VIDEO_INFO_ID = "extra.video.info_id";
    public static final String EXTRA_VIDEO_INFO_BACKGROUND = "extra.video.info_background";
    public static final String EXTRA_VIDEO_INFO_RATING = "extra.video.info_rating";
    public static final String EXTRA_VIDEO_INFO_TITLE = "extra.video.info_title";
    public static final String EXTRA_VIDEO_INFO_CREATE = "extra.video.info_create";
    public static final String EXTRA_VIDEO_INFO_TOTAL_VIEWS = "extra.video.info_total_views";

    private Video mVideo;
    private ImageView mImage;
    private TextView mImageName;
    private TextView mImageRating;
    private TextView mImageCreate;
    private TextView mImageViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_info);
        Intent intent = getIntent();
        mVideo = new Video(intent.getIntExtra(EXTRA_VIDEO_INFO_ID, 0), intent.getStringExtra(EXTRA_VIDEO_INFO_TITLE), intent.getIntExtra(EXTRA_VIDEO_INFO_TOTAL_VIEWS, 0), intent.getStringExtra(EXTRA_VIDEO_INFO_CREATE), intent.getStringExtra(EXTRA_VIDEO_INFO_BACKGROUND), intent.getDoubleExtra(EXTRA_VIDEO_INFO_RATING, 0));
        mImage = (ImageView) findViewById(R.id.video_image);
        mImageName = (TextView) findViewById(R.id.video_name);
        mImageRating = (TextView) findViewById(R.id.video_rating);
        mImageViews = (TextView) findViewById(R.id.video_views);
        mImageCreate = (TextView) findViewById(R.id.video_create);
        Picasso.get().load(mVideo.getThumb()).into(mImage);
        mImageName.setText("Name: " + mVideo.getName());
        mImageRating.setText("Rating: " + String.valueOf(mVideo.getRating()));
        mImageViews.setText("Total Views: " + String.valueOf(mVideo.getTotal_views()));
        mImageCreate.setText("Created at: " + mVideo.getCreated_at());
    }
}
