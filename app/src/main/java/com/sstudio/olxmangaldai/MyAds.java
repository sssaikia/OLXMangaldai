package com.sstudio.olxmangaldai;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class MyAds extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter<Items, RViewHolder> firebaseRecyclerAdapter;
    private AdRequest adRequest;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference reference;
    private StorageReference mStorageRef;
    boolean img1=false,img2=false,img3=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_ads);

        firebaseDatabase= FirebaseDatabase.getInstance();
        reference=firebaseDatabase.getReference().child("items");

        mStorageRef = FirebaseStorage.getInstance().getReference();

        adRequest = new AdRequest.Builder().build();
        recyclerView=findViewById(R.id.myads);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Items,RViewHolder>
                (Items.class,R.layout.recycler_lay,RViewHolder.class,reference.orderByChild("userId")
                        .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            @Override
            protected void populateViewHolder(RViewHolder viewHolder, Items model, int position) {
                Log.d(" populating  : ",model.getTexts()+" "+model.getUrl()+" "+model.getUserId());
                if (model.getUserId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                    viewHolder.textView.setText(model.getTitle());
                    Picasso.with(MyAds.this)
                            .load(model.getUrl())
                            .into(viewHolder.imageView);
                    viewHolder.adView.setVisibility(View.GONE);
                    if (position%5==0) {
                        viewHolder.adView.setVisibility(View.VISIBLE);
                        viewHolder.adView.loadAd(adRequest);

                    }
                }
            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(this,R.style.dialog);
        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setBackgroundColor(Color.TRANSPARENT);
        alertDialog.setView(progressBar);
        alertDialog.setTitle("Loading data..");
        final android.app.AlertDialog dialog = alertDialog.create();
        dialog.show();
        firebaseRecyclerAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                dialog.cancel();
            }
        });

        recyclerView.addOnItemTouchListener(new RecyclerItemClick(this, new RecyclerItemClick.OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {

                final Items items;
                items=firebaseRecyclerAdapter.getItem(position);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    new AlertDialog.Builder(MyAds.this,R.style.dialog)
                            .setTitle("Delete Ad?")
                            .setMessage(Html.fromHtml("Are you sure you want " +
                                    "to delete ad with title <b>"+items.getTitle()+"</b>",Html.FROM_HTML_MODE_LEGACY))
                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    removeVal(items,position);
                                }
                            }).setNegativeButton("keep",null).show();
                }else {
                    new AlertDialog.Builder(MyAds.this,R.style.dialog)
                            .setTitle("Delete ad?")
                            .setMessage(Html.fromHtml("Are you sure you want " +
                                    "to delete ad with title <b>"+items.getTitle()+"</b>"))
                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    removeVal(items,position);
                                }
                            }).setNegativeButton("keep",null).show();
                }
            }
        }));


    }
    public boolean removeVal(final Items items,final int position){
        (FirebaseStorage.getInstance().getReferenceFromUrl(items.getUrl()))
                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                img1=true;(FirebaseStorage.getInstance().getReferenceFromUrl(items.getUrl2()))
                        .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                img2=true;(FirebaseStorage.getInstance().getReferenceFromUrl(items.getUrl3()))
                                        .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                img3=true;
                                                firebaseRecyclerAdapter.getRef(position).removeValue();
                                                Snackbar.make(findViewById(R.id.MyadCordinatorLay),"Ad deleted",Snackbar.LENGTH_SHORT)
                                                        .show();
                                            }
                                        });
                            }
                        });
            }
        });


        return img3;
    }
}
