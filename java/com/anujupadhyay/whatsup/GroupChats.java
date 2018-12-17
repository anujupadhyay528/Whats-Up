package com.anujupadhyay.whatsup;

import android.os.Message;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class GroupChats extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageButton imageButton;
    private EditText editText;
    private ScrollView scrollView;
    private TextView textView;
    private String currentClickedGroup,CurrentUserId,CurrentUserNames, CurrentTime, CurrentDate;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference,ForGroupReferance, ForGroupMessageKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chats);

        currentClickedGroup = getIntent().getExtras().get("groupName").toString();
        Toast.makeText(this, currentClickedGroup, Toast.LENGTH_SHORT).show();
        firebaseAuth = FirebaseAuth.getInstance();
        CurrentUserId = firebaseAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        ForGroupReferance = FirebaseDatabase.getInstance().getReference().child("Groups").child(currentClickedGroup);
        FieldInialize();
        GettingUSerInformation();
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StoreMsgInDatabase();
                editText.setText("");
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    private void StoreMsgInDatabase() {
        String Messages = editText.getText().toString();
        String MessageKey = ForGroupReferance.push().getKey();

        if(TextUtils.isEmpty(Messages)){
            Toast.makeText(this, "Please Write Something", Toast.LENGTH_SHORT).show();
        }
        else{
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd, yyyy");
            CurrentDate = simpleDateFormat.format(calendar.getTime());

            Calendar calendarTime = Calendar.getInstance();
            SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("hh:mm a");
            CurrentTime = simpleTimeFormat.format(calendarTime.getTime());

            HashMap<String, Object> groupMesgKeys = new HashMap<>();
            ForGroupReferance.updateChildren(groupMesgKeys);
            ForGroupMessageKey = ForGroupReferance.child(MessageKey);

            HashMap<String, Object> msgInformationMap = new HashMap<>();
                msgInformationMap.put("name",CurrentUserNames);
                msgInformationMap.put("message",Messages);
                msgInformationMap.put("date",CurrentDate);
                msgInformationMap.put("time",CurrentTime);

            ForGroupReferance.updateChildren(msgInformationMap);
        }
    }

    private void GettingUSerInformation() {
        databaseReference.child(CurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    CurrentUserNames = dataSnapshot.child("name").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        ForGroupReferance.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if(dataSnapshot.exists()){
                    DisplayMessage(dataSnapshot);
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()){
                    DisplayMessage(dataSnapshot);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void DisplayMessage(DataSnapshot dataSnapshot) {
        Iterator iterator = dataSnapshot.getChildren().iterator();

        while(iterator.hasNext()){
            String chatDates = (String) ((DataSnapshot) iterator.next()).getValue();
            String chatMessage = (String) ((DataSnapshot) iterator.next()).getValue();
            String chatNames = (String) ((DataSnapshot) iterator.next()).getValue();
            String chatTimes = (String) ((DataSnapshot) iterator.next()).getValue();

            textView.append(chatNames + " :\n" + chatMessage + "\n" + chatTimes + "     " + chatDates + "\n\n");
            scrollView.fullScroll(ScrollView.FOCUS_DOWN);
        }
    }

    private void FieldInialize() {
        toolbar = (Toolbar) findViewById(R.id.group_chats_id);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(currentClickedGroup);
        imageButton = (ImageButton) findViewById(R.id.send_msg_button);
        editText = (EditText) findViewById(R.id.group_chats_msg);
        scrollView = (ScrollView) findViewById(R.id.scroll_view_id);
        textView = (TextView) findViewById(R.id.group_chats_text);
    }
}
