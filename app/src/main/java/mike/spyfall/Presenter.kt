package mike.spyfall

import android.view.MenuItem
import com.google.android.material.navigation.NavigationView
import mike.spyfall.Interfaces.PresenterInterface

class Presenter(val navigationView: NavigationView): PresenterInterface, NavigationView.OnNavigationItemSelectedListener{

    fun constructer (){}

    init {
        navigationView.setNavigationItemSelectedListener(this)
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

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item){
        }
        return true
    }


}