package com.drawmyemoticon;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

public class SetupActivity extends AppCompatActivity {
    EditText editTitle, editSize;
    CheckBox gifCheck, scaleCheck;

    boolean isGif = false;
    boolean isOriginalScale = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_setup);

        setViews();
    }

    private void setViews() {
        editTitle = findViewById(R.id.editCanvasTitle);
        editSize = findViewById(R.id.editCanvasSize);
        editSize.setEnabled(false);

        gifCheck = findViewById(R.id.gifCheckBox);
        gifCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isGif = b;
            }
        });
        scaleCheck = findViewById(R.id.saveOriginalCheckBox);
        scaleCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isOriginalScale = b;
                if (b) {
                    editSize.setEnabled(false);
                } else {
                    editSize.setEnabled(true);
                }
            }
        });
    }

    public void createNewCanvas(View view) {
        if (editTitle.getText().toString().equals("")) {
            Toast.makeText(this,R.string.please_insert_name,Toast.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent(this, CanvasActivity.class);
            intent.putExtra("canvasTitle", editTitle.getText().toString());
            intent.putExtra("canvasSize", Integer.parseInt(0 + editSize.getText().toString()));
            intent.putExtra("isGif", isGif);
            intent.putExtra("saveAsOriginalScale", isOriginalScale);
            startActivity(intent);
            finish();
        }
    }
}