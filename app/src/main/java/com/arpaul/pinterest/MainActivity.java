package com.arpaul.pinterest;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.arpaul.filedownloader.DownloadInterface;
import com.arpaul.filedownloader.PinDownloader;
import com.arpaul.filedownloader.webservices.WebServiceConstant;
import com.arpaul.pinterest.adapter.ProfileAdapter;
import com.arpaul.pinterest.common.AppConstants;
import com.arpaul.pinterest.dataobject.ProfileDO;
import com.arpaul.pinterest.webservices.FetchDataService;
import com.arpaul.utilitieslib.LogUtils;
import com.arpaul.utilitieslib.NetworkUtility;
import com.arpaul.utilitieslib.PermissionUtils;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks, DownloadInterface {

    private RecyclerView rvList;
    private TextView tvNoList;
    final int LOADER_FETCH_ALL_DATA = 1;

    private ProfileAdapter adapter;
    final String SAVE_IMAGE = "SAVE_IMAGE";
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });

        loadData();
    }

    @Override
    protected void onResume() {
        super.onResume();

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
                if (arrProfileDO != null && arrProfileDO.size() > 0) {
                    adapter.refresh(arrProfileDO);
                } else {
                    rvList.setVisibility(View.GONE);
                    tvNoList.setVisibility(View.VISIBLE);
                }
                break;
        }
        getSupportLoaderManager().destroyLoader(loader.getId());
        hideloader();
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

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
    public void getMessage(final Object object, final String errorStatus) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hideloader();
                if (errorStatus.equalsIgnoreCase(WebServiceConstant.STAT_SUCCESS))
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

    private void loadData() {
        if (NetworkUtility.isConnectionAvailable(this)) {
            if (getSupportLoaderManager().getLoader(LOADER_FETCH_ALL_DATA) == null)
                getSupportLoaderManager().initLoader(LOADER_FETCH_ALL_DATA, null, MainActivity.this).forceLoad();
            else
                getSupportLoaderManager().restartLoader(LOADER_FETCH_ALL_DATA, null, MainActivity.this).forceLoad();
        } else {
            Toast.makeText(this, getString(R.string.internet_connection_not_available), Toast.LENGTH_SHORT).show();
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
        swipeRefreshLayout.setRefreshing(true);
    }

    private void hideloader() {
        swipeRefreshLayout.setRefreshing(false);
    }

    private void initialiseControls() {
        rvList = (RecyclerView) findViewById(R.id.rvList);

        tvNoList = (TextView) findViewById(R.id.tvNoList);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);

        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_red_light,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_blue_bright);

        adapter = new ProfileAdapter(this, new ArrayList<ProfileDO>());
        rvList.setAdapter(adapter);

        AppConstants.EXTERNAL_FOLDER_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
    }
}
