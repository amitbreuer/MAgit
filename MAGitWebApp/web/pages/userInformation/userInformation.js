var USERS_DATA_URL = buildUrlWithContextPath("usersInformation");
var ALL_USERS_DATA;

function updateCurrentUsername(userName) {
    $("#Hello").html("Hello," + userName);
}

function createCurrentUserSingleRepositoryData(currentUserSingleRepositoryData) {
    return "<div class=\"card repositoryItem\">\n" +
        "<div class=\"card-header\" role=\"tab\">\n" +
        "<h5 class=\"mb-0\"><a data-toggle=\"collapse\" aria-expanded=\"true\" aria-controls=\"accordion-2 .item-1\" href=\"#accordion-2 .item-1\">" + currentUserSingleRepositoryData.name + "</a><button class=\"btn btn-primary btn-sm float-right\" type=\"button\">Watch</button></h5>\n" +
        "</div>\n" +
        "<div class=\"collapse show item-1 repositoryData\" role=\"tabpanel\" data-parent=\"#accordion-2\">\n" +
        "<div class=\"card-body\"><small class =\"d-md-flex justify-content-md-start\">Active branch: &nbsp" + currentUserSingleRepositoryData.activeBranchName + "</small>" +
        "<small class=\"d-md-flex d-lg-flex justify-content-md-start justify-content-lg-start\">Number of branches :" + currentUserSingleRepositoryData.numberOfBranches + "&nbsp;</small>" +
        "<code class=\"text-warning d-md-flex justify-content-md-start\">Last commit's time stamp: " + currentUserSingleRepositoryData.lastCommitDate + "</code>" +
        "<em class=\"d-md-flex d-lg-flex justify-content-md-start justify-content-lg-start\">Last commit's message: " + currentUserSingleRepositoryData.lastCommitMessage + " </em>" + "</div>\n" +
        "</div>\n" +
        "</div>";
}

function addSingleRepositoryDataToCurrentUser(index, currentUserSingleRepositoryData) {
    var singleRepositoryDataButton = createCurrentUserSingleRepositoryData(currentUserSingleRepositoryData);
    $("#accordion-2").append(singleRepositoryDataButton);
}

function showCurrentUserRepositories() {
    $("#accordion-2").empty();
    $.each(ALL_USERS_DATA.currentUserData.repositoriesDataList || [], addSingleRepositoryDataToCurrentUser);
}

function findOtherUserDataInList(otherUsername) {

for(var i=0;i<ALL_USERS_DATA.otherUsersDataList.length;i++ ){

    if(ALL_USERS_DATA.otherUsersDataList[i].userName === otherUsername){
        console.log("success");
        return ALL_USERS_DATA.otherUsersDataList[i];

    }
}
 /*   $.each(ALL_USERS_DATA.otherUsersDataList || [], function (index, singleOtherUserData) {
        console.log(singleOtherUserData.userName);
        console.log(otherUsername);
        if (singleOtherUserData.userName === otherUsername) {
            console.log(singleOtherUserData.userName === otherUsername);
            console.log("success");
            return singleOtherUserData;

        }
    })
    ;*/
}

function createOtherUserSingleRepositoryData(otherUserSingleRepositoryData) {
    return "<div class=\"card otherUsersRepositoryItem\">\n" +
        "<div class=\"card-header\" role=\"tab\">\n" +
        "<h5 class=\"mb-0\"><a data-toggle=\"collapse\" aria-expanded=\"true\" aria-controls=\"accordion-2 .item-1\" href=\"#accordion-2 .item-1\">" + otherUserSingleRepositoryData.name + "</a><button class=\"btn btn-primary btn-sm float-right\" type=\"button\">Fork</button></h5>\n" +
        "</div>\n" +
        "<div class=\"collapse show item-1 repositoryData\" role=\"tabpanel\" data-parent=\"#accordion-2\">\n" +
        "<div class=\"card-body\"><small class=\"d-md-flex justify-content-md-start\">Active branch: &nbsp" + otherUserSingleRepositoryData.activeBranchName + "</small>" +
        "<small class=\"d-md-flex d-lg-flex justify-content-md-start justify-content-lg-start\">Number of branches :" + otherUserSingleRepositoryData.numberOfBranches + "&nbsp;</small>" +
        "<code class=\"text-warning d-md-flex justify-content-md-start\">Last commit's time stamp: " + otherUserSingleRepositoryData.lastCommitDate + "</code>" +
        "<em class=\"d-md-flex d-lg-flex justify-content-md-start justify-content-lg-start\">Last commit's message: " + otherUserSingleRepositoryData.lastCommitMessage + " </em>" + "</div>\n" +
        "</div>\n" +
        "</div>";


}

function addSingleRepositoryDataToOtherUser(index, otherUserSingleRepositoryData) {
    var singleRepositoryDataButton = createOtherUserSingleRepositoryData(otherUserSingleRepositoryData);
    $("#accordion-2").append(singleRepositoryDataButton);
}

function showOtherUserRepositories(otherUsername) {
    $("#accordion-2").empty();
    var otherUserData = findOtherUserDataInList(otherUsername);
    console.log(otherUserData);
    $.each(otherUserData.repositoriesDataList || [], addSingleRepositoryDataToOtherUser)


}


function addSingleOtherUserButton(index, otherUserData) {

    var otherUserButton = document.createElement('button');
    otherUserButton.textContent = otherUserData.userName;
    otherUserButton.setAttribute("class","btn btn-link btn-sm border-white");
    otherUserButton.id = otherUserData.userName + 'sRepositoriesButton';
    otherUserButton.onclick =function() {
        showOtherUserRepositories(otherUserData.userName);
    }
    var otherUserli = document.createElement('li');
    otherUserli.prepend(otherUserButton);

    $("#otherUsersList").append(otherUserli);

    // var otherUserName = "<li> <button id='" + otherUserData.userName + "sRepositoriesButton' class=\ type=\"button\">" + otherUserData.userName + "</button></li>";
}


function refreshOtherUsersList() {
    $("#otherUsersList").empty();
    $.each(ALL_USERS_DATA.otherUsersDataList || [], addSingleOtherUserButton);
}

function ajaxAllUsersData(callback) {
    $.ajax({
        url: USERS_DATA_URL,
        dataType: "json",
        success: function (allUsersData) {
            callback(allUsersData);
        }
    });
}

$(function () {
    ajaxAllUsersData(function (allUsersData) {
        ALL_USERS_DATA = allUsersData;

        showCurrentUserRepositories();
        refreshOtherUsersList();
    });
});