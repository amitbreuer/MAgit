var REPOSITORY_NAME_AND_RR_DATA_URL = buildUrlWithContextPath("repositoryNameAndRRData");
var HEAD_BRANCH_INFORMATION_URL = buildUrlWithContextPath("headBranchInformation");
var MAIN_FOLDER_OF_COMMIT_URL = buildUrlWithContextPath("mainFolderOfCommit");
var REPOSITORY_NAME;
var WC_STATUS_URL = buildUrlWithContextPath("wcStatus");

function setRepositoryName(name) {
    //$("#repositoryName").val(name);
    REPOSITORY_NAME = name;
    $("#repositoryName-label")[0].textContent = name;
}


function ajaxRepositoryNameAndRRData() {
    console.log(REPOSITORY_NAME_AND_RR_DATA_URL);
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
    console.log(repositoryNameAndRRData[0]);
    setRepositoryName(repositoryNameAndRRData[0]);
    if (repositoryNameAndRRData[1]) {
        setRRData(repositoryNameAndRRData[1], repositoryNameAndRRData[2]);
    }
}

function addTextFileItem(folderComponent,containingFolderId,index) {
    var textFileItem = "<div class=\"card\">\n" +
        "<div role=\"tab\" class=\"card-header\">\n" +
        "<h5 class=\"mb-0\"><a data-toggle=\"collapse\" aria-expanded=\"false\" aria-controls=\""+containingFolderId+" .item-"+index+"\" href=\"#"+containingFolderId+" .item-"+index+"\" style=\"font-size: 14px;\"><i class=\"fa fa-file-text-o\"></i> "+folderComponent.name+"</a></h5>\n" +
        "</div>\n" +
        "<div class=\"collapse item-"+index+"\" data-parent=\"#"+containingFolderId+"\" role=\"tabpanel\" >\n" +
        "<div class=\"card-body\">\n" +
        "<p class=\"card-text\">"+folderComponent.folderComponent.content+"</p>\n" +
        "</div>\n" +
        "</div>\n" +
        "</div>\n";

    $("#"+containingFolderId+"").append(textFileItem);
}

function addFolderItem(folderComponent,containingFolderId,index) {
    var components = folderComponent.folderComponent.components;
    var folderItem = "<div class=\"card\">\n" +
        "<div role=\"tab\" class=\"card-header\">\n" +
        "<h5 class=\"mb-0\"><a data-toggle=\"collapse\" aria-expanded=\"false\" aria-controls=\""+containingFolderId+" .item-"+index+"\" href=\"#"+containingFolderId+" .item-"+index+"\" style=\"font-size: 14px;\"><i class=\"fas fa-folder\" style=\"color: rgb(241,232,15);\"></i> "+folderComponent.name+"</a></h5>\n" +
        "</div>\n"+
        "<div role=\"tabpanel\" data-parent=\"#"+containingFolderId+"\" class=\"collapse item-"+index+"\">\n" +
        "<div class=\"card-body\">" +
        "<div role=\"tablist\" id=\""+folderComponent.name+"-accordion\"></div>\n" +
        "</div>\n" +
        "</div>\n" +
        "</div>\n";

    $("#"+containingFolderId+"").append(folderItem);

    for(var i=0;i<components.length;i++) {

        if(components[i].folderComponent.content) { // blob
            addTextFileItem(components[i],folderComponent.name + "-accordion",i+1);
        } else {
            addFolderItem(components[i],folderComponent.name + "-accordion",i+1);
        }
    }
}

function addFileItemToWCDisplay(folderComponent,index) {

    if(folderComponent.folderComponent.content) { // blob
        addTextFileItem(folderComponent,"wc-accordion",index);
    } else { // folder
        addFolderItem(folderComponent,"wc-accordion",index);
    }
}

function showWCStatus(wcFolderData) {
    var folderComponents = wcFolderData.components;
    for(var i=0;i<folderComponents.length;i++) {
        addFileItemToWCDisplay(folderComponents[i],i+1);
    }
}

function ajaxWCFiles() {
    $.ajax({
        url: WC_STATUS_URL,
        data: {
            currentWatchedRepository : "rep 1"
        },
        success: showWCStatus
    })
}

$(function () {
    console.log("initializing window");
    ajaxRepositoryNameAndRRData();
    ajaxHeadBranchInformation()
    //ajaxRepositoryNameAndRRData();
    //ajaxBranchesInfo();
    ajaxWCFiles();
});


function ajaxHeadBranchInformation() {
    $.ajax(
        {
            url: HEAD_BRANCH_INFORMATION_URL,
            success: ajaxHeadBranchInformationCallback
        }
    )
}

function createHeadBranchSingleCommitElement(headBranchSingleCommitData) {
    return "<div class=\"card\">" +
        "<div class=\"card-header\" role=\"tab\">" +
        "<h5 class=\"d-none d-lg-flex align-items-center align-items-lg-center mb-0\"><a id=\""+headBranchSingleCommitData.sha1+"\" data-toggle=\"collapse\" aria-expanded=\"false\" aria-controls=\"accordion-1 .item-2\" href=\"#accordion-1 .item-2\" style=\"margin: 7px;font-size: 15px;\">" + headBranchSingleCommitData.sha1 + "</a><em style=\"margin: 7px;font-size: 15px;\">" + headBranchSingleCommitData.message + "</em><strong class=\"float-right d-lg-flex align-items-lg-end" +
        "style=\"margin: 7px;font-size: 15px;\">" + headBranchSingleCommitData.creator + "</strong><code class=\"text-warning float-right\" style=\"font-size: 14px;margin: 7px;\">" + headBranchSingleCommitData.dateCreated + "</code></h5>" +
        "</div>" +
        "<div role=\"tabpanel\" data-parent=\"#headbranch-commits-accordion\" class=\"collapse item-2\">" +
        "<div id=\""+headBranchSingleCommitData.sha1+"-main-folder\" class=\"card-body\"></div>" +
        "</div>" +
        "</div>"
}

function ajaxMainFolderOfCommit(commitSha1) {
    $.ajax(
        {
            url: MAIN_FOLDER_OF_COMMIT_URL,
            data: {commitSha1:commitSha1
            },
            success:function (commitMainFolder){
                addCommitMainFolderComponentsToCommitDisplay(commitSha1,commitMainFolder);
            }
        }
    )
}
function addCommitMainFolderComponentsToCommitDisplay(commitSha1,commitMainFolder) {
    var commitMainFolderElementId= commitSha1+"-main-folder";
    document.getElementById(commitMainFolderElementId).innerHTML="";
    document.getElementById(commitMainFolderElementId).append("amitMethod");

}

function addSingleCommitToHeadBranchCommitsDisplay(headBranchSingleCommitData) {
    var commitSha1 = headBranchSingleCommitData.sha1;
    var singleCommitElement = createHeadBranchSingleCommitElement(headBranchSingleCommitData);
console.log(singleCommitElement);
    $("#headbranch-commits-accordion").append(singleCommitElement);

    document.getElementById(commitSha1).onclick = function () {
        ajaxMainFolderOfCommit(commitSha1);

    };

}

function updateHeadBranchCommitsDisplay(headBranchInformation) {
    for (var i = 1; i < headBranchInformation.length; i++) {
        addSingleCommitToHeadBranchCommitsDisplay(headBranchInformation[i]);
    }
}

function updateHeadBranchInformation(headBranchInformation) {
    $("#headBranch-label")[0].textContent = headBranchInformation[0];
    updateHeadBranchCommitsDisplay(headBranchInformation);
}

function ajaxHeadBranchInformationCallback(headBranchInformation) {
    updateHeadBranchInformation(headBranchInformation);

}