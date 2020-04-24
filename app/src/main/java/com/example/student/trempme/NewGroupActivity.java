package com.example.student.trempme;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;

public class NewGroupActivity extends AppCompatActivity {

    Button btnCreateNewGroup;
    EditText etGroupName;

    FirebaseUser userAuth;
    FirebaseDatabase database;
    DatabaseReference myRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);

        setFirebaseVariables();
        setViews();

        Helper.setDefaultLanguage(this,"en_US ");
    }

    private void setFirebaseVariables() {
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        userAuth = FirebaseAuth.getInstance().getCurrentUser();
    }

    public void setViews() {
        btnCreateNewGroup=findViewById(R.id.btnCreateNewGroup);
        etGroupName=findViewById(R.id.etGroupName);
        btnCreateNewGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etGroupName.getText().toString().equals("")){
                    Toast.makeText(NewGroupActivity.this,"You should insert group name",Toast.LENGTH_LONG).show();
                }
                else{
                    Query group=myRef.child("Group").orderByChild("groupName").equalTo(etGroupName.getText().toString());
                    group.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.w("datasnap",dataSnapshot.toString());
                            if (dataSnapshot.getValue()==null){
                                Log.w("snap val is null","here");
                                String uniqueID = UUID.randomUUID().toString();
                                Log.w("new group",etGroupName.getText().toString());
                                Group group=new Group(uniqueID,etGroupName.getText().toString().toLowerCase());
                                myRef.child("Group").child(uniqueID).setValue(group);
                                Intent intent = new Intent();
                                intent.putExtra("groupName",etGroupName.getText().toString().toLowerCase());
                                setResult(RESULT_OK,intent);
                                finish();
                            }
                            else{
                                Toast.makeText(NewGroupActivity.this,"This group is already exist\n Choose new group name",Toast.LENGTH_LONG).show();

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
    }
}
