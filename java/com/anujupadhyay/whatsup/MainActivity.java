package com.anujupadhyay.whatsup;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.appcompat.app.AlertDialog;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private TabAccessorsAdapter tabAccessorsAdapter;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        toolbar = (Toolbar) findViewById(R.id.main_activity_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("What's Up");

        viewPager = (ViewPager) findViewById(R.id.main_tab_pager);
        tabAccessorsAdapter = new TabAccessorsAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tabAccessorsAdapter);

        tabLayout = (TabLayout) findViewById(R.id.main_tab);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(firebaseUser == null){
            goToLoginActivity();
        }
        else{
            VerifyExistanceOfUser();
        }
    }

    private void VerifyExistanceOfUser() {
        String CurrentsUserId = firebaseAuth.getCurrentUser().getUid();
        databaseReference.child("Users").child(CurrentsUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if((dataSnapshot.child("name").exists())){
                    Toast.makeText(MainActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
                }
                else{
                    goToSettings();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void goToLoginActivity() {
        Intent intent = new Intent(MainActivity.this, Login.class);
        intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
    private void goToSettings() {
        Intent setintent = new Intent(MainActivity.this, Settings.class);
        setintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(setintent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menus,menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.menu_settings){
            goToSettings();
        }
        if(item.getItemId() == R.id.menu_find_friends){

        }
        if(item.getItemId() == R.id.menu_group){
            RequestForCreateNewGroups();
        }
        if(item.getItemId() == R.id.menu_logout){
            firebaseAuth.signOut();
            goToLoginActivity();
        }

        return  true;
    }

    private void RequestForCreateNewGroups() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialog);
        builder.setTitle("Enter New Group Name : ");

        final EditText NewGroupName = new EditText(MainActivity.this);
        NewGroupName.setHint("e.g. BFF, CodeSky YouTube...");
        builder.setView(NewGroupName);
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newGroupName = NewGroupName.getText().toString();

                if(TextUtils.isEmpty(newGroupName)){
                    Toast.makeText(MainActivity.this, "Please Enter Group Name....", Toast.LENGTH_SHORT).show();
                }
                else{
                    CreateNewGroups(newGroupName);
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void CreateNewGroups(final String newGroupName) {
        databaseReference.child("Groups").child(newGroupName).setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(MainActivity.this, newGroupName + " Group is Created Successfully", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
