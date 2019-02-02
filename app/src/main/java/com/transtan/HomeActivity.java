package com.transtan;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import com.transtan.R;

public class HomeActivity extends AppCompatActivity {

    private Animation scale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Display screenOrientation = getWindowManager().getDefaultDisplay();
        int orientation = Configuration.ORIENTATION_UNDEFINED;
        if(screenOrientation.getWidth() < screenOrientation.getHeight()) {
            setContentView(R.layout.activity_home_portrait);
        }
        else {
            setContentView(R.layout.activity_home_landscape);
        }
        final Button btnGetStarted = (Button) findViewById(R.id.btnGetStarted);
        btnGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        scale = AnimationUtils.loadAnimation(this, R.anim.pulse);
        btnGetStarted.startAnimation(scale);
    }
}
