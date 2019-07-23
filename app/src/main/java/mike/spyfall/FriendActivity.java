package mike.spyfall;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FriendActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    //private ListView mListView;
    private DatabaseReference mRef;
    private DatabaseReference mRefForRecView;
    private AutoCompleteTextView friendLookUpView;
    private String currentUserName;
    private String currentUID;
    private String currentName;
    private String currentGame;
    private String friendUserName;
    private String friendUID;
    private String friendName;
    private AdView mAdView;

    public void onFriendSearch(View view){
        friendUserName = friendLookUpView.getText().toString().toLowerCase();
        if(friendUserName.equals(currentUserName)) {
            Toast.makeText(FriendActivity.this, "Don't search for yourself, loser.", Toast.LENGTH_LONG).show();
        } else {

            mRef = FirebaseDatabase.getInstance().getReference("friendUID");
            mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(friendUserName).exists()) {
                        //sends a request to specified user
                        friendUID = dataSnapshot.child(friendUserName).getValue().toString();
                        mRef = FirebaseDatabase.getInstance().getReference("users/" + friendUID + "/friends/users/" + currentUserName);
                        mRef.child("from").setValue(true);
                        mRef.child("requestAccepted").setValue(false);
                        mRef.child("userName").setValue(currentUserName);
                        //shows up on user's list also
                        //get friends name to put on main user's friends list
                        mRef = FirebaseDatabase.getInstance().getReference("users/" + friendUID + "/userInfo");
                        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot friendDataSnapshot) {
                                friendName = friendDataSnapshot.child("name").getValue().toString();

                                //add the friend on main user's list
                                mRef = FirebaseDatabase.getInstance().getReference("users/" + currentUID + "/friends/users/" + friendUserName);
                                mRef.child("from").setValue(false);
                                mRef.child("requestAccepted").setValue(false);
                                mRef.child("userName").setValue(friendUserName);
                                Toast.makeText(FriendActivity.this, "Friend request sent!", Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Toast.makeText(FriendActivity.this, "Connection error. Retry.", Toast.LENGTH_LONG).show();
                            }
                        });

                    } else {
                        Toast.makeText(FriendActivity.this, "User doesn't exist.", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(FriendActivity.this, "Connection error. Retry.", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        //startActivity(new Intent(this, MainActivity.class));
        toMenu();
    }

    public boolean onOptionsItemSelected(MenuItem item){
        toMenu();
        return true;
    }

    public void toMenu() {
        Intent intent = new Intent(FriendActivity.this, MainActivity.class);
        // Tell the new activity how return when finished.
        intent.putExtra("anim id in", R.anim.right_in);
        intent.putExtra("anim id out", R.anim.right_out);
        intent.putExtra("kicked", 0);
        startActivity(intent);
        // This makes the new screen slide up as it fades in
        // while the current screen slides up as it fades out.
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    public void toGuestGame(){
        Intent intent = new Intent(FriendActivity.this, GuestGameActivity.class);
        // Tell the new activity how return when finished.
        intent.putExtra("anim id in", R.anim.left_in);
        intent.putExtra("anim id out", R.anim.left_out);
        startActivity(intent);
        // This makes the new screen slide up as it fades in
        // while the current screen slides up as it fades out.
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend2);
        MobileAds.initialize(this, "ca-app-pub-7054487445717644~4798964612");
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("145310EF75B6B1FE8013E630E72F45CB").build();
        //AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        //sets the current name and user id
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        currentUID = user.getUid();

        mRecyclerView = (RecyclerView) findViewById(R.id.friendsList);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        
        //sets the current username of the user
        mRef = FirebaseDatabase.getInstance().getReference("users/" + currentUID);
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot currentUserInfoSnapshot) {
                currentUserName = currentUserInfoSnapshot.child("userInfo").child("userName").getValue().toString();
                currentName = currentUserInfoSnapshot.child("userInfo").child("name").getValue().toString();
                if (currentUserInfoSnapshot.child("userInfo").child("inGame").exists()) {
                    currentGame = currentUserInfoSnapshot.child("userInfo").child("inGame").getValue().toString();
                }


                friendLookUpView = (AutoCompleteTextView) findViewById(R.id.userName);

                mRefForRecView = FirebaseDatabase.getInstance().getReference("users/" + currentUID + "/friends/users");
                if(currentUserInfoSnapshot.child("friends").exists()) {

                    FirebaseRecyclerAdapter<FriendClass, FriendViewHolder> mAdapter = new FirebaseRecyclerAdapter<FriendClass, FriendViewHolder>(
                            FriendClass.class,
                            R.layout.friend_list_template,
                            FriendViewHolder.class,
                            mRefForRecView

                    ) {
                        @Override
                        protected void populateViewHolder(final FriendViewHolder view, final FriendClass mFriend, int position) {
                            if (mFriend.getUserName() != null) {
                                System.out.println(mFriend.getUserName() + "**************************************************");
                                final String tempUserName = mFriend.getUserName();
                                view.setUserName(tempUserName);

                                //find the UID of user so name and game status can be gathered
                                mRef = FirebaseDatabase.getInstance().getReference("friendUID");
                                mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        if(dataSnapshot.child(tempUserName).exists()) {
                                            System.out.println(dataSnapshot.child(tempUserName).getValue() + "===========================================");
                                            final String tempUID = dataSnapshot.child(tempUserName).getValue().toString();

                                            //find name and game status
                                            mRef = FirebaseDatabase.getInstance().getReference("users/" + tempUID + "/userInfo");
                                            mRef.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    if(dataSnapshot.child("name").exists()) {
                                                        final String tempName = dataSnapshot.child("name").getValue().toString();
                                                        final String tempGameStatus;
                                                        view.setName(tempName);

                                                        if (dataSnapshot.child("inGame").exists()) {
                                                            tempGameStatus = dataSnapshot.child("inGame").getValue().toString();
                                                        } else {
                                                            tempGameStatus = null;
                                                        }

                                                        //if the friend request needs to be accepted, and it is from another user
                                                        if (!mFriend.getRequestAccepted() && mFriend.getFrom()) {
                                                            //the accept and decline buttons will be shown
                                                            view.showAcceptButton();
                                                            view.showDeclineButton();
                                                            view.hideJoinGameButton();
                                                            view.setStatus("Accept friend request?");
                                                            Button accBtn = (Button) view.getView().findViewById(R.id.acceptButton);
                                                            Button decBtn = (Button) view.getView().findViewById(R.id.declineButton);


                                                            //When user clicks accept,
                                                            accBtn.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    //changes the friend's profile under the users account to true
                                                                    mRef = FirebaseDatabase.getInstance().getReference("users/" + currentUID + "/friends/users");
                                                                    mRef.child(tempUserName).child("requestAccepted").setValue(true);
                                                                    //changes the users friend account under the friend's profile
                                                                    mRef = FirebaseDatabase.getInstance().getReference("users/" + tempUID + "/friends/users");
                                                                    mRef.child(currentUserName).child("requestAccepted").setValue(true);
                                                                    Toast.makeText(FriendActivity.this, "Request accepted!", Toast.LENGTH_LONG).show();

                                                                    checkGameStatus(view, tempName, tempUID, tempUserName, tempGameStatus);

                                                                }
                                                            });

                                                            decBtn.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    mRef = FirebaseDatabase.getInstance().getReference("users/" + currentUID + "/friends/users");
                                                                    mRef.child(tempUserName).removeValue();
                                                                    mRef = FirebaseDatabase.getInstance().getReference("users/" + tempUID + "/friends/users");
                                                                    mRef.child(currentUserName).removeValue();

                                                                }
                                                            });

                                                            //if friend request needs to be accepted, and current user sent it,
                                                        } else if (!mFriend.getRequestAccepted() && !mFriend.getFrom()) {
                                                            view.setStatus("Waiting for " + tempName + " to accept.");
                                                            view.hideJoinGameButton();
                                                            view.hideDeclineButton();
                                                            view.hideAcceptButton();
                                                        }

                                                        //if the friend request has been accepted, join button will show
                                                        if (mFriend.getRequestAccepted()) {

                                                            checkGameStatus(view, tempName, tempUID, tempUserName, tempGameStatus);

                                                        }
                                                    }

                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {
                                                    Toast.makeText(FriendActivity.this, "Connection Error", Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Toast.makeText(FriendActivity.this, "Connection Error", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }

                        }

                        public void checkGameStatus(final FriendViewHolder view, final String friendName, final String friendUID, final String tempUserName, final String hostUserName) {
                            if (hostUserName != null) {
                                //if they are not in your game
                                if(!hostUserName.equals(currentUserName)) {
                                    mRef = FirebaseDatabase.getInstance().getReference("friendUID");
                                    mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            //gets the UID of game host
                                            final String hostUID = dataSnapshot.child(hostUserName).getValue().toString();
                                            System.out.println(hostUID + "======================");
                                            mRef = FirebaseDatabase.getInstance().getReference("users/" + hostUID + "/currentGame");
                                            mRef.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.exists()) {
                                                        System.out.println(dataSnapshot.child("location").getValue().toString() + "***********");
                                                        if (dataSnapshot.child("location").getValue().toString().equals("none")) {
                                                            view.setStatus(friendName + " is in " + hostUserName + "'s game.");
                                                            view.hideAcceptButton();
                                                            view.hideDeclineButton();
                                                            view.showJoinGameButton();
                                                            Button joinBtn = (Button) view.getView().findViewById(R.id.joinGameButton);
                                                            joinBtn.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    //leaves current game and joines friend's game
                                                                    if (currentGame != null) {
                                                                        if (currentGame.equals(currentUserName)) {
                                                                            System.out.println(1 + "  " + hostUID);
                                                                            mRef = FirebaseDatabase.getInstance().getReference("users");
                                                                            mRef.child(currentUID).child("currentGame").removeValue();
                                                                            mRef.child(currentUID).child("userInfo").child("inGame").setValue(hostUserName);
                                                                            mRef.child(hostUID).child("currentGame").child("users").child(currentUserName).child("userName").setValue(currentUserName);
                                                                            toGuestGame();
                                                                        } else {
                                                                            mRef = FirebaseDatabase.getInstance().getReference("friendUID");
                                                                            mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                @Override
                                                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                    System.out.println(2 + "  " + hostUID + "   " + dataSnapshot.child(currentGame));
                                                                                    mRef = FirebaseDatabase.getInstance().getReference("users");
                                                                                    mRef.child(dataSnapshot.child(currentGame).getValue().toString() + "/currentGame/users").child(currentUserName).removeValue();
                                                                                    mRef.child(currentUID).child("userInfo").child("inGame").setValue(hostUserName);
                                                                                    mRef.child(hostUID).child("currentGame").child("users").child(currentUserName).child("userName").setValue(currentUserName);
                                                                                    toGuestGame();
                                                                                }

                                                                                @Override
                                                                                public void onCancelled(DatabaseError databaseError) {

                                                                                }
                                                                            });
                                                                        }
                                                                    } else {
                                                                        System.out.println(3 + "  " + hostUID);
                                                                        mRef = FirebaseDatabase.getInstance().getReference("users");
                                                                        mRef.child(currentUID).child("userInfo").child("inGame").setValue(hostUserName);
                                                                        mRef.child(hostUID).child("currentGame").child("users").child(currentUserName).child("userName").setValue(currentUserName);
                                                                        toGuestGame();
                                                                    }

                                                                }
                                                            });
                                                        } else {
                                                            view.setStatus("Wait for " + friendName + "'s game to end");
                                                            view.hideAcceptButton();
                                                            view.hideDeclineButton();
                                                            view.hideJoinGameButton();
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {
                                                    Toast.makeText(FriendActivity.this, "Connection Error", Toast.LENGTH_LONG).show();

                                                }
                                            });

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            Toast.makeText(FriendActivity.this, "Connection Error", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                //if they are in your game
                                } else {
                                    view.setStatus(friendName + " is in your game!");
                                    view.hideAcceptButton();
                                    view.hideDeclineButton();
                                    view.hideJoinGameButton();
                                }
                            } else {
                                view.setStatus(friendName + " is not in game.");
                                view.hideAcceptButton();
                                view.hideDeclineButton();
                                view.hideJoinGameButton();
                            }

                        }


                    };
                    mRecyclerView.setAdapter(mAdapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(FriendActivity.this, "Connection Error", Toast.LENGTH_LONG).show();
            }
        });
    }


    public static class FriendViewHolder extends RecyclerView.ViewHolder{
        View mView;

        public FriendViewHolder(View itemView){
            super(itemView);
            mView = itemView;
        }

        public View getView(){
            return mView;
        }

        public void setName(String name){
            TextView nametext = (TextView) mView.findViewById(R.id.nameText);
            nametext.setText(name);
        }
        public void setStatus(String status) {
            TextView statustext = (TextView) mView.findViewById(R.id.statusText);
            statustext.setText(status);
        }

        public void setUserName(String userName){
            TextView userNametext = (TextView) mView.findViewById(R.id.userNameText);
            userNametext.setText("(" + userName + ")");
        }

        public void showAcceptButton(){
            Button a = (Button) mView.findViewById(R.id.acceptButton);
            a.setVisibility(View.VISIBLE);
        }

        public void hideAcceptButton(){
            Button a = (Button) mView.findViewById(R.id.acceptButton);
            a.setVisibility(View.GONE);
        }

        public void showDeclineButton(){
            Button d = (Button) mView.findViewById(R.id.declineButton);
            d.setVisibility(View.VISIBLE);
        }

        public void hideDeclineButton(){
            Button d = (Button) mView.findViewById(R.id.declineButton);
            d.setVisibility(View.GONE);
        }

        public void showJoinGameButton(){
            Button j = (Button) mView.findViewById(R.id.joinGameButton);
            j.setVisibility(View.VISIBLE);
        }

        public void hideJoinGameButton(){
            Button j = (Button) mView.findViewById(R.id.joinGameButton);
            j.setVisibility(View.GONE);
        }



    }

}
