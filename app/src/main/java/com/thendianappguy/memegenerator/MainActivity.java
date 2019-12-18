package com.thendianappguy.memegenerator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSION_REQUEST = 1;
    private static final int RESULT_LOAD_IMAGE = 22;

    Button load, save, share , go;
    TextView imgTopText, imgBottomText;
    EditText topEditText, bottomEditText;
    ImageView imageView;

    String currentImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // checking for the permission and showing dialog
        checkStorePermissionAndShowDialog();

        //defining layouts
        defineLayouts();

        //setting up the onclicklistner
        setUpOnClicklistenr();


    }

    public static Bitmap getScreenShot(View view){

        view.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        return bitmap;

    }

    public void store(Bitmap bm, String filename){

        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/MEME";
        File dir = new File(dirPath);
        if(!dir.exists()){
            dir.mkdir();
        }
        File file = new File(dirPath, filename);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(this, "Error, saving!", Toast.LENGTH_SHORT).show();
        }
    }

    private void setUpOnClicklistenr() {

        load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, RESULT_LOAD_IMAGE);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View context = findViewById(R.id.lay);
                Bitmap bitmap = getScreenShot(context);

                currentImage = "meme" + System.currentTimeMillis() + ".png";
                store(bitmap, currentImage);
                share.setEnabled(true);
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareImage(currentImage);
            }
        });

        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String topText = topEditText.getText().toString();
                String bottomText = bottomEditText.getText().toString();

                imgTopText.setText(topText);
                imgBottomText.setText(bottomText);
            }
        });

    }

    private void shareImage(String fileName) {

        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/MEME";
        Uri uri = Uri.fromFile(new File(dirPath, fileName));
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");

        intent.putExtra(Intent.EXTRA_SUBJECT,"");
        intent.putExtra(Intent.EXTRA_TEXT,"");
        intent.putExtra(Intent.EXTRA_STREAM,uri);

        try{
            startActivity(Intent.createChooser(intent,"Share via"));
        }catch (ActivityNotFoundException e){
            Toast.makeText(this, "No SHaring App Found", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data){
            Uri selectImage  = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectImage, filePathColumn, null,null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            save.setEnabled(true);
            share.setEnabled(true);
        }
    }

    private void defineLayouts() {

        load = findViewById(R.id.load);
        save = findViewById(R.id.save);
        share = findViewById(R.id.share);
        go = findViewById(R.id.go);

        topEditText = findViewById(R.id.topEditText);
        bottomEditText = findViewById(R.id.bottomEditText);

        imgBottomText = findViewById(R.id.imgBottomText);
        imgTopText = findViewById(R.id.imgTopText);
        imageView = findViewById(R.id.imageView);


    }

    private void checkStorePermissionAndShowDialog() {
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);

            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);

            }
        } else {

            //do nothing
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case MY_PERMISSION_REQUEST:{
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                        //do nothing
                    }
                } else{

                    Toast.makeText(this,"No permission granted",Toast.LENGTH_SHORT).show();
                    finish();
                }

                return;
            }
        }
    }
}
