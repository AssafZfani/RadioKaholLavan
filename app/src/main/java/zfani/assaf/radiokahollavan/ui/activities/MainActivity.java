package zfani.assaf.radiokahollavan.ui.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

import zfani.assaf.radiokahollavan.App;
import zfani.assaf.radiokahollavan.R;
import zfani.assaf.radiokahollavan.base.BaseActivity;
import zfani.assaf.radiokahollavan.player.RadioManager;

public class MainActivity extends BaseActivity {

    public RadioManager radioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each menu should be considered as top level destinations.
        // AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.live_broadcast, R.id.broadcast_schedule, R.id.sending_regards, R.id.yemeni, R.id.contact_us).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        // NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
        radioManager = RadioManager.getInstance();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (App.isServiceAllowed(this)) {
            radioManager.bind(this);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 10001);
        }
    }

    @Override
    protected void onDestroy() {
        if (App.isServiceAllowed(this)) {
            radioManager.unbind(this);
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 10001) {
            if (permissions.length > 0 && grantResults.length > 0 && Objects.equals(permissions[0],
                    Manifest.permission.READ_PHONE_STATE) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                radioManager.bind(this);
            } else {
                Toast.makeText(this, "הנגן לא יכול לפעול ללא מתן הרשאה", Toast.LENGTH_LONG).show();
            }
        }
    }
}
