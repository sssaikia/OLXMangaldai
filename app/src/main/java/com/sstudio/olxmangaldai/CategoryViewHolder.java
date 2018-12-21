package com.sstudio.olxmangaldai;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Alan on 9/25/2017.
 */

public class CategoryViewHolder extends RecyclerView.ViewHolder {
    TextView textView;
    public CategoryViewHolder(View itemView) {
        super(itemView);
        textView=itemView.findViewById(R.id.cattext);
    }
}
