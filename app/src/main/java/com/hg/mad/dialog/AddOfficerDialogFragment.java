 package com.hg.mad.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hg.mad.R;
import com.hg.mad.model.Officer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

 /**
 * Dialog Fragment containing filter form.
 */
public class AddOfficerDialogFragment extends DialogFragment implements View.OnClickListener {


    private View rootView;

    private Button add;
    private Button cancel;
    private Button upload;
    private Button camera;

    private EditText name;
    private EditText position;
    private EditText contact;

    private DocumentReference chapterRef;

    private String profile;


     FirebaseStorage storage;
     StorageReference storageReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.dialog_add_officer, container, false);

        add = (Button) rootView.findViewById(R.id.button_addOfficer);
        cancel = (Button) rootView.findViewById(R.id.button_cancel);
        upload = (Button) rootView.findViewById(R.id.button_upload);
        camera = (Button) rootView.findViewById(R.id.button_camera);

        name = (EditText) rootView.findViewById(R.id.editText_name);
        position = (EditText) rootView.findViewById(R.id.editText_position);
        contact = (EditText) rootView.findViewById(R.id.editText_contact);



        profile = null;

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        add.setOnClickListener(this);
        cancel.setOnClickListener(this);
        upload.setOnClickListener(this);
        camera.setOnClickListener(this);

        return rootView;
    }

     public void setChapterRef(DocumentReference chapterRef) {
         this.chapterRef = chapterRef;
     }

     @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_addOfficer:
                if(name.getText()!=null && position.getText()!=null && contact.getText()!=null){
                    onAddClicked();
                } else{
                    Toast.makeText(getContext(), "Fill out fields", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.button_cancel:
                onCancelClicked();
                break;
            case R.id.button_upload:
                onUploadClicked();
                break;
            case R.id.button_camera:
                onCameraClicked();
                break;
        }
    }

     @Override
     public void onResume() {
         super.onResume();
         name.setText(null);
         position.setText(null);
         contact.setText(null);
         getDialog().getWindow().setLayout(
                 ViewGroup.LayoutParams.MATCH_PARENT,
                 ViewGroup.LayoutParams.WRAP_CONTENT);
     }

    public void onUploadClicked(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }

    public void onCameraClicked(){
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 0);
    }


    public void onAddClicked() {
        Map<String, Object> newOfficer = new HashMap<>();
        newOfficer.put("name",name.getText().toString());
        newOfficer.put("position",position.getText().toString());
        newOfficer.put("contact", contact.getText().toString());

        if(profile!=null){

            newOfficer.put("profile", profile);
        }
        chapterRef.collection("officers").add(newOfficer);
        System.out.println("officer added");

        dismiss();
        Toast.makeText(getContext(), "Officer Added", Toast.LENGTH_SHORT).show();
    }

    public void onCancelClicked() {
        dismiss();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==Activity.RESULT_OK){
            Bitmap bm = null;
            switch (requestCode){
                case 0:
                    System.out.println("cas0");
                     bm= (Bitmap) data.getExtras().get("data");
                    break;
                case 1:
                    Uri contentURI = data.getData();
                    try {
                        bm = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), contentURI);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    break;

            }
            System.out.println("completed");
            if(bm!=null) {
                System.out.println("uploaded");
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                double scale  =550/bm.getHeight();
                Bitmap scaled  = Bitmap.createScaledBitmap(bm, (int)(bm.getWidth()*scale), (int)(bm.getHeight()*scale), true);
                scaled.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[]picture = stream.toByteArray();

                profile = "images/"+UUID.randomUUID().toString()+".jpg";

                storageReference.child(profile).putBytes(picture);

                Toast.makeText(getContext(), "Image Uploaded", Toast.LENGTH_SHORT).show();
                bm.recycle();
            }


        }
    }


}
