package com.sstudio.olxmangaldai;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.NativeExpressAdView;

/**
 * Created by Alan on 9/23/2017.
 */

public class RViewHolder extends RecyclerView.ViewHolder {
    ImageView imageView;
    TextView textView;
    NativeExpressAdView adView;
    Context c;
    public RViewHolder(View itemView) {
        super(itemView);
        c=itemView.getContext();
        textView=itemView.findViewById(R.id.rimg);
        imageView=itemView.findViewById(R.id.rtext);
        adView=itemView.findViewById(R.id.adView);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(c, "texview", Toast.LENGTH_SHORT).show();
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(c, "imageView", Toast.LENGTH_SHORT).show();
                mClickListener.onItemClick(view, getAdapterPosition());
            }
        });
        adView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(c, "Ad view", Toast.LENGTH_SHORT).show();
            }
        });
       /* AdSize adSize=new AdSize(280,150);
        adView.setAdUnitId("ca-app-pub-5433263595056427/6269917892");
        adView.setAdSize(adSize);*/

    }
    private RViewHolder.ClickListener mClickListener;

    //Interface to send callbacks...
    public interface ClickListener{
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }

    public void setOnClickListener(RViewHolder.ClickListener clickListener){
        mClickListener = clickListener;
    }
}
