package mike.spyfall.Interfaces

import mike.spyfall.ItemClasses.CurrentGamePlayerListClass
import mike.spyfall.ItemClasses.FriendClass
import mike.spyfall.ItemClasses.Location
import java.util.*

interface ModelDataFlowInterface {
    fun updateUID(UID: String)
    fun updateUserName(userName: String)
    fun updateName(name: String)
    fun updateStartTime(startTime: Long)
    fun updateCurrentTime(currentTime: Long)
    fun updateGameTime(gameTime: Long)
    fun addFriend(newFriend: FriendClass)
    fun removeFriend(friend: FriendClass)
    fun addLocation(newLocation: Location)
    fun removeLocation(location: Location)
    fun addPlayer(newPlayer: CurrentGamePlayerListClass)
    fun removePlayer(player: CurrentGamePlayerListClass)

}