var CURRENT_USERNAME_URL = buildUrlWithContextPath("userInformation");


function updateCurrentUsername(userName) {
    $("#Hello").html("Hello," + userName);
    console.log("******************");
}

function createsingleRepositoryDataButton(singleRepositoryData) {
    var singleRepositoryDataButton=
        $("#currentUserRepositoriesArea").append(
            '<li><div class="dropdown" ' +
            '<button class="repositorydropbtn">'+singleRepositoryData.name+'</button>' +
            '<div class="dropdown-content">' +
            '<div class = "repositoryDataLine">'+"Active branch name: "+singleRepositoryData.activeBranchName+'</div>' +
            '<div class = "repositoryDataLine">'+"Number of branches: "+ singleRepositoryData.numberOfBranches+'</div>' +
            '<div class = "repositoryDataLine">'+"Last commit date: "+ singleRepositoryData.lastCommitDate+'</div>' +
            '<div class = "repositoryDataLine">'+"Last commit message: "+ singleRepositoryData.lastCommitMessage+'</div>' +
            '</div>' +
            '</div>' +
            '</li>');
    $("#currentUserRepositoriesArea").css()

    return singleRepositoryDataButton;
}

function addSingleRepositoryDataToCurrentUser(index, singleRepositoryData) {
    var singleRepositoryDataButton = createsingleRepositoryDataButton(singleRepositoryData);

    $("#currentUserRepositoriesArea").append(singleRepositoryDataButton);

}



function updateCurrentUserRepositories(repositoriesDataList) {
    $.each(repositoriesDataList || [], addSingleRepositoryDataToCurrentUser);
}

function updateCurrentUserData(userData) {
    updateCurrentUsername(userData.userName);
    updateCurrentUserRepositories(userData.repositoriesDataList);
}

function ajaxCurrentUser(callback) {
    $.ajax({
        url: CURRENT_USERNAME_URL,
        dataType: "json",
        success: function (userData) {
            callback(userData);
        }
    });
}


$(function () {
    ajaxCurrentUser(function (userData) {
        updateCurrentUserData(userData);
    });
});

$(function () {
$("#logoutButton").on('click',function (event) {
    this.redirect
})
});

