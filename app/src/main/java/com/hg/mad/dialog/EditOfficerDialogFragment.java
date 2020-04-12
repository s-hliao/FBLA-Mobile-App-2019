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
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.hg.mad.R;
import com.hg.mad.model.Officer;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

 /**
 * Dialog Fragment containing filter form.
 */
public class EditOfficerDialogFragment extends DialogFragment implements View.OnClickListener {


     private View rootView;

     private Button edit;
     private Button cancel1;
     private Button cancel2;
     private Button upload;
     private Button camera;
     private Button remove;

     private EditText name;
     private EditText position;
     private EditText contact;

     private DocumentSnapshot officer;

     private byte[] profile;


     @Nullable
     @Override
     public View onCreateView(@NonNull LayoutInflater inflater,
                              @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
         rootView = inflater.inflate(R.layout.dialog_remove_officer, container, false);

         edit = (Button) rootView.findViewById(R.id.button_edit_officer);
         cancel1 = (Button) rootView.findViewById(R.id.button_cancel1);
         cancel2 = (Button) rootView.findViewById(R.id.button_cancel2);
         remove = (Button) rootView.findViewById(R.id.button_remove);
         upload = (Button) rootView.findViewById(R.id.button_upload);
         camera = (Button) rootView.findViewById(R.id.button_camera);

         name = (EditText) rootView.findViewById(R.id.editText_name);
         position = (EditText) rootView.findViewById(R.id.editText_position);
         contact = (EditText) rootView.findViewById(R.id.editText_contact);

         profile = null;


         edit.setOnClickListener(this);
         cancel1.setOnClickListener(this);
         upload.setOnClickListener(this);
         camera.setOnClickListener(this);

         name.setText(officer.get("name").toString());
         position.setText(officer.get("position").toString());
         contact.setText(officer.get("contact").toString());

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
             case R.id.button_cancel1:
                 onCancelClicked();
                 break;
             case R.id.button_cancel2:
                 onCancelClicked();;
                 break;
             case R.id.button_remove:
                 onRemoveClicked();
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
         getDialog().getWindow().setLayout(
                 ViewGroup.LayoutParams.MATCH_PARENT,
                 ViewGroup.LayoutParams.WRAP_CONTENT);
     }

     public void onEditClicked(){
         Map<String, Object> updates = new HashMap<String, Object>();
         updates.put("name", name);
         updates.put("position", position);
         updates.put("contact", contact);

         if(profile!=null){
            updates.put("profileImage", profile);
         }

         officer.getReference().update(updates);
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
         if(resultCode== Activity.RESULT_OK){
             Bitmap bm = null;
             switch (requestCode){
                 case 0:
                     bm= (Bitmap) data.getExtras().get("data");
                     break;
                 case 1:
                     Uri uri =  data.getData();
                     String[] filePathColumn = {MediaStore.Images.Media.DATA};
                     if (uri != null) {

                         Cursor cursor = getContext().getContentResolver().query(uri,
                                 filePathColumn, null, null, null);
                         if (cursor != null) {
                             cursor.moveToFirst();

                             int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                             String picturePath = cursor.getString(columnIndex);
                             bm = BitmapFactory.decodeFile(picturePath);
                             cursor.close();
                         }
                     }

                     break;

             }
             if(bm!=null){
                 ByteArrayOutputStream stream = new ByteArrayOutputStream();
                 bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
                 profile = stream.toByteArray();
                 Toast.makeText(getContext(), "Image Uploaded", Toast.LENGTH_SHORT).show();
                 bm.recycle();
             }


         }
     }


 }