package com.example.prabowo.gamabot;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.Random;


public class FormForum extends AppCompatActivity implements View.OnClickListener {


    String Matkul;
    private EditText ETtambahjudul, ETtambahdesc, ETtambahimg;
    public static int langkah = 1;
    private static int jmlpost;
    private FloatingActionButton fab;
    private int counter = 0;
    private ImageView foto;
    private FirebaseStorage storage;
    private StorageReference mStorageRef;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    Random rand = new Random();
    long random = rand.nextLong();
    private String poster;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    DatabaseReference feedReference = databaseReference.child(ForumFragment.FEED_ROOT);
    DatabaseReference commentReference = databaseReference.child(ForumFragment.COMMENT_ROOT);
    final String key = databaseReference.push().getKey();

    ForumModel feed = new ForumModel();
    CommentModel comment = new CommentModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_forum);
        counter = 0;


        ETtambahjudul = (EditText) findViewById(R.id.ETtambahjudulkonsul);
        ETtambahdesc = (EditText) findViewById(R.id.ETtambahdesckonsul);
        //ETtambahimg = (EditText)findViewById(R.id.ETtambahimgkonsul);

        databaseReference = FirebaseDatabase.getInstance().getReference();


        fab = (FloatingActionButton) findViewById(R.id.fabaddkonsul);
        fab.setOnClickListener(this);
    }

    @Override
    public void onClick(final View v) {
        String judul = ETtambahjudul.getText().toString().trim();
        final String desc = ETtambahdesc.getText().toString().trim();


        if (TextUtils.isEmpty(judul)) {
            Toast.makeText(this, "Masukkan Judul", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(desc)) {
            Toast.makeText(this, "Masukkan Deskripsi", Toast.LENGTH_SHORT).show();
            return;
        }

        firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = firebaseAuth.getCurrentUser();


        if (v == fab) {


            feedReference.addValueEventListener(new ValueEventListener() {

                                                    @Override
                                                    public void onDataChange(DataSnapshot snapshot) {
                                                        feed.setIdUser(user.getUid());
                                                        feed.setName(user.getDisplayName());
                                                        feed.setPhotoAvatar(user.getPhotoUrl() == null ? "default_uri" : user.getPhotoUrl().toString());
                                                        feed.setText(desc);
                                                        feed.setTime(Calendar.getInstance().getTimeInMillis());
                                                        feed.setIdFeed(key);
                                                        databaseReference.child("feed").child(key).setValue(feed);

                                                    }


                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }

                                                }
            );


        }
        commentReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                comment.setUser(user.getDisplayName());
                comment.setComment("Halo guys");
                comment.setCommentId(user.getUid());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        finish();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));


    }


    public void upload(View view) {


        final CharSequence[] options = {"Ambil Foto", "Pilih dari Galeri"};


        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Ambil gambar dari :");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Ambil Foto")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 1);
                } else if (options[item].equals("Pilih dari Galeri")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);
                }
            }

        });

        builder.setNegativeButton("Kembali", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        foto = (ImageView) findViewById(R.id.imageView4);
        storage = FirebaseStorage.getInstance();
        mStorageRef = storage.getReferenceFromUrl("gs://gamabot-ebcc0.appspot.com");
        if (resultCode == RESULT_OK) {

            if (requestCode == 1) {


                Bitmap mphoto = (Bitmap) data.getExtras().get("data");
                foto.setImageBitmap(mphoto);
                foto.setMaxHeight(100);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                mphoto.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] dataimage = baos.toByteArray();


                StorageReference foto = mStorageRef.child(random + "Konsultasi.jpg");
                counter = 1;
                UploadTask uploadTask = foto.putBytes(dataimage);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        feed.setPhotoFeed(taskSnapshot.getMetadata().getDownloadUrl().toString());
                        databaseReference.child("feed").child(key).setValue(feed);
                    }
                });


            } else if (requestCode == 2) {


                Uri selectedImage = data.getData();


                String[] filePath = {MediaStore.Images.Media.DATA};

                Cursor c = this.getContentResolver().query(selectedImage, filePath, null, null, null);

                c.moveToFirst();

                int columnIndex = c.getColumnIndex(filePath[0]);

                String picturePath = c.getString(columnIndex);

                c.close();

                Bitmap thumbnail = BitmapFactory.decodeFile(picturePath);

                Log.w("Gambar dari Galeri", picturePath + "");
                foto.setImageBitmap(thumbnail);
                foto.setMaxHeight(100);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
                builder.setContentText("Foto Telah Terupload");


                StorageReference foto = mStorageRef.child(random + "Konsultasi.jpg");
                counter = 1;

                UploadTask uploadTask = foto.putFile(selectedImage);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        feed.setPhotoFeed(taskSnapshot.getMetadata().getDownloadUrl().toString());
                        databaseReference.child("feed").child(key).setValue(feed);
                    }
                });
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("Uploading...");
                progressDialog.show();

                uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        progressDialog.setMessage(((int) progress) + "% Uploaded .... ");

                        if (progress == 100) {
                            progressDialog.hide();
                        }
                    }
                })
                ;


            }


        }
    }

}




