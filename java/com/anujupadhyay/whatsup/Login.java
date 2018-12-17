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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

public class Login extends AppCompatActivity {

    private Button ButtonLogin, PhoneButtonLogin;
    private EditText EditTextEmail, EditTextPassword;
    private TextView ForgetPassword, NewUser;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
        InitializationOfFields();
        NewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToRegisterUser();
            }
        });
        ButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserLogin();
            }
        });
        PhoneButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this,PhoneAuthantication.class);
                startActivity(intent);
            }
        });
    }

    private void UserLogin() {
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
        else{

            progressDialog.setTitle("Authenticating User...");
            progressDialog.setMessage("Please Wait....");
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.show();
            firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        goToMainActivity();
                        Toast.makeText(Login.this, "Successfully Login", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                    else{
                        if (!(task.getException() instanceof FirebaseAuthUserCollisionException)){
                            Toast.makeText(getApplicationContext(), "Access Denied ! Enter Valid Credantials", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                        else{
                            String msg = task.getException().toString();
                            Toast.makeText(Login.this, "Error " + msg, Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                }
            });
        }
    }

    private void InitializationOfFields() {
        ButtonLogin = (Button) findViewById(R.id.login_button);
        PhoneButtonLogin = (Button) findViewById(R.id.login_phone);
        EditTextEmail = (EditText) findViewById(R.id.login_Email);
        EditTextPassword = (EditText) findViewById(R.id.login_password);
        ForgetPassword = (TextView) findViewById(R.id.forget_password);
        NewUser = (TextView) findViewById(R.id.register_link);
        progressDialog = new ProgressDialog(this);
    }

    private void goToMainActivity() {
        Intent mainIntent = new Intent(Login.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        finish();
    }
    private void goToRegisterUser() {
        Intent regIntent = new Intent(Login.this, Register.class);
        regIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(regIntent);
    }
}
