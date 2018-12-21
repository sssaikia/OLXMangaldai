package com.sstudio.olxmangaldai;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialcamera.MaterialCamera;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import id.zelory.compressor.Compressor;

public class SellActivity extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference reference, categories;
    ImageView image1, image2, image3;
    Button update;
    EditText editText, phoneNo;
    boolean im1 = false, im2 = false, im3 = false;
    StorageReference storageReference;
    private StorageReference mStorageRef;
    FirebaseAuth firebaseAuth;
    String url1, url2, url3;
    //Bitmap bitmap1, bitmap2, bitmap3;
    private Intent picdata1, picdata2, picdata3;
    private File actualImage;
    private TextView textView, catText;
    Context context;
    FirebaseRecyclerAdapter<Items, RViewHolder> recyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell);
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference().child("items");
        mStorageRef = FirebaseStorage.getInstance().getReference();
        categories = firebaseDatabase.getReference().child("cat");
        firebaseAuth = FirebaseAuth.getInstance();

        Log.d(" reference ::  " + reference.push().getKey(), " removed - ::  " + reference.push().getKey().substring(1));
        context = this;
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.app_name);
        userLogin();
        image1 = findViewById(R.id.image1);
        image2 = findViewById(R.id.image2);
        image3 = findViewById(R.id.image3);

        update = findViewById(R.id.confirm);
        editText = findViewById(R.id.description);
        textView = findViewById(R.id.textCount);
        phoneNo = findViewById(R.id.phoneNo);


        image1.setBackgroundResource(R.drawable.normal_shape);
        image2.setBackgroundResource(R.drawable.normal_shape);
        image3.setBackgroundResource(R.drawable.normal_shape);
        catText = findViewById(R.id.category);


        catText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RecyclerView recyclerView = new RecyclerView(context);
                RecyclerView.LayoutManager l = new LinearLayoutManager(context);
                recyclerView.setLayoutManager(l);
                recyclerAdapter = new FirebaseRecyclerAdapter<Items, RViewHolder>(Items.class,
                        R.layout.recycler_lay, RViewHolder.class, categories) {
                    @Override
                    protected void populateViewHolder(RViewHolder viewHolder, Items model, int position) {
                        viewHolder.textView.setText(model.getCategory());
                        viewHolder.textView.setBackgroundResource(R.drawable.catback);
                        viewHolder.adView.setVisibility(View.GONE);
                        viewHolder.imageView.setVisibility(View.GONE);
                    }
                };
                recyclerView.setAdapter(recyclerAdapter);
                AlertDialog.Builder a = new AlertDialog.Builder(SellActivity.this);
                a.setTitle("Select category for the Ad.").setMessage("Tap to select.")
                        .setView(recyclerView);
                final AlertDialog b = a.create();
                b.show();
                recyclerView.addOnItemTouchListener(new RecyclerItemClick(context, new RecyclerItemClick.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        catText.setText(recyclerAdapter.getItem(position).getCategory());
                        Snackbar
                                .make(findViewById
                                        (R.id.sellcordinatorLayout), recyclerAdapter
                                        .getItem(position)
                                        .getCategory() + " selected.", Snackbar.LENGTH_SHORT).show();
                        //Toast.makeText(SellActivity.this, recyclerAdapter.getItem(position).getCategory() + " selected.", Toast.LENGTH_SHORT).show();
                        b.dismiss();
                    }
                }));

               /* recyclerView.addOnItemTouchListener(new RecyclerItemClick(context, new RecyclerItemClick.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        catText.setText(recyclerAdapter.getItem(position).getCategory());

                    }
                }));*/
            }
        });


        image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context, R.style.dialog)
                        .setTitle("Select source")
                        .setNegativeButton("Gallery", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent1 = new Intent(
                                        Intent.ACTION_PICK,
                                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(intent1, 111);
                            }
                        }).setPositiveButton("Camera", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new MaterialCamera(SellActivity.this)
                                .stillShot()
                                .start(111);
                    }
                }).show();

            }
        });
        image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context, R.style.dialog)
                        .setTitle("Select source")
                        .setNegativeButton("Gallery", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent1 = new Intent(
                                        Intent.ACTION_PICK,
                                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(intent1, 112);
                            }
                        }).setPositiveButton("Camera", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new MaterialCamera(SellActivity.this)
                                .stillShot()
                                .start(112);
                    }
                }).show();
            }
        });
        image3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context, R.style.dialog)
                        .setTitle("Select source")
                        .setNegativeButton("Gallery", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent1 = new Intent(
                                        Intent.ACTION_PICK,
                                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(intent1, 113);
                            }
                        }).setPositiveButton("Camera", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new MaterialCamera(SellActivity.this)
                                .stillShot()
                                .start(113);
                    }
                }).show();
            }
        });

        phoneNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 9) {
                    phoneNo.setBackgroundResource(R.drawable.success_shape);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                editText.setBackgroundResource(R.drawable.normal_shape);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                textView.setText(charSequence.length() + "");
                if (editText.getText().toString().trim().length() < 20) {
                    editText.setBackgroundResource(R.drawable.normal_shape);
                } else {
                    editText.setBackgroundResource(R.drawable.success_shape);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        ((EditText) findViewById(R.id.Adtitle)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                update.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        ((EditText) findViewById(R.id.Adtitle))
                .addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        if (charSequence.toString().trim().length() != 0) {
                            findViewById(R.id.Adtitle).setBackgroundResource(R.drawable.success_shape);
                        } else {
                            findViewById(R.id.Adtitle).setBackgroundResource(R.drawable.failed_shape);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editText.getText().toString().trim().length() < 20) {
                    editText.setBackgroundResource(R.drawable.failed_shape);
                }
                if (catText.getText().equals("Select Category")) {
                    new AlertDialog.Builder(context, R.style.dialog)
                            .setTitle("Select category")
                            .setMessage("Ad must be listed in a category.")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    catText.callOnClick();
                                }
                            }).show();
                } else {
                    if (phoneNo.getText().toString().length() > 9
                            && im1 && im2 && im3 &&
                            editText.getText().toString().trim().length()
                                    >= 20 && ((EditText) findViewById(R.id.Adtitle))
                            .getText().toString().trim().length() != 0) {


                        new AlertDialog.Builder(SellActivity.this, R.style.dialog)
                                .setTitle("Post ad?")
                                .setMessage("Check for any missing information.\n" +
                                        "Please post your ads with a well detailed description.\n" +
                                        "Once posted you cannot edit.")
                                .setPositiveButton("Post", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        update.setEnabled(false);
                                        ProgressBar progressBar = new ProgressBar(SellActivity.this);
                                        AlertDialog.Builder a = new AlertDialog.Builder(SellActivity.this);
                                        a.setView(progressBar);
                                        a.setTitle("Uploading data..");
                                        a.setCancelable(false);
                                        alertDialog = a.create();
                                        alertDialog.show();
                                        uploadData(picdata1, picdata2, picdata3);
                                    }
                                }).setNegativeButton("Edit", null)
                                .show();
                    } else {
                        new AlertDialog.Builder(SellActivity.this, R.style.dialog)
                                .setTitle("Data incomplete.")
                                .setMessage("1. All three photos must be selected.\n" +
                                        "2. Title must not be blank.\n" +
                                        "3. Description must have atleast 50 characters.\n" +
                                        "4. Valid contact details. (Phone no)\n" +
                                        "\nThank you.")
                                .setPositiveButton("OK", null).show();
                    }
                }
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                //Log.v(TAG,"Permission is granted");
                //File write logic here
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 123);
            }
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    Bitmap bit1, bit2, bit3;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 111 && resultCode == RESULT_OK) {


            try {
                actualImage = FileUtil.from(this, data.getData());
                bit1 = new Compressor(this).compressToBitmap(actualImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Picasso.with(SellActivity.this)
                    .load(getImageUri(SellActivity.this, bit1))
                    .into(image1);
            im1 = true;
            picdata1 = data;
        }
        if (requestCode == 112 && resultCode == RESULT_OK) {

            try {
                actualImage = FileUtil.from(this, data.getData());
                bit2 = new Compressor(this).compressToBitmap(actualImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Picasso.with(SellActivity.this)
                    .load(getImageUri(SellActivity.this, bit2))
                    .into(image2);
            im2 = true;
            picdata2 = data;


        }
        if (requestCode == 113 && resultCode == RESULT_OK) {

            try {
                actualImage = FileUtil.from(this, data.getData());
                bit3 = new Compressor(this).compressToBitmap(actualImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Picasso.with(SellActivity.this)
                    .load(getImageUri(SellActivity.this, bit3))
                    .into(image3);
            im3 = true;
            picdata3 = data;
        }
    }

    AlertDialog alertDialog;

    public void pushData() {
        if (i1 && i2 && i3) {

            long timeStamp = System.currentTimeMillis();
            timeStamp = -timeStamp;
            Items items = new Items(((EditText) findViewById(R.id.Adtitle))
                    .getText().toString()
                    .trim(), editText.getText()
                    .toString(), url1, url2, url3, FirebaseAuth
                    .getInstance().getCurrentUser().getUid()
                    , catText.getText().toString().trim()
                    , Long.parseLong(phoneNo.getText().toString().trim()), timeStamp);
            String id = reference.push().getKey();
            id = "I" + id.substring(1);
            reference.push().setValue(items, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    alertDialog.dismiss();

                    new AlertDialog.Builder(SellActivity.this, R.style.dialog)
                            .setTitle("Success.")
                            .setMessage("Your Ad has been successfully posted.\n" +
                                    "Thankyou.")
                            .setCancelable(false)
                            .setPositiveButton("View My Ads", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(SellActivity.this, MyAds.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }).show();
                }
            });
        }

    }

    boolean i1 = false, i2 = false, i3 = false;

    public void uploadData(final Intent data1, final Intent data2, final Intent data3) {
        storageReference = mStorageRef.child(FirebaseAuth.getInstance().getCurrentUser()
                .getUid()).child("images/" + ((EditText) findViewById(R.id.Adtitle))
                .getText().toString()
                .trim() + "/image1.jpg");
        //Log.d("path to image : ",""+getImageUri(this,(Bitmap) data.getExtras().get("data")));
        storageReference.putFile(getImageUri(SellActivity.this, bit1))
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        url1 = taskSnapshot.getDownloadUrl().toString();
                        image1.setBackgroundResource(R.drawable.success_shape);
                        i1 = true;

                        storageReference = mStorageRef.child(FirebaseAuth.getInstance().getCurrentUser()
                                .getUid()).child("images/" + ((EditText) findViewById(R.id.Adtitle))
                                .getText().toString()
                                .trim() + "/image2.jpg");
                        //Log.d("path to image : ",""+getImageUri(this,(Bitmap) data.getExtras().get("data")));
                        storageReference.putFile(getImageUri(SellActivity.this, bit2))
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        url2 = taskSnapshot.getDownloadUrl().toString();
                                        i2 = true;

                                        image2.setBackgroundResource(R.drawable.success_shape);


                                        storageReference = mStorageRef.child(FirebaseAuth.getInstance().getCurrentUser()
                                                .getUid()).child("images/" + ((EditText) findViewById(R.id.Adtitle))
                                                .getText().toString()
                                                .trim() + "/image3.jpg");
                                        //Log.d("path to image : ",""+getImageUri(this,(Bitmap) data.getExtras().get("data")));
                                        storageReference.putFile(getImageUri(SellActivity.this, bit3))
                                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                    @Override
                                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                                        url3 = taskSnapshot.getDownloadUrl().toString();
                                                        i3 = true;
                                                        image3.setBackgroundResource(R.drawable.success_shape);
                                                        pushData();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                image3.setBackgroundResource(R.drawable.failed_shape);
                                                Toast.makeText(SellActivity.this, "Image 3 Upload failed. Please try again.", Toast.LENGTH_SHORT).show();
                                            }
                                        });


                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                image2.setBackgroundResource(R.drawable.failed_shape);
                                Toast.makeText(SellActivity.this, "Image 2 Upload failed. Please try again.", Toast.LENGTH_SHORT).show();
                            }
                        });


                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                image1.setBackgroundResource(R.drawable.failed_shape);
                Toast.makeText(SellActivity.this, "Image 1 Upload failed. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this, R.style.dialog)
                .setTitle("Leave?")
                .setMessage("Are you sure you want to leave? All proggress will be lost.")
                .setPositiveButton("No", null)
                .setNegativeButton("Leave", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                }).show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.about) {
            new AlertDialog.Builder(this, R.style.dialog)
                    .setTitle("About")
                    .setIcon(R.mipmap.ic_launcher)
                    .setMessage(R.string.about_message)
                    .setPositiveButton("ok", null)
                    .show();
        }
        if (item.getItemId() == R.id.myads) {
            if (firebaseAuth.getCurrentUser() != null) {
                Intent intent = new Intent(SellActivity.this, MyAds.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else {
                new AlertDialog.Builder(this, R.style.dialog)
                        .setTitle("Failed!")
                        .setMessage("To view the Ads you have posted, you must SignIn first.\n" +
                                "Go to the sell section to SignIn.")
                        .setPositiveButton("ok", null)
                        .show();
            }
        }
        if (item.getItemId() == R.id.signOut) {
            if (firebaseAuth.getCurrentUser() != null) {
                firebaseAuth.signOut();
                finish();
            } else {
                new AlertDialog.Builder(this, R.style.dialog)
                        .setMessage("Already logged out.")
                        .setPositiveButton("ok", null)
                        .show();
            }
        }
        if (item.getItemId() == R.id.exit) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void userLogin() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Picasso.with(context)
                    .load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl())
                    .into((ImageView) findViewById(R.id.user));
        } else {
            ((ImageView) findViewById(R.id.user))
                    .setImageResource(R.drawable.ic_person_black_24dp);
        }
    }
}
