package com.arpaul.pinterest;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.arpaul.filedownloader.DownloadInterface;
import com.arpaul.filedownloader.PinDownloader;
import com.arpaul.pinterest.common.AppConstants;
import com.arpaul.utilitieslib.LogUtils;
import com.arpaul.utilitieslib.PermissionUtils;

import java.io.File;

public class MainActivity extends AppCompatActivity implements DownloadInterface {

    private RecyclerView rvList;
    private Button btnSave;
    private TextView tvNoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);

        initialiseControls();

        bindControls();
    }

    private void bindControls() {
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                if(new PermissionUtils().checkPermission(this, new String[]{
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE}) != PackageManager.PERMISSION_GRANTED){
                    new PermissionUtils().verifyPermission(this,new String[]{
                            android.Manifest.permission.READ_EXTERNAL_STORAGE,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE});
                } else
                    createFolder();
            } else
                createFolder();

        } catch (Exception ex){
            ex.printStackTrace();
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PinDownloader download = new PinDownloader(MainActivity.this);
                download.loadURL("1.15");
                download.saveDetail(AppConstants.EXTERNAL_FOLDER_PATH + AppConstants.EXTERNAL_FOLDER, "image1");
                download.begin();
            }
        });

    }

    @Override
    public void getMessage(Object object, String errorStatus) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        int copyFile = 0;
        if (requestCode == 1) {
            for(int i = 0; i < permissions.length; i++){
                if(permissions[i].equalsIgnoreCase(android.Manifest.permission.READ_EXTERNAL_STORAGE) && grantResults[i] == PackageManager.PERMISSION_GRANTED)
                    copyFile++;
                else if(permissions[i].equalsIgnoreCase(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) && grantResults[i] == PackageManager.PERMISSION_GRANTED)
                    copyFile++;
            }

            if(copyFile == 2)
                createFolder();
        }
    }

    public void createFolder(){
        try {
            String path = Environment.getExternalStorageDirectory() + AppConstants.EXTERNAL_FOLDER;
            LogUtils.infoLog("FOLDER_PATH", path);
            File fileDir = new File(path);
            if(!fileDir.exists())
                fileDir.mkdirs();
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void initialiseControls() {
        rvList      = (RecyclerView) findViewById(R.id.rvList);
        btnSave     = (Button) findViewById(R.id.btnSave);
        tvNoList    = (TextView) findViewById(R.id.tvNoList);
    }
}
