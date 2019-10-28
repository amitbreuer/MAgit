package servlets.repositoryInformationPageServlets;

import com.google.gson.Gson;
import engine.users.PullRequest;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PRResponseServlet extends HttpServlet {
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        UserManager userManager = ServletUtils.getUserManager(getServletContext());

        String username = SessionUtils.getUsername(request);
        User user = userManager.getUser(username);
        String targetBranchName = request.getParameter("prResponse-targetBranchName");
        String baseBranchName = request.getParameter("prResponse-baseBranchName");
        String rejectionMessage = request.getParameter("rejectionMessage");
        String action = request.getParameter("responseRadio");
        String otherUsername = request.getParameter("prResponse-creatorsName");
        String prId = request.getParameter("PRNumber");
        User otherUser = userManager.getUser(otherUsername);

        if(action.equals("Accept")) {
            user.getMagitManager().MergeTargetBranchIntoBaseBranch(targetBranchName,baseBranchName);
            otherUser.AddMessage(username + " has accepted your pull request for repository: " + user.getMagitManager().getRepositoryName());
            user.ChangeStatusOfPR(user.getMagitManager().getRepositoryName(),Integer.parseInt(prId),PullRequest.Status.CLOSED);
        } else {
            otherUser.AddMessage(username + " has rejected your pull request for repository: " + user.getMagitManager().getRepositoryName()+
                    " and responded: " + "\""+rejectionMessage+"\"");
            user.ChangeStatusOfPR(user.getMagitManager().getRepositoryName(),Integer.parseInt(prId),PullRequest.Status.REJECTED);
        }

        String message = "Response was sent";

        try (PrintWriter out = response.getWriter()) {
            Gson gson = new Gson();
            String json = gson.toJson(message);
            out.println(json);
            out.flush();
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
