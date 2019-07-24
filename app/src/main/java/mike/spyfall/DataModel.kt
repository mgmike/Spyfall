package mike.spyfall

import android.renderscript.Sampler
import android.util.Log
import com.google.firebase.database.*
import mike.spyfall.Interfaces.ModelDataFlowInterface
import mike.spyfall.ItemClasses.CurrentGamePlayerListClass
import mike.spyfall.ItemClasses.FriendClass
import mike.spyfall.ItemClasses.LocationClass
import java.util.*

class DataModel:ModelDataFlowInterface {

    lateinit var mRef: DatabaseReference

    var userLoggedIn = false
    var UID = ""
    var userName = ""
    var name = ""
    var location = "none"
    var host = ""
    var endTime: Long = -1
    var currentTime: Long = -1
    var gameTime:Long = 480000
    val friends = ArrayList<FriendClass>()
    val locations = ArrayList<LocationClass>()
    val players = ArrayList<CurrentGamePlayerListClass>()


    override fun signIn() {
        userLoggedIn = true
        //presenter.signedIn()
    }

    override fun signOut() {
        userLoggedIn = false
        //presenter.signedOff()
    }

    override fun updateUID(UID: String) {
        this.UID = UID
    }

    override fun updateUserName(userName: String) {
        this.userName = userName
    }

    override fun updateName(name: String) {
        this.name = name
    }

    override fun updateHost(name: String) {
        this.host = host
    }

    override fun updateEndTime(endTime: Long) {
        this.endTime = endTime
    }

    override fun updateCurrentTime(currentTime: Long) {
        this.currentTime = currentTime
    }

    override fun updateGameTime(gameTime: Long) {
        this.gameTime = gameTime
    }

    override fun updateLocation(location: String) {
        this.location = location
    }

    override fun addFriend(newFriend: FriendClass) {
        friends.add(newFriend)
    }

    override fun removeFriend(friend: FriendClass) {
        if(friends.contains(friend))
            friends.remove(friend)
    }

    override fun addLocation(newLocation: LocationClass) {
        locations.add(newLocation)
    }

    override fun removeLocation(location: LocationClass) {
        if(locations.contains(location))
            locations.remove(location)
    }

    override fun addPlayer(newPlayer: CurrentGamePlayerListClass) {
        players.add(newPlayer)
    }

    override fun removePlayer(player: CurrentGamePlayerListClass){
        if(players.contains(player))
            players.remove(player)
    }

    override fun removeAllPlayers(){
        players.removeAll(players)
    }


    fun setUpInitialVlaues(){
        if(UID != ""){
            mRef = FirebaseDatabase.getInstance().getReference("users/" + UID)
            mRef.addListenerForSingleValueEvent(InitialValues(this))
            mRef = FirebaseDatabase.getInstance().getReference("defaultLocations")
            mRef.addListenerForSingleValueEvent(InitialLocations(this))
        }
    }

    class ListenerForUserInfo(dm: DataModel, variable: () -> Boolean, presenterMethod: String): ChildEventListener{
        private var dataModel: DataModel
        private var variable: () -> Boolean
        init {
            dataModel = dm
            this.variable = variable
        }
        override fun onCancelled(p0: DatabaseError) {

        }

        override fun onChildMoved(p0: DataSnapshot, p1: String?) {
        }

        override fun onChildChanged(p0: DataSnapshot, p1: String?) {
        }

        override fun onChildAdded(p0: DataSnapshot, p1: String?) {

        }

        override fun onChildRemoved(p0: DataSnapshot) {
        }

    }

    class InitialValues(dm: DataModel): ValueEventListener{
        private var dataModel: DataModel

        init {
            dataModel = dm
        }

        override fun onCancelled(p0: DatabaseError) {
        }

        override fun onDataChange(p0: DataSnapshot) {
            if(p0.hasChild("userName"))
                dataModel.updateUserName(p0.child("userName").value.toString())
            if(p0.hasChild("name"))
                dataModel.updateUserName(p0.child("name").value.toString())
            if(p0.hasChild("friends")){
                for(i in p0.child("friends").child("users").children){
                    var newFriend = FriendClass()
                    newFriend.userName = i.key
                    newFriend.from  = i.child("from").value.toString().toBoolean()
                    newFriend.from  = i.child("requestAccepted").value.toString().toBoolean()
                    dataModel.addFriend(newFriend)
                }
            }
            if(p0.hasChild("currentGame")){
                if(p0.child("currentGame").hasChild("endTime"))
                    dataModel.updateEndTime(p0.child("currentGame").child("endTime").value.toString().toLong())
                dataModel.updateHost(p0.child("currentGame").child("host").value.toString())
                dataModel.updateLocation(p0.child("currentGame").child("location").value.toString())
                if(p0.child("currentGame").hasChild("startTime"))
                    dataModel.updateGameTime(p0.child("currentGame").child("startTime").value.toString().toLong())
                for(i in p0.child("currentGame").child("users").children){
                    var newPlayer = CurrentGamePlayerListClass()
                    newPlayer.userName = i.key
                    newPlayer.role = i.child("role").value.toString()
                    dataModel.addPlayer(newPlayer)
                }
            }

            Log.d("InitialValues", dataModel.location)
            Log.d("InitialValues", dataModel.UID)
            for(i in dataModel.friends) {
                Log.d("InitialValues", i.userName)
            }
        }
    }

    class InitialLocations(dm: DataModel): ValueEventListener{
        private lateinit var dataModel: DataModel

        init {
            dataModel = dm
        }

        override fun onCancelled(p0: DatabaseError) {
            Log.e("InitialLocations", p0.message)
        }

        override fun onDataChange(p0: DataSnapshot) {
            var location = LocationClass()
            for(i in p0.children){
                location.location = i.key.toString()
                for(roles in i.child("roles").children){
                    location.roleList.add(roles.value.toString())
                }
                for(repeat in i.child("repeats").children){
                    location.repeatList.add(repeat.value.toString())
                }
            }
        }

    }
}