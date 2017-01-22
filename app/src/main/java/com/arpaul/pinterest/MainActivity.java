package com.arpaul.pinterest;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.arpaul.filedownloader.DownloadInterface;
import com.arpaul.filedownloader.PinDownloader;
import com.arpaul.filedownloader.webservices.WEBSERVICE_TYPE;
import com.arpaul.filedownloader.webservices.WebServiceConstant;
import com.arpaul.filedownloader.webservices.WebServiceResponse;
import com.arpaul.pinterest.adapter.ProfileAdapter;
import com.arpaul.pinterest.common.AppConstants;
import com.arpaul.pinterest.dataobject.ProfileDO;
import com.arpaul.pinterest.webservices.FetchDataService;
import com.arpaul.utilitieslib.LogUtils;
import com.arpaul.utilitieslib.PermissionUtils;

import java.io.File;
import java.util.ArrayList;

import static com.arpaul.filedownloader.webservices.WebServiceResponse.FAILURE;
import static com.arpaul.filedownloader.webservices.WebServiceResponse.SUCCESS;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks, DownloadInterface {

    private RecyclerView rvList;
    private Button btnSave, btnRefresh;
    private TextView tvNoList;
    private ProgressBar pbLoading;
    final int LOADER_FETCH_ALL_DATA = 1;

    private ProfileAdapter adapter;


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
                if (new PermissionUtils().checkPermission(this, new String[]{
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE}) != PackageManager.PERMISSION_GRANTED) {
                    new PermissionUtils().verifyPermission(this, new String[]{
                            android.Manifest.permission.READ_EXTERNAL_STORAGE,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE});
                } else
                    createFolder();
            } else
                createFolder();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PinDownloader download = new PinDownloader(MainActivity.this);
                download.loadURL("https://images.unsplash.com/photo-1464550883968-cec281c19761?ixlib=rb-0.3.5\\u0026q=80\\u0026fm=jpg\\u0026crop=entropy\\u0026w=1080\\u0026fit=max\\u0026s=1881cd689e10e5dca28839e68678f432");
                download.saveDetail(AppConstants.EXTERNAL_FOLDER_PATH + AppConstants.EXTERNAL_FOLDER, "image1.png");
                download.begin();
            }
        });

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getSupportLoaderManager().getLoader(LOADER_FETCH_ALL_DATA) == null)
                    getSupportLoaderManager().initLoader(LOADER_FETCH_ALL_DATA, null, MainActivity.this).forceLoad();
                else
                    getSupportLoaderManager().restartLoader(LOADER_FETCH_ALL_DATA, null, MainActivity.this).forceLoad();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        AppConstants.EXTERNAL_FOLDER_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
        getSupportLoaderManager().initLoader(LOADER_FETCH_ALL_DATA, null, this).forceLoad();
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        showloader();
        switch (id) {
            case LOADER_FETCH_ALL_DATA:
                return new FetchDataService(this);

            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        switch (loader.getId()) {
            case LOADER_FETCH_ALL_DATA:
                ArrayList<ProfileDO> arrProfileDO = (ArrayList<ProfileDO>) data;
                if(arrProfileDO != null && arrProfileDO.size() > 0) {
                    adapter.refresh(arrProfileDO);
                }
                break;
        }
        getSupportLoaderManager().destroyLoader(loader.getId());
        hideloader();
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    final String SAVE_IMAGE = "SAVE_IMAGE";
    public void downloadImage(ProfileDO objProfileDO) {
        synchronized (SAVE_IMAGE) {
            showloader();

            PinDownloader download = new PinDownloader(this);
            download.loadURL(objProfileDO.UserImage);
            download.saveDetail(AppConstants.EXTERNAL_FOLDER_PATH + AppConstants.EXTERNAL_FOLDER, objProfileDO.UserName + ".png");
            download.begin();
        }
    }

    @Override
    public void getMessage(final Object object,final  String errorStatus) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hideloader();
                if(errorStatus.equalsIgnoreCase(WebServiceConstant.STAT_SUCCESS))
                    Toast.makeText(MainActivity.this, "download success", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(MainActivity.this, "download fail", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        int copyFile = 0;
        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equalsIgnoreCase(android.Manifest.permission.READ_EXTERNAL_STORAGE) && grantResults[i] == PackageManager.PERMISSION_GRANTED)
                    copyFile++;
                else if (permissions[i].equalsIgnoreCase(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) && grantResults[i] == PackageManager.PERMISSION_GRANTED)
                    copyFile++;
            }

            if (copyFile == 2)
                createFolder();
        }
    }

    public void createFolder() {
        try {
            String path = Environment.getExternalStorageDirectory() + AppConstants.EXTERNAL_FOLDER;
            LogUtils.infoLog("FOLDER_PATH", path);
            File fileDir = new File(path);
            if (!fileDir.exists())
                fileDir.mkdirs();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void showloader() {
        pbLoading.setVisibility(View.VISIBLE);
    }

    private void hideloader() {
        pbLoading.setVisibility(View.GONE);
    }

    private void initialiseControls() {
        rvList          = (RecyclerView) findViewById(R.id.rvList);

        btnSave         = (Button) findViewById(R.id.btnSave);
        btnRefresh      = (Button) findViewById(R.id.btnRefresh);

        tvNoList        = (TextView) findViewById(R.id.tvNoList);

        pbLoading       = (ProgressBar) findViewById(R.id.pbLoading);

        adapter         = new ProfileAdapter(this, new ArrayList<ProfileDO>());
        rvList.setAdapter(adapter);
    }
}
