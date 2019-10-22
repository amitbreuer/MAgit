package servlets.repositoryInformationPageServlets;

import com.google.gson.Gson;
import constants.Constants;
import engine.users.RepositoryData;
import engine.users.User;
import engine.users.UserManager;
import utils.ServletUtils;
import utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class CreateNewBranchServlet extends HttpServlet {
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        String currentUserName = SessionUtils.getUsername(request);
        User currentUser = userManager.getUser(currentUserName);
        String currentRepositoryName = currentUser.getMagitManager().getRepositoryName();
        String branchToAddName = request.getParameter(Constants.NEW_BRANCH_NAME);

        try {
            currentUser.getMagitManager().CreateNewRegularBranch(branchToAddName, false, true, null);
            for (RepositoryData repositoryData : currentUser.getRepositoriesData()) {
                if (repositoryData.getName().equals(currentRepositoryName)) {
                    repositoryData.setNumberOfBranches(repositoryData.getNumberOfBranches() + 1);
                }
            }
            try (PrintWriter out = response.getWriter()) {
                Gson gson = new Gson();
                String json = gson.toJson("The branch was added successfully");
                out.println(json);
                out.flush();
            }


        } catch (Exception e) {
            try (PrintWriter out = response.getWriter()) {
                Gson gson = new Gson();
                String json = gson.toJson("There is already branch with this name");
                out.println(json);
                out.flush();
            }
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
