package zfani.assaf.radiokahollavan.ui.activities;

import android.os.Bundle;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

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
        radioManager.bind(this);
    }

    @Override
    protected void onDestroy() {
        radioManager.unbind(this);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
