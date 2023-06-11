package com.example.mychatapp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultCaller;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpActivity extends AppCompatActivity {

    private CircleImageView imageViewCircle;
    private TextInputEditText editTextEmailSignUp,editTextPasswordSignup,editTextUserNameSignup;
    private Button buttonRegister;
    boolean imageControl = false;
    ActivityResultLauncher<Intent> activityResultLauncher;

    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference reference;

    FirebaseStorage fireBaseStorage;
    StorageReference storageReference;
    Uri imageUri;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //register
        registerActivityForSelectImage();

        imageViewCircle = findViewById(R.id.imageViewCircle);
        editTextEmailSignUp = findViewById(R.id.editTextEmailSignup);
        editTextPasswordSignup = findViewById(R.id.editTextPasswordSignup);
        editTextUserNameSignup = findViewById(R.id.editTextUserNameSignup);
        buttonRegister = findViewById(R.id.buttonRegister);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();

        fireBaseStorage = FirebaseStorage.getInstance();
        storageReference = fireBaseStorage.getReference();




        imageViewCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageChooser();

            }
        });
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = editTextEmailSignUp.getText().toString();
                String password = editTextPasswordSignup.getText().toString();
                String userName = editTextUserNameSignup.getText().toString();

                if (!email.equals("")&& !password.equals("")&& !userName.equals("")){
                    signup(email,password,userName);
                }

            }
        });

    }
    public void imageChooser() {
       Intent intent = new Intent();
       intent.setType("image/*");
       intent.setAction(Intent.ACTION_GET_CONTENT);
       //startActivityForResult(intent,1);
        activityResultLauncher.launch(intent);

   }

   private void registerActivityForSelectImage(){
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        int resultCode = result.getResultCode();
                        Intent data = result.getData();

                        if (resultCode == RESULT_OK && data != null) {
                            imageUri = data.getData();
                            Picasso.get().load(imageUri).into(imageViewCircle);
                            imageControl = true;
                        } else {
                            imageControl = false;
                        }
                    }

                });
    }
    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null){
            Uri imageUri = data.getData();
            Picasso.get().load(imageUri).into(imageViewCircle);
            imageControl = true;

        }
        else
        {
            imageControl = false;
        }
    }*/

    public void  signup(String email, String password, String userName){
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {

                if (task.isSuccessful()){
                    reference.child("Users").child(auth.getUid()).child("userName").setValue(userName);

                    if (imageControl){
                        UUID randomID = UUID.randomUUID();
                        String imageName = "images/"+randomID+".jpg";
                        storageReference.child(imageName).putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                StorageReference myStorageRef = fireBaseStorage.getReference(imageName);
                                myStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String filePath = uri.toString();
                                        reference.child("Users").child(auth.getUid()).child("image").setValue(filePath).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(SignUpActivity.this,"Write to database is success",Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(Exception e) {
                                                Toast.makeText(SignUpActivity.this,"Write to database is not successful",Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                });
                            }
                        });

                    }
                    else {
                            reference.child("Users").child(auth.getUid()).child("image").setValue("null");
                    }
                    Intent intent = new Intent(SignUpActivity.this,MainActivity.class);
                    intent.putExtra("userName",userName);
                    startActivity(intent);
                    finish();


                }
                else {
                    Toast.makeText(SignUpActivity.this,"There is a problem",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}