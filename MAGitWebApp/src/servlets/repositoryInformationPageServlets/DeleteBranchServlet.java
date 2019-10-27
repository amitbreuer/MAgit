package servlets.repositoryInformationPageServlets;

import com.google.gson.Gson;
import constants.Constants;
import engine.Commit;
import engine.MagitManager;
import engine.users.CommitData;
import engine.users.RepositoryData;
import engine.users.User;
import engine.users.UserManager;
import utils.ServletUtils;
import utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class DeleteBranchServlet extends HttpServlet {
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        String currentUserName = SessionUtils.getUsername(request);
        User currentUser = userManager.getUser(currentUserName);
        String currentRepositoryName = currentUser.getMagitManager().getRepositoryName();
        String branchToDeleteName = request.getParameter(Constants.BRANCH_TO_DELETE_NAME);

        try {
            if(currentUser.getMagitManager().BranchIsRB(branchToDeleteName)){
                int index = branchToDeleteName.indexOf("\\");//removing the RRName from the branch name
                String RBName = branchToDeleteName.substring(index+1);
                String RRPath = currentUser.getMagitManager().getRRPath();
                String RRUsername = ServletUtils.getRRUserNameFromOtherRepositoryPath(RRPath);
                String RRName = currentUser.getMagitManager().GetCurrentRepository().getRemoteRepositoryname();

                User RRUser = userManager.getUser(RRUsername);
                if( RRUser.getMagitManager().getRepositoryName()!=null && RRUser.getMagitManager().getRepositoryName().equals(RRName)){
                    RRUser.getMagitManager().DeleteBranch(RBName);
                }else {
                    Files.delete(Paths.get(RRPath+ File.separator+".magit"+File.separator+"branches"+File.separator+RBName+".txt"));
                }

                for (RepositoryData repositoryData : RRUser.getRepositoriesData()) {
                    if (repositoryData.getName().equals(RRName)) {
                        repositoryData.setNumberOfBranches(repositoryData.getNumberOfBranches() - 1);
                    }
                }
            }

            currentUser.getMagitManager().DeleteBranch(branchToDeleteName);
            for (RepositoryData repositoryData : currentUser.getRepositoriesData()) {
                if (repositoryData.getName().equals(currentRepositoryName)) {
                    repositoryData.setNumberOfBranches(repositoryData.getNumberOfBranches() - 1);
                }
            }
            try (PrintWriter out = response.getWriter()) {
                Gson gson = new Gson();
                String json = gson.toJson(branchToDeleteName+" branch was deleted");
                out.println(json);
                out.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
