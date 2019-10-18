package engine.users;

import java.util.ArrayList;
import java.util.List;

public class SingleUserData {

    final private String userName;
    final private List<RepositoryData> repositoriesDataList = new ArrayList<>();

    public void AddRepositoryDataToRepositorysDataList(RepositoryData repositoryData) {
        this.repositoriesDataList.add(repositoryData);
    }

    public SingleUserData(String userName){
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public List<RepositoryData> getRepositoriesDataList() {
        return repositoriesDataList;
    }
}
