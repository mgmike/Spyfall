package mike.spyfall.Interfaces

interface PresenterInterface{
    fun logIn()
    fun logOut()
    fun userNameChanged()
    fun nameChanged()
    fun friendAdded()
    fun friendModified()
    fun friendRemoved()
    fun locationAdded()
    fun locationModified()
    fun locationRemoved()
    fun hostChanged()
    fun locationChanged()
    fun endTimeChanged()
    fun gameTimeChanged()
    fun playerAdded()
    fun playerModified()
    fun playerRemoved()
}