package com.example.calories_caculator.activity;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.calories_caculator.fragment.ChatbotFragment;
import com.example.calories_caculator.fragment.HomeFragment;
import com.example.calories_caculator.fragment.ProfileFragment;
import com.example.calories_caculator.R;
import com.example.calories_caculator.fragment.WorkoutFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Navbar
        BottomNavigationView bottomNavBar= findViewById(R.id.bottom_nav_bar);
        replaceFragment(new HomeFragment());
        bottomNavBar.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.home) {
                replaceFragment(new HomeFragment());
            } else if (id == R.id.workout) {
                replaceFragment(new WorkoutFragment());
            } else if (id == R.id.chatBot) {
                replaceFragment(new ChatbotFragment());
            } else if (id == R.id.profile) {
                replaceFragment(new ProfileFragment());
            }
            return true;
        });
    }
    private void replaceFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }
}