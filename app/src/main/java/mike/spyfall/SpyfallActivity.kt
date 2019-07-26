package mike.spyfall

import android.os.Bundle
import android.os.PersistableBundle
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import mike.spyfall.Fragments.LoginFragment
import mike.spyfall.Interfaces.PresenterInterface
import mike.spyfall.Interfaces.PresenterUIInterface

class SpyfallActivity:FragmentActivity(), PresenterInterface{

    lateinit var presenter: Presenter
    lateinit var dataModel: DataModel
    lateinit var mViewPager: ViewPager
    lateinit var mAdapter: StatePagerAdapter
    val fragmentArray = ArrayList<Fragment>()

    companion object{
        val SIGN_IN_FRAGMENT = 0
        val REGISTER_FRAGMENT = 1
        val PROFILE_FRAGMENT = 2
        val GAME_FRAGMENT = 3
        val FRIEND_FRAGMENT = 4
        val CREATE_NEW_LOCATION_FRAGMENT = 5
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

        setUpAuxillaryCasses(savedInstanceState)
        setUpFragments(savedInstanceState)
    }

    fun setUpAuxillaryCasses(savedInstanceState: Bundle?){
        dataModel = DataModel(FirebaseAuth.getInstance().currentUser.toString(), this)
    }

    fun setUpFragments(savedInstanceState: Bundle?){

        fragmentArray.add(LoginFragment(this as PresenterUIInterface))

        var UID = FirebaseAuth.getInstance().currentUser
        if(UID == null){
            changeFragment(savedInstanceState)
        }

    }

    fun changeFragment(savedInstanceState: Bundle?){
        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById<RelativeLayout>(R.id.fragment_signin) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return
            }

            // Create a new Fragment to be placed in the activity layout
            val firstFragment = LoginFragment(this as PresenterUIInterface)

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            firstFragment.arguments = intent.extras

            // Add the fragment to the 'fragment_container' FrameLayout
            supportFragmentManager.beginTransaction()
                    .add(R.id.fragment_signin, firstFragment).commit()
        }

        // Create fragment and give it an argument specifying the article it should show
        val newFragment = ArticleFragment()
        Bundle args = Bundle()
        args.putInt(ArticleFragment.ARG_POSITION, position)
        newFragment.arguments = args

        val transaction = supportFragmentManager.beginTransaction().apply {
            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back
            replace(R.id.fragment_container, newFragment)
            addToBackStack(null)
        }

// Commit the transaction
        transaction.commit();

    }


    override fun logIn() {
    }

    override fun logOut() {
    }

    override fun userNameChanged() {
    }

    override fun nameChanged() {
    }

    override fun friendAdded() {
    }

    override fun friendModified() {
    }

    override fun friendRemoved() {
    }

    override fun locationAdded() {
    }

    override fun locationModified() {
    }

    override fun locationRemoved() {
    }

    override fun hostChanged() {
    }

    override fun locationChanged() {
    }

    override fun endTimeChanged() {
    }

    override fun gameTimeChanged() {
    }

    override fun playerAdded() {
    }

    override fun playerModified() {
    }

    override fun playerRemoved() {
    }

}