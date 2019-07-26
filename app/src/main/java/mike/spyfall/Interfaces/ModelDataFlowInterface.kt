package mike.spyfall.Interfaces

import mike.spyfall.ItemClasses.CurrentGamePlayerListClass
import mike.spyfall.ItemClasses.FriendClass
import mike.spyfall.ItemClasses.LocationClass
import java.util.*

interface ModelDataFlowInterface {
    fun signIn():Boolean
    fun signOut():Boolean
    fun updateUserName(userName: String):Boolean
    fun updateName(name: String):Boolean
    fun updateHost(name: String):Boolean
    fun updateEndTime(endTime: Long):Boolean
    fun updateCurrentTime(currentTime: Long):Boolean
    fun updateGameTime(gameTime: Long):Boolean
    fun updateLocation(location: String):Boolean
    fun addFriend(newFriend: FriendClass):Boolean
    fun removeFriend(friend: FriendClass):Boolean
    fun addLocation(newLocation: LocationClass):Boolean
    fun removeLocation(location: LocationClass):Boolean
    fun addPlayer(newPlayer: CurrentGamePlayerListClass):Boolean
    fun removePlayer(player: CurrentGamePlayerListClass):Boolean
    fun removeAllPlayers():Boolean

}