package mike.spyfall;

/**
 * Created by Michael on 2/25/2017.
 */

    public class CurrentGamePlayerListClass {
        private String userName;
        private String role;


        public CurrentGamePlayerListClass() {

        }

        public CurrentGamePlayerListClass(String userName, String role) {
            this.userName = userName;
            this.role = role;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }
        public void setRole(String role){
            this.role = role;
        }

        public String getUserName() {
            return userName;
        }
        public String getRole(){return role;}
    }
