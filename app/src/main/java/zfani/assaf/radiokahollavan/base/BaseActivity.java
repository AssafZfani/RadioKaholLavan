package zfani.assaf.radiokahollavan.base;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import zfani.assaf.radiokahollavan.R;

@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        int statusBarColor = ContextCompat.getColor(this, getStatusBarColor());
        float[] hsv = new float[3];
        Color.colorToHSV(statusBarColor, hsv);
        hsv[2] *= 0.8f;
        statusBarColor = Color.HSVToColor(hsv);
        getWindow().setStatusBarColor(statusBarColor);
    }

    protected int getStatusBarColor() {
        return R.color.colorPrimary;
    }
}
