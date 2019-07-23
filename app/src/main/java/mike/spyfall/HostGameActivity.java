package mike.spyfall;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class HostGameActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private DatabaseReference mRefForRecView;
    private DatabaseReference mRef;
    private Random randomNumber = new Random();
    private String currentUID;
    private String currentUserName;
    private ArrayList<Location> locationArrayList = new ArrayList<>();
    private GridView locationList;
    private Button startGameButton;
    private Button leaveGameButton;
    private Button addFriendButton;
    private Button hideRoleButton;
    private Button addTimeButton;
    private Button reduceTimeButton;
    private LinearLayout timeChange;
    private TextView roleText;
    private TextView timeText;
    private AdView mAdView;

    //480000
    private long startTime = 480000;
    private CounterClass timer = new CounterClass(startTime, 1000);

    private String location;


    public void onAddFriends(View view) {
        for(int i = 1;i < 10; i++){
            mRef = FirebaseDatabase.getInstance().getReference();
            mRef.child("users").child(currentUID).child("currentGame").child("users").child("player" + i).child("userName").setValue("player" + i);
            mRef.child("users").child(Integer.toString(i)).child("userInfo").child("inGame").setValue("mgmike1034");
            //mRef.child("users").child(Integer.toString(i)).child("userInfo").child("name").setValue("P"+i);
            //mRef.child("users").child(Integer.toString(i)).child("userInfo").child("userID").setValue(Integer.toString(i));
            //mRef.child("users").child(Integer.toString(i)).child("userInfo").child("userName").setValue("player"+i);
            //mRef.child("friendUID").child("player" + Integer.toString(i)).setValue(Integer.toString(i));
        }
        //startActivity(new Intent(HostGameActivity.this, AddFriend.class));
    }

    public void onHideRole(View view) {
        if (hideRoleButton.getText().equals("Hide role")) {
            hideRoleButton.setText("Show role");
            roleText.setText("Play fair!");
        } else {
            hideRoleButton.setText("Hide role");
            displayRole();
        }
    }

    public void onLeaveGame(final View view) {
        mRef = FirebaseDatabase.getInstance().getReference("friendUID");
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot friendUIDSnapshot) {
                mRef = FirebaseDatabase.getInstance().getReference("users/" + currentUID);
                mRef.child("currentGame").child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //removes inGame status for each player
                        mRef = FirebaseDatabase.getInstance().getReference("users");
                        for(DataSnapshot temp: dataSnapshot.getChildren()){
                            mRef.child(friendUIDSnapshot.child(temp.child("userName").getValue().toString()).getValue().toString()).child("userInfo").child("inGame").removeValue();
                        }
                        mRef.child(currentUID).child("userInfo").child("inGame").removeValue();
                        mRef.child(currentUID).child("currentGame").removeValue();
                        toMenu();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(HostGameActivity.this, "Connection Error", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(HostGameActivity.this, "Connection Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void onStartGame(View view) {
        startGame();
    }

    //this is so i can call this method in the onFinish method of timer without having to pass a view
    public void startGame(){
        //finds location
        mRef = FirebaseDatabase.getInstance().getReference("users/" + currentUID + "/currentGame/location");
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                location = dataSnapshot.getValue().toString();

                // game lobby
                if (location.equals("none")) {
                    startGameButton.setText("End Game");
                    leaveGameButton.setVisibility(View.GONE);
                    addFriendButton.setVisibility(View.GONE);
                    hideRoleButton.setVisibility(View.VISIBLE);
                    timeChange.setVisibility(View.VISIBLE);

                    addTimeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            System.out.println("up********************************");
                            startTime += 60000;
                            if(startTime > 1800000){
                                startTime = 1800000;
                                Toast.makeText(HostGameActivity.this, "30 mins max!", Toast.LENGTH_LONG);
                            } else {
                                mRef = FirebaseDatabase.getInstance().getReference("users/" + currentUID + "/currentGame");
                                mRef.child("startTime").setValue(startTime);
                                String time = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(startTime),
                                        TimeUnit.MILLISECONDS.toSeconds(startTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(startTime)));
                                timeText.setText(time);
                            }
                        }
                    });
                    reduceTimeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            System.out.println("down********************************");
                            startTime -= 60000;
                            if(startTime < 60000){
                                startTime = 60000;
                                Toast.makeText(HostGameActivity.this, "Nice try, but you can't go back in time!", Toast.LENGTH_LONG);
                            } else {
                                mRef = FirebaseDatabase.getInstance().getReference("users/" + currentUID + "/currentGame");
                                mRef.child("startTime").setValue(startTime);
                                String time = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(startTime),
                                        TimeUnit.MILLISECONDS.toSeconds(startTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(startTime)));
                                timeText.setText(time);
                            }
                        }
                    });

                    //Creates a list of all players
                    mRef = FirebaseDatabase.getInstance().getReference("users/" + currentUID + "/currentGame/users");
                    mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            final int playerCount = (int) dataSnapshot.getChildrenCount();
                            int i = 0;
                            final String playerList[][] = new String[playerCount][2];
                            for (DataSnapshot temp : dataSnapshot.getChildren()) {
                                playerList[i][0] = temp.getKey();
                                i++;
                            }

                            //picks a random location and updates database
                            location = locationArrayList.get(randomNumber.nextInt(locationArrayList.size())).getItemLocation();

                            //***************************************************************************************************************************
                            mRef = FirebaseDatabase.getInstance().getReference("defaultLocations");
                            mRef.child(location).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    //Fills a temparary list with the same amount of roles as players
                                    /*
                                    String roleList[] = new String[playerCount];
                                    for (int j = 0; j < playerCount - 1; j++) {
                                        roleList[j] = dataSnapshot.child("roles").child(Integer.toString(j)).getValue().toString();
                                    }
                                    roleList[playerCount - 1] = "Spy";
                                    */
                                    //^ temp role assign

                                    int rolesCount = (int) dataSnapshot.child("roles").getChildrenCount();
                                    ArrayList<String> roleList = new ArrayList<String>();
                                    if((playerCount - 1) <= rolesCount && playerCount > 0){
                                        for (int j = 0; j < playerCount - 1; j++) {
                                            roleList.add(dataSnapshot.child("roles").child(Integer.toString(j)).getValue().toString());
                                        }
                                        roleList.add("Spy");
                                    } else {
                                        for (int j = 0; j < rolesCount; j++) {
                                            roleList.add(dataSnapshot.child("roles").child(Integer.toString(j)).getValue().toString());
                                            //System.out.println(roleList.get(j));
                                        }
                                        int playersLeft = playerCount - rolesCount - 1;
                                        int repeatCount = (int) dataSnapshot.child("repeats").getChildrenCount();
                                        for(int k = 0; k < playersLeft; k++){
                                            roleList.add(dataSnapshot.child("repeats").child(Integer.toString((playersLeft - k) % repeatCount)).getValue().toString());
                                            //System.out.println(roleList.get(rolesCount + k));
                                        }
                                        roleList.add("Spy");
                                    }

                                    //Assigns each player a role
                                    int tempSub;
                                    for (int k = 0; k < playerCount; k++) {
                                        tempSub = randomNumber.nextInt(playerCount);
                                        while (playerList[tempSub][1] != null) {
                                            tempSub = randomNumber.nextInt(playerCount);
                                        }
                                        playerList[tempSub][1] = roleList.get(k);
                                        //System.out.println(playerList[tempSub][1]);
                                    }

                                    //updates the database
                                    mRef = FirebaseDatabase.getInstance().getReference("users/" + currentUID + "/currentGame/users");
                                    for (int l = 0; l < playerCount; l++) {
                                        mRef.child(playerList[l][0]).child("role").setValue(playerList[l][1]);
                                    }
                                    displayRole();
                                    //sets up timer and updates database
                                    long currentTime = Calendar.getInstance().getTimeInMillis();
                                    long endTime = currentTime + startTime;
                                    mRef = FirebaseDatabase.getInstance().getReference("users/" + currentUID + "/currentGame");
                                    mRef.child("endTime").setValue(endTime);
                                    mRef.child("location").setValue(location);
                                    timer = new CounterClass(startTime, 1000);
                                    timer.start();
                                    System.out.println(currentTime);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Toast.makeText(HostGameActivity.this, "Connection Error", Toast.LENGTH_LONG).show();
                                }
                            });

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(HostGameActivity.this, "Connection Error", Toast.LENGTH_LONG);
                        }
                    });

                    timeChange.setVisibility(View.GONE);
                    //game end
                } else {
                    //update locationView cells alpha
                    for (int i = 0; i < locationArrayList.size(); i++){
                        locationArrayList.get(i).reset();
                    }
                    timer.cancel();
                    if(!timeText.getText().equals("Time's up!")) {
                        String time = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(startTime),
                                TimeUnit.MILLISECONDS.toSeconds(startTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(startTime)));
                        timeText.setText(time);
                    }
                    startGameButton.setText("Start Game");
                    leaveGameButton.setVisibility(View.VISIBLE);
                    addFriendButton.setVisibility(View.GONE);
                    hideRoleButton.setVisibility(View.GONE);
                    timeChange.setVisibility(View.GONE);
                    roleText.setText("Pres 'Start Game' to begin!");
                    mRef = FirebaseDatabase.getInstance().getReference("users/" + currentUID + "/currentGame");
                    mRef.child("endGame").removeValue();
                    mRef.child("location").setValue("none");
                    mRef.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot temp : dataSnapshot.getChildren()) {
                                mRef.child("users").child(temp.getKey()).child("role").removeValue();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(HostGameActivity.this, "Connection Error", Toast.LENGTH_LONG);
                        }
                    });
                    timeChange.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(HostGameActivity.this, "Connection Error", Toast.LENGTH_LONG).show();
            }
        });

    }

    public void displayRole() {
        mRef = FirebaseDatabase.getInstance().getReference("users/" + currentUID + "/currentGame");
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String loc = dataSnapshot.child("location").getValue().toString();
                String role = dataSnapshot.child("users").child(currentUserName).child("role").getValue().toString();
                if (role.equals("Spy")) {
                    roleText.setText("You are the Spy!");
                } else {
                    roleText.setText("The location is " + loc + ".\nYour Role is " + role + ".");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(HostGameActivity.this, "Connection Error", Toast.LENGTH_LONG);
            }
        });
    }

    //this is so that i can call AppCompatActivity onCreate without calling HostGameActivity onCreate
    public void callSuper(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onBackPressed(){
        toMenu();
    }

    public void toMenu() {
        Intent intent = new Intent(HostGameActivity.this, MainActivity.class);
        // Tell the new activity how return when finished.
        intent.putExtra("anim id in", R.anim.right_in);
        intent.putExtra("anim id out", R.anim.right_out);
        intent.putExtra("kicked", 0);
        startActivity(intent);
        // This makes the new screen slide up as it fades in
        // while the current screen slides up as it fades out.
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }
    //on the left arrow click
    public boolean onOptionsItemSelected(MenuItem item){
        toMenu();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_host_game);
        MobileAds.initialize(this, "ca-app-pub-7054487445717644~4798964612");
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("145310EF75B6B1FE8013E630E72F45CB").build();
        //AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        //sets up the location clickable list
        locationList = (GridView) findViewById(R.id.locationList);
        mRef = FirebaseDatabase.getInstance().getReference();
        mRef.child("defaultLocations").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                locationArrayList.clear();
                int i = 0;
                for (DataSnapshot temp : dataSnapshot.getChildren()) {
                    Location tempLocation = new Location(temp.getKey());
                    locationArrayList.add(tempLocation);
                    i++;
                }
                locationList.setAdapter(new Adapter(HostGameActivity.this));
                locationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                        mRef = FirebaseDatabase.getInstance().getReference("users/" + currentUID + "/currentGame/location");
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
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(HostGameActivity.this, "Connection Error", Toast.LENGTH_LONG).show();
            }
        });

        startGameButton = (Button) findViewById(R.id.startGameButton);
        leaveGameButton = (Button) findViewById(R.id.leaveGameButton);
        addFriendButton = (Button) findViewById(R.id.addFriendButton);
        hideRoleButton = (Button) findViewById(R.id.hideRoleButton);
        addTimeButton = (Button) findViewById(R.id.addTimeButton);
        reduceTimeButton = (Button) findViewById(R.id.reduceTimeButton);
        timeChange = (LinearLayout) findViewById(R.id.timeChange);
        roleText = (TextView) findViewById(R.id.roleText);
        timeText = (TextView) findViewById(R.id.timeText);
        roleText.setMovementMethod(new ScrollingMovementMethod());


        while (currentUID == null) {
            //gets the user's unique id and username
            currentUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        //finds location
        mRef = FirebaseDatabase.getInstance().getReference("users/" + currentUID + "/currentGame");
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("location").exists()) {
                    location = dataSnapshot.child("location").getValue().toString();

                } else {
                    location = "none";
                }
                //if you are mid game, the top will update
                if (location.equals("none")) {
                    startGameButton.setVisibility(View.VISIBLE);
                    startGameButton.setText("Start Game");
                    leaveGameButton.setVisibility(View.VISIBLE);
                    addFriendButton.setVisibility(View.GONE);
                    hideRoleButton.setVisibility(View.GONE);
                    timeChange.setVisibility(View.VISIBLE);
                    roleText.setText("Press 'Start Game' to begin!");
                    if(!timeText.getText().equals("Time's up!")) {
                        timeText.setText("08:00");
                    }

                    addTimeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            System.out.println("up********************************");
                            startTime += 60000;
                            if(startTime > 1800000){
                                startTime = 1800000;
                                Toast.makeText(HostGameActivity.this, "30 mins max!", Toast.LENGTH_LONG);
                            } else {
                                mRef = FirebaseDatabase.getInstance().getReference("users/" + currentUID + "/currentGame");
                                mRef.child("startTime").setValue(startTime);
                                String time = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(startTime),
                                        TimeUnit.MILLISECONDS.toSeconds(startTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(startTime)));
                                timeText.setText(time);
                            }
                        }
                    });
                    reduceTimeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            System.out.println("down********************************");
                            startTime -= 60000;
                            if(startTime < 60000){
                                startTime = 60000;
                                Toast.makeText(HostGameActivity.this, "Nice try, but you can't go back in time!", Toast.LENGTH_LONG);
                            } else {
                                mRef = FirebaseDatabase.getInstance().getReference("users/" + currentUID + "/currentGame");
                                mRef.child("startTime").setValue(startTime);
                                String time = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(startTime),
                                        TimeUnit.MILLISECONDS.toSeconds(startTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(startTime)));
                                timeText.setText(time);
                            }
                        }
                    });
                } else { //if you are in mid game
                    startGameButton.setVisibility(View.VISIBLE);
                    startGameButton.setText("End Game");
                    leaveGameButton.setVisibility(View.GONE);
                    addFriendButton.setVisibility(View.GONE);
                    hideRoleButton.setVisibility(View.VISIBLE);
                    timeChange.setVisibility(View.GONE);
                    displayRole();
                    //set up timer
                    mRef = FirebaseDatabase.getInstance().getReference("users/" + currentUID + "/currentGame");
                    mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            long currentTime = Calendar.getInstance().getTimeInMillis();
                            if(dataSnapshot.child("startTime").exists()){
                                startTime = Integer.parseInt(dataSnapshot.child("startTime").getValue().toString());
                            } else {
                                startTime = 480000;
                            }
                            long timeLeft = (Long.parseLong(dataSnapshot.child("endTime").getValue().toString()) - currentTime);
                            if(timeLeft > 0 && timeLeft < startTime){
                                timer = new CounterClass(timeLeft, 1000);
                                timer.start();
                            } else if(timeLeft > 0 && timeLeft > startTime){
                                startGame();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(HostGameActivity.this, "Connection Error", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(HostGameActivity.this, "Connection Error", Toast.LENGTH_LONG).show();
            }
        });

        //set up recyclerView
        mRecyclerView = (RecyclerView) findViewById(R.id.playersList);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //grabs username and populates currentGame field in database if not already populated
        mRef = FirebaseDatabase.getInstance().getReference("users/" + currentUID);
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentUserName = dataSnapshot.child("userInfo").child("userName").getValue().toString();
                if (dataSnapshot.child("currentGame").exists()) {
                } else {
                    mRef = FirebaseDatabase.getInstance().getReference("users/" + currentUID);
                    mRef.child("currentGame").child("location").setValue("none");
                    mRef.child("userInfo").child("inGame").setValue(currentUserName);
                    mRef.child("currentGame").child("host").setValue(currentUserName);
                    mRef.child("currentGame").child("users").child(currentUserName).child("userName").setValue(currentUserName);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(HostGameActivity.this, "Connection Error", Toast.LENGTH_LONG).show();
            }
        });

        mRefForRecView = FirebaseDatabase.getInstance().getReference("users/" + currentUID + "/currentGame/users");
        FirebaseRecyclerAdapter<CurrentGamePlayerListClass, CurrentGamePlayerViewHolder> mAdapter = new FirebaseRecyclerAdapter<CurrentGamePlayerListClass, CurrentGamePlayerViewHolder>(
                CurrentGamePlayerListClass.class,
                R.layout.player_list_template,
                HostGameActivity.CurrentGamePlayerViewHolder.class,
                mRefForRecView

        ) {
            @Override
            protected void populateViewHolder(final HostGameActivity.CurrentGamePlayerViewHolder viewHolder, final CurrentGamePlayerListClass model, int position) {
                if(!model.getUserName().equals(null)) {
                    final String tempUserName = model.getUserName();
                    final Button kickBtn = (Button) viewHolder.getView().findViewById(R.id.kickButton);
                    final LinearLayout linLayout = (LinearLayout) viewHolder.getView().findViewById(R.id.linearLayout);
                    viewHolder.setUserName(tempUserName);
                    //find the UID of user so name can be gathered
                    mRef = FirebaseDatabase.getInstance().getReference("friendUID");
                    mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //find UID
                            if(dataSnapshot.child(tempUserName).exists()) {
                                //System.out.println(dataSnapshot.child(tempUserName).getValue() + "===========================================");
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
                                        Toast.makeText(HostGameActivity.this, "Connection Error", Toast.LENGTH_LONG).show();
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
                                                            Toast.makeText(HostGameActivity.this, "Friend request sent!", Toast.LENGTH_LONG).show();
                                                            viewHolder.removeAddButton();

                                                        }
                                                    });
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                                Toast.makeText(HostGameActivity.this, "Connection Error", Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(HostGameActivity.this, "Connection Error", Toast.LENGTH_LONG).show();
                        }
                    });
                    //if game has not yet started
                    if (model.getRole() == null) {
                        linLayout.setAlpha((float) 1);
                        linLayout.setOnClickListener(null);
                        //if this list item is not the host's, kick button will not appear
                        if (!model.getUserName().equals(currentUserName)) {
                            kickBtn.setVisibility(View.VISIBLE);
                            kickBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mRef = FirebaseDatabase.getInstance().getReference("friendUID");
                                    mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            mRef = FirebaseDatabase.getInstance().getReference("users");
                                            mRef.child(currentUID).child("currentGame").child("users").child(model.getUserName()).removeValue();
                                            mRef.child(dataSnapshot.child(model.getUserName()).getValue().toString()).child("userInfo").child("inGame").removeValue();
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            Toast.makeText(HostGameActivity.this, "Connection Error", Toast.LENGTH_LONG).show();
                                        }
                                    });

                                }
                            });
                        } else {
                            kickBtn.setVisibility(View.GONE);
                        }
                    //if game has started
                    } else {
                        //add clickable function here
                        kickBtn.setVisibility(View.GONE);
                        linLayout.setAlpha((float) 1);
                        linLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                viewHolder.clicked();
                            }
                        });

                    }
                }
            }
        };
        mRecyclerView.setAdapter(mAdapter);
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
            startGame();
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

            //if (!locationArrayList.get(position).checkTextView()) {
                //System.out.println(position);
                locationArrayList.get(position).setTextView((TextView) convertView.findViewById(R.id.locationTextView));
                locationArrayList.get(position).updateText();
                locationArrayList.get(position).setAlpha();
                locationArrayList.get(position).getButton().setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL);
            //} else {
               // locationArrayList.get(position).setAlpha();
            //}

            return convertView;
        }
    }
    /*
    public class Location {
        private TextView textView;
        private String itemLocation;
        private boolean buttonClicked = false;

        public Location (String itemLocation){
            this.itemLocation = itemLocation;

        }

        public String getText() {
            return textView.getText().toString();
        }

        public void setText(String text) {
            textView.setText(text);
        }

        public TextView getButton() {
            return textView;
        }

        public void setTextView(TextView button) {
            this.textView = button;
        }

        public String getItemLocation() {
            return itemLocation;
        }

        public void updateAlpha(){
            if(!buttonClicked){
                textView.setAlpha((float) 0.6);
                buttonClicked = true;
            } else {
                textView.setAlpha((float) 1);
                buttonClicked = false;
            }
        }

        public boolean getAlpha(){
            return buttonClicked;
        }

        public void reset(){
            if (textView != null) {
                buttonClicked = false;
                textView.setAlpha((float) 1);
            }
        }

        public void setAlpha(){
            if (!buttonClicked){
                textView.setAlpha((float) 1);
            } else {
                textView.setAlpha((float) 0.6);
            }
        }

        public boolean checkTextView(){
            if (textView == null){
                return false;
            }else{
                return true;
            }
        }

        public void updateText(){
            //if(button.getText() == null){
                textView.setText(itemLocation);
            //}
        }
    }
    */
}