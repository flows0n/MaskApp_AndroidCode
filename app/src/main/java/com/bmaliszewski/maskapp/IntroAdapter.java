package com.bmaliszewski.maskapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class IntroAdapter extends RecyclerView.Adapter<IntroAdapter.IntroHolder>{

    private List<IntroScreenItem> introScreenItems;

    public IntroAdapter(List<IntroScreenItem> introScreenItems) {
        this.introScreenItems = introScreenItems;
    }

    @NonNull
    @Override
    public IntroHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new IntroHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.intro_screen,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull IntroHolder holder, int position) {
        holder.setIntroData(introScreenItems.get(position));
    }

    @Override
    public int getItemCount() {
        return introScreenItems.size();
    }

    class IntroHolder extends RecyclerView.ViewHolder {
        private TextView introTitle;
        private TextView text1;
        private TextView text2;
        private ImageView introImg;

        IntroHolder(@NonNull View itemView) {
            super(itemView);
            introTitle = itemView.findViewById(R.id.introTitle);
            text1 = itemView.findViewById(R.id.introText1);
            text2 = itemView.findViewById(R.id.introText2);
            introImg = itemView.findViewById(R.id.introImage);
        }
        void setIntroData(IntroScreenItem introScreenItem){
            introTitle.setText(introScreenItem.getTitle());
            text1.setText(introScreenItem.getText1());
            text2.setText(introScreenItem.getText2());
            introImg.setImageResource(introScreenItem.getIntroImg());
        }
    }

}


