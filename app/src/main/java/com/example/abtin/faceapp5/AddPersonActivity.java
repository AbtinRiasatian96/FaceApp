package com.example.abtin.faceapp5;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;

import ch.zhaw.facerecognitionlibrary.Helpers.FileHelper;

public class AddPersonActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_person);
    }

    public void onStartClick(View view) {

        EditText personName = (EditText)findViewById(R.id.addedPersonName);
        String addedPersonName = personName.getText().toString();

        Intent intent = new Intent(this, TakePhotosOfPersonActivity.class);
        intent.putExtra("AddedPersonName", addedPersonName);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        if(isNameUsed(new FileHelper().getTrainingList(), addedPersonName)){
            Toast.makeText(getApplicationContext(), "This name is already used. Please choose another one.", Toast.LENGTH_SHORT).show();
        }

        else {
            startActivity(intent);
        }

    }

    private boolean isNameUsed(File[] list, String name){
        boolean used = false;
        if(list != null && list.length > 0){
            for(File person : list){
                // The last token is the name --> Folder name = Person name
                String[] tokens = person.getAbsolutePath().split("/");
                final String foldername = tokens[tokens.length-1];
                if(foldername.equals(name)){
                    used = true;
                    break;
                }
            }
        }
        return used;
    }

}
