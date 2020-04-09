package com.hg.mad;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ContactUsActivity extends AppCompatActivity {
    EditText subject;
    EditText message;
    Button send;
    Toast myToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        getSupportActionBar().setTitle("Contact Us");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        subject = findViewById(R.id.editText6);
        message = findViewById(R.id.editText5);
        send = findViewById(R.id.button3);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(subject.getText().toString().equals("")){
                    alert("Needs a subject.");
                } else if(message.getText().toString().equals("")){
                    alert("Needs a message.");
                } else {
                    sendEmail(message.getText().toString(), subject.getText().toString());
                }

            }
        });

    }

    protected void sendEmail(String messageText, String subjectText) {

        String[] TO = {"s-hliao@lwsd.org, s-jiazhang@lwsd.org"};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");


        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subjectText);
        emailIntent.putExtra(Intent.EXTRA_TEXT, messageText);

        subject.setText("");
        message.setText("");

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            finish();
            alert("Select Email to Use");
        } catch (android.content.ActivityNotFoundException ex) {
            alert("There is no email client installed.");
        }


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void alert(String message) {
        if(myToast != null)
            myToast.cancel();
        myToast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        myToast.show();
    }
}
