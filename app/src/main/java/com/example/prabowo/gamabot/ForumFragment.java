package com.example.prabowo.gamabot;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
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
import com.sandrios.sandriosCamera.internal.SandriosCamera;
import com.sandrios.sandriosCamera.internal.configuration.CameraConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link ForumFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ForumFragment extends Fragment implements  View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public static final String FEED_ROOT = "feed";
    public static final String LIKE_ROOT = "like";
    public static final String COMMENT_ROOT = "comment";
    public static final int GET_PHOTO = 11;

    private RecyclerView recyclerView;
    private ProgressBar mProgressBar;

    private List<ForumModel> mList;

    private boolean flagLike;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private CustomImageView IVpost;
    private FirebaseStorage storage;
    private StorageReference mStorageRef;

    //private OnFragmentInteractionListener mListener;

    public ForumFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ForumFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ForumFragment newInstance(String param1, String param2) {
        ForumFragment fragment = new ForumFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forum, container, false);



        mProgressBar = (ProgressBar) view.findViewById(R.id.pb);
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(this);
        recyclerView = (RecyclerView) view.findViewById(R.id.rv_list_feed);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    /*public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }*/

    @Override
    public void onResume() {
        super.onResume();

        retrieveData();
    }

    private void getPhoto(){
        new SandriosCamera(getActivity(), GET_PHOTO)
                .setShowPicker(false)
                .setMediaAction(CameraConfiguration.MEDIA_ACTION_PHOTO)
                .enableImageCropping(true)
                .launchCamera();
    }

    @Override
   public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GET_PHOTO && resultCode == RESULT_OK) {
            Log.e("File", "" + data.getStringExtra(CameraConfiguration.Arguments.FILE_PATH));
            sendPhotoFirebase( data.getStringExtra(CameraConfiguration.Arguments.FILE_PATH) );
        }

    }

    private void sendPhotoFirebase(String file) {
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setTitle("Uploading");
        dialog.show();

        Uri uri = Uri.fromFile(new File(file));
        StorageReference reference = FirebaseStorage.getInstance().getReference().child("image_feed/" + Calendar.getInstance().getTime() + ".jpg");
        reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @SuppressWarnings("VisibleForTests")
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(FEED_ROOT);
                String key = databaseReference.push().getKey();

                if (user != null) {
                    ForumModel feed = new ForumModel();
                    feed.setIdUser(user.getUid());
                    feed.setName(user.getDisplayName());
                    feed.setPhotoAvatar(user.getPhotoUrl() == null ? "default_uri" : user.getPhotoUrl().toString());
                    feed.setPhotoFeed(taskSnapshot.getDownloadUrl().toString());
                    feed.setText("Percobaan Forum");
                    feed.setTime(Calendar.getInstance().getTimeInMillis());
                    feed.setIdFeed(key);
                    databaseReference.child(key).setValue(feed);
                }

                dialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                Toast.makeText(getActivity(), "Error " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                @SuppressWarnings("VisibleForTests")
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                dialog.setMessage("Uploaded " + ((int) progress) + "%...");
            }
        });
    }


    private void retrieveData() {
        showProgress(true);
        mList = new ArrayList<>();
        DatabaseReference feedReference = FirebaseDatabase.getInstance().getReference().child(FEED_ROOT).orderByChild("time").getRef();
        feedReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ForumModel feed = snapshot.getValue(ForumModel.class);
                    mList.add(feed);
                }
                initRecyclerView(mList, getContext());
                showProgress(false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initRecyclerView(List<ForumModel> list, Context context) {
        recyclerView.setAdapter(new AdapterForum(list, context));
    }

    private void showProgress(boolean b) {
        mProgressBar.setVisibility(b ? View.VISIBLE : View.GONE);
    }

    private void addLike(final ForumModel feed) {
        flagLike = true;
        final DatabaseReference referenceLike = FirebaseDatabase.getInstance().getReference().child(LIKE_ROOT);
        final FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();
        referenceLike.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (flagLike) {
                    if (dataSnapshot.child(feed.getIdFeed()).hasChild(auth.getUid())) {
                        referenceLike.child(feed.getIdFeed()).child(auth.getUid()).removeValue();
                        flagLike = false;
                    } else {
                        referenceLike.child(feed.getIdFeed()).child(auth.getUid()).setValue(true);
                        flagLike = false;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    /*@Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }*/

   /* @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }*/



    /*@Override
    public void onClickItemFeed(int position, View view) {
        ForumModel feed = mList.get(position);
        switch (view.getId()) {
            case R.id.iv_like:
                addLike(feed);
                break;
        }
    }*/

    @Override
    public void onClick(View view) {
        startActivity(new Intent(getActivity(), FormForum.class));






    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    /*public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }*/
}
