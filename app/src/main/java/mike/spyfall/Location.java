package mike.spyfall;

import android.widget.TextView;

/**
 * Created by Michael on 7/23/2017.
 */

public class Location {
    private TextView textView;
    private String itemLocation;
    private boolean buttonClicked = false;

    public Location (String itemLocation){
        this.itemLocation = itemLocation;

    }

    public String getText() {
        return textView.getText().toString();
    }

    public void setText(String text) {
        textView.setText(text);
    }

    public TextView getButton() {
        return textView;
    }

    public void setTextView(TextView button) {
        this.textView = button;
    }

    public String getItemLocation() {
        return itemLocation;
    }

    public void updateAlpha(){
        if(!buttonClicked){
            textView.setAlpha((float) 0.6);
            buttonClicked = true;
        } else {
            textView.setAlpha((float) 1);
            buttonClicked = false;
        }
    }

    public boolean getAlpha(){
        return buttonClicked;
    }

    public void reset(){
        if (textView != null) {
            buttonClicked = false;
            textView.setAlpha((float) 1);
        }
    }

    public void setAlpha(){
        if (!buttonClicked){
            textView.setAlpha((float) 1);
        } else {
            textView.setAlpha((float) 0.6);
        }
    }

    public boolean checkTextView(){
        if (textView == null){
            return false;
        }else{
            return true;
        }
    }

    public void updateText(){
        //if(button.getText() == null){
        textView.setText(itemLocation);
        //}
    }
}
