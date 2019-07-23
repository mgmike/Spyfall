package mike.spyfall;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

//import com.firebase.ui.Firebase;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mRef;
    private String hostUserName;
    private String currentUserName;
    private String currentUID;
    private String currentName;
    //private AdView mAdView;

    public void onFriendsButton(View view){
        toNextActivity(new Intent(MainActivity.this, FriendActivity.class));
    }


    public void onStartGame(View view){
        if(hostUserName == null) {
            toNextActivity(new Intent(MainActivity.this, HostGameActivity.class));
        } else if(hostUserName.equals(currentUserName)) {
            toNextActivity(new Intent(MainActivity.this, HostGameActivity.class));
        } else {
            toNextActivity(new Intent(MainActivity.this, GuestGameActivity.class));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_layout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == R.id.log_out){
            mAuth.signOut();
            toNextActivity(new Intent(MainActivity.this, LoginActivity.class));

        }else if (id == R.id.addLocation) {
            //Intent getAddLocationIntent = new Intent(this, AddLocation.class);
            //startActivity(getAddLocationIntent);
            toNextActivity(new Intent(MainActivity.this, add_location.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
    }

    public void toNextActivity(Intent intent){
        // Tell the new activity how return when finished.
        intent.putExtra("anim id in", R.anim.left_in);
        intent.putExtra("anim id out", R.anim.left_out);
        startActivity(intent);
        // This makes the new screen slide up as it fades in
        // while the current screen slides up as it fades out.
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        //Firebase.setAndroidContext(this);
        /*
        MobileAds.initialize(this, "ca-app-pub-7054487445717644~4798964612");
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("145310EF75B6B1FE8013E630E72F45CB").build();
        //AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        if(adRequest.isTestDevice(MainActivity.this)){
            System.out.println("This is a test device. _-*-_-*-_-*-_-*-_-*-_-*-_-*-_-*-_-*-_");
        }
        */
        if(getIntent().getExtras() != null){
                if (getIntent().getIntExtra("kicked", -1) == 1) {
                    Toast.makeText(MainActivity.this, "You were kicked!", Toast.LENGTH_LONG).show();
                }
        }

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        currentUID = user.getUid();
        mRef = FirebaseDatabase.getInstance().getReference("users/" + currentUID);

        mRef.child("userInfo").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                TextView displayUserName = (TextView) findViewById(R.id.userNameDisplay);
                TextView displayName = (TextView) findViewById(R.id.nameDisplay);
                final Button startGame = (Button) findViewById(R.id.startButton);

                if(dataSnapshot.child("userName").exists()) {
                    currentUserName = dataSnapshot.child("userName").getValue().toString();
                    System.out.println(currentUserName + currentUID + "******************************");
                    displayUserName.setText(currentUserName);
                    displayName.setText(dataSnapshot.child("name").getValue().toString());
                    if (dataSnapshot.child("inGame").exists()) {
                        hostUserName = dataSnapshot.child("inGame").getValue().toString();

                        if (hostUserName.equals(currentUserName)) {
                            startGame.setText("Join your own game");
                        } else {
                            //gets the hosts UID
                            mRef = FirebaseDatabase.getInstance().getReference("friendUID");
                            mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    //gets hosts username
                                    mRef = FirebaseDatabase.getInstance().getReference("users/" + dataSnapshot.child(hostUserName).getValue().toString() + "/userInfo/name");
                                    mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            startGame.setText("Join " + dataSnapshot.getValue().toString() + "'s game");
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            Toast.makeText(MainActivity.this, "Connection Error", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Toast.makeText(MainActivity.this, "Connection Error", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    } else {
                        hostUserName = null;
                        startGame.setText("Start Game");
                    }
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Connection Error", Toast.LENGTH_LONG).show();
            }
        });





    }



    @Override
    protected void onStart() {
        super.onStart();

    }
}

