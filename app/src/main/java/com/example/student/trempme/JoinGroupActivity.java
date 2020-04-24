package com.example.student.trempme;

import android.content.Intent;
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

import java.util.ArrayList;
import java.util.List;

public class JoinGroupActivity extends AppCompatActivity implements View.OnClickListener {

    EditText etGroupName,etFullName,etPhoneNumber;
    Button btnJoinGroup, btnGoToNewGroup;

    FirebaseUser userAuth;
    FirebaseDatabase database;
    DatabaseReference myRef;

    User myUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_group);

        setFirebaseVariables();
        setMyUser();
        setInputViews();
        setBtnJoinGroupListener();
        setBtnGoToNewGroup();
        //static func from main activity that keep the screen ltr
        Helper.setDefaultLanguage(this,"en_US ");
    }

    private void setFirebaseVariables(){
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        userAuth = FirebaseAuth.getInstance().getCurrentUser();
    }

    public void setBtnJoinGroupListener() {
        btnJoinGroup=findViewById(R.id.btnJoinGroup);
        btnJoinGroup.setOnClickListener(this);
    }

    public void setBtnGoToNewGroup() {
        btnGoToNewGroup=findViewById(R.id.btnGoToNewGroup);
        btnGoToNewGroup.setOnClickListener(this);
    }

    public void setInputViews(){
        etGroupName=findViewById(R.id.etGroupName);
        etFullName=findViewById(R.id.etFullName);
        etPhoneNumber=findViewById(R.id.etPhoneNumber);
    }

    @Override
    public void onClick(View view) {
        if(view==btnJoinGroup){
            etGroupName=findViewById(R.id.etGroupName);
            addUserToGroup(etGroupName.getText().toString().toLowerCase());
        }

        if (view==btnGoToNewGroup){
            //send an intent to CreateNewGroup and wait for result
            Intent intent=new Intent(this,NewGroupActivity.class);
            startActivityForResult(intent,0);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==0){
            if (resultCode==RESULT_OK){
                //if the user created new group,
                //set the groupName to the name of the new group
                etGroupName.setText(data.getStringExtra("groupName"));
            }
        }

    }

    /*
     *add the user to the group with the name groupName
     *@param groupName
     */
    private void addUserToGroup(String groupName){
        if(etFullName.getText().toString().equals("")||
                etPhoneNumber.getText().toString().equals("")||
                etGroupName.getText().toString().equals("")){
            Toast.makeText(this,"some details are missing",Toast.LENGTH_LONG).show();
        }
        else{
            Query allGroups=myRef.child("Group").orderByChild("groupName").equalTo(groupName);

            allGroups.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.w("add User",dataSnapshot.toString());
                    if(dataSnapshot.getValue()==null){
                        Toast.makeText(JoinGroupActivity.this,"There is not such a group",Toast.LENGTH_LONG).show();
                    }
                    else{
                        for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                            Group group=singleSnapshot.getValue(Group.class);
                            Log.w("group name", group.getGroupName());
                            List<User> usersGroup =group.getUsers();
                            myUser.setGroupId(group.getGroupId());
                            myUser.setFullName(etFullName.getText().toString());
                            myUser.setPhoneNumber(etPhoneNumber.getText().toString());
                            if(usersGroup!=null){
                                myRef.child("Group").child(group.getGroupId()).child("users").child(usersGroup.size()+"").setValue(myUser);
                                //User user=new User(userAuth.getUid(),group.getGroupId(),null,null,null,null,null,null);
                                myRef.child("User").child(userAuth.getUid()).setValue(group.getGroupId());
                                Intent intent =new Intent(JoinGroupActivity.this,MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else{
                                List<User> newUsersGroup=new ArrayList<>();
                                newUsersGroup.add(myUser);
                                group.setUsers(newUsersGroup);
                                Log.w("group id",group+"");
                                //User user=new User(userAuth.getUid(),group.getGroupId(),null,null,null,null,null,null);
                                myRef.child("Group").child(group.getGroupId()).setValue(group);
                                myRef.child("User").child(userAuth.getUid()).setValue(group.getGroupId());
                                Intent intent =new Intent(JoinGroupActivity.this,MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }

    // set global variable myUser to the connected user
    public void setMyUser(){
        final Query userQuery=myRef.child("User").child(userAuth.getUid());

        userQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.getValue().getClass().equals(String.class)) {
                    myUser=dataSnapshot.getValue(User.class);
                }
                else {
                    startActivity(new Intent(JoinGroupActivity.this,MainActivity.class));
                }




            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
