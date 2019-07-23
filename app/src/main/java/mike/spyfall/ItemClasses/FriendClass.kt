package mike.spyfall.ItemClasses

/**
 * Created by Michael on 1/10/2017.
 */

class FriendClass {

    var userName: String? = null
    var requestAccepted: Boolean = false
    var from: Boolean = false

    constructor() {

    }

    constructor(from: Boolean, requestAccepted: Boolean, userName: String) {
        this.requestAccepted = requestAccepted
        this.from = from
        this.userName = userName
    }
}
