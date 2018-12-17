package com.anujupadhyay.whatsup;

import android.app.ProgressDialog;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

public class Register extends AppCompatActivity {

    private FirebaseUser firebaseUser;
    private Button ButtonRegister;
    private EditText EditTextEmail, EditTextPassword;
    private TextView LoginLink;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        InitializationOfFields();
        LoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLoginUser();
            }
        });

        ButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateNewUser();
            }
        });
    }

    private void CreateNewUser() {
        String email = EditTextEmail.getText().toString();
        String password = EditTextPassword.getText().toString();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Please Enter Your Email Id", Toast.LENGTH_SHORT).show();
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            EditTextEmail.setError("Please Enter Valid Email ID!");
            EditTextEmail.requestFocus();
            return;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please Enter Password", Toast.LENGTH_SHORT).show();
        }
        if(password.length()<8){
            EditTextPassword.setError("Minimum Length of Password should be 8");
            EditTextPassword.requestFocus();
            return;
        }
        else{
            progressDialog.setTitle("Registering User...");
            progressDialog.setMessage("Please Wait....");
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.show();
            firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){

                        String currentUserId = firebaseAuth.getCurrentUser().getUid();
                        databaseReference.child("Users").child(currentUserId).setValue("");
                        

                        goToMainActivity();
                        Toast.makeText(Register.this, "Registration Successfull", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                    else{

                        if(task.getException() instanceof FirebaseAuthUserCollisionException){
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(),"User is Already Registered!", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            String msg = task.getException().toString();
                            Toast.makeText(Register.this, "Error " + msg, Toast.LENGTH_SHORT).show();

                        }
                    }
                }
            });
        }
    }

    private void goToMainActivity() {
        Intent mainsIntent = new Intent(Register.this, MainActivity.class);
        mainsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainsIntent);
        finish();
    }

    private void goToLoginUser() {
        Intent LogIntent = new Intent(Register.this, Login.class);
        LogIntent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(LogIntent);
    }
    private void InitializationOfFields() {
        ButtonRegister = (Button) findViewById(R.id.register_button);
        EditTextEmail = (EditText) findViewById(R.id.register_Email);
        EditTextPassword = (EditText) findViewById(R.id.register_password);
        LoginLink = (TextView) findViewById(R.id.login_link);
        progressDialog = new ProgressDialog(this);
    }


}
