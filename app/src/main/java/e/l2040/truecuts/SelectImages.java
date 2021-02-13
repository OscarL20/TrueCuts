package e.l2040.truecuts;

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


public class SelectImages extends Fragment {

    FragmentManager manager;

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PICK_IMAGES_REQUEST = 2;
    //true = profile image, false = shop image
    boolean profileOrShop;

    ImageView profileImage;
    ImageView shopImage;
    Button chooseProfileImage;
    Button chooseShopImage;
    Button chooseImages;
    Button upload;
    RecyclerView recyclerView;
    PhotoUriAdapter photoUriAdapter;
    List<PhotoUri> photoList;

    Uri imageUri;
    Uri profileUri;
    Uri shopUri;
    Uri tempUri;

    List<Uri> previousWorkUris;
    List<String> previousWorkDownloadUrls;


    private StorageReference storageRef;
    private DatabaseReference databaseRef;

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
        return inflater.inflate(R.layout.fragment_select_images, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        manager = getActivity().getSupportFragmentManager();

        storageRef = FirebaseStorage.getInstance().getReference("Barbers")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        databaseRef = FirebaseDatabase.getInstance().getReference().child("barbers")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("images");

        profileImage = view.findViewById(R.id.profileImage);
        shopImage = view.findViewById(R.id.shopImage);
        chooseProfileImage = view.findViewById(R.id.chooseProfileImage);
        chooseShopImage = view.findViewById(R.id.chooseShopImage);
        chooseImages = view.findViewById(R.id.chooseImages);
        upload = view.findViewById(R.id.upload);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        previousWorkUris = new ArrayList<>();
        previousWorkDownloadUrls = new ArrayList<>();
        photoList = new ArrayList<>();
        photoUriAdapter = new PhotoUriAdapter(getContext(), photoList);
        recyclerView.setAdapter(photoUriAdapter);

        chooseProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profileOrShop = true;
                openFileChooserSingle();
            }
        });

        chooseShopImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profileOrShop = false;
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
        && data!= null && data.getClipData() != null){
            int count = data.getClipData().getItemCount(); //evaluate the count before the for loop --- otherwise, the count is evaluated every loop.
            for(int i = 0; i < count; i++) {
                imageUri = data.getClipData().getItemAt(i).getUri();
                photoList.add(new PhotoUri(imageUri));

                //create uri array for upload to firebase
                previousWorkUris.add(imageUri);
            }
            photoUriAdapter.notifyDataSetChanged();
        }else if (data.getData() != null){
            imageUri = data.getData();
            if (requestCode == PICK_IMAGES_REQUEST){
                previousWorkUris.add(imageUri);
                photoList.add(new PhotoUri(imageUri));
                photoUriAdapter.notifyDataSetChanged();
            }
            if (profileOrShop && requestCode == PICK_IMAGE_REQUEST){
                Picasso.with(getActivity()).load(imageUri).fit().centerCrop().into(profileImage);
                profileUri = imageUri;
            }
            if (!profileOrShop && requestCode == PICK_IMAGE_REQUEST){
                Picasso.with(getActivity()).load(imageUri).fit().centerCrop().into(shopImage);
                shopUri = imageUri;
            }

        }
    }



    private void uploadFiles(){
        if (previousWorkUris != null && profileUri != null && shopUri != null){

            //int counter;

            if (counter < previousWorkUris.size() + 2){

                StorageReference profileImageRef = storageRef.child("ProfileImage");
                StorageReference barberShopImageRef = storageRef.child("BarberShopImage");
                StorageReference previousWorkImagesRef;


                if (counter == 0){
                    tempUri = profileUri;
                    fileReference = profileImageRef;
                }
                if (counter == 1){
                    tempUri = shopUri;
                    fileReference = barberShopImageRef;
                }
                if (counter > 1) {
                    String uuid = UUID.randomUUID().toString();

                    tempUri = previousWorkUris.get(counter - 2);
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

                                if (counter == 0){
                                    databaseRef.child("ProfileImage").setValue(downloadUri.toString());
                                }
                                if (counter == 1){
                                    databaseRef.child("BarberShopImage").setValue(downloadUri.toString());
                                }
                                if (counter > 1) {
                                    previousWorkDownloadUrls.add(downloadUri.toString());
                                    //checking for final index
                                    if (counter == previousWorkUris.size() + 1){
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
            //Toast.makeText(getContext(), "Profile Updated", Toast.LENGTH_SHORT).show();
            manager.beginTransaction().replace(R.id.fragment_container, new BarberHomeFragment()).commit();
            }
        }
    }

