package mike.spyfall;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Michael on 1/18/2017.
 */

public class AddFriend extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private LinearLayout userNameLinearLayout;
    private FirebaseDatabase mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_friend2);

        mRecyclerView = (RecyclerView) findViewById(R.id.friendsList);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        userNameLinearLayout = (LinearLayout) findViewById(R.id.userNameLinearLayout);
        userNameLinearLayout.setVisibility(View.GONE);


    }

}
