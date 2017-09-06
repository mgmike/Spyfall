package mike.spyfall;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;

public class add_location extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mRef;
    private String currentUserName;
    private String currentUID;
    private AdView mAdView;
    private GridView locationLayout;
    private GridView roleLayout;
    private EditText locationField;
    private ArrayList<Location> locationArrayList = new ArrayList<>();
    private ArrayList<Location> roleArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);
        //Firebase.setAndroidContext(this);
        MobileAds.initialize(this, "ca-app-pub-7054487445717644~4798964612");
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("145310EF75B6B1FE8013E630E72F45CB").build();
        //AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        if(adRequest.isTestDevice(add_location.this)){
            System.out.println("This is a test device. _-*-_-*-_-*-_-*-_-*-_-*-_-*-_-*-_-*-_");
        }

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        currentUID = user.getUid();

        locationField = (EditText) findViewById(R.id.editText);
        locationLayout = (GridView) findViewById(R.id.locationList);
        roleLayout = (GridView) findViewById(R.id.roleList);
        mRef = FirebaseDatabase.getInstance().getReference("defaultLocations");
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                locationArrayList.clear();
                int i = 0;
                for (DataSnapshot temp : dataSnapshot.getChildren()) {
                    Location tempLocation = new Location(temp.getKey());
                    locationArrayList.add(tempLocation);
                    i++;
                }
                locationLayout.setAdapter(new Adapter(add_location.this));
                locationLayout.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                        Toast.makeText(add_location.this, locationArrayList.get(position).getItemLocation(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        roleArrayList.add(new Location(""));
        //roleLayout.setAdapter(new RoleAdapter(add_location.this));
        roleLayout.setAdapter(new ArrayAdapter<Location>(this,R.layout.role_template, roleArrayList));

        mRef = FirebaseDatabase.getInstance().getReference();


    }

    public void toMenu() {
        Intent intent = new Intent(add_location.this, MainActivity.class);
        // Tell the new activity how return when finished.
        intent.putExtra("anim id in", R.anim.right_in);
        intent.putExtra("anim id out", R.anim.right_out);
        intent.putExtra("kicked", 0);
        startActivity(intent);
        // This makes the new screen slide up as it fades in
        // while the current screen slides up as it fades out.
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
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
            System.out.println(position);
            locationArrayList.get(position).setTextView((TextView) convertView.findViewById(R.id.textView));
            locationArrayList.get(position).updateText();
            locationArrayList.get(position).getButton().setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL);
            //} else {
            // locationArrayList.get(position).setAlpha();
            //}

            return convertView;
        }
    }

    public static class RoleViewHolder extends RecyclerView.ViewHolder {
        View mView;
        public RoleViewHolder(View view){
            super(view);
            this.mView = view;
        }

        public View getmView(){return mView;}


    }

}
