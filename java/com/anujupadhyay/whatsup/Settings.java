package com.anujupadhyay.whatsup;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

public class Settings extends AppCompatActivity {

    private Button UpdateProfile;
    private EditText usersName, userStatus;
    private CircleImageView UserProfile;
    private String CurrentsUserId;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private static final int GalleryPick = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        firebaseAuth = FirebaseAuth.getInstance();
        CurrentsUserId = firebaseAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        Initialized();
        UpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateSetting();
            }
        });
        RetriveUserData();

        UserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallary = new Intent();
                gallary.setAction(Intent.ACTION_GET_CONTENT);
                gallary.setType("image/*");
            }
        });
    }

    private void RetriveUserData() {
        databaseReference.child("Users").child(CurrentsUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if((dataSnapshot.exists()) && (dataSnapshot.hasChild("name") && (dataSnapshot.hasChild("image")))){

                    String retriveUserName = dataSnapshot.child("name").getValue().toString();
                    String retriveUserStatus = dataSnapshot.child("status").getValue().toString();
                    String retriveProfileImg = dataSnapshot.child("image").getValue().toString();

                    usersName.setText(retriveUserName);
                    userStatus.setText(retriveUserStatus);

                }
                else if((dataSnapshot.exists()) && (dataSnapshot.hasChild("name"))){
                    String retriveUserName = dataSnapshot.child("name").getValue().toString();
                    String retriveUserStatus = dataSnapshot.child("status").getValue().toString();

                    usersName.setText(retriveUserName);
                    userStatus.setText(retriveUserStatus);
                }
                else{
                    Toast.makeText(Settings.this, "Pleas Upadte Your Profile....", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void UpdateSetting() {
        String Username = usersName.getText().toString();
        String UsersStatus = userStatus.getText().toString();

        if(TextUtils.isEmpty(Username)){
            Toast.makeText(this, "Please Enter Your Name Here...", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(UsersStatus)){
            Toast.makeText(this, "Please Write Status Here...", Toast.LENGTH_SHORT).show();
        }
        else{
            HashMap<String,String> ProfileMap = new HashMap<>();
            ProfileMap.put("uid",CurrentsUserId);
            ProfileMap.put("name",Username);
            ProfileMap.put("status",UsersStatus);
            databaseReference.child("Users").child(CurrentsUserId).setValue(ProfileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        goToMainActivity();
                        Toast.makeText(Settings.this, "Thanks For Updating Profile", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        String msg = task.getException().toString();
                        Toast.makeText(Settings.this, "Error " + msg, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void Initialized() {
        UpdateProfile = (Button) findViewById(R.id.update_button);
        usersName = (EditText) findViewById(R.id.name_edit);
        userStatus = (EditText) findViewById(R.id.status);
        UserProfile = (CircleImageView) findViewById(R.id.profile_image);
    }

    private void goToMainActivity() {
        Intent mainIntent = new Intent(Settings.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        finish();
    }
}
