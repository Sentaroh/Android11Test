package com.sentaroh.android.android11test;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {
    private Context mContext=null;
    private TextView mMessageView=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext=this;

        mMessageView=(TextView)findViewById(R.id.main_message);
        File[] fl=getExternalFilesDirs(null);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (isLegacyStorageAccessGranted()) {
            performTest();
        } else {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 10);
        }
    }

    private void performTest() {
        Thread th=new Thread(){
            @Override
            public void run() {
                File[] fl=getExternalFilesDirs(null);
                for(File item:fl) {
                    if (item.getPath().startsWith("/storage/emulated/0/")) {
                        android11Test1("/storage/emulated/0");
                        android11Test2("/storage/emulated/0");
                        android11Test3("/storage/emulated/0");
                    } else {
                        String[] dir_parts=item.getPath().split("/");
                        String mp="/"+dir_parts[1]+"/"+dir_parts[2];
                        android11Test1(mp);
                        android11Test2(mp);
                        android11Test3(mp);
                    }
                }
            }
        };
        th.start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (10 == requestCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                performTest();
            } else {
                Toast.makeText(MainActivity.this, "Media storage permission not granted", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }


    private boolean isLegacyStorageAccessGranted() {
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) return false;
        return true;
    }


    private Handler mUiHandler=new Handler();
    private void putMessage(final String msg) {
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                mMessageView.setText(mMessageView.getText().toString()+"\n"+msg);
            }
        });
    }

    private void android11Test1(String mp) {
        String fn="test.file";
        String fp=mp+"/"+fn;
        putMessage("Test1 Write to "+fp);
        File of=new File(fp);
        byte[] buff=new byte[1024*64];
        try {
            OutputStream os=new FileOutputStream(of);
            os.write(buff, 0, 1024*64);
            os.flush();
            os.close();
            putMessage("Test1 file write Success");
        } catch (Exception e) {
            e.printStackTrace();
            putMessage("Test1 error="+e.getMessage());
        }
        putMessage("Test1 lastModified="+of.lastModified());
        putMessage("Test1 delete="+of.delete());
        putMessage(" ");
    }

    private void android11Test2(String mp) {
        String fn="test.pptx";
        String fp=mp+"/Android/data/com.sentaroh.android.android11test/files/"+fn;
        putMessage("Test2 Write to "+fp);
        File app=new File(fp);
        try {
            byte[] buff=new byte[1024*64];
            OutputStream os=new FileOutputStream(app);
            os.write(buff, 0, 1024*64);
            os.flush();
            os.close();
            putMessage("Test2 file write Success");
        } catch (Exception e) {
            e.printStackTrace();
            putMessage("Test2 error="+e.getMessage());
        }
        File sd=new File(mp+"/test.pptx");
        boolean rc_delete=sd.delete();
        boolean rc_set_last_modified=app.setLastModified(1000);
        boolean rc_rename=app.renameTo(sd);
        putMessage("Test2 delete new file="+rc_delete+", rename="+rc_rename+", setLastModified="+rc_set_last_modified);
        putMessage("Test2 lastModified="+sd.lastModified());
//        putMessage("Test2 delete="+sd.delete());
        putMessage(" ");
    }

    private void android11Test3(String mp) {
        String fn="test.ppty";
        String fp=mp+"/"+fn;
        putMessage("Test3 Write to "+fp);
        File app=new File(fp);
        boolean rc_delete=app.delete();
        try {
            byte[] buff=new byte[1024*64];
            OutputStream os=new FileOutputStream(app);
            os.write(buff, 0, 1024*64);
            os.flush();
            os.close();
            putMessage("Test3 file write Success");
        } catch (Exception e) {
            e.printStackTrace();
            putMessage("Test3 error="+e.getMessage());
        }
        boolean rc_set_last_modified=app.setLastModified(10000);
        putMessage("Test3 delete="+rc_delete+", setLastModified="+rc_set_last_modified);
        putMessage("Test3 lastModified="+app.lastModified());

        File new_file=new File(mp+"/test.pptz");
        putMessage("Test3 Rename from="+app.getPath()+", to="+new_file.getPath());
        boolean rc_rename=app.renameTo(new_file);
        putMessage("Test3 Rename="+rc_rename);

//        putMessage("Test3 delete test file="+new_file.delete());
        putMessage(" ");
    }

}