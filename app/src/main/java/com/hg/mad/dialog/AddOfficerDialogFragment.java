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

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.hg.mad.R;
import com.hg.mad.model.Officer;

import java.io.ByteArrayOutputStream;
import java.util.Collection;
import java.util.List;

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

    private byte[] profile;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.dialog_filter, container, false);

        add = (Button) rootView.findViewById(R.id.button_addOfficer);
        cancel = (Button) rootView.findViewById(R.id.button_cancel);
        upload = (Button) rootView.findViewById(R.id.button_upload);
        camera = (Button) rootView.findViewById(R.id.button_camera);

        name = (EditText) rootView.findViewById(R.id.editText_name);
        position = (EditText) rootView.findViewById(R.id.editText_position);
        contact = (EditText) rootView.findViewById(R.id.editText_contact);

        profile = null;


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
            case R.id.button_search:
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

    public void onUploadClicked(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }

    public void onCameraClicked(){
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 0);
    }


    public void onAddClicked() {
        Officer o;
        o = new Officer(name.getText().toString(), position.getText().toString(), contact.getText().toString());
        if(profile!=null){
            o.setProfileImage(profile);
        }
        chapterRef.collection("officers").add(o);

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
                bm.recycle();
            }


        }
    }


}
