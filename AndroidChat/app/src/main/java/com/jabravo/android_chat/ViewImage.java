package com.jabravo.android_chat;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class ViewImage extends AppCompatActivity {

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

        imageView = (ImageView) findViewById(R.id.imageShow);
        Bundle extras = getIntent().getExtras();
        String path = String.valueOf(extras.get("path")) ;

        Bitmap photobmp = BitmapFactory.decodeFile(path);
        imageView.setImageBitmap(photobmp);
    }
}
