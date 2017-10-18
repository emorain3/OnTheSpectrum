package com.ots.tdd.onthespectrum;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class EditEmergencyActivity extends AppCompatActivity {

    public EmergencyElement emergency;
    public EditText emergencyTitle;
    public ImageButton emergencyImage;

    private static int RESULT_LOAD_IMAGE = 1;

    BitmapFactory.Options options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_emergency);
        int emergencyNum = Integer.parseInt(getIntent().getStringExtra("emergencyNum"));
        emergency = ListOfEmergenciesActivity.scenarioList.get(emergencyNum);
        emergencyTitle = (EditText) findViewById(R.id.emergencyTitle);
        emergencyImage = (ImageButton) findViewById(R.id.emergencyImage);

        emergencyTitle.setText(emergency.getTitle());
        emergencyImage.setBackground( new BitmapDrawable(getResources(), emergency.getImage()) );
        emergencyImage.setLayoutParams(new LinearLayout.LayoutParams(350, 350)); //currently hardcoded, change later


    }

    public void saveChanges(View v) {
        emergency.setTitle(emergencyTitle.getText().toString());
        //emergency.setImage(((BitmapDrawable)emergencyImage.getBackground()).getBitmap());
        if (((BitmapDrawable)emergencyImage.getDrawable()) != null) {
            emergency.setImage(((BitmapDrawable)emergencyImage.getDrawable()).getBitmap());
        } else {
            //toastMessage("Null Image");
        }


        // notify gridview of data changed
        ListOfEmergenciesActivity.adapter.notifyDataSetChanged();
        finish();
    }

    public void cancelChanges(View v) {
        finish();
    }

    public void changeImage(View v) {
        toastMessage("Changing Image");

        Intent i = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri photoUri = data.getData();
            // Do something with the photo based on Uri
            //https://guides.codepath.com/android/Accessing-the-Camera-and-Stored-Media#accessing-stored-media
            try {
                Bitmap selectedImg = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
                // Load the selected image into a preview
                emergencyImage.setImageBitmap(selectedImg);
            } catch (Exception e) {
                toastMessage("Choose a different image");
            }
            // scale this later
        }


    }

    public void toastMessage(String msg) {
        Context context = getApplicationContext();
        CharSequence text = msg;
        int duration = Toast.LENGTH_SHORT;

        Toast.makeText(context, text, duration).show();
    }
}