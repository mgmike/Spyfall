package mike.spyfall.ItemClasses

import java.util.ArrayList

class LocationClass{
    var location = ""
    val roleList = ArrayList<String>()
    val repeatList = ArrayList<String>()
    var clicked = false

    constructor()

    fun addRole(newRole: String){
        roleList.add(newRole)
    }

    fun addRepeat(newRepeat: String){
        repeatList.add(newRepeat)
    }

}