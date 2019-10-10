package engine.users;

import java.util.ArrayList;
import java.util.List;

public class UserData {

    final private String userName;
    final private List<RepositoryData> repositoriesDataList = new ArrayList<>();

    public void AddRepositoryDataToRepositorysDataList(RepositoryData repositoryData) {
        this.repositoriesDataList.add(repositoryData);
    }

    public UserData(String userName){
        this.userName = userName;
    }

}
