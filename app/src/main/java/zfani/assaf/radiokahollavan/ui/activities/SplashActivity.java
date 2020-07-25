package zfani.assaf.radiokahollavan.ui.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import zfani.assaf.radiokahollavan.App;
import zfani.assaf.radiokahollavan.R;
import zfani.assaf.radiokahollavan.base.BaseActivity;

public class SplashActivity extends BaseActivity {

    @Override
    protected int getStatusBarColor() {
        return android.R.color.white;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        findViewById(R.id.mainView).setBackgroundResource(R.drawable.login_screen);
        App.broadcastWorkStatus.observe(SplashActivity.this, workInfo -> {
            if (workInfo != null && workInfo.getState().isFinished()) {
                Executors.newSingleThreadScheduledExecutor().schedule(() -> {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                }, 1, TimeUnit.SECONDS);
            }
        });
    }
}
