package cn.edu.tsinghua.zhouhang.liuyihao.thubbs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.databinding.ActivityEditBinding;
import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.utils.Alert;

public class EditActivity extends AppCompatActivity {

    private ActivityEditBinding binding;
    private ArrayList<String> mImageUrlList = new ArrayList<>();
    private String mLocation = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide(); //隐藏标题栏
        }
        initView();
        initListener();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constant.LOCATION_PERMISSION) {
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    Alert.info(this, "获取位置信息失败");
                    return;
                }
            }
            setLocation();
        } else if (requestCode == Constant.STORAGE_PERMISSION) {
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    Alert.info(this, "获取存储权限失败");
                    return;
                }
            }
            binding.imageGroup.selectImage();
        }
    }

    private void setLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    Constant.LOCATION_PERMISSION);
            return;
        }
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location location = locationManager.getLastKnownLocation(provider);
            if (location == null) {
                continue;
            }
            if (bestLocation == null || location.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = location;
            }
        }
        if (bestLocation == null) {
            mLocation = "(0°E, 0°N)";
            binding.addLocation.setText(mLocation);
            Alert.info(this, "获取位置信息失败");
            return;
        }
        mLocation = "(" + bestLocation.getLongitude() + ", " + bestLocation.getLatitude() + ")";
        binding.addLocation.setText(mLocation);
        Alert.info(this, "获取位置信息失败");
    }

    private void initView() {
        binding.imageGroup.setEditable(true);
        binding.imageGroup.bind(this, mImageUrlList);
    }

    private void initListener() {
        binding.cancel.setOnClickListener(view -> finish());
        binding.locationButton.setOnClickListener(view -> new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_title_info)
                .setMessage(R.string.question_add_location)
                .setNegativeButton(R.string.button_cancel, ((dialogInterface, i) -> {
                }))
                .setPositiveButton(R.string.button_ok, (dialogInterface, i) -> {
                    setLocation();
                }).create().show()
        );
        binding.imageButton.setOnClickListener(view -> binding.imageGroup.selectImage());
    }
}