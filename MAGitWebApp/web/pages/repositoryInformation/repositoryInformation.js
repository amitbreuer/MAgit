var REPOSITORY_NAME_AND_RR_DATA_URL = buildUrlWithContextPath("repositoryNameAndRRData");
var HEAD_BRANCH_INFORMATION_URL = buildUrlWithContextPath("headBranchInformation");
var MAIN_FOLDER_OF_COMMIT_URL = buildUrlWithContextPath("mainFolderOfCommit");
var WC_STATUS_URL = buildUrlWithContextPath("wcStatus");
var OTHER_BRANCHES_INFORMATION_URL = buildUrlWithContextPath("otherBranchesInformation");
var DELETE_BRANCH_URL = buildUrlWithContextPath("deleteBranch");
var CHECKOUT_BRANCH_URL = buildUrlWithContextPath("checkout");
var CREATE_NEW_BRANCH_URL = buildUrlWithContextPath("createNewBranch");
var PUSH_URL = buildUrlWithContextPath("push");
var PULL_URL = buildUrlWithContextPath("pull");
var REPOSITORY_NAME;


function setRepositoryName(name) {
    REPOSITORY_NAME = name;
    $("#repositoryName-label")[0].textContent = name;
}

function ajaxRepositoryNameAndRRData() {
    $.ajax(
        {
            url: REPOSITORY_NAME_AND_RR_DATA_URL,

            success: ajaxRepositoryNameAndRRDataCallback
        }
    )
}

function setRRData(RRUser, RRName) {
    $("rrName-label").val = RRName;
    $("rrUser-label").val = RRUser;
}

function hideCollaborationButtons() {
    $(".collaboration-buttons").hide();
}

function ajaxRepositoryNameAndRRDataCallback(repositoryNameAndRRData) {
    setRepositoryName(repositoryNameAndRRData[0]);
    if (repositoryNameAndRRData[1]) {
        setRRData(repositoryNameAndRRData[1], repositoryNameAndRRData[2]);
    }else {
        hideCollaborationButtons();
    }
    ajaxWCFiles();

}

function addTextFileItem(folderComponent, containingFolderId, index) {
    var textFileItem = "<div class=\"card\">\n" +
        "<div role=\"tab\" class=\"card-header\">\n" +
        "<h5 class=\"mb-0\"><a data-toggle=\"collapse\" aria-expanded=\"false\" aria-controls=\"" + containingFolderId + " .item-" + index + "\" href=\"#" + containingFolderId + " .item-" + index + "\" style=\"font-size: 14px;\"><i class=\"fa fa-file-text-o\"></i> " + folderComponent.name + "</a></h5>\n" +
        "</div>\n" +
        "<div class=\"collapse item-" + index + "\" data-parent=\"#" + containingFolderId + "\" role=\"tabpanel\" >\n" +
        "<div class=\"card-body\">\n" +
        "<p class=\"card-text\">" + folderComponent.folderComponent.content + "</p>\n" +
        "</div>\n" +
        "</div>\n" +
        "</div>\n";

    //document.getElementById(containingFolderId).appendChild(textFileItem);
    $("#" + containingFolderId).append(textFileItem);
}

function addFolderItem(folderComponent, containingFolderId, index) {
    var components = folderComponent.folderComponent.components;
    var folderItem = "<div class=\"card\">\n" +
        "<div role=\"tab\" class=\"card-header\">\n" +
        "<h5 class=\"mb-0\"><a data-toggle=\"collapse\" aria-expanded=\"false\" aria-controls=\"" + containingFolderId + " .item-" + index + "\" href=\"#" + containingFolderId + " .item-" + index + "\" style=\"font-size: 14px;\"><i class=\"fas fa-folder\" style=\"color: rgb(241,232,15);\"></i> " + folderComponent.name + "</a></h5>\n" +
        "</div>\n" +
        "<div role=\"tabpanel\" data-parent=\"#" + containingFolderId + "\" class=\"collapse item-" + index + "\">\n" +
        "<div class=\"card-body\">" +
        "<div role=\"tablist\" id=\"" + containingFolderId + "-" + folderComponent.name + "-accordion\"></div>\n" +
        "</div>\n" +
        "</div>\n" +
        "</div>\n";

    //document.getElementById(containingFolderId).appendChild(folderItem);
    $("#" + containingFolderId).append(folderItem);

    for (var i = 0; i < components.length; i++) {

        if (components[i].folderComponent.content) { // blob
            addTextFileItem(components[i], containingFolderId + '-' + folderComponent.name + "-accordion", i + 1);
        } else {
            addFolderItem(components[i], containingFolderId + '-' + folderComponent.name + "-accordion", i + 1);
        }
    }
}

function addFileItemToWCDisplay(folderComponent, index) {

    if (folderComponent.folderComponent.content) { // blob
        addTextFileItem(folderComponent, "wc-accordion", index);
    } else { // folder
        addFolderItem(folderComponent, "wc-accordion", index);
    }
}

function showWCStatus(wcFolderData) {

    var folderComponents = wcFolderData.components;
    for (var i = 0; i < folderComponents.length; i++) {
        addFileItemToWCDisplay(folderComponents[i], i + 1);
    }

}

function ajaxWCFiles() {
    $.ajax({
        url: WC_STATUS_URL,
        dataType: "json",
        data: {
            currentWatchedRepository: REPOSITORY_NAME
        },
        success: showWCStatus
    })
}

function ajaxHeadBranchInformation(callback) {
    $.ajax(
        {
            url: HEAD_BRANCH_INFORMATION_URL,
            dataType: "json",

            success: callback
        }
    )
}

function createHeadBranchSingleCommitElement(headBranchSingleCommitData, index) {
    return "<div class=\"card\">" +
        "<div class=\"card-header\" role=\"tab\">" +
        "<h5 class=\"d-none d-lg-flex align-items-center align-items-lg-center mb-0\"><a id=\"" + headBranchSingleCommitData.sha1 + "\" data-toggle=\"collapse\" aria-expanded=\"false\" aria-controls=\"headbranch-commits-accordion .item-" + index + "\" href=\"#headbranch-commits-accordion .item-" + index + "\" style=\"margin: 7px;font-size: 15px;\">" + headBranchSingleCommitData.sha1 + "</a><em style=\"margin: 7px;font-size: 15px;\">" + headBranchSingleCommitData.message + "</em><strong class=\"float-right d-lg-flex align-items-lg-end\"" +
        " style=\"margin: 7px;font-size: 15px;\">" + headBranchSingleCommitData.creator + "</strong><code class=\"text-warning float-right\" style=\"font-size: 14px;margin: 7px;\">" + headBranchSingleCommitData.dateCreated + "</code></h5>" +
        "</div>" +
        "<div role=\"tabpanel\" data-parent=\"#headbranch-commits-accordion\" class=\"collapse item-" + index + "\">" +
        "<div class=\"card-body\" >" +
        "<div id=\"s-" + headBranchSingleCommitData.sha1 + "-main-folder\" role=\"tablist\"></div>" +
        "</div>" +
        "</div>" +
        "</div>"
}

function addFileItemToCommitFilesDesplay(folderComponent, containingFolderId, index) {
    if (folderComponent.folderComponent.content) { // blob
        addTextFileItem(folderComponent, containingFolderId, index);
    } else { // folder
        addFolderItem(folderComponent, containingFolderId, index);
    }
}

function addCommitMainFolderComponentsToCommitDisplay(commitSha1, commitMainFolder) {
    var commitMainFolderElementId = "s-" + commitSha1 + "-main-folder";

    for (var i = 0; i < commitMainFolder.components.length; i++) {
        addFileItemToCommitFilesDesplay(commitMainFolder.components[i], commitMainFolderElementId, i + 1);
    }
}

function ajaxMainFolderOfCommit(commitSha1) {
    $.ajax(
        {
            url: MAIN_FOLDER_OF_COMMIT_URL,
            data: {
                commitSha1: commitSha1
            },
            success: function (commitMainFolder) {
                addCommitMainFolderComponentsToCommitDisplay(commitSha1, commitMainFolder);
            }
        }
    )
}

function addSingleCommitToHeadBranchCommitsDisplay(headBranchSingleCommitData, index) {
    var commitSha1 = headBranchSingleCommitData.sha1;
    var singleCommitElement = createHeadBranchSingleCommitElement(headBranchSingleCommitData, index);
    $("#headbranch-commits-accordion").append(singleCommitElement);

    document.getElementById(commitSha1).onclick = function () {
        if (!document.getElementById("s-" + commitSha1 + "-main-folder").hasChildNodes()) {
            ajaxMainFolderOfCommit(commitSha1);
        }
    };
}

function updateHeadBranchCommitsDisplay(headBranchInformation) {
    for (var i = 1; i < headBranchInformation.length; i++) {
        addSingleCommitToHeadBranchCommitsDisplay(headBranchInformation[i], i + 1);
    }
}

function updateHeadBranchInformation(headBranchInformation) {
    $("#headBranch-label")[0].textContent = headBranchInformation[0];
    $("#headbranch-commits-accordion").empty();
    updateHeadBranchCommitsDisplay(headBranchInformation);
}

function ajaxHeadBranchInformationCallback(headBranchInformation) {
    updateHeadBranchInformation(headBranchInformation);
}

function ajaxOtherBranchesInformation() {
    /*
                 data will arrive in the next form:
                 {
                    json[0] = branch name
                    json[1] = branch commit's sha1
                    json[2] = another branch name
                    json[3] = another branch commit's sha1
                    .
                    .
                    .
                 */
    $.ajax(
        {
            url: OTHER_BRANCHES_INFORMATION_URL,
            dataType: "json",
            success: ajaxOtherBranchesInformationCallback
        }
    )
}

function createOtherBranchElement(singleBranchName, singleBranchCommitSha1) {
    return "<div class=\"text-center\"><a class=\"btn btn-primary btn-sm text-white\" data-toggle=\"collapse\" aria-expanded=\"false\" aria-controls=\"collapse-" + singleBranchName + "\" href=\"#collapse-" + singleBranchName + "\" role=\"button\" style=\"height: 28px;font-size: 15px;margin-bottom: 4px;\">" + singleBranchName + "</a>" +
        "<div class=\"collapse text-center\"" +
        "id=\"collapse-" + singleBranchName + "\"><em class=\"text-white d-flex\" style=\"font-size: 10px;\">" + singleBranchCommitSha1 + "<br /></em>" +
        "<div role=\"group\" class=\"btn-group\"><button id =\"delete-branch-" + singleBranchName + "\" class=\"btn btn-primary btn-sm\" type=\"button\" style=\"margin-right: 2px;font-size: 12px;height: 25px;\">Delete</button><button id =\"checkout-branch-" + singleBranchName + "\" class=\"btn btn-primary btn-sm\" type=\"button\" style=\"height: 25px;font-size: 12px;\">Checkout</button></div>" +
        "</div>" +
        "</div>" +
        "<hr style=\"margin: 5px;\" />"
}

function ajaxDeleteBranch(singleBranchName) {
    $.ajax({
        url: DELETE_BRANCH_URL,
        data: {
            branchToDeleteName: singleBranchName
        },
        success: ajaxDeleteBranchCallback
    })
}

function ajaxDeleteBranchCallback(message) {
    ShowMessage(message);
    ajaxOtherBranchesInformation();
}

function ShowMessage(message) {
    var modal = $("#MessageModal")[0];
    var span = document.getElementsByClassName("closeMessage")[0];
    var content = document.getElementById("MessageContent");
    content.textContent = message;
    modal.style.display = "block";
    span.onclick = function () {
        modal.style.display = "none";
    };
}


function ajaxCheckout(singleBranchName) {
    $.ajax({
        url: CHECKOUT_BRANCH_URL,
        dataType: "json",
        data: {
            repositoryName: REPOSITORY_NAME,
            branchToCheckoutName: singleBranchName
        },
        success: function (newUrl) {
            var fullUrl = buildUrlWithContextPath(newUrl);
            window.location.replace(fullUrl);
        }
    })
}

function addSingleOtherBranchItem(singleBranchName, singleBranchCommitSha1) {
    var branchElement = createOtherBranchElement(singleBranchName, singleBranchCommitSha1);
    $("#otherBranchesList").append(branchElement);
    document.getElementById("checkout-branch-" + singleBranchName).onclick = function (ev) {
        ajaxCheckout(singleBranchName);
    };
    document.getElementById("delete-branch-" + singleBranchName).onclick = function (ev) {
        ajaxDeleteBranch(singleBranchName);
    };
}

function ajaxOtherBranchesInformationCallback(otherBranchesInformation) {
    $("#otherBranchesList").empty();
    for (var i = 0; i < otherBranchesInformation.length; i += 2) {
        addSingleOtherBranchItem(otherBranchesInformation[i], otherBranchesInformation[i + 1]);
    }
}


function ajaxCreateNewBranchCallback(message) {
    if (message === "There is already branch with this name") {
        ShowMessage(message);
    } else {
        ajaxOtherBranchesInformation();
    }
    $("#branchNameModal")[0].style.display = "none";
}


function showNewBranchModal() {
    var branchNameModal = $("#branchNameModal")[0];
    branchNameModal.style.display = "block";
}

function initializeBranchNameModal() {
    var branchNameModal = $("#branchNameModal")[0];
    var span = $("#branchName-xbutton")[0];
    span.onclick = function () {
        $("#branchNameTextInput").val("");
        branchNameModal.style.display = "none";
    };

    $("#branchNameForm").submit(function (event) {
        event.preventDefault();
            $.ajax({
                url: CREATE_NEW_BRANCH_URL,
                data: $(this).serialize(),
                success: ajaxCreateNewBranchCallback
            });
        $("#branchNameTextInput").val("");

        return false;
        }
    );
}

function initializeModals() {
    initializeBranchNameModal();

}

function push() {
    $.ajax(
        {
            url: PUSH_URL,
            success: pushCallback
        }
    )
}

function pushCallback(message) {
ShowMessage(message);
}

function pull() {
    $.ajax(
        {
            url: PULL_URL,
            success: pullCallback
        }
    )
}
function pullCallback(message) {
    ShowMessage(message);
    if(message === "pull executed successfully"){
        ajaxHeadBranchInformation();
    }
}

function pullRequest(){

}

function BackToUserInformationPage(){
    var fullUrl = buildUrlWithContextPath("pages/userInformation/userInformation.html");
    window.location.replace(fullUrl);

}

$(function () {
    ajaxRepositoryNameAndRRData();
    ajaxHeadBranchInformation(function (headBranchInformation) {
        ajaxHeadBranchInformationCallback(headBranchInformation);
        ajaxOtherBranchesInformation();
    });
    initializeModals();
});