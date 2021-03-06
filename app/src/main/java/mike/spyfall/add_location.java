package mike.spyfall;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import mike.spyfall.ItemClasses.Location;
import mike.spyfall.ItemClasses.NewRoleElement;

public class add_location extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mRef;

    private String currentUserName;
    private String currentUID;

    //private AdView mAdView;
    private GridView locationLayout;
    private RecyclerView roleLayout;
    private EditText locationField;
    private RoleRecyclerAdapter roleRecyclerAdapter;
    private ArrayList<Location> locationArrayList = new ArrayList<>();
    private ArrayList<NewRoleElement> roleArrayList = new ArrayList<>();

    public void onAddLocation (View view){
        boolean enoughRepeats = false;
        if (!locationField.getText().equals("")) {
            String location = String.valueOf(locationField.getText());
            //checks if any check boxes have been checked
            for (int i = 0; i < roleArrayList.size(); i++) {
                if (roleArrayList.get(i).checkBoxValue()) {
                    enoughRepeats = true;
                }
            }
            if (enoughRepeats) {
                int tempRepeats = 0;
                int tempRoles = 0;
                for (int j = 0; j < roleArrayList.size(); j++) {
                    //if this role is repeatable it will go in the repeats catagory in custom locations
                    if (roleArrayList.get(j).checkBoxValue()) {
                        mRef.child("users/" + currentUID + "/customLocations/added/" + location + "/repeats/" + j).setValue(roleArrayList.get(j).getRole());
                    } else {
                        mRef.child("users/" + currentUID + "/customLocations/added/" + location + "/roles/" + j).setValue(roleArrayList.get(j).getRole());
                    }
                }
                Toast.makeText(this, "Location has been added!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "You need a repeatable role.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);
        //Firebase.setAndroidContext(this);

        /*
        MobileAds.initialize(this, "ca-app-pub-7054487445717644~4798964612");
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("145310EF75B6B1FE8013E630E72F45CB").build();
        //AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        if(adRequest.isTestDevice(add_location.this)){
            System.out.println("This is a test device. _-*-_-*-_-*-_-*-_-*-_-*-_-*-_-*-_-*-_");
        }
        */

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        currentUID = user.getUid();

        locationField = (EditText) findViewById(R.id.editText);
        locationLayout = (GridView) findViewById(R.id.locationList);
        roleLayout = (RecyclerView) findViewById(R.id.roleList);
        roleRecyclerAdapter = new RoleRecyclerAdapter(roleArrayList, this);
        roleLayout.setHasFixedSize(true);
        roleLayout.setLayoutManager(new LinearLayoutManager(this));
        roleLayout.setAdapter(roleRecyclerAdapter);

        roleArrayList.add(new NewRoleElement());
        //roleRecyclerAdapter.notifyItemInserted(roleArrayList.size() - 1);

        mRef = FirebaseDatabase.getInstance().getReference();
        mRef.child("defaultLocations").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                locationArrayList.clear();
                int i = 0;
                for (DataSnapshot temp : dataSnapshot.getChildren()) {
                    Location tempLocation = new Location(temp.getKey());
                    locationArrayList.add(tempLocation);
                    locationArrayList.get(i).setTextView((TextView) findViewById(R.id.locationTextView));
                    for (DataSnapshot tempRole : dataSnapshot.child(temp.getKey()).child("roles").getChildren()) {
                        locationArrayList.get(i).addRole(tempRole.getValue().toString());
                    }

                    for (DataSnapshot tempRole : dataSnapshot.child(temp.getKey()).child("repeats").getChildren()) {
                        locationArrayList.get(i).addRepeat(tempRole.getValue().toString());
                    }
                    i++;
                }
                locationArrayList.add(0, new Location("New location"));
                locationLayout.setAdapter(new Adapter(add_location.this));
                locationLayout.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                        Toast.makeText(add_location.this, locationArrayList.get(position).getItemLocation(), Toast.LENGTH_SHORT).show();
                        if (position == 0){
                            locationField.setText("");
                            roleArrayList.clear();
                            roleArrayList.add(new NewRoleElement("", false));
                            roleRecyclerAdapter.notifyDataSetChanged();
                        } else {
                            locationField.setText(locationArrayList.get(position).getText());
                            roleArrayList.clear();
                            ArrayList<String> tempRoles = locationArrayList.get(position).getRoleList();
                            ArrayList<String> tempRepeats = locationArrayList.get(position).getRepeatList();
                            //for every role in temproles in the database, a new role object will be created.
                            for (int i = 0; i < tempRoles.size(); i++) {
                                roleArrayList.add(new NewRoleElement(tempRoles.get(i), false));
                                //roleArrayList.get(i).setRoleInput((EditText) findViewById(R.id.roleText));
                                //roleArrayList.get(i).setRepeatCheckBox((CheckBox) findViewById(R.id.checkBox));
                                //roleArrayList.get(i).setText(tempRoles.get(i));
                                System.out.println(tempRoles.get(i));
                                roleRecyclerAdapter.notifyDataSetChanged();

                            }
                            for (int i = 0; i < tempRepeats.size(); i++) {
                                roleArrayList.add(new NewRoleElement(tempRepeats.get(i), true));
                                //roleArrayList.get(i).setRoleInput((EditText) findViewById(R.id.roleText));
                                //roleArrayList.get(i).setRepeatCheckBox((CheckBox) findViewById(R.id.checkBox));
                                //roleArrayList.get(i).updateViews();
                                System.out.println(tempRepeats.get(i));
                                roleRecyclerAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //roleLayout.setAdapter(new RoleAdapter(add_location.this));
    /*
        roleLayout.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                System.out.println(roleArrayList.get(position).getRole() + "********************");
                if (roleArrayList.get(position).getRole().equals("")){
                    roleArrayList.add(new NewRoleElement());
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        roleLayout.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println(roleArrayList.get(position).getRole() + "************************************************************************************");
                if(roleArrayList.get(position).getRole().equals("")) {
                    if (roleArrayList.size() == 1 ) {
                        roleArrayList.add(new NewRoleElement());
                    } else {
                        if (!roleArrayList.get(roleArrayList.size() - 1).getRole().equals("")) {
                            roleArrayList.add(new NewRoleElement());
                        }
                    }
                }
            }
        });
*/

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
            //System.out.println(position);
            locationArrayList.get(position).setTextView((TextView) convertView.findViewById(R.id.locationTextView));
            locationArrayList.get(position).updateText();
            locationArrayList.get(position).getButton().setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
            //} else {
            // locationArrayList.get(position).setAlpha();
            //}
            return convertView;
        }
    }

    /*
    public class RoleAdapter extends BaseAdapter{
        Context c;

        public RoleAdapter(Context context) {
            this.c = context;
        }

        @Override
        public int getCount() {
            return roleArrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return roleArrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return roleArrayList.indexOf(position);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.role_template, null);

            }
            if (roleArrayList.get(position).getRoleInput() == null) {
                roleArrayList.get(position).setRoleInput((EditText) convertView.findViewById(R.id.roleText));
            } else if (roleArrayList.get(position).getRepeatCheckBox() == null) {
                roleArrayList.get(position).setRepeatCheckBox((CheckBox) convertView.findViewById(R.id.checkBox));
            } else {
                roleArrayList.get(position).updateViews();
            }
            return convertView;
        }
    }
    */

    public class RoleRecyclerAdapter extends RecyclerView.Adapter<RoleRecyclerAdapter.RoleViewHolder>{

        private ArrayList<NewRoleElement> roleElements;
        private Context context;


         public RoleRecyclerAdapter(ArrayList<NewRoleElement> roleElements, Context context){
            this.roleElements = roleElements;
            this.context = context;
        }

        @Override
        public RoleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.role_template, parent, false);
            //LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            return new RoleViewHolder(v);
        }

        @Override
        public void onBindViewHolder(RoleViewHolder holder, int position) {
            //onBindViewHolder(holder, position);
            NewRoleElement roleElement = roleElements.get(position);
            holder.setText(roleElement.getRole());
            holder.checked(roleElement.checkBoxValue());
        }

        @Override
        public int getItemCount() {
            return roleArrayList.size();
        }

        public class RoleViewHolder extends RecyclerView.ViewHolder {
            View mView;
            private EditText roleInput;
            private CheckBox repeatCheckBox;

            public RoleViewHolder(View view){
                super(view);
                this.mView = view;
                roleInput = (EditText) view.findViewById(R.id.roleText);
                repeatCheckBox = (CheckBox) view.findViewById(R.id.checkBox);
            }

            public void setText(String role){
                roleInput.setText(role);
            }

            public void checked(Boolean clicked){
                repeatCheckBox.setChecked(clicked);
            }

        }
    }

}
