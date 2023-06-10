package com.example.mychatapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpActivity extends AppCompatActivity {

    private CircleImageView imageViewCircle;
    private TextInputEditText editTextEmailSignUp,editTextPasswordSignup,editTextUserNameSignup;
    private Button buttonRegister;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        imageViewCircle = findViewById(R.id.imageViewCircle);
        editTextEmailSignUp = findViewById(R.id.editTextEmailSignup);
        editTextPasswordSignup = findViewById(R.id.editTextPasswordSignup);
        editTextUserNameSignup = findViewById(R.id.editTextUserNameSignup);
        buttonRegister = findViewById(R.id.buttonRegister);

    }
}