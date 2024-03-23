package com.example.camarasample;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.sql.CommonDataSource;

public class Gridlist extends AppCompatActivity {
    public String basePath = null;
    public GridView mGridView;
    public GridViewBaseAdapter mGridViewBaseAdapter;
    String str;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gridview);


        final File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "디렉토리 생성 실패!");
            }
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true)

        }
        basePath = mediaStorageDir.getPath();


        mGridView = findViewById(R.id.gridView);
        mGridViewBaseAdapter = new GridViewBaseAdapter(this, basePath);
        mGridView.setAdapter(mGridViewBaseAdapter);



        mGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position1, long l) {

                AlertDialog.Builder builder = new AlertDialog.Builder(Gridlist.this,R.style.Theme_AppCompat_Dialog_Alert);
                builder.setTitle("삭제 하시겠습니까?");
                builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        File temp = new File(mGridViewBaseAdapter.getItemPath(position1));
                        temp.delete();
                        mGridViewBaseAdapter.notifyDataSetChanged();

                    }
                });

                builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });


                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                return true;
            }
        });



        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


                str = mGridViewBaseAdapter.getItemPath(i);
                Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
                if (str.endsWith(".mp4")) {
                    Intent a = new Intent(getApplicationContext(), VideoPlayer.class);
                    a.putExtra("목록", str);
                    startActivity(a);

                } else if(str.endsWith(".jpg")) {
                    Intent s = new Intent(getApplicationContext(), image.class);
                    s.putExtra("목록", str);
                    startActivity(s);


                }



            }






        });


    }

}
