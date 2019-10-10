var REPOSITORY_NAME = buildUrlWithContextPath("repositoryName");

// function ajaxBranchesInfo() {
//     $.ajax({
//         url:
//     })
// }

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

$(function () {
    //ajaxBranchesInfo();
    ajaxRepositoryName();
});