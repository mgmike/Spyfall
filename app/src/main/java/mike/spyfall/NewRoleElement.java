package mike.spyfall;

import android.widget.CheckBox;
import android.widget.EditText;

/**
 * Created by Michael on 9/10/2017.
 */

public class NewRoleElement {
    private String role;
    private boolean check = false;

    public NewRoleElement(){
    }

    public NewRoleElement(String role, boolean isRepeat){

        this.role = role;
        this.check = isRepeat;
    }

    public void setText(String text){
        role = text;
    }

    public void setCheckBoxValue(boolean c){
        check = c;
    }

    public boolean checkBoxValue(){
        return check;
    }

    public String getRole(){return role;
    }
}
