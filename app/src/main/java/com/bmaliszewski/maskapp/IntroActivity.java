package com.bmaliszewski.maskapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;

public class IntroActivity extends AppCompatActivity {

    private IntroAdapter introAdapter;
    private LinearLayout layoutIntroIndicators;
    private ImageButton button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //when this activity is about to launch we need to check its opened true or false
        if (restorePrefData()){
            Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(mainActivity);
            finish();
        }
        setContentView(R.layout.activity_intro);

                layoutIntroIndicators = findViewById(R.id.indicators);
                button = findViewById(R.id.introButton);

        setupIntroItems();

        ViewPager2 introViewPager = findViewById(R.id.introViewPager);
        introViewPager.setAdapter(introAdapter);

        setupIntroIndicators();
        setCurrentIndicator(0);
        setCurrentButton(0);

        introViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentIndicator(position);
                setCurrentButton(position);
            }
        });

        button.setOnClickListener(v -> {
            if(introViewPager.getCurrentItem() + 1 < introAdapter.getItemCount()){
                introViewPager.setCurrentItem(introViewPager.getCurrentItem() + 1);
            }else{
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                savePrefData();
                finish();
            }
        });
    }

    private void setupIntroItems() {
        List<IntroScreenItem> introScreenItemList = new ArrayList<>();

        IntroScreenItem item = new IntroScreenItem();
        item.setTitle(getString(R.string.introTitle1));
        item.setText1(getString(R.string.text1_1));
        item.setText2(getString(R.string.text2_1));
        item.setIntroImg(R.drawable.mask);

        IntroScreenItem secItem = new IntroScreenItem();
        secItem.setTitle(getString(R.string.introTitle2));
        secItem.setText1(getString(R.string.text1_2));
        secItem.setText2(getString(R.string.text2_2));
        secItem.setIntroImg(R.drawable.settings);

        IntroScreenItem thirdItem = new IntroScreenItem();
        thirdItem.setTitle(getString(R.string.introTitle3));
        thirdItem.setText1(getString(R.string.text1_3));
        thirdItem.setText2(getString(R.string.text2_3));
        thirdItem.setIntroImg(R.drawable.permissions);

        IntroScreenItem lastItem = new IntroScreenItem();
        lastItem.setTitle(getString(R.string.introTitle4));
        lastItem.setText1(getString(R.string.text1_4));
        lastItem.setText2(getString(R.string.text2_4));
        lastItem.setIntroImg(R.drawable.ok);

        introScreenItemList.add(item);
        introScreenItemList.add(secItem);
        introScreenItemList.add(thirdItem);
        introScreenItemList.add(lastItem);

        introAdapter = new IntroAdapter(introScreenItemList);
    }

    private void setupIntroIndicators(){
        ImageView[] indicators = new ImageView[introAdapter.getItemCount()];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,  ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(14,0,14,0);
        for(int i = 0; i<indicators.length;i++){
            indicators[i] = new ImageView(getApplicationContext());
            indicators[i].setImageDrawable(ContextCompat.getDrawable(
                    getApplicationContext(),R.drawable.circle_default));
            indicators[i].setLayoutParams(layoutParams);
            layoutIntroIndicators.addView(indicators[i]);
        }
    }

    private void setCurrentIndicator(int index){
        int cCount = layoutIntroIndicators.getChildCount();
        for(int i = 0; i < cCount;i++){
            ImageView imageview = (ImageView) layoutIntroIndicators.getChildAt(i);
            if(i == index){
                imageview.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.circle_selected));
            }else{
                imageview.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.circle_default)
                );
            }
        }
    }

    private void setCurrentButton(int index){
            if(layoutIntroIndicators.getChildCount() - 1 == index){
                button.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.intro_button_done));
            }else{
                button.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.intro_button_next));
            }
        }

    private void savePrefData(){
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("Pre",MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("Opened", true);
        editor.apply();
    }
    private boolean restorePrefData(){
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("Pre",MODE_PRIVATE);
        boolean ActivityOpen;
        ActivityOpen = preferences.getBoolean("Opened", false);
        return ActivityOpen;

    }
}