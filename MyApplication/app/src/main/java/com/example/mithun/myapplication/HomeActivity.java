package com.example.mithun.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Random;

public class HomeActivity extends AppCompatActivity {
    boolean doubleBackToExitPressedOnce = false;
    SharedPreferences sharedpreferences;
    Random r;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Button btnShop=(Button)findViewById(R.id.btnshop);

        btnShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                r= new Random();
                sharedpreferences = getSharedPreferences("ShoPref", Context.MODE_PRIVATE);
                int randomNumber = r.nextInt(500);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("bno", String.valueOf(randomNumber));
                editor.commit();
               // Toast.makeText(this,"hello "+String.valueOf(randomNumber),Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }
    @Override
    public void onBackPressed() {

        if (doubleBackToExitPressedOnce) {
            System.exit(0);
            //super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}
