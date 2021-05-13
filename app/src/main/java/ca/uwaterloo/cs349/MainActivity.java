package ca.uwaterloo.cs349;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Base64;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_library, R.id.navigation_addition)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        SharedPreferences  mPrefs = getPreferences(MODE_PRIVATE);

        String s = mPrefs.getString("myKey", "");
        byte[] bytes = Base64.getDecoder().decode(s);
        try{
            OneStroke.loadCurrStatefromByte(bytes);
        }catch (Exception e) {
            e.printStackTrace();
        }

    }



    @Override
    protected void onStart() {
        super.onStart();
        Log.d(getClass().getSimpleName(),"OnStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(getClass().getSimpleName(),"OnStop");
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        Log.d(getClass().getSimpleName(),"OnRestoreInstanceState");
        SharedPreferences  mPrefs = getPreferences(MODE_PRIVATE);

        String s = mPrefs.getString("myKey", "");
        byte[] bytes = Base64.getDecoder().decode(s);
        try{
            OneStroke.loadCurrStatefromByte(bytes);
        }catch (Exception e){
            e.printStackTrace();
        }
        super.onRestoreInstanceState(savedInstanceState);

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(getClass().getSimpleName(),"OnSaveInstanceState");


        SharedPreferences  mPrefs = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = mPrefs.edit();
        byte[] bytes = OneStroke.convertCurrStateToByte();
        String s = Base64.getEncoder().encodeToString(bytes);
        editor.putString("myKey",s);
        editor.commit();

    }
}