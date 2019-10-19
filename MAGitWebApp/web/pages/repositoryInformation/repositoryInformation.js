var REPOSITORY_NAME_AND_RR_DATA_URL = buildUrlWithContextPath("repositoryNameAndRRData");
var WC_STATUS_URL = buildUrlWithContextPath("wcStatus");
var REPOSITORY_NAME;

function setRepositoryName(name) {
    REPOSITORY_NAME = name;
    $("#repositoryName").val(name);
}

function ajaxRepositoryNameAndRRData() {
    $.ajax({
        url: REPOSITORY_NAME_AND_RR_DATA_URL,
        success: ajaxRepositoryNameAndRRDataCallback
    })
}

function setRRData(RRName, RRUser) {
    $("rrName-label").val = RRName;
    $("rrUser-label").val = RRUser;
}

function ajaxRepositoryNameAndRRDataCallback(repositoryNameAndRRData) {
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
    //ajaxRepositoryNameAndRRData();
    //ajaxBranchesInfo();
    ajaxWCFiles();
});