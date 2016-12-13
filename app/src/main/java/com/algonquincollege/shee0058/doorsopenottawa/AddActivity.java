package com.algonquincollege.shee0058.doorsopenottawa;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.algonquincollege.shee0058.doorsopenottawa.model.Building;

public class AddActivity extends Activity {

    public static final String REST_URI = "https://doors-open-ottawa-hurdleg.mybluemix.net/buildings";

    private ProgressBar pb;
    private final int CAMERA_REQUEST_CODE = 100;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        pb = (ProgressBar) findViewById(R.id.progressBar2);
        imageView = (ImageView) findViewById(R.id.imageView);

        final EditText buildingNameEditText = (EditText) findViewById(R.id.building_name_editText);
        final EditText buildingAddressEditText = (EditText) findViewById(R.id.building_address_editText);
        final EditText buildingDescriptionEditText = (EditText) findViewById(R.id.building_description_editText);

        Button addButton = (Button) findViewById(R.id.addBtn);
        Button cancelButton = (Button) findViewById(R.id.cancelBtn);
        ImageButton cameraButton = (ImageButton) findViewById(R.id.cameraBtn);

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA_REQUEST_CODE);
            }
        });

        final String buildingName = buildingNameEditText.getText().toString();
        final String buildingAddress = buildingAddressEditText.getText().toString();
        final String buildingDescription = buildingDescriptionEditText.getText().toString();

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Building building = new Building();
                building.setName(buildingName);
                building.setAddress(buildingAddress + " Ottawa, Ontario");
                building.setImage("sample.png");
                building.setDescription(buildingDescription);

                RequestPackage pkg = new RequestPackage();
                pkg.setMethod(HttpMethod.POST);
                pkg.setUri(REST_URI);
                pkg.setParam("name", building.getName());
                pkg.setParam("address", building.getAddress());
                pkg.setParam("image", building.getImage());
                pkg.setParam("description", building.getDescription());

                Log.i("BUILDINGS", pkg + "");

                DoTask doTask = new DoTask();
                doTask.execute(pkg);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildingNameEditText.setText("");
                buildingAddressEditText.setText("");
                buildingDescriptionEditText.setText("");

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    private class DoTask extends AsyncTask<RequestPackage, String, String> {

        @Override
        protected void onPreExecute() {
            pb.setVisibility(View.VISIBLE);

        }

        @Override
        protected String doInBackground(RequestPackage... params) {

            String content = HttpManager.getNewData(params[0], "shee0058", "password");
            return content;
        }

        @Override
        protected void onPostExecute(String result) {


            pb.setVisibility(View.INVISIBLE);

            if (result == null) {
                Toast.makeText(AddActivity.this, "Web service not available", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {
        Bundle extras;
        Bitmap imageBitmap;

        if (resultCode == RESULT_CANCELED) {
            Toast.makeText(getApplicationContext(), "Cancelled!", Toast.LENGTH_SHORT).show();
            return;
        }

        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                extras = resultIntent.getExtras();
                imageBitmap = (Bitmap) extras.get("data");

                if (imageBitmap != null) {
                    imageView.setImageBitmap(imageBitmap);
                }
                break;
        }
    }

}

