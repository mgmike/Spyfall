package mike.spyfall.ItemClasses;

/**
 * Created by Michael on 1/10/2017.
 */

    public class FriendClass {

        private String userName;
        private boolean requestAccepted;
        private boolean from;

        public FriendClass(){

        }

        public FriendClass(boolean from, boolean requestAccepted, String userName){
            this.requestAccepted = requestAccepted;
            this.from = from;
            this.userName = userName;
        }


        public void setFrom(boolean from) {
            this.from = from;
        }

        public void setUserName(String  userName) {
            this.userName = userName;
        }

        public void setRequestAccepted(boolean requestAccepted) {
            this.requestAccepted = requestAccepted;
        }


        public String getUserName(){
            return userName;
        }

        public boolean getRequestAccepted(){
            return requestAccepted;
        }

        public boolean getFrom(){
            return from;
        }
    }
