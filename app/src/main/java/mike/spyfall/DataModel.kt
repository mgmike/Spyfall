package mike.spyfall

import mike.spyfall.Interfaces.ModelDataFlowInterface
import mike.spyfall.ItemClasses.CurrentGamePlayerListClass
import mike.spyfall.ItemClasses.FriendClass
import mike.spyfall.ItemClasses.Location
import java.util.*

class DataModel:ModelDataFlowInterface {

    var UID = ""
    var userName = ""
    var name = ""
    var startTime: Long = -1
    var currentTime: Long = -1
    var gameTime:Long = -1
    val friends = ArrayList<FriendClass>()
    val locations = ArrayList<Location>()
    val players = ArrayList<CurrentGamePlayerListClass>()

    override fun updateUID(UID: String) {
        this.UID = UID
    }

    override fun updateUserName(userName: String) {
        this.userName = userName
    }

    override fun updateName(name: String) {
        this.name = name
    }

    override fun updateStartTime(startTime: Long) {
        this.startTime = startTime
    }

    override fun updateCurrentTime(currentTime: Long) {
        this.currentTime = currentTime
    }

    override fun updateGameTime(gameTime: Long) {
        this.gameTime = gameTime
    }

    override fun addFriend(newFriend: FriendClass) {
        friends.add(newFriend)
    }

    override fun removeFriend(friend: FriendClass) {
        if(friends.contains(friend))
            friends.remove(friend)
    }

    override fun addLocation(newLocation: Location) {
        locations.add(newLocation)
    }

    override fun removeLocation(location: Location) {
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


}