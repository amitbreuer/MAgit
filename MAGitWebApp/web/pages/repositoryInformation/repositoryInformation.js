var REPOSITORY_NAME = buildUrlWithContextPath("repositoryName");
var REPOSITORY_NAME_AND_RR_DATA_URL = buildUrlWithContextPath("repositoryNameAndRRData");


function setRepositoryName(name) {
    $("#repositoryName").val(name);
}

function ajaxRepositoryName() {
    $.ajax({
        url: REPOSITORY_NAME,
        success: function(name) {
            setRepositoryName(name);
        }
    })
}

function ajaxRepositoryNameAndRRData() {
    $.ajax({
        url:REPOSITORY_NAME_AND_RR_DATA_URL,
        success: ajaxRepositoryNameAndRRDataCallback
    })
}

function setRRData(RRName, RRUser) {
$("rrName-label").val=RRName;
$("rrUser-label").val = RRUser;
}

function ajaxRepositoryNameAndRRDataCallback(repositoryNameAndRRData){
setRepositoryName(repositoryNameAndRRData[0]);
if(repositoryNameAndRRData[1]){
    setRRData(repositoryNameAndRRData[1],repositoryNameAndRRData[2]);
}
}

$(function () {
    ajaxRepositoryNameAndRRData();
    //ajaxBranchesInfo();
    ajaxRepositoryName();
});