var REPOSITORY_NAME_AND_RR_DATA_URL = buildUrlWithContextPath("repositoryNameAndRRData");
var HEAD_BRANCH_INFORMATION_URL = buildUrlWithContextPath("headBranchInformation");
var MAIN_FOLDER_OF_COMMIT_URL = buildUrlWithContextPath("mainFolderOfCommit");
var WC_Files_URL = buildUrlWithContextPath("wcFiles");
var OTHER_BRANCHES_INFORMATION_URL = buildUrlWithContextPath("otherBranchesInformation");
var DELETE_BRANCH_URL = buildUrlWithContextPath("deleteBranch");
var CHECKOUT_BRANCH_URL = buildUrlWithContextPath("checkout");
var CREATE_NEW_BRANCH_URL = buildUrlWithContextPath("createNewBranch");
var PUSH_NONRTB_TO_RR_URL = buildUrlWithContextPath("pushNonRTBToRR");
var PULL_URL = buildUrlWithContextPath("pull");
var REPOSITORY_NAME;
var EDIT_FILE_URL = buildUrlWithContextPath("editFile");
var DELETE_FILE_URL = buildUrlWithContextPath("deleteFile");
var ADD_FILE_URL = buildUrlWithContextPath("addFile");
var OPEN_CHANGES = buildUrlWithContextPath("openChanges");
var COMMIT_URL = buildUrlWithContextPath("commit");
var MESSAGES_URL = buildUrlWithContextPath("messages");
var POSSIBLE_BRANCHES_FOR_PULL_REQUEST_URL = buildUrlWithContextPath("branchesForPr");
var NEW_PULL_REQUEST_URL = buildUrlWithContextPath("newPR");
var REPOSITORY_PRS = buildUrlWithContextPath("repositoryPRs");

var messagesVersion = 0;

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
    } else {
        hideCollaborationButtons();
    }
    ajaxWCFiles();
    ajaxOpenChanges();
    refreshPRs();

}

function emptyOpenChangesLists() {
    $("#addedFiles-list").empty();
    $("#updatedFiles-list").empty();
    $("#deletedFiles-list").empty();
}

function showOpenChanges(delta) {
    var addedFiles = delta.addedFiles;
    var updatedFiles = delta.updatedFiles;
    var deletedFiles = delta.deletedFiles;

    emptyOpenChangesLists();

    for (var i = 0; i < addedFiles.length; i++) {
        addFileToOpenChanges(addedFiles[i].fullNameFromMainFolder, "addedFiles-list");
    }
    for (var j = 0; j < updatedFiles.length; j++) {
        addFileToOpenChanges(updatedFiles[j].fullNameFromMainFolder, "updatedFiles-list");
    }
    for (var k = 0; k < deletedFiles.length; k++) {
        addFileToOpenChanges(deletedFiles[k].fullNameFromMainFolder, "deletedFiles-list");
    }
}

function ajaxOpenChanges() {
    $.ajax({
        url: OPEN_CHANGES,
        data: {
            currentWatchedRepository: REPOSITORY_NAME
        },
        success: showOpenChanges
    })
}

function ajaxSaveNewContent(fileItemContentId, saveButtonId, fileID, content) {
    $.ajax({
        url: EDIT_FILE_URL,
        data: {
            currentWatchedRepository: REPOSITORY_NAME,
            fileFullName: fileID,
            fileNewContent: content
        },
        success: function (message) {
            document.getElementById(fileItemContentId).setAttribute("contentEditable", "false");
            document.getElementById(saveButtonId).setAttribute("class", "btn btn-secondary btn-sm d-none float-right");
            ShowMessage(message);
            ajaxOpenChanges();
        }
    })
}

function editTextFile(fileItemContentId, saveButtonId) {
    document.getElementById(fileItemContentId).setAttribute("contentEditable", "true");
    document.getElementById(saveButtonId).setAttribute("class", "btn btn-secondary btn-sm float-right");
}

function ajaxDeleteFile(fileID) {
    $.ajax({
        url: DELETE_FILE_URL,
        data: {
            currentWatchedRepository: REPOSITORY_NAME,
            fileFullName: fileID
        },
        success: function (message) {
            document.getElementById(fileID + "Item").remove();
            ShowMessage(message);
            ajaxOpenChanges();
        }
    })
}

function addEditableTextFileItem(folderComponent, containingFolderId, index) {

    var fileName = containingFolderId + "-" + folderComponent.name;
    var contentId = fileName + "-content";
    var saveButtonId = fileName + "-save";

    var textFileItem = "<div id=\"" + fileName + "Item\" class=\"card\">" +
        "<div role=\"tab\" class=\"card-header\">" +
        "<h5 class=\"mb-0\"><a data-toggle=\"collapse\" aria-expanded=\"false\" aria-controls=\"" + containingFolderId + " .item-" + index + "\" href=\"#" + containingFolderId + " .item-" + index + "\" style=\"font-size: 14px;\"><i class=\"fa fa-file-text-o\"></i> " + folderComponent.name + "</a>" +
        "<button class=\"btn btn-secondary btn-sm float-right\" type=\"button\" onclick='ajaxDeleteFile(\"" + fileName + "\")'><i class=\"fas fa-trash-alt\"></i> Delete</button>" +
        "<button class=\"btn btn-secondary btn-sm float-right\" type=\"button\" onclick='editTextFile(\"" + contentId + "\",\"" + saveButtonId + "\")' style=\"margin-right: 3px;\"><i class=\"fa fa-pencil\"></i> Edit</button>" +
        "<button id=\"" + saveButtonId + "\" class=\"btn btn-secondary btn-sm d-none float-right\" type=\"button\" onclick='ajaxSaveNewContent(\"" + contentId + "\",\"" + saveButtonId + "\",\"" + containingFolderId + "-" + folderComponent.name + "\",document.getElementById(\"" + contentId + "\").textContent)' style=\"margin-right: 2px;\"><i class=\"fa fa-save\"></i> Save</button>" +
        "</h5>" +
        "</div>" +
        "<div class=\"collapse item-" + index + "\" data-parent=\"#" + containingFolderId + "\" role=\"tabpanel\" >" +
        "<div class=\"card-body\">" +
        "<p id=\"" + contentId + "\" class=\"card-text\">" + folderComponent.folderComponent.content + "</p>" +
        "</div>" +
        "</div>" +
        "</div>";

    $("#" + containingFolderId).append(textFileItem);
}

function addEditableFolderItem(folderComponent, containingFolderId, index, containingFolderName) {
    var fixedFolderName = containingFolderName;
    if (containingFolderName !== "") {
        fixedFolderName += "/";
    }
    fixedFolderName += folderComponent.name;

    var components = folderComponent.folderComponent.components;
    var folderItem = "<div id=\"" + containingFolderId + "-" + folderComponent.name + "Item\" class=\"card\">" +
        "<div role=\"tab\" class=\"card-header\">" +
        "<h5 class=\"mb-0\"><a data-toggle=\"collapse\" aria-expanded=\"false\" aria-controls=\"" + containingFolderId + " .item-" + index + "\" href=\"#" + containingFolderId + " .item-" + index + "\" style=\"font-size: 14px;\"><i class=\"fas fa-folder\" style=\"color: rgb(241,232,15);\"></i> " + folderComponent.name + "</a>" +
        "<button class=\"btn btn-secondary btn-sm float-right\" type=\"button\" onclick='ajaxDeleteFile(\"" + containingFolderId + "-" + folderComponent.name + "\")'><i class=\"fas fa-trash-alt\"></i> Delete</button>" +
        "<button class=\"btn btn-secondary btn-sm float-right\" type=\"button\" onclick='showCreateFileModal(\"" + containingFolderId + "-" + folderComponent.name + "\",\"" + fixedFolderName + "\")' style=\"margin-right: 3px;\"><i class=\"icon ion-plus-round\"></i> Add File</button>" +
        "</h5>" +
        "</div>" +
        "<div role=\"tabpanel\" data-parent=\"#" + containingFolderId + "\" class=\"collapse item-" + index + "\">" +
        "<div class=\"card-body\">" +
        "<div role=\"tablist\" id=\"" + containingFolderId + "-" + folderComponent.name + "\"></div>" +
        "</div>" +
        "</div>" +
        "</div>";

    $("#" + containingFolderId).append(folderItem);

    for (var i = 0; i < components.length; i++) {

        if (components[i].folderComponent.content) { // blob
            addEditableTextFileItem(components[i], containingFolderId + '-' + folderComponent.name, i + 1);
        } else {
            addEditableFolderItem(components[i], containingFolderId + '-' + folderComponent.name, i + 1, fixedFolderName);
        }
    }
}

function addTextFileItem(folderComponent, containingFolderId, index) {
    var textFileItem = "<div class=\"card\">" +
        "<div role=\"tab\" class=\"card-header\">" +
        "<h5 class=\"mb-0\"><a data-toggle=\"collapse\" aria-expanded=\"false\" aria-controls=\"" + containingFolderId + " .item-" + index + "\" href=\"#" + containingFolderId + " .item-" + index + "\" style=\"font-size: 14px;\"><i class=\"fa fa-file-text-o\"></i> " + folderComponent.name + "</a></h5>" +
        "</div>" +
        "<div class=\"collapse item-" + index + "\" data-parent=\"#" + containingFolderId + "\" role=\"tabpanel\" >" +
        "<div class=\"card-body\">" +
        "<p class=\"card-text\">" + folderComponent.folderComponent.content + "</p>" +
        "</div>" +
        "</div>" +
        "</div>";

    $("#" + containingFolderId).append(textFileItem);
}

function addFolderItem(folderComponent, containingFolderId, index) {
    var components = folderComponent.folderComponent.components;
    var folderItem = "<div class=\"card\">" +
        "<div role=\"tab\" class=\"card-header\">" +
        "<h5 class=\"mb-0\"><a data-toggle=\"collapse\" aria-expanded=\"false\" aria-controls=\"" + containingFolderId + " .item-" + index + "\" href=\"#" + containingFolderId + " .item-" + index + "\" style=\"font-size: 14px;\"><i class=\"fas fa-folder\" style=\"color: rgb(241,232,15);\"></i> " + folderComponent.name + "</a></h5>" +
        "</div>" +
        "<div role=\"tabpanel\" data-parent=\"#" + containingFolderId + "\" class=\"collapse item-" + index + "\">" +
        "<div class=\"card-body\">" +
        "<div role=\"tablist\" id=\"" + containingFolderId + "-" + folderComponent.name + "\"></div>" +
        "</div>" +
        "</div>" +
        "</div>";

    $("#" + containingFolderId).append(folderItem);

    for (var i = 0; i < components.length; i++) {

        if (components[i].folderComponent.content) { // blob
            addTextFileItem(components[i], containingFolderId + '-' + folderComponent.name, i + 1);
        } else {
            addFolderItem(components[i], containingFolderId + '-' + folderComponent.name, i + 1);
        }
    }
}

function addFileItemToWCDisplay(folderComponent, index) {

    if (folderComponent.folderComponent.content) { // blob
        addEditableTextFileItem(folderComponent, "wc-accordion", index);
    } else { // folder
        addEditableFolderItem(folderComponent, "wc-accordion", index, "");
    }
}

function showCreateFileModal(containingItemId, containingFolder) {
    var modal = $("#createFileModal")[0];

    var pathLabel = document.getElementsByName("createFile-path")[0];

    pathLabel.value = REPOSITORY_NAME;
    if (containingFolder !== "") {
        pathLabel.value += "/" + containingFolder;
    }

    modal.style.display = "block";
}

function addFileToOpenChanges(fileFullName, changesList) {
    var newItem;
    if (fileFullName.match(".txt")) {
        newItem = "<li class=\"list-group-item\"><i class=\"fa fa-file-text-o\"></i><button class=\"btn btn-link btn-sm border-white\" type=\"button\"> " + fileFullName + "</button></li>";
    } else {
        newItem = "<li class=\"list-group-item\"><i class=\"fa fa-folder\"></i><button class=\"btn btn-link btn-sm border-white\" type=\"button\"> " + fileFullName + "</button></li>";

    }
    $("#" + changesList).append(newItem);
}

function createFileCallBack() {
    ajaxOpenChanges();
    ajaxWCFiles();
}

function showWCFiles(wcFolderData) {
    $("#wc-accordion").empty();
    var folderComponents = wcFolderData.components;
    for (var i = 0; i < folderComponents.length; i++) {
        addFileItemToWCDisplay(folderComponents[i], i + 1);
    }
}

function ajaxWCFiles() {
    $.ajax({
        url: WC_Files_URL,
        data: {
            currentWatchedRepository: REPOSITORY_NAME
        },
        success: showWCFiles
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

function showCommitMessageModal() {
    var modal = $("#commitMessageModal")[0];
    modal.style.display = "block";
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
    for (var i = 2; i < headBranchInformation.length; i++) {
        addSingleCommitToHeadBranchCommitsDisplay(headBranchInformation[i], i + 1);
    }
}


function disablePushNonRTBToRRButton() {
    document.getElementById("pushNonRTBTORRButton").disabled = true;
}

function enablePushNonRTBToRRButton() {
    document.getElementById("pushNonRTBTORRButton").disabled = false;
}

function updateHeadBranchInformation(headBranchInformation) {
    $("#headBranch-label")[0].textContent = headBranchInformation[0];
    if (headBranchInformation[1] === true) {
        disablePushNonRTBToRRButton();
    } else {
        enablePushNonRTBToRRButton();
    }

    $("#headbranch-commits-accordion").empty();
    updateHeadBranchCommitsDisplay(headBranchInformation);
}

function ajaxHeadBranchInformationCallback(headBranchInformation) {
    updateHeadBranchInformation(headBranchInformation);
}

function initializeAddFileModal() {
    var xButton = document.getElementById("createFile-xButton");
    var fileNameTextField = document.getElementsByName("createFile-name")[0];
    var contentTextArea = document.getElementsByName("createFile-content")[0];
    xButton.onclick = function () {
        var modal = $("#createFileModal")[0];
        fileNameTextField.value = "";
        contentTextArea.value = "";
        modal.style.display = "none";
    };
    $("#createFileForm").submit(function () {
        $.ajax({
            url: ADD_FILE_URL,
            data: $(this).serialize(),
            success: function (data) {
                ShowMessage(data[0]);
                createFileCallBack();
            }
        });
        var modal = $("#createFileModal")[0];
        var fileNameTextField = document.getElementsByName("createFile-name")[0];
        var contentTextArea = document.getElementsByName("createFile-content")[0];
        modal.style.display = "none";
        fileNameTextField.value = "";
        contentTextArea.value = "";
        return false;
    });
}

function commitCallBack() {
    //ajaxOpenChanges();
    location.reload();
}

function initializeCommitMessageModal() {
    var modal = $("#commitMessageModal")[0];
    var xButton = document.getElementById("commitMessage-xButton");
    var commitsMessage = document.getElementsByName("commitMessage")[0];
    xButton.onclick = function () {
        commitsMessage.value = "";
        modal.style.display = "none";
    };
    $("#commitMessageForm").submit(function () {
        $.ajax({
            url: COMMIT_URL,
            data: $(this).serialize(),
            success: function (message) {
                ShowMessage(message);
                commitCallBack();
            }
        });
        var commitsMessage = document.getElementsByName("commitMessage")[0];
        var modal = $("#commitMessageModal")[0];
        modal.style.display = "none";
        commitsMessage.value = "";
        return false;
    });
}

function ajaxOtherBranchesInformation() {
    $.ajax(
        {
            url: OTHER_BRANCHES_INFORMATION_URL,
            dataType: "json",
            success: ajaxOtherBranchesInformationCallback
        }
    )
}

function createOtherBranchElement(singleBranchName, singleBranchCommitSha1, branchIndex) {
    return "<div class=\"text-center\"><a class=\"btn btn-primary btn-sm text-white\" data-toggle=\"collapse\" aria-expanded=\"false\" aria-controls=\"branchesCollapse-" + branchIndex + "\" href=\"#branchesCollapse-" + branchIndex + "\" role=\"button\" style=\"height: 28px;font-size: 15px;margin-bottom: 4px;\">" + singleBranchName + "</a>" +
        "<div class=\"collapse text-center\" id=\"branchesCollapse-" + branchIndex + "\"><em class=\"text-white d-flex\" style=\"font-size: 10px;\">" + singleBranchCommitSha1 + "<br /></em>" +
        "<div role=\"group\" class=\"btn-group\"><button id =\"delete-branch-" + singleBranchName + "\" class=\"btn btn-primary btn-sm\" type=\"button\" style=\"margin-right: 2px;font-size: 12px;height: 25px;\">Delete</button><button name=\"" + singleBranchName + "\" id =\"checkout-branch-" + singleBranchName + "\" class=\"btn btn-primary btn-sm\" type=\"button\" style=\"height: 25px;font-size: 12px;\">Checkout</button></div>" +
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

function addSingleOtherBranchItem(singleBranchName, singleBranchCommitSha1, singleBranchCommitIsRB, branchIndex) {
    var branchElement = createOtherBranchElement(singleBranchName, singleBranchCommitSha1, branchIndex);
    $("#otherBranchesList").append(branchElement);

    if (singleBranchCommitIsRB) {
        var firstSlashIndex = singleBranchName.indexOf('\\') + 1;
        var newRTBName = singleBranchName.substring(firstSlashIndex);
        document.getElementById("checkout-branch-" + singleBranchName).innerText += " as RTB";

        document.getElementById("checkout-branch-" + singleBranchName).onclick = function (ev) {
            if (document.getElementsByName(newRTBName).length === 0) {
                $.ajax({
                    url: CREATE_NEW_BRANCH_URL,
                    data: {
                        newBranchName: newRTBName,
                        isRTB: true
                    },
                    success: function (message) {
                        ajaxCreateNewBranchCallback(message);
                        ajaxCheckout(newRTBName);
                    }
                });
            } else {
                if ($("#headBranch-label")[0].textContent === newRTBName) {
                    ShowMessage("The RTB of this RB is already the head branch");
                } else {
                    ajaxCheckout(newRTBName);
                }
            }

        }

    } else {
        document.getElementById("checkout-branch-" + singleBranchName).onclick = function (ev) {
            ajaxCheckout(singleBranchName);
        };
    }


    document.getElementById("delete-branch-" + singleBranchName).onclick = function (ev) {
        ajaxDeleteBranch(singleBranchName);
    };
}

function ajaxOtherBranchesInformationCallback(otherBranchesInformation) {
    $("#otherBranchesList").empty();
    var branchIndex = 1;
    for (var i = 0; i < otherBranchesInformation.length; i += 3) {
        addSingleOtherBranchItem(otherBranchesInformation[i], otherBranchesInformation[i + 1], otherBranchesInformation[i + 2], branchIndex);
        branchIndex++;
    }
}


function ajaxCreateNewBranchCallback(message) {
    if (message === "There is already branch with this name") {
        ShowMessage(message);
    } else {
        ajaxOtherBranchesInformation();
    }
}


function showNewBranchModal() {
    var branchNameModal = $("#branchNameModal")[0];
    branchNameModal.style.display = "block";
}

function initializeBranchNameModal() {
    var span = $("#branchName-xbutton")[0];
    span.onclick = function () {
        var branchNameModal = $("#branchNameModal")[0];
        branchNameModal.style.display = "none";
        $("#branchNameTextInput").val("");
    };

    $("#branchNameForm").submit(function (event) {
            event.preventDefault();
            $.ajax({
                url: CREATE_NEW_BRANCH_URL,
                data: $(this).serialize(),
                success: ajaxCreateNewBranchCallback
            });
            $("#branchNameModal")[0].style.display = "none";
            $("#branchNameTextInput").val("");
            return false;
        }
    );
}

function newPullRequestCallback(message) {
    ShowMessage(message);
}

function initializePRModal() {
    var xButton = $("#prModal-xButton")[0];
    xButton.onclick = function() {
        var prMessageTextArea = document.getElementsByName("PRMessage")[0];
        prMessageTextArea.value = "";
        var prModal = $("#prModal")[0];
        prModal.style.display = "none";
    };
    $("#newPRForm").submit(function () {
        $.ajax({
            url: NEW_PULL_REQUEST_URL,
            data: $(this).serialize(),
            success: newPullRequestCallback
        });
        var prMessageTextArea = document.getElementsByName("PRMessage")[0];
        prMessageTextArea.value = "";
        var prModal = $("#prModal")[0];
        prModal.style.display = "none";
        return false;
    })
}

function initializeModals() {
    initializeBranchNameModal();
    initializeAddFileModal();
    initializeCommitMessageModal();
    initializePRModal();
}

function pushNonRTBTORR() {
    $.ajax(
        {
            url: PUSH_NONRTB_TO_RR_URL,
            success: pushNonRTBTORRCallback
        }
    )
}

function pushNonRTBTORRCallback(message) {
    ShowMessage(message);

    location.reload();
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
    if (message === "pull executed successfully") {
        ajaxHeadBranchInformation(ajaxHeadBranchInformationCallback);
    }
}

function addBranchesToSelectsLists(possibleBranches) {
    var targetBranchesSelect = $("#targetBranchesSelect");
    var baseBranchesSelect = $("#baseBranchesSelect");
    targetBranchesSelect.empty();
    baseBranchesSelect.empty();

    var possibleTargetBranches = possibleBranches[0];
    var possibleBaseBranches = possibleBranches[1];

    for(var i=0;i<possibleTargetBranches.length;i++){
        var targetBranchOption = document.createElement("option");
        targetBranchOption.value = possibleTargetBranches[i];
        targetBranchOption.textContent = possibleTargetBranches[i];
        targetBranchesSelect.append(targetBranchOption);
    }

    for(var j=0;j<possibleBaseBranches.length;j++){
        var baseBranchOption = document.createElement("option");
        baseBranchOption.value = possibleBaseBranches[j];
        baseBranchOption.textContent = possibleBaseBranches[j];
        baseBranchesSelect.append(baseBranchOption);
    }

}

function showPullRequestModal() {
    $.ajax({
        url: POSSIBLE_BRANCHES_FOR_PULL_REQUEST_URL,
        success: function(possibleBranches) {
            addBranchesToSelectsLists(possibleBranches);
            var modal = $("#prModal")[0];
            modal.style.display = "block";
        }
    });
}

function BackToUserInformationPage() {
    var fullUrl = buildUrlWithContextPath("pages/userInformation/userInformation.html");
    window.location.replace(fullUrl);
}

function refreshMessages() {
    $.ajax({
        url: MESSAGES_URL,
        data: "messagesVersion=" + messagesVersion,
        dataType: 'json',
        success: function (data) {
            if (data.version !== messagesVersion) {
                messagesVersion = data.version;
                appendMessagesToMessages(data.messages);
            }
        }

    })
}


function addPRDeltaFilesToPRItem(delta, index) {
    var addedFiles = delta.addedFiles;
    var updatedFiles = delta.updatedFiles;
    var deletedFiles = delta.deletedFiles;
    var prItemId = "pr-accordion-"+index;

    for (var i = 0; i < addedFiles.length; i++) {
        if(addedFiles[i].folderComponent.content) {
            addTextFileItem(addedFiles[i],prItemId+"-tab-added",i);
        } else {
            addFolderItem(addedFiles[i],prItemId+"-tab-added",i);
        }
    }
    for (var j = 0; j < updatedFiles.length; j++) {
        if(updatedFiles[j].folderComponent.content) {
            addTextFileItem(updatedFiles[j],prItemId+"-tab-updated",j);
        } else {
            addFolderItem(updatedFiles[j],prItemId+"-tab-updated",j);
        }
    }
    for (var k = 0; k < deletedFiles.length; k++) {
        if(deletedFiles[k].folderComponent.content) {
            addTextFileItem(deletedFiles[k],prItemId+"-tab-deleted",k);
        } else {
            addFolderItem(deletedFiles[k],prItemId+"-tab-deleted",k);
        }
    }
}

function addSinglePRElementToTable(PR,delta,index) {
    var prElement = "<div class=\"card\" id=\"prItem-"+index+"\">"+
        "<div role=\"tab\" class=\"card-header\">"+
        "<h6 id=\"prItem-"+index+"-header\" class=\"mb-0\"><a id=\"prItem-"+index+"-button\" data-toggle=\"collapse\" aria-expanded=\"true\" aria-controls=\"pr-accordion .item-"+index+"\" href=\"#pr-accordion .item-"+index+"\">" +
        "From:  "+PR.creator+"   ,   Target:  "+PR.targetBranch+"   ,   Base:  "+PR.baseBranch+"   ,   On:  "+PR.dateCreated+"   ,   Status:  "+PR.status+"\"</a></h6></div>"+
        "<div role=\"tabpanel\" data-parent=\"#pr-accordion\" class=\"collapse show item-"+index+"\">"+
        "<div class=\"card-body\">"+
        "<div>"+
        "<ul class=\"nav nav-tabs\">"+
        "<li class=\"nav-item\"><a role=\"tab\" data-toggle=\"tab\" class=\"nav-link active\" href=\"#pr-accordion-"+index+"-tab-added\">Added</a></li>"+
        "<li class=\"nav-item\"><a role=\"tab\" data-toggle=\"tab\" class=\"nav-link\" href=\"#pr-accordion-"+index+"-tab-updated\">Updated</a></li>"+
        "<li class=\"nav-item\"><a role=\"tab\" data-toggle=\"tab\" class=\"nav-link\" href=\"#pr-accordion-"+index+"-tab-deleted\">Deleted</a></li>"+
        "</ul>"+
        "<div class=\"tab-content\">"+
        "<div role=\"tabpanel\" class=\"tab-pane active\" id=\"pr-accordion-"+index+"-tab-added\"></div>"+
        "<div role=\"tabpanel\" class=\"tab-pane\" id=\"pr-accordion-"+index+"-tab-updated\"></div>"+
        "<div role=\"tabpanel\" class=\"tab-pane\" id=\"pr-accordion-"+index+"-tab-deleted\"></div>"+
        "</div>"+
        "</div>"+
        "</div>";


    $("#pr-accordion").append(prElement);

    if(PR.status === "OPEN") {
        addPRDeltaFilesToPRItem(delta,index);
        var openButton = document.createElement("button");
        openButton.setAttribute("class","btn btn-success btn-sm float-right");
        openButton.textContent = "Respond";
        openButton.onclick = function() {
            showPRResponseModal(PR.targetBranch,PR.baseBranch);
        };
        $("#prItem-"+index+"-header").append(openButton);
    } else {
        var button = document.getElementById("prItem-"+index+"-button");
        button.disabled = true;
    }
}

function showPRResponseModal(targetBranch,baseBranch) {

    var targetBranchName = document.getElementsByName("prResponse-targetBranchName")[0];
    targetBranchName.value = targetBranch;
    var baseBranchName = document.getElementsByName("prResponse-baseBranchName")[0];
    baseBranchName.value = baseBranch;

    $("#AcceptRadio").onclick = function () {
        $("#rejectionMessageTextArea").disabled = true;
    };
    $("#RejectedRadio").onclick = function () {
        $("#rejectionMessageTextArea").disabled = false;
    };

    $("#prResponseModal")[0].style.display = "block";
}


function appendPRsOfRepository(data) {
    for(var i=0;i<data.length;i+=2) {
        addSinglePRElementToTable(data[i],data[i+1],i+1); //pr,delta
    }
}

function refreshPRs() {
    $.ajax({
        url: REPOSITORY_PRS,
        data: {
            currentWatchedRepository: REPOSITORY_NAME
        },
        success: function (data) {
            appendPRsOfRepository(data);
        }
    })
}

function appendMessagesToMessages(messages) {
    for (var i = 0; i < messages.length; i++) {
        addSingleMessageToMessagesDisplay(messages[i]);
    }
}

function addSingleMessageToMessagesDisplay(message) {
    var messageElement = createMessageElement(message);
    $("#messagesList").append(messageElement);
}

function createMessageElement(message) {
    return "<a class=\"d-flex align-items-center dropdown-item\" href=\"#\">"+
        "<div class=\"font-weight-bold\">"+message+"</div>"+
        "</a>"
}

$(function () {
    ajaxRepositoryNameAndRRData();
    ajaxHeadBranchInformation(function (headBranchInformation) {
        ajaxHeadBranchInformationCallback(headBranchInformation);
        ajaxOtherBranchesInformation();
    });
    initializeModals();
});

$(function () {
    setInterval(refreshMessages,2000);
    //setInterval(refreshPRs,10000);
});