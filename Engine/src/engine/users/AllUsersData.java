package engine.users;

import engine.users.SingleUserData;

import java.util.ArrayList;
import java.util.List;

public class AllUsersData {
    SingleUserData currentUserData;
    List<SingleUserData> otherUsersDataList = new ArrayList<>();

    public List<SingleUserData> getOtherUsersData() {
        return otherUsersDataList;
    }

    public void setCurrentUserData(SingleUserData currentUserData) {
        this.currentUserData = currentUserData;
    }

    public SingleUserData getCurrentUserData() {
        return currentUserData;
    }
    public void AddOtherUserData(SingleUserData otherUserData){
        otherUsersDataList.add(otherUserData);
    }
}



