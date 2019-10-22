var REPOSITORY_NAME_AND_RR_DATA_URL = buildUrlWithContextPath("repositoryNameAndRRData");
var HEAD_BRANCH_INFORMATION_URL = buildUrlWithContextPath("headBranchInformation");
var MAIN_FOLDER_OF_COMMIT_URL = buildUrlWithContextPath("mainFolderOfCommit");
var WC_Files_URL = buildUrlWithContextPath("wcFiles");
var REPOSITORY_NAME;
var EDIT_FILE_URL = buildUrlWithContextPath("editFile");
var DELETE_FILE_URL = buildUrlWithContextPath("deleteFile");
var ADD_FILE_URL = buildUrlWithContextPath("addFile");
var OPEN_CHANGES = buildUrlWithContextPath("openChanges");
var COMMIT_URL = buildUrlWithContextPath("commit");

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

function ajaxRepositoryNameAndRRDataCallback(repositoryNameAndRRData) {
    setRepositoryName(repositoryNameAndRRData[0]);
    if (repositoryNameAndRRData[1]) {
        setRRData(repositoryNameAndRRData[1], repositoryNameAndRRData[2]);
    }
    ajaxWCFiles();
}

function emptyOpenChanesLists() {
    $("#addedFiles-list").empty();
    $("#updatedFiles-list").empty();
    $("#deletedFiles-list").empty();
}

function showOpenChanges(delta) {
    var addedFiles = delta.addedFiles;
    var updatedFiles = delta.updatedFiles;
    var deletedFiles = delta.deletedFiles;

    emptyOpenChanesLists();

    for(var i=0;i<addedFiles.length;i++){
        addFileToOpenChanges(addedFiles[i].fullNameFromMainFolder,"addedFiles-list");
    }
    for(var j=0;j<updatedFiles.length;j++){
        addFileToOpenChanges(updatedFiles[j].fullNameFromMainFolder,"updatedFiles-list");
    }
    for(var k=0;k<deletedFiles.length;k++){
        addFileToOpenChanges(deletedFiles[k].fullNameFromMainFolder,"deletedFiles-list");
    }
}

function ajaxOpenChanges() {
    $.ajax({
        url: OPEN_CHANGES,
        data: {
            currentWatchedRepository : REPOSITORY_NAME
        },
        success: showOpenChanges
    })
}

function ajaxSaveNewContent(fileItemContentId,saveButtonId,fileID,content) {
    $.ajax({
        url: EDIT_FILE_URL,
        data: {
            currentWatchedRepository : REPOSITORY_NAME,
            fileFullName : fileID,
            fileNewContent : content
        },
        success: function (message){
            document.getElementById(fileItemContentId).setAttribute("contentEditable","false");
            document.getElementById(saveButtonId).setAttribute("class","btn btn-secondary btn-sm d-none float-right");
            console.log(message);
            ajaxOpenChanges();
        }
    })
}

function editTextFile(fileItemContentId,saveButtonId) {
    document.getElementById(fileItemContentId).setAttribute("contentEditable","true");
    document.getElementById(saveButtonId).setAttribute("class","btn btn-secondary btn-sm float-right");
}

function ajaxDeleteFile(fileID) {
    $.ajax({
        url : DELETE_FILE_URL,
        data : {
            currentWatchedRepository : REPOSITORY_NAME,
            fileFullName : fileID
        },
        success: function (message){
            document.getElementById(fileID+"Item").remove();
            console.log(message);
            ajaxOpenChanges();
        }
    })
}

function addEditableTextFileItem(folderComponent, containingFolderId, index) {

    var fileName = containingFolderId+"-"+folderComponent.name;
    var contentId = fileName+"-content";
    var saveButtonId = fileName+"-save";

    var textFileItem = "<div id=\""+fileName+"Item\" class=\"card\">" +
        "<div role=\"tab\" class=\"card-header\">" +
        "<h5 class=\"mb-0\"><a data-toggle=\"collapse\" aria-expanded=\"false\" aria-controls=\"" + containingFolderId + " .item-" + index + "\" href=\"#" + containingFolderId + " .item-" + index + "\" style=\"font-size: 14px;\"><i class=\"fa fa-file-text-o\"></i> " + folderComponent.name + "</a>" +
        "<button class=\"btn btn-secondary btn-sm float-right\" type=\"button\" onclick='ajaxDeleteFile(\""+fileName+"\")'><i class=\"fas fa-trash-alt\"></i> Delete</button>" +
        "<button class=\"btn btn-secondary btn-sm float-right\" type=\"button\" onclick='editTextFile(\""+contentId+"\",\""+saveButtonId+"\")' style=\"margin-right: 3px;\"><i class=\"fa fa-pencil\"></i> Edit</button>" +
        "<button id=\""+saveButtonId+"\" class=\"btn btn-secondary btn-sm d-none float-right\" type=\"button\" onclick='ajaxSaveNewContent(\""+contentId+"\",\""+saveButtonId+"\",\""+containingFolderId+"-"+folderComponent.name+"\",document.getElementById(\""+contentId+"\").textContent)' style=\"margin-right: 2px;\"><i class=\"fa fa-save\"></i> Save</button>" +
        "</h5>" +
        "</div>" +
        "<div class=\"collapse item-" + index + "\" data-parent=\"#" + containingFolderId + "\" role=\"tabpanel\" >" +
        "<div class=\"card-body\">" +
        "<p id=\""+contentId+"\" class=\"card-text\">" + folderComponent.folderComponent.content + "</p>" +
        "</div>" +
        "</div>" +
        "</div>";

    $("#" + containingFolderId).append(textFileItem);
}

function addEditableFolderItem(folderComponent, containingFolderId, index,containingFolderName) {
    var fixedFolderName = containingFolderName;
    if(containingFolderName !== "") {
        fixedFolderName += "/";
    }
    fixedFolderName  += folderComponent.name;

    var components = folderComponent.folderComponent.components;
    var folderItem = "<div id=\""+ containingFolderId + "-" + folderComponent.name + "Item\" class=\"card\">" +
        "<div role=\"tab\" class=\"card-header\">" +
        "<h5 class=\"mb-0\"><a data-toggle=\"collapse\" aria-expanded=\"false\" aria-controls=\"" + containingFolderId + " .item-" + index + "\" href=\"#" + containingFolderId + " .item-" + index + "\" style=\"font-size: 14px;\"><i class=\"fas fa-folder\" style=\"color: rgb(241,232,15);\"></i> " + folderComponent.name + "</a>" +
        "<button class=\"btn btn-secondary btn-sm float-right\" type=\"button\" onclick='ajaxDeleteFile(\""+ containingFolderId + "-" + folderComponent.name + "\")'><i class=\"fas fa-trash-alt\"></i> Delete</button>" +
        "<button class=\"btn btn-secondary btn-sm float-right\" type=\"button\" onclick='showCreateFileModal(\""+containingFolderId+"-"+folderComponent.name+"\",\""+ fixedFolderName + "\")' style=\"margin-right: 3px;\"><i class=\"icon ion-plus-round\"></i> Add File</button>" +
        "</h5>" +
        "</div>" +
        "<div role=\"tabpanel\" data-parent=\"#" + containingFolderId + "\" class=\"collapse item-" + index + "\">" +
        "<div class=\"card-body\">" +
        "<div role=\"tablist\" id=\""+ containingFolderId + "-" + folderComponent.name + "\"></div>" +
        "</div>" +
        "</div>" +
        "</div>";

    $("#"+containingFolderId).append(folderItem);

    for (var i = 0; i < components.length; i++) {

        if (components[i].folderComponent.content) { // blob
            addEditableTextFileItem(components[i], containingFolderId+'-'+folderComponent.name, i + 1);
        } else {
            addEditableFolderItem(components[i], containingFolderId+'-'+folderComponent.name, i + 1,fixedFolderName);
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
        "<div role=\"tablist\" id=\""+ containingFolderId + "-" + folderComponent.name + "\"></div>" +
        "</div>" +
        "</div>" +
        "</div>";

    $("#"+containingFolderId).append(folderItem);

    for (var i = 0; i < components.length; i++) {

        if (components[i].folderComponent.content) { // blob
            addTextFileItem(components[i], containingFolderId+'-'+folderComponent.name, i + 1);
        } else {
            addFolderItem(components[i], containingFolderId+'-'+folderComponent.name, i + 1);
        }
    }
}

function addFileItemToWCDisplay(folderComponent, index) {

    if (folderComponent.folderComponent.content) { // blob
        addEditableTextFileItem(folderComponent, "wc-accordion", index);
    } else { // folder
        addEditableFolderItem(folderComponent, "wc-accordion", index,"");
    }
}

function showCreateFileModal(containingItemId,containingFolder) {
    var modal = $("#createFileModal")[0];

    var pathLabel = document.getElementsByName("createFile-path")[0];

    pathLabel.value = REPOSITORY_NAME;
    if(containingFolder !== "") {
        pathLabel.value += "/" + containingFolder;
    }

    modal.style.display = "block";
}

function addFileToOpenChanges(fileFullName, changesList) {
    var newItem;
    if(fileFullName.match(".txt")) {
        newItem = "<li class=\"list-group-item\"><i class=\"fa fa-file-text-o\"></i><button class=\"btn btn-link btn-sm border-white\" type=\"button\"> "+fileFullName+"</button></li>";
    } else {
        newItem = "<li class=\"list-group-item\"><i class=\"fa fa-folder\"></i><button class=\"btn btn-link btn-sm border-white\" type=\"button\"> "+fileFullName+"</button></li>";

    }
    $("#"+changesList).append(newItem);
}

function createFileCallBack() {
    ajaxOpenChanges();
    ajaxWCFiles();

    var modal = $("#createFileModal")[0];
    var fileNameTextField = document.getElementsByName("createFile-name")[0];
    var contentTextArea = document.getElementsByName("createFile-content")[0];
    modal.style.display = "none";
    fileNameTextField.value = "";
    contentTextArea.value = "";
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

function ajaxHeadBranchInformation() {
    $.ajax(
        {
            url: HEAD_BRANCH_INFORMATION_URL,
            success: ajaxHeadBranchInformationCallback
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
    for (var i = 1; i < headBranchInformation.length; i++) {
        addSingleCommitToHeadBranchCommitsDisplay(headBranchInformation[i], i + 1);
    }
}

function updateHeadBranchInformation(headBranchInformation) {
    $("#headBranch-label")[0].textContent = headBranchInformation[0];
    updateHeadBranchCommitsDisplay(headBranchInformation);
}

function ajaxHeadBranchInformationCallback(headBranchInformation) {
    updateHeadBranchInformation(headBranchInformation);
}

function initializeAddFileModal() {
    var modal = $("#createFileModal");
    var xButton = document.getElementById("createFile-xButton");
    var fileNameTextField = document.getElementsByName("createFile-name")[0];
    var contentTextArea = document.getElementsByName("createFile-content")[0];
    xButton.onclick = function () {
        fileNameTextField.value = "";
        contentTextArea.value = "";
        modal.style.display = "none";
    };
    $("#createFileForm").submit(function () {
        $.ajax({
            url: ADD_FILE_URL,
            data: $(this).serialize(),
            success: function(message) {
                console.log(message);
                createFileCallBack();
            }
        });
        return false;
    });
}

function commitCallBack() {
    ajaxOpenChanges();
    var commitsMessage = document.getElementsByName("commitMessage")[0];
    var modal = $("#commitMessageModal")[0];
    modal.style.display = "none";
    commitsMessage.value = "";
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
            success: function(message) {
                console.log(message);
                commitCallBack();
            }
        });
        return false;
    });
}

$(function () {
    ajaxRepositoryNameAndRRData();
    ajaxHeadBranchInformation();

    initializeAddFileModal();
    initializeCommitMessageModal();


});