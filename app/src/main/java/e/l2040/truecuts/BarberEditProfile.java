package e.l2040.truecuts;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;


public class BarberEditProfile extends Fragment {

    FragmentManager manager;

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PICK_IMAGES_REQUEST = 2;

    ImageView profileImage;
    Button chooseProfileImage;
    String username;
    EditText editUsername;
    Button upload;
    Button chooseImages;

    RecyclerView recyclerView;

    List<PhotoUri> photoList;
    List<Uri> previousWorkUris;
    List<String> previousWorkDownloadUrls;
    PhotoUriAdapter photoUriAdapter;


    Uri profileUri;
    Uri imageUri;
    Uri tempUri;


    private StorageReference storageRef;
    private DatabaseReference databaseRef;
    private DatabaseReference databaseRefTwo;

    private StorageReference fileReference;

    int counter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_barber_edit_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        manager = getActivity().getSupportFragmentManager();

        databaseRefTwo = FirebaseDatabase.getInstance().getReference().child("barbers")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("username");

        storageRef = FirebaseStorage.getInstance().getReference("Barbers")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        databaseRef = FirebaseDatabase.getInstance().getReference().child("barbers")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("images");

        chooseImages = view.findViewById(R.id.chooseImages);

        photoList = new ArrayList<>();
        previousWorkUris = new ArrayList<>();
        previousWorkDownloadUrls = new ArrayList<>();
        photoUriAdapter = new PhotoUriAdapter(getContext(), photoList);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        recyclerView.setAdapter(photoUriAdapter);

        profileImage = view.findViewById(R.id.profileImage);
        chooseProfileImage = view.findViewById(R.id.chooseProfileImage);
        upload = view.findViewById(R.id.upload);

        editUsername = view.findViewById(R.id.editUsername);


        chooseProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooserSingle();
            }
        });

        chooseImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previousWorkUris.clear();
                photoList.clear();
                openFileChooserMultiple();
            }
        });


        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counter = 0;
                uploadFiles();
            }
        });

    }

    private void openFileChooserMultiple(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), PICK_IMAGES_REQUEST);
    }

    private void openFileChooserSingle(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGES_REQUEST && resultCode == RESULT_OK
                && data!= null && data.getClipData() != null) {
            int count = data.getClipData().getItemCount(); //evaluate the count before the for loop --- otherwise, the count is evaluated every loop.
            for (int i = 0; i < count; i++) {
                imageUri = data.getClipData().getItemAt(i).getUri();
                photoList.add(new PhotoUri(imageUri));

                //create uri array for upload to firebase
                previousWorkUris.add(imageUri);
            }
            photoUriAdapter.notifyDataSetChanged();
        }
        else if (data.getData() != null){
            imageUri = data.getData();
            if (requestCode == PICK_IMAGES_REQUEST){
                previousWorkUris.add(imageUri);
                photoList.add(new PhotoUri(imageUri));
                photoUriAdapter.notifyDataSetChanged();
            }
            if (requestCode == PICK_IMAGE_REQUEST){
                Picasso.with(getActivity()).load(imageUri).fit().centerCrop().into(profileImage);
                profileUri = imageUri;
            }

        }
    }



    private void uploadFiles(){
        if (previousWorkUris != null && profileUri != null){

            if (counter < previousWorkUris.size() + 1) {

                StorageReference profileImageRef = storageRef.child("ProfileImage");
                StorageReference previousWorkImagesRef;


                if (counter == 0) {
                    tempUri = profileUri;
                    fileReference = profileImageRef;
                }
                if (counter > 0) {
                    String uuid = UUID.randomUUID().toString();

                    tempUri = previousWorkUris.get(counter - 1);
                    previousWorkImagesRef = storageRef.child("PreviousWorkImages").child(uuid);
                    fileReference = previousWorkImagesRef;
                }


                Task<Uri> urlTask = fileReference.putFile(tempUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        // Continue with the task to get the download URL
                        return fileReference.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            //Log.i("testingOrder", downloadUri.toString());

                            if (counter == 0) {
                                databaseRef.child("ProfileImage").setValue(downloadUri.toString());
                            }
                            if (counter > 0) {
                                previousWorkDownloadUrls.add(downloadUri.toString());
                                if (counter == previousWorkUris.size()) {
                                    databaseRef.child("PreviousWorkImages").setValue(previousWorkDownloadUrls);
                                    previousWorkDownloadUrls.clear();

                                }
                            }

                            counter += 1;
                            uploadFiles();
                        } else {
                            // Handle failures
                            //.....
                            Log.i("testingOrder", "error");
                        }
                    }
                });
            }
        }
        username = editUsername.getText().toString();
        databaseRefTwo.setValue(username);
        //Toast.makeText(getContext(), "Profile Updated", Toast.LENGTH_SHORT).show();
        manager.beginTransaction().replace(R.id.fragment_container, new BarberHomeFragment()).commit();

    }

}

