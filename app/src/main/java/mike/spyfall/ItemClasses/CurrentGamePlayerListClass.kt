package mike.spyfall.ItemClasses

/**
 * Created by Michael on 2/25/2017.
 */

class CurrentGamePlayerListClass {
    var userName: String? = null
    var role: String? = null


    constructor() {

    }

    constructor(userName: String, role: String) {
        this.userName = userName
        this.role = role
    }
}
