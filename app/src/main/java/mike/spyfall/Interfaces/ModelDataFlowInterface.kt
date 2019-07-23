package mike.spyfall.Interfaces

import mike.spyfall.ItemClasses.CurrentGamePlayerListClass
import mike.spyfall.ItemClasses.FriendClass
import mike.spyfall.ItemClasses.LocationClass
import java.util.*

interface ModelDataFlowInterface {
    fun signIn()
    fun signOut()
    fun updateUID(UID: String)
    fun updateUserName(userName: String)
    fun updateName(name: String)
    fun updateHost(name: String)
    fun updateEndTime(endTime: Long)
    fun updateCurrentTime(currentTime: Long)
    fun updateGameTime(gameTime: Long)
    fun updateLocation(location: String)
    fun addFriend(newFriend: FriendClass)
    fun removeFriend(friend: FriendClass)
    fun addLocation(newLocation: LocationClass)
    fun removeLocation(location: LocationClass)
    fun addPlayer(newPlayer: CurrentGamePlayerListClass)
    fun removePlayer(player: CurrentGamePlayerListClass)
    fun removeAllPlayers()

}