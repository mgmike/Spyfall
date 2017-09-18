package mike.spyfall;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Created by Michael on 5/21/2017.
 */

public class GuestGameActivity extends HostGameActivity{

    private RecyclerView mRecyclerView;
    private DatabaseReference mRefForRecView;
    private DatabaseReference mRef;
    private String currentUID;
    private String currentUserName;
    private String inGame;
    private String hostUID;
    private String hostName;
    private String loc;
    private ArrayList<Location> locationArrayList = new ArrayList<>();
    private Button startGameButton;
    private Button leaveGameButton;
    private Button addFriendButton;
    private Button hideRoleButton;
    private TextView roleText;
    private TextView timeText;
    private long startTime = 480000;
    private CounterClass timer = new CounterClass(startTime, 1000);
    private int counter = 0;
    private AdView mAdView;


    @Override
    public void displayRole(){
        mRef = FirebaseDatabase.getInstance().getReference("users/" + hostUID + "/currentGame/users/" + currentUserName);
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot currentRoleSnapshot) {
                if (currentRoleSnapshot.child("role").exists()) {
                    String role = currentRoleSnapshot.child("role").getValue().toString();
                    if (role.equals("Spy")) {
                        roleText.setText("You are the Spy!");
                    } else {
                        roleText.setText("The location is " + loc + ".\nYour Role is " + role + ".");
                    }
                } else {
                    displayRole();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(GuestGameActivity.this, "Connection Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onHideRole(View view) {
        if (hideRoleButton.getText().equals("Hide role")) {
            hideRoleButton.setText("Show role");
            roleText.setText("Play fair!");
        } else {
            hideRoleButton.setText("Hide role");
            displayRole();
        }
    }

    @Override
    public void onLeaveGame(View view){
        mRef = FirebaseDatabase.getInstance().getReference("users");
        mRef.child(hostUID).child("currentGame").child("users").child(currentUserName).removeValue();
        mRef.child(currentUID).child("userInfo").child("inGame").removeValue();
        //startActivity(new Intent(GuestGameActivity.this, MainActivity.class));
        toMenu(0);
    }

    @Override
    public void onBackPressed() {
        //startActivity(new Intent(this, MainActivity.class));
        toMenu(0);
    }

    public void toMenu(int wasKicked) {
        counter++;
        Intent intent = new Intent(GuestGameActivity.this, MainActivity.class);
        // Tell the new activity how return when finished.
        intent.putExtra("anim id in", R.anim.right_in);
        intent.putExtra("anim id out", R.anim.right_out);
        if(counter > 1){
            intent.putExtra("kicked", 0);
        } else if(counter == 1){
            intent.putExtra("kicked", wasKicked);
        }
        startActivity(intent);
        // This makes the new screen slide up as it fades in
        // while the current screen slides up as it fades out.
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        toMenu(0);
        return true;
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        callSuper(savedInstanceState);
        setContentView(R.layout.activity_host_game);
        MobileAds.initialize(this, "ca-app-pub-7054487445717644~4798964612");
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("145310EF75B6B1FE8013E630E72F45CB").build();
        //AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        startGameButton = (Button) findViewById(R.id.startGameButton);
        leaveGameButton = (Button) findViewById(R.id.leaveGameButton);
        addFriendButton = (Button) findViewById(R.id.addFriendButton);
        hideRoleButton = (Button) findViewById(R.id.hideRoleButton);
        roleText = (TextView) findViewById(R.id.roleText);
        timeText = (TextView) findViewById(R.id.timeText);
        //sets up the location clickable list
        final GridView locationList = (GridView) findViewById(R.id.locationList);
        mRef = FirebaseDatabase.getInstance().getReference();
        mRef.child("defaultLocations").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                locationArrayList.clear();
                locationArrayList.clear();
                int i = 0;
                for (DataSnapshot temp : dataSnapshot.getChildren()) {
                    Location tempLocation = new Location(temp.getKey());
                    locationArrayList.add(tempLocation);
                    i++;
                }
                locationList.setAdapter(new HostGameActivity.Adapter(GuestGameActivity.this));
                locationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                        mRef = FirebaseDatabase.getInstance().getReference("users/" + hostUID + "/currentGame/location");
                        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    System.out.println(dataSnapshot.getValue().toString());
                                    if (!dataSnapshot.getValue().toString().equals("none")) {
                                        locationArrayList.get(position).updateAlpha();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });

                locationList.setAdapter(new Adapter(GuestGameActivity.this));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(GuestGameActivity.this, "Connection Error", Toast.LENGTH_LONG).show();
            }
        });

        //sets up recycler view
        mRecyclerView = (RecyclerView) findViewById(R.id.playersList);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        while (currentUID == null) {
            //gets the user's unique id
            currentUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        //This will move you to the main menu if the host kicked you
        mRef = FirebaseDatabase.getInstance().getReference("users/" + currentUID + "/userInfo");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot currentUserInfoSnapshot) {
                if(!currentUserInfoSnapshot.child("inGame").exists()){
                    toMenu(1);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(GuestGameActivity.this, "Connection Error", Toast.LENGTH_LONG).show();
            }
        });

        mRef = FirebaseDatabase.getInstance().getReference("users/" + currentUID + "/userInfo");
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot currentUserInfoSnapshot) {

                currentUserName = currentUserInfoSnapshot.child("userName").getValue().toString();
                inGame = currentUserInfoSnapshot.child("inGame").getValue().toString();
                mRef = FirebaseDatabase.getInstance().getReference("friendUID");
                mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot friendUIDSnapshot) {
                        hostUID = friendUIDSnapshot.child(inGame).getValue().toString();
                        //changes whenever location is updated
                        mRef = FirebaseDatabase.getInstance().getReference("users/" + hostUID + "/currentGame/location");
                        mRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot currentLocSnapshot) {
                                if (currentLocSnapshot.exists()) {
                                    //no game is occurring or game has ended
                                    //update locationView cells alpha
                                    for (int i = 0; i < locationArrayList.size(); i++){
                                        locationArrayList.get(i).reset();
                                    }
                                    loc = currentLocSnapshot.getValue().toString();
                                    if (loc.equals("none")) {
                                        if (hostName == null) {
                                            mRef = FirebaseDatabase.getInstance().getReference("users/" + hostUID + "/userInfo/name");
                                            mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    hostName = dataSnapshot.getValue().toString();
                                                    roleText.setText("Waiting for " + hostName + " to start!");
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {
                                                    Toast.makeText(GuestGameActivity.this, "Connection Error", Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        } else {
                                            roleText.setText("Waiting for " + hostName + " to start!");
                                        }
                                        startGameButton.setVisibility(View.GONE);
                                        leaveGameButton.setVisibility(View.VISIBLE);
                                        hideRoleButton.setVisibility(View.GONE);
                                        timer.cancel();

                                        //if time ends, times up will stay
                                        if(!timeText.getText().toString().equals("Time's up!")){
                                            String time = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(startTime),
                                                    TimeUnit.MILLISECONDS.toSeconds(startTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(startTime)));
                                            timeText.setText(time);
                                        }
                                            //update timer when host changes it
                                            mRef = FirebaseDatabase.getInstance().getReference("users/" + hostUID + "/currentGame/startTime");
                                            mRef.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.exists()) {
                                                        if (loc.equals("none") && !timeText.getText().toString().equals("Time's up!")) {
                                                            startTime = Integer.parseInt(dataSnapshot.getValue().toString());
                                                            String time = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(startTime),
                                                                    TimeUnit.MILLISECONDS.toSeconds(startTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(startTime)));
                                                            timeText.setText(time);
                                                        }
                                                    } else {
                                                        startTime = 480000;
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });


                                        mRef = FirebaseDatabase.getInstance().getReference("users/" + hostUID + "/currentGame/users/" + currentUserName);
                                        mRef.child("time").removeValue();

                                        //game has started
                                    } else {
                                        displayRole();
                                        startGameButton.setVisibility(View.GONE);
                                        leaveGameButton.setVisibility(View.GONE);
                                        hideRoleButton.setVisibility(View.VISIBLE);
                                        //set up timer
                                        mRef = FirebaseDatabase.getInstance().getReference("users/" + hostUID + "/currentGame");
                                        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                long rightNow = Calendar.getInstance().getTimeInMillis();
                                                if (dataSnapshot.child("startTime").exists()) {
                                                    startTime = Integer.parseInt(dataSnapshot.child("startTime").getValue().toString());
                                                }
                                                if (dataSnapshot.child("users").child(currentUserName).child("time").exists()) {
                                                    long timeLeft = startTime - (rightNow - Long.parseLong(dataSnapshot.child("time").getValue().toString()));
                                                    if (timeLeft > 0 && timeLeft < startTime) {
                                                        timer = new CounterClass(timeLeft, 1000);
                                                        timer.start();
                                                    }
                                                } else {
                                                    mRef = FirebaseDatabase.getInstance().getReference("users/" + hostUID + "/currentGame/users/" + currentUserName);
                                                    mRef.child("time").setValue(rightNow);
                                                    timer = new CounterClass(startTime - 1000, 1000);
                                                    timer.start();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                                Toast.makeText(GuestGameActivity.this, "Connection Error", Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }

                                //host has left game
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Toast.makeText(GuestGameActivity.this, "Connection Error", Toast.LENGTH_LONG).show();
                            }
                        });

                        mRef = FirebaseDatabase.getInstance().getReference("users/" + hostUID + "/currentGame/startTime");
                        mRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists())
                                    startTime = Integer.parseInt(dataSnapshot.getValue().toString());
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        mRefForRecView = FirebaseDatabase.getInstance().getReference("users/" + hostUID + "/currentGame/users");
                        FirebaseRecyclerAdapter<CurrentGamePlayerListClass, CurrentGamePlayerViewHolder> mAdapter = new FirebaseRecyclerAdapter<CurrentGamePlayerListClass, CurrentGamePlayerViewHolder>(
                                CurrentGamePlayerListClass.class,
                                R.layout.player_list_template,
                                GuestGameActivity.CurrentGamePlayerViewHolder.class,
                                mRefForRecView

                        ) {
                            @Override
                            protected void populateViewHolder(final GuestGameActivity.CurrentGamePlayerViewHolder viewHolder, final CurrentGamePlayerListClass model, int position) {
                                viewHolder.setUserName(model.getUserName());

                                final LinearLayout linLayout = (LinearLayout) viewHolder.getView().findViewById(R.id.linearLayout);
                                Button kickBtn = (Button) viewHolder.getView().findViewById(R.id.kickButton);
                                kickBtn.setVisibility(View.GONE);

                                if(!model.getUserName().equals(null)){
                                    final String tempUserName = model.getUserName();
                                    viewHolder.setUserName(tempUserName);
                                    //find the UID of user so name can be gathered
                                    mRef = FirebaseDatabase.getInstance().getReference("friendUID");
                                    mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            if(dataSnapshot.child(tempUserName).exists()) {
                                                System.out.println(dataSnapshot.child(tempUserName).getValue() + "===========================================");
                                                final String tempUID = dataSnapshot.child(tempUserName).getValue().toString();

                                                //find name
                                                mRef = FirebaseDatabase.getInstance().getReference("users/" + tempUID + "/userInfo");
                                                mRef.addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        if(dataSnapshot.child("name").exists()) {
                                                            viewHolder.setPlayerName(dataSnapshot.child("name").getValue().toString());
                                                        }

                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {
                                                        Toast.makeText(GuestGameActivity.this, "Connection Error", Toast.LENGTH_LONG).show();
                                                    }
                                                });

                                                if(model.getRole() == null){
                                                    //if this item's user is not on your friends list, a button will be visible to add them
                                                    if(!currentUserName.equals(tempUserName)) {
                                                        mRef = FirebaseDatabase.getInstance().getReference("users/" + currentUID + "/friends/users");
                                                        mRef.addValueEventListener(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                if (!dataSnapshot.child(tempUserName).exists()) {
                                                                    viewHolder.makeAddButtonVisible();
                                                                    final Button addFriend = (Button) viewHolder.getView().findViewById(R.id.addFriend);
                                                                    addFriend.setOnClickListener(new View.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(View v) {
                                                                            //sends a request to specified user
                                                                            mRef = FirebaseDatabase.getInstance().getReference("users/" + tempUID + "/friends/users/" + currentUserName);
                                                                            mRef.child("from").setValue(true);
                                                                            mRef.child("requestAccepted").setValue(false);
                                                                            mRef.child("userName").setValue(currentUserName);
                                                                            //shows up on user's list also
                                                                            //get friends name to put on main user's friends list
                                                                            //add the friend on main user's list
                                                                            mRef = FirebaseDatabase.getInstance().getReference("users/" + currentUID + "/friends/users/" + tempUserName);
                                                                            mRef.child("from").setValue(false);
                                                                            mRef.child("requestAccepted").setValue(false);
                                                                            mRef.child("userName").setValue(tempUserName);
                                                                            Toast.makeText(GuestGameActivity.this, "Friend request sent!", Toast.LENGTH_LONG).show();
                                                                            viewHolder.removeAddButton();

                                                                        }
                                                                    });
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(DatabaseError databaseError) {
                                                                Toast.makeText(GuestGameActivity.this, "Connection Error", Toast.LENGTH_LONG).show();
                                                            }
                                                        });
                                                    }
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            Toast.makeText(GuestGameActivity.this, "Connection Error", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                                //if game has started
                                if(model.getRole() != null){
                                    //add clickable function here
                                    kickBtn.setVisibility(View.GONE);
                                    linLayout.setAlpha((float) 1);
                                    linLayout.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            viewHolder.clicked();
                                        }
                                    });

                                } else {
                                    linLayout.setAlpha((float) 1);

                                }
                            }
                        };
                        mRecyclerView.setAdapter(mAdapter);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(GuestGameActivity.this, "Connection Error", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(GuestGameActivity.this, "Connection Error", Toast.LENGTH_LONG).show();
            }
        });

    }

    public class CounterClass extends CountDownTimer {

        public CounterClass(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millis) {
            String time = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(millis),
                    TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
            timeText.setText(time);
        }

        @Override
        public void onFinish() {
            timeText.setText("Time's up!");
        }
    }

    public static class CurrentGamePlayerViewHolder extends RecyclerView.ViewHolder {
        View mView;
        private boolean buttonClicked = false;

        public CurrentGamePlayerViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setUserName(String userName) {
            TextView userNameTextView = (TextView) mView.findViewById(R.id.userName);
            userNameTextView.setText(userName);
        }

        public void setPlayerName(String playerName){
            TextView playerNameTextView = (TextView) mView.findViewById(R.id.playerName);
            playerNameTextView.setText(playerName);
        }

        public String getUserName(){
            TextView userNameTextView = (TextView) mView.findViewById(R.id.userName);
            return userNameTextView.getText().toString();

        }

        public String getPlayerName(){

            TextView playerNameTextView = (TextView) mView.findViewById(R.id.playerName);
            return playerNameTextView.getText().toString();
        }

        public void removeKickButton() {
            mView.findViewById(R.id.kickButton).setVisibility(View.GONE);
        }

        public void makeAddButtonVisible(){
            mView.findViewById(R.id.addFriend).setVisibility(View.VISIBLE);
        }

        public void removeAddButton(){

            mView.findViewById(R.id.addFriend).setVisibility(View.GONE);
        }

        public View getView() {
            return mView;
        }

        public void clicked() {
            if (!buttonClicked) {
                buttonClicked = true;
                mView.findViewById(R.id.linearLayout).setAlpha((float) 0.6);
            } else {
                buttonClicked = false;
                mView.findViewById(R.id.linearLayout).setAlpha((float) 1);
            }
        }

    }

    public class Adapter extends BaseAdapter {

        Context c;

        public Adapter(Context context) {
            this.c = context;
        }

        @Override
        public int getCount() {
            return locationArrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return locationArrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return locationArrayList.indexOf(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.location_list_template, null);
            }

            System.out.println(position);
            locationArrayList.get(position).setTextView((TextView) convertView.findViewById(R.id.locationTextView));
            locationArrayList.get(position).updateText();
            locationArrayList.get(position).setAlpha();
            locationArrayList.get(position).getButton().setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL);

            return convertView;
        }
    }

}
