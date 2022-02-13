package com.bmaliszewski.maskapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class InfoActivity extends AppCompatActivity {
    private ImageButton backButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(v -> {
            backToMain();
        });
        TextView github =  findViewById(R.id.github);

        github.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://github.com/flows0n/MaskApp_AndroidCode");
                Intent internet = new Intent(Intent.ACTION_VIEW,uri);
                startActivity(internet);
            }
        });
    }
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        backToMain();
    }

    private void backToMain(){
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

}