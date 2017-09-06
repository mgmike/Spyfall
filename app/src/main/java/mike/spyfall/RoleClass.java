package mike.spyfall;

/**
 * Created by Michael on 7/24/2017.
 */

public class RoleClass {
    public String repeats;
    public String roles;

    public RoleClass(){
    }

    public RoleClass(String repeats, String roles) {
        this.repeats = repeats;
        this.roles = roles;
    }

    public String getRepeats() {
        return repeats;
    }

    public void setRepeats(String repeats) {
        this.repeats = repeats;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }
}
