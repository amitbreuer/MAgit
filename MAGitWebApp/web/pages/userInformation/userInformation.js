//var USERS_DATA_URL = buildUrlWithContextPath("usersInformation");
var CURRENT_USER_DATA_URL = buildUrlWithContextPath("currentUserInformation");
var WATCH_URL = buildUrlWithContextPath("watchRepository");
var OTHER_USERS_DATA_URL = buildUrlWithContextPath("otherUsersInformation");
var NEW_REPOSITORY_URL = buildUrlWithContextPath("newRepository");
var FORK_URL = buildUrlWithContextPath("fork");
var CURRENT_USER_DATA;
var OTHER_USERS_DATA;

function updateCurrentUserButton() {
    var currentUserButton = document.getElementById("currentUser");
    currentUserButton.innerHTML = CURRENT_USER_DATA.userName;
    currentUserButton.onclick = function () {
        ajaxCurrentUserData(function (currentUserData) {
            ajaxCurrentUserDataCallback(currentUserData);
            showCurrentUserRepositories();
        });
    };
}

function createCurrentUserSingleRepositoryData(currentUserSingleRepositoryData, watchButtonID) {
    return "<div class=\"card repositoryItem\">\n" +
        "<div class=\"card-header\" role=\"tab\">\n" +
        "<h5 class=\"mb-0\"><a data-toggle=\"collapse\" aria-expanded=\"true\" aria-controls=\"accordion-2 .item-1\" href=\"#accordion-2 .item-1\">" + currentUserSingleRepositoryData.name + "</a><button id='" + watchButtonID + "' class=\"btn btn-primary btn-sm float-right\" type=\"button\">Watch</button></h5>\n" +
        "</div>\n" +
        "<div class=\"collapse show item-1 repositoryData\" role=\"tabpanel\" data-parent=\"#accordion-2\">\n" +
        "<div class=\"card-body\"><small class =\"d-md-flex justify-content-md-start\">Active branch: &nbsp" + currentUserSingleRepositoryData.activeBranchName + "</small>" +
        "<small class=\"d-md-flex d-lg-flex justify-content-md-start justify-content-lg-start\">Number of branches :" + currentUserSingleRepositoryData.numberOfBranches + "&nbsp;</small>" +
        "<code class=\"text-warning d-md-flex justify-content-md-start\">Last commit's time stamp: " + currentUserSingleRepositoryData.lastCommitDate + "</code>" +
        "<em class=\"d-md-flex d-lg-flex justify-content-md-start justify-content-lg-start\">Last commit's message: " + currentUserSingleRepositoryData.lastCommitMessage + " </em>" + "</div>\n" +
        "</div>\n" +
        "</div>";
}

function ajaxWatch(repositoryName) {
    $.ajax({
        url: WATCH_URL,
        dataType:"json",
        data:{
            repositoryName : repositoryName
        },
        success:function(newUrl){
            var fullUrl = buildUrlWithContextPath(newUrl);
            window.location.replace(fullUrl);
        }
    });

}

function watch(repositoryName) {
    ajaxWatch(repositoryName);
}

function addSingleRepositoryDataToCurrentUser(index, currentUserSingleRepositoryData) {
    var watchButtonID = "watch-" + currentUserSingleRepositoryData.name;
    var singleRepositoryDataButton = createCurrentUserSingleRepositoryData(currentUserSingleRepositoryData, watchButtonID);

    $("#accordion-2").append(singleRepositoryDataButton);
    document.getElementById(watchButtonID).onclick = function () {
        watch(currentUserSingleRepositoryData.name);
    }
}

function showCurrentUserRepositories() {
    $("#accordion-2").empty();

    $.each(CURRENT_USER_DATA.repositoriesDataList || [], addSingleRepositoryDataToCurrentUser);
}


function addNewRepositoryToCurrentUser(event) {
    var file = event.target.files[0];
    ajaxNewRepository(file, ShowMessage);

}

function ajaxNewRepository(file, callback) {
    var reader = new FileReader();

    reader.onload = function () {
        var content = reader.result;
        $.ajax(
            {
                url: NEW_REPOSITORY_URL,
                data: {
                    file: content
                },
                type: 'POST',
                success: repositoryAjaxSucceededCallback

            }
        );
    };
    reader.readAsText(file);

    function repositoryAjaxSucceededCallback(message) {
        ShowMessage(message);
        $(fileInput).val(null);
    }

}

function ShowMessage(message) {
    var modal = $("#newRepositoryMessageModal")[0];
    var span = document.getElementsByClassName("closeRepositoryMessage")[0];
    var content = document.getElementById("newRepositoryMessageContent");
    content.textContent = message;
    modal.style.display = "block";
    span.onclick = function () {
        modal.style.display = "none";
    };

    //alert(message);

}

function findOtherUserDataInList(otherUsername) {
    for (var i = 0; i < OTHER_USERS_DATA.length; i++) {
        if (OTHER_USERS_DATA[i].userName === otherUsername) {
            return OTHER_USERS_DATA[i];
        }
    }
}

function createOtherUserSingleRepositoryData(otherUserSingleRepositoryData, forkButtonID) {

    return "<div class=\"card otherUsersRepositoryItem\">\n" +
        "<div class=\"card-header\" role=\"tab\">\n" +
        "<h5 class=\"mb-0\"><a data-toggle=\"collapse\" aria-expanded=\"true\" aria-controls=\"accordion-2 .item-1\" href=\"#accordion-2 .item-1\">" + otherUserSingleRepositoryData.name + "</a><button id='" + forkButtonID + "' class=\"btn btn-primary btn-sm float-right\" type=\"button\">Fork</button></h5>\n" +
        "</div>\n" +
        "<div class=\"collapse show item-1 repositoryData\" role=\"tabpanel\" data-parent=\"#accordion-2\">\n" +
        "<div class=\"card-body\"><small class=\"d-md-flex justify-content-md-start\">Active branch: &nbsp" + otherUserSingleRepositoryData.activeBranchName + "</small>" +
        "<small class=\"d-md-flex d-lg-flex justify-content-md-start justify-content-lg-start\">Number of branches :" + otherUserSingleRepositoryData.numberOfBranches + "&nbsp;</small>" +
        "<code class=\"text-warning d-md-flex justify-content-md-start\">Last commit's time stamp: " + otherUserSingleRepositoryData.lastCommitDate + "</code>" +
        "<em class=\"d-md-flex d-lg-flex justify-content-md-start justify-content-lg-start\">Last commit's message: " + otherUserSingleRepositoryData.lastCommitMessage + " </em>" + "</div>\n" +
        "</div>\n" +
        "</div>";

}

function ajaxFork(otherUserName, otherUserRepositoryName, callback) {
    $.ajax({
        url: FORK_URL,
        data:{
            otherUserName : otherUserName,
            otherUserRepositoryName: otherUserRepositoryName
        },
        success: function (message) {
            callback(message);
        }
    });
}

function fork(otherUserName, otherUserRepositoryName) {

    ajaxFork(otherUserName, otherUserRepositoryName, ShowMessage);
}

function addSingleRepositoryDataToOtherUser(otherUserSingleRepositoryData, otherUserName) {
    var forkButtonID = "fork-" + otherUserName + '-' + otherUserSingleRepositoryData.name;

    var singleRepositoryDataButton = createOtherUserSingleRepositoryData(otherUserSingleRepositoryData, forkButtonID);
    $("#accordion-2").append(singleRepositoryDataButton);
    document.getElementById(forkButtonID).onclick = function (ev) {
        fork(otherUserName, otherUserSingleRepositoryData.name);
    }


}


/*function ajaxFork(otherUserName, otherUserRepositoryName, callback) {
    $.ajax({
        url: FORK_URL,
        dataType: "json",
        success: function (message) {
            callback(message);
        }
    });
}*/

function showOtherUserRepositories(otherUsername) {
    $("#accordion-2").empty();
    var otherUserData = findOtherUserDataInList(otherUsername);
    for (var i = 0; otherUserData.repositoriesDataList.length; i++) {
        addSingleRepositoryDataToOtherUser(otherUserData.repositoriesDataList[i], otherUsername);
    }
}

function addSingleOtherUserButton(index, otherUserData) {

    var otherUserButton = document.createElement('button');

    otherUserButton.textContent = otherUserData.userName;
    otherUserButton.setAttribute("class", "btn btn-link btn-sm border-white");
    otherUserButton.id = otherUserData.userName + 'sRepositoriesButton';
    otherUserButton.onclick = function () {
        showOtherUserRepositories(otherUserData.userName);
    };
    var otherUserli = document.createElement('li');
    otherUserli.prepend(otherUserButton);

    $("#otherUsersList").append(otherUserli);
}

function refreshOtherUsersList() {
    $("#otherUsersList").empty();
    $.each(OTHER_USERS_DATA || [], addSingleOtherUserButton);
}

function ajaxCurrentUserData(callback) {
    $.ajax({
        url: CURRENT_USER_DATA_URL,
        dataType: "json",
        success: function (currentUserData) {
            callback(currentUserData);
        }
    });
}

function ajaxOtherUsersData(callback) {
    $.ajax({
        url: OTHER_USERS_DATA_URL,
        dataType: "json",
        success: function (otherUsersData) {
            callback(otherUsersData);
        }
    });
}

function ajaxOtherUsersDataCallback(otherUsersData) {
    OTHER_USERS_DATA = otherUsersData;
    refreshOtherUsersList();

}

function initializeWindow() {
    ajaxCurrentUserData(function (currentUserData) {
        ajaxCurrentUserDataCallback(currentUserData);
        updateCurrentUserButton();
        showCurrentUserRepositories();
    });
    refreshOtherUsersDisplay();
}

$(function () {
    initializeWindow();
});


function ajaxCurrentUserDataCallback(currentUserData) {
    CURRENT_USER_DATA = currentUserData;
}

function refreshCurrentUserData() {
    ajaxCurrentUserData(function (currentUserData) {
        ajaxCurrentUserDataCallback(currentUserData)
    });
}

function refreshOtherUsersDisplay() {
    ajaxOtherUsersData(function (otherUsersData) {
        ajaxOtherUsersDataCallback(otherUsersData)
    });
}

$(function () {
    //setInterval(refreshOtherUsersDisplay, 2000);
});
