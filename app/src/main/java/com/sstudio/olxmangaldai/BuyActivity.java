package com.sstudio.olxmangaldai;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import java.util.Arrays;

public class BuyActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    FirebaseRecyclerAdapter<Items, RViewHolder> firebaseRecyclerAdapter;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;
    AdRequest adRequest;
    Context context;
    private FirebaseRecyclerAdapter<Items, CategoryViewHolder> recyclerAdapter;
    private DatabaseReference categories;
    private FirebaseAuth mAuth;
    int spancount = 2;
    StaggeredGridLayoutManager linearLayoutManager;
    Query query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy);
        context = this;
        mAuth = FirebaseAuth.getInstance();
        recyclerView = findViewById(R.id.recyclerView);
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference().child("items");
        categories = firebaseDatabase.getReference().child("cat");
        query = reference.orderByChild("timeSamop");
        //Log.d("Firebase msg token : ", FirebaseInstanceId.getInstance().getToken());
        userLogin();
        findViewById(R.id.user).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAuth.getCurrentUser() != null) {
                    new AlertDialog.Builder(context, R.style.dialog)
                            .setTitle("Hi \n" + mAuth.getCurrentUser().getDisplayName())
                            .setMessage("Do you want to sign out?")
                            .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    mAuth.signOut();
                                    userLogin();
                                    if (mAuth.getCurrentUser() == null) {
                                        Snackbar.make(findViewById(R.id.buycoordinatelayout),
                                                "Successfully signed out", Snackbar.LENGTH_SHORT)
                                                .show();
                                    }
                                }
                            }).setNegativeButton("No", null).show();
                } else {
                    startActivityForResult(
                            AuthUI.getInstance().createSignInIntentBuilder()
                                    .setTheme(R.style.AppTheme)
                                    .setAvailableProviders(Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                            new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                                    .setTosUrl("https://www.google.com/policies/terms/")
                                    .setIsSmartLockEnabled(false)
                                    .build(),
                            11);
                }
            }
        });


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        MobileAds.initialize(context, "ca-app-pub-5433263595056427~5076912269");
        adRequest = new AdRequest.Builder()./*addTestDevice("3583D003E2DCC4625291FB38D6C8CD30").*/build();
        Log.d(" reference  ::", reference.toString());
        
        RecyclerView recyclerView1 = findViewById(R.id.buyCat);
        RecyclerView.LayoutManager l = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL);
        recyclerView1.setLayoutManager(l);
        recyclerAdapter = new FirebaseRecyclerAdapter<Items, CategoryViewHolder>
                (Items.class, R.layout.category_lay, CategoryViewHolder.class, categories) {
            @Override
            protected void populateViewHolder(CategoryViewHolder viewHolder, Items model, int position) {
                viewHolder.textView.setText(model.getCategory());
            }
        };
        recyclerView1.setAdapter(recyclerAdapter);
        recyclerView1.addOnItemTouchListener(new RecyclerItemClick(context, new RecyclerItemClick.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                firebaseRecyclerAdapter.cleanup();
                firebaseRecyclerAdapter.notifyDataSetChanged();
                ((TextView) findViewById(R.id.username)).setText("Viewing in " + recyclerAdapter.getItem(position).getCategory());
                query = reference
                        .orderByChild("category").equalTo(recyclerAdapter.getItem(position).getCategory());
                if (recyclerAdapter.getItem(position).getCategory().equals("All")) {
                    query = reference.orderByChild("timeSamop");
                    reloadMethod(query);
                } else {
                    reloadMethod(query);
                }

            }
        }));

        reloadMethod(query);

        findViewById(R.id.material_design_floating_action_menu_item1)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (linearLayoutManager.getSpanCount() == 2) {
                            linearLayoutManager.setSpanCount(1);
                            spancount = 1;
                            ((FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item1))
                                    .setLabelText("Show grid");
                            ((FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item1))
                                    .setImageResource(R.drawable.ic_grid_on_black_24dp);
                            firebaseRecyclerAdapter.cleanup();
                            firebaseRecyclerAdapter.notifyDataSetChanged();
                            reloadMethod(query);
                        } else{
                            spancount = 2;
                            ((FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item1))
                                    .setImageResource(R.drawable.ic_view_list_black_24dp);
                            ((FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item1))
                                    .setLabelText("Show list");
                            linearLayoutManager.setSpanCount(2);
                            firebaseRecyclerAdapter.cleanup();
                            firebaseRecyclerAdapter.notifyDataSetChanged();
                            reloadMethod(query);
                        }
                    }
                });
        findViewById(R.id.material_design_floating_action_menu_item2)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mAuth.getCurrentUser() != null) {
                            startActivity(new Intent(BuyActivity.this, SellActivity.class));
                        } else {
                            startActivityForResult(
                                    AuthUI.getInstance().createSignInIntentBuilder()
                                            .setTheme(R.style.AppTheme)
                                            .setAvailableProviders(Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                                    new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                                            .setTosUrl("https://www.google.com/policies/terms/")
                                            .setIsSmartLockEnabled(false)
                                            .build(),
                                    111);
                        }
                    }
                });
        findViewById(R.id.material_design_floating_action_menu_item3)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new android.support.v7.app.AlertDialog.Builder(context, R.style.dialog)
                                .setTitle("About")
                                .setIcon(R.mipmap.ic_launcher)
                                .setMessage(R.string.about_message)
                                .setPositiveButton("ok", null)
                                .show();
                    }
                });


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            android.Manifest.permission.CAMERA,
                            android.Manifest.permission.INTERNET,
                            android.Manifest.permission.CALL_PHONE}
                    , 111);
        }

    }

    public void reloadMethod(Query query1) {
        //Log.e("Reload called", "reload called");
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, R.style.dialog);
        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setBackgroundColor(Color.TRANSPARENT);
        alertDialog.setView(progressBar);
        alertDialog.setTitle("Loading data..");
        final AlertDialog dialog = alertDialog.create();
        dialog.show();
        linearLayoutManager = new StaggeredGridLayoutManager(spancount, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemViewCacheSize(20);
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Items, RViewHolder>
                (Items.class, R.layout.recycler_lay, RViewHolder.class, query1) {
            @Override
            protected void populateViewHolder(RViewHolder viewHolder, Items model, int position) {
                viewHolder.textView.setText(model.getTitle());
                Picasso.with(BuyActivity.this)
                        .load(model.getUrl())
                        .into(viewHolder.imageView);
                viewHolder.adView.setVisibility(View.GONE);
                if (position % 10 == 0) {
                    viewHolder.adView.setVisibility(View.VISIBLE);
                    viewHolder.adView.loadAd(adRequest);
                }
            }

            @Override
            public void onDataChanged() {
                //Log.d("ondatachanged. count : ", firebaseRecyclerAdapter.getItemCount() + "");
                if (firebaseRecyclerAdapter.getItemCount() == 0) {
                    (findViewById(R.id.noitem)).setVisibility(View.VISIBLE);
                    dialog.dismiss();
                } else {
                    findViewById(R.id.noitem).setVisibility(View.GONE);
                }

            }

            @Override
            public RViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                RViewHolder r = super.onCreateViewHolder(parent, viewType);
                r.setOnClickListener(new RViewHolder.ClickListener() {
                    @Override
                    public void onItemClick(View view, final int position) {
                        View view1 = getLayoutInflater().inflate(R.layout.zoom_image_lay, null);
                        ImageView imageView, imageView1, imageView2;
                        TextView textView = view1.findViewById(R.id.descView);
                        textView.setText(firebaseRecyclerAdapter.getItem(position).getTexts());
                        imageView = view1.findViewById(R.id.imageview1);
                        imageView1 = view1.findViewById(R.id.imageview2);
                        imageView2 = view1.findViewById(R.id.imageview3);
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog(firebaseRecyclerAdapter.getItem(position).getUrl());
                            }
                        });
                        imageView1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog(firebaseRecyclerAdapter.getItem(position).getUrl2());
                            }
                        });
                        imageView2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog(firebaseRecyclerAdapter.getItem(position).getUrl3());
                            }
                        });
                        view1.findViewById(R.id.call)
                                .setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(Intent.ACTION_CALL);

                                        intent.setData(Uri.parse("tel:" + firebaseRecyclerAdapter.getItem(position).getPhone()));
                                        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                            // TODO: Consider calling
                                            //    ActivityCompat#requestPermissions
                                            // here to request the missing permissions, and then overriding
                                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                            //                                          int[] grantResults)
                                            // to handle the case where the user grants the permission. See the documentation
                                            // for ActivityCompat#requestPermissions for more details.
                                            Snackbar.make(findViewById(R.id.buycoordinatelayout),"Call failed. Permission denied",Snackbar.LENGTH_SHORT).show();
                                        }
                                        context.startActivity(intent);
                                    }
                                });
                        ((TextView) view1.findViewById(R.id.numberview))
                                .setText(firebaseRecyclerAdapter.getItem(position).getPhone() + "");
                        Picasso.with(BuyActivity.this)
                                .load(firebaseRecyclerAdapter.getItem(position).getUrl())
                                .into(imageView);
                        Picasso.with(BuyActivity.this)
                                .load(firebaseRecyclerAdapter.getItem(position).getUrl2())
                                .into(imageView1);
                        Picasso.with(BuyActivity.this)
                                .load(firebaseRecyclerAdapter.getItem(position).getUrl3())
                                .into(imageView2);
                        new AlertDialog.Builder(BuyActivity.this, R.style.dialog)
                                .setView(view1)
                                .show();
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {

                    }
                });
                return r;
            }
        };
recyclerView.setAdapter(firebaseRecyclerAdapter);
        recyclerView.getLayoutManager().setItemPrefetchEnabled(true);
        firebaseRecyclerAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                dialog.cancel();
            }
        });
    }

    public void dialog(String data) {
        ImageView imageView = new ImageView(context);
        Picasso.with(BuyActivity.this)
                .load(data)
                .into(imageView);
        new AlertDialog.Builder(context, R.style.dialog)
                .setView(imageView)
                .show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 111 && resultCode == RESULT_OK) {
            Intent intent = new Intent(BuyActivity.this, SellActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        new android.support.v7.app.AlertDialog.Builder(this, R.style.dialog)
                .setTitle("Exit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                }).setNegativeButton("NO", null)
                .setNeutralButton("Share", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT,
                                "Introducing the new application Intown. An application for trading both your old" +
                                        " and new goods, available at Mangaldai and nearby regions(Darrang)." +
                                        " We are privileged to share the application preview with " +
                                        "you." +
                                        "Download and share your thoughts at : https://sstudiome.blogspot.com/2017/09/have-you-heard-of-new-application.html");
                        sendIntent.setType("text/plain");
                        startActivity(sendIntent);

                        //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/sstudiome")));
                    }
                }).show();
    }

    public void userLogin() {
        if (mAuth.getCurrentUser() != null) {
            Picasso.with(context)
                    .load(mAuth.getCurrentUser().getPhotoUrl())
                    .into((ImageView) findViewById(R.id.user));
            Snackbar.make(findViewById(R.id.buycoordinatelayout),
                    "Welcome " + mAuth.getCurrentUser()
                            .getDisplayName(), Snackbar.LENGTH_SHORT)
                    .show();
        } else {
            ((ImageView) findViewById(R.id.user))
                    .setImageResource(R.drawable.ic_person_black_24dp);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        userLogin();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
