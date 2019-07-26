package mike.spyfall

import android.renderscript.Sampler
import android.util.Log
import android.widget.Toast
import com.google.firebase.database.*
import mike.spyfall.Interfaces.ModelDataFlowInterface
import mike.spyfall.Interfaces.PresenterInterface
import mike.spyfall.ItemClasses.CurrentGamePlayerListClass
import mike.spyfall.ItemClasses.FriendClass
import mike.spyfall.ItemClasses.LocationClass
import java.util.*

class DataModel(val UID: String, val presenter: PresenterInterface):ModelDataFlowInterface {

    lateinit var mRef: DatabaseReference

    var userLoggedIn = false

    /**
     * Will almost always be not empty
     */

    var userName = ""
    var name = ""
    val friends = ArrayList<FriendClass>()
    val locations = ArrayList<LocationClass>()

    /**
     * Can be empty when no games are being played
     */

    var host = ""
    var location = "none"
    var endTime: Long = -1
    var currentTime: Long = -1
    var gameTime:Long = 480000
    val players = ArrayList<CurrentGamePlayerListClass>()


    override fun signIn():Boolean {
        userLoggedIn = true
        //presenter.signedIn()
        return true
    }

    override fun signOut():Boolean {
        userLoggedIn = false
        //presenter.signedOff()
        return true
    }

    override fun updateUserName(userName: String):Boolean {
        if(this.userName != userName) {
            this.userName = userName
            return true
        }
        else
            return false
    }

    override fun updateName(name: String):Boolean {
        if(this.name != name) {
            this.name = name
            return true
        }
        else
            return false
    }

    override fun updateHost(name: String):Boolean {
        if(this.host != name) {
            this.host = name
            return true
        }
        else
            return false
    }

    override fun updateEndTime(endTime: Long):Boolean {
        if(this.endTime != endTime) {
            this.endTime = endTime
            return true
        }
        else
            return false
    }

    override fun updateCurrentTime(currentTime: Long):Boolean {
        if(this.currentTime != currentTime) {
            this.currentTime = currentTime
            return true
        }
        else
            return false
    }

    override fun updateGameTime(gameTime: Long):Boolean {
        if(this.gameTime != gameTime) {
            this.gameTime = gameTime
            return true
        }
        else
            return false
    }

    override fun updateLocation(location: String):Boolean {
        if(this.location != location) {
            this.location = location
            return true
        }
        else
            return false
    }

    override fun addFriend(newFriend: FriendClass):Boolean {
        return friends.add(newFriend)
    }

    override fun removeFriend(friend: FriendClass):Boolean {
        if(friends.contains(friend))
            return friends.remove(friend)
        else
            return false
    }

    override fun addLocation(newLocation: LocationClass):Boolean {
        return locations.add(newLocation)
    }

    override fun removeLocation(location: LocationClass):Boolean {
        if(locations.contains(location))
            return locations.remove(location)
        else
            return false
    }

    override fun addPlayer(newPlayer: CurrentGamePlayerListClass):Boolean {
        return players.add(newPlayer)
    }

    override fun removePlayer(player: CurrentGamePlayerListClass):Boolean {
        if(players.contains(player))
            return players.remove(player)
        else
            return false
    }

    override fun removeAllPlayers():Boolean {
        return players.removeAll(players)
    }


    fun setUpInitialValues(){
        if(UID != ""){
            mRef = FirebaseDatabase.getInstance().getReference("users/" + UID + "/userInfo")
            mRef.addChildEventListener(ListenerForUserInfo(this))
            mRef = FirebaseDatabase.getInstance().getReference("users/" + UID + "/currentGame")
            mRef.addChildEventListener(ListenerForCurrentGameInfo(this))
            mRef = FirebaseDatabase.getInstance().getReference("users/" + UID + "/friends/users")
            mRef.addChildEventListener(ListenerForFriends(this))
            mRef = FirebaseDatabase.getInstance().getReference("users/" + UID + "/currentGame/users")
            mRef.addChildEventListener(ListenerForPlayers(this))

            mRef = FirebaseDatabase.getInstance().getReference("users/" + UID)
            mRef.addListenerForSingleValueEvent(InitialValues(this))
            mRef = FirebaseDatabase.getInstance().getReference("users/" + UID + "/currentGame")
            mRef.addListenerForSingleValueEvent(InitialGameInfo(this))
            mRef = FirebaseDatabase.getInstance().getReference("defaultLocations")
            mRef.addListenerForSingleValueEvent(InitialLocations(this))
        }
    }


    class ListenerForCurrentGameInfo(dm: DataModel): ChildEventListener{
        private var dataModel: DataModel = dm

        override fun onCancelled(p0: DatabaseError) {
            Log.e("ListenerForCurrentGameInfo", p0.message + " - " + p0.details)
        }

        override fun onChildMoved(p0: DataSnapshot, p1: String?) {
        }

        override fun onChildChanged(p0: DataSnapshot, p1: String?) {
            when (p0.key){
                "endTime" -> dataModel.updateEndTime(p0.value.toString().toLong())
                "startTime" -> dataModel.updateGameTime(p0.value.toString().toLong())
                "location" -> dataModel.updateLocation(p0.value.toString())
                "host" -> dataModel.updateHost(p0.value.toString())
            }
            Log.d("CurrentGameInfo", p0.key.toString() + " modified")
        }

        override fun onChildAdded(p0: DataSnapshot, p1: String?) {
            when (p0.key){
                "endTime" -> dataModel.updateEndTime(p0.value.toString().toLong())
                "startTime" -> dataModel.updateGameTime(p0.value.toString().toLong())
                "location" -> dataModel.updateLocation(p0.value.toString())
                "host" -> dataModel.updateHost(p0.value.toString())
            }
            Log.d("CurrentGameInfo", p0.key.toString() + " added")
        }

        override fun onChildRemoved(p0: DataSnapshot) {
            when (p0.key){
                "endTime" -> dataModel.updateEndTime(-1)
                "startTime" -> dataModel.updateGameTime(-1)
                "location" -> dataModel.updateLocation("")
                "host" -> dataModel.updateHost("")
            }
            Log.d("CurrentGameInfo", p0.key.toString() + " removed")
        }
    }

    class ListenerForPlayers(dm: DataModel): ChildEventListener{
        private var dataModel: DataModel = dm

        override fun onCancelled(p0: DatabaseError) {
            Log.e("ListenerForPlayers", p0.message + " - " + p0.details)

        }

        override fun onChildMoved(p0: DataSnapshot, p1: String?) {
        }

        override fun onChildChanged(p0: DataSnapshot, p1: String?) {
            for(i in dataModel.players){
                if(p0.value.toString() == i.userName){
                    i.role = p0.child("role").value.toString()
                }
            }
        }

        override fun onChildAdded(p0: DataSnapshot, p1: String?) {
            var newPlayer: CurrentGamePlayerListClass = CurrentGamePlayerListClass(p0.key!!)
            newPlayer.role = ""
            dataModel.addPlayer(newPlayer)
            Log.d("PlayerListener", p0.key.toString() + " added")

        }

        override fun onChildRemoved(p0: DataSnapshot) {
            var un = p0.key
            for(i in dataModel.friends.filter( {n -> n.userName == p0.key})){
                dataModel.removeFriend(i)
            }
        }
    }

    class ListenerForFriends(dm: DataModel): ChildEventListener{
        private var dataModel: DataModel = dm

        override fun onCancelled(p0: DatabaseError) {
            Log.e("ListenerForFriends", p0.message + " - " + p0.details)
        }

        override fun onChildMoved(p0: DataSnapshot, p1: String?) {

        }

        override fun onChildChanged(p0: DataSnapshot, p1: String?) {
            for(i in dataModel.friends){
                if(p0.value.toString() == i.userName){
                    i.from = p0.child("from").value.toString().toBoolean()
                    i.requestAccepted = p0.child("requestAccpeted").value.toString().toBoolean()
                }
            }
        }

        override fun onChildAdded(p0: DataSnapshot, p1: String?) {
            var newFriend: FriendClass = FriendClass()
            newFriend.userName = p0.key
            newFriend.from = p0.child("from").value.toString().toBoolean()
            newFriend.requestAccepted = p0.child("requestAccepted").value.toString().toBoolean()
            dataModel.addFriend(newFriend)
        }

        override fun onChildRemoved(p0: DataSnapshot) {
            for(i in dataModel.friends.filter( {n -> n.userName == p0.key})){
                dataModel.removeFriend(i)
            }

        }

    }

    class ListenerForUserInfo(dm: DataModel): ChildEventListener{
        private var dataModel: DataModel = dm

        override fun onCancelled(p0: DatabaseError) {
            Log.e("ListenerForUserInfo", p0.message + " - " + p0.details)

        }

        override fun onChildMoved(p0: DataSnapshot, p1: String?) {
        }

        override fun onChildChanged(p0: DataSnapshot, p1: String?) {
            when(p0.key){
                "inGame" -> dataModel.updateHost(p0.value.toString())
                "name" -> dataModel.updateName(p0.value.toString())
                "userName" -> dataModel.updateUserName(p0.value.toString())
            }
            Log.d("UserInfoListener", p0.value.toString())
        }

        override fun onChildAdded(p0: DataSnapshot, p1: String?) {
            when(p0.key){
                "inGame" -> dataModel.updateHost(p0.value.toString())
            }
        }

        override fun onChildRemoved(p0: DataSnapshot) {

            when(p0.key){
                "inGame" -> dataModel.updateHost("")
            }
        }

    }

    class InitialValues(dm: DataModel): ValueEventListener{
        private var dataModel: DataModel = dm

        override fun onCancelled(p0: DatabaseError) {
            Log.e("InitialValues", p0.message + " - " + p0.details)

        }

        override fun onDataChange(p0: DataSnapshot) {
            if(p0.hasChild("userName"))
                dataModel.updateUserName(p0.child("userName").value.toString())
            if(p0.hasChild("name"))
                dataModel.updateName(p0.child("name").value.toString())
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
                    var newPlayer = CurrentGamePlayerListClass(i.key!!)
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

    class InitialGameInfo(dm: DataModel): ValueEventListener{
        private var dataModel: DataModel = dm

        override fun onCancelled(p0: DatabaseError) {
            Log.e("InitialGameInfo", p0.message + " - " + p0.details)
        }

        override fun onDataChange(p0: DataSnapshot) {
            if(p0.hasChild("endTime"))
                dataModel.updateEndTime(p0.child("endTime").value.toString().toLong())
            if(p0.hasChild("startTime"))
                dataModel.updateGameTime(p0.child("startTime").value.toString().toLong())
            if(p0.hasChild("location"))
                dataModel.updateLocation(p0.child("location").value.toString())
            if(p0.hasChild("host"))
                dataModel.updateHost(p0.child("host").value.toString())

            if(p0.hasChild("users")){
                for (i in p0.child("users").children){
                    var newPlayer = CurrentGamePlayerListClass(i.key!!)
                    if(i.hasChild("role"))
                        newPlayer.role = i.child("role").value.toString()
                }
            }
        }
    }

    class InitialLocations(dm: DataModel): ValueEventListener{
        private var dataModel: DataModel = dm

        override fun onCancelled(p0: DatabaseError) {
            Log.e("InitialLocations", p0.message + " - " + p0.details)
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