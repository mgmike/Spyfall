package mike.spyfall;

import android.widget.CheckBox;
import android.widget.EditText;

/**
 * Created by Michael on 9/10/2017.
 */

public class NewRoleElement {
    private EditText roleInput;
    private CheckBox repeatCheckBox;
    private String role; // not sure if im gonna need this

    public NewRoleElement(){
    }

    public void setText(String text){
        roleInput.setText(text);
    }

    public boolean checkBoxValue(){
        return repeatCheckBox.isChecked();
    }

    public String getRole(){
        return String.valueOf(roleInput.getText());
    }

    public EditText getRoleInput() {
        return roleInput;
    }

    public void setRoleInput(EditText roleInput) {
        this.roleInput = roleInput;
    }

    public CheckBox getRepeatCheckBox() {
        return repeatCheckBox;
    }

    public void setRepeatCheckBox(CheckBox repeatCheckBox) {
        this.repeatCheckBox = repeatCheckBox;
    }
}
