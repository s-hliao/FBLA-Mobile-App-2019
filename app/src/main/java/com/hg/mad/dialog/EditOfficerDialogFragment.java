 package com.hg.mad.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hg.mad.R;
import com.hg.mad.model.Officer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

 /**
 * Dialog Fragment containing filter form.
 */
public class EditOfficerDialogFragment extends DialogFragment implements View.OnClickListener {


     private View rootView;

     private Button edit;
     private Button cancel2;
     private LinearLayout upload;
     private LinearLayout camera;
     private Button remove;

     private EditText name;
     private EditText position;
     private EditText contact;

     private DocumentSnapshot officer;

     private String profile;

     FirebaseStorage storage;
     StorageReference storageReference;


     @Nullable
     @Override
     public View onCreateView(@NonNull LayoutInflater inflater,
                              @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
         rootView = inflater.inflate(R.layout.dialog_remove_officer, container, false);

         edit = (Button) rootView.findViewById(R.id.button_edit_officer);
         cancel2 = (Button) rootView.findViewById(R.id.button_cancel2);
         remove = (Button) rootView.findViewById(R.id.button_remove);
         upload = (LinearLayout) rootView.findViewById(R.id.layout_upload);
         camera = (LinearLayout) rootView.findViewById(R.id.layout_camera);

         name = (EditText) rootView.findViewById(R.id.editText_name);
         position = (EditText) rootView.findViewById(R.id.editText_position);
         contact = (EditText) rootView.findViewById(R.id.editText_contact);

         if(officer.get("profile")!=null){
             profile = officer.get("profile").toString();
         }


         edit.setOnClickListener(this);
         cancel2.setOnClickListener(this);
         upload.setOnClickListener(this);
         camera.setOnClickListener(this);
         remove.setOnClickListener(this);

         name.setText((String) officer.get("name"));
         position.setText((String) officer.get("position"));
         contact.setText((String) officer.get("contact"));

         storage = FirebaseStorage.getInstance();
         storageReference = storage.getReference();

         return rootView;
     }

     public void setOfficer(DocumentSnapshot officer){
         this.officer = officer;
     }

     @Override
     public void onClick(View v) {
         switch (v.getId()) {
             case R.id.button_edit_officer:
                 onEditClicked();
                 break;
             case R.id.button_cancel2:
                 onCancelClicked();;
                 break;
             case R.id.button_remove:
                 onRemoveClicked();
                 break;
             case R.id.layout_upload:
                 onUploadClicked();
                 break;
             case R.id.layout_camera:
                 onCameraClicked();
                 break;
         }
     }

     @Override
     public void onResume() {
         super.onResume();
         name.setText(officer.get("name").toString());
         position.setText(officer.get("position").toString());
         contact.setText(officer.get("contact").toString());

         getDialog().getWindow().setLayout(
                 ViewGroup.LayoutParams.MATCH_PARENT,
                 ViewGroup.LayoutParams.WRAP_CONTENT);
     }

     public void onEditClicked(){
         Map<String, Object> updates = new HashMap<String, Object>();
         updates.put("name", name.getText().toString());
         updates.put("position", position.getText().toString());
         updates.put("contact", contact.getText().toString());

         if(profile!=null){
            updates.put("profile", profile);
         }

         officer.getReference().update(updates);
         dismiss();
         Toast.makeText(getContext(), "Officer Edited", Toast.LENGTH_SHORT).show();
     }

     public void onRemoveClicked(){
        officer.getReference().delete();
        dismiss();
        Toast.makeText(getContext(), "Officer Deleted", Toast.LENGTH_SHORT).show();
     }

     public void onUploadClicked(){
         Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
         startActivityForResult(intent, 1);
     }

     public void onCameraClicked(){
         Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
         startActivityForResult(intent, 0);
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
                 double scale  =550.0/bm.getHeight();
                 Bitmap scaled  = Bitmap.createScaledBitmap(bm, (int)(bm.getWidth()*scale), (int)(bm.getHeight()*scale), true);
                 scaled.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                 byte[]picture = stream.toByteArray();

                 if(profile!=null) storageReference.child(profile).delete();

                 profile = "images/"+UUID.randomUUID().toString()+".jpg";
                 storageReference.child(profile).putBytes(picture);

                 Toast.makeText(getContext(), "Image Uploaded", Toast.LENGTH_SHORT).show();
                 bm.recycle();
             }


         }
     }


 }