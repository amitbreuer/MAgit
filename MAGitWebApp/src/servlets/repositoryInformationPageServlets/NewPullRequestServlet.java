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

public class NewPullRequestServlet extends HttpServlet {
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        UserManager userManager = ServletUtils.getUserManager(getServletContext());

        String username = SessionUtils.getUsername(request);
        User user = userManager.getUser(username);
        String repositoryName = user.getMagitManager().GetCurrentRepository().getRemoteRepositoryname();
        String targetBranch = request.getParameter("targetBranchSelect");
        String baseBranch = request.getParameter("baseBranchSelect");

        if(user.getMagitManager().BranchIsRB(baseBranch)) { // if the branch is RB in creator's repository
            int index = baseBranch.indexOf("\\");
            baseBranch = baseBranch.substring(index+1);
        }
        String prMessage = request.getParameter("PRMessage");
        String otherUserName = ServletUtils.getRRUserNameFromOtherRepositoryPath(user.getMagitManager().GetCurrentRepository().getRemoteRepositoryPath().toString());
        User otherUser = userManager.getUser(otherUserName);
        otherUser.AddMessage("You got a new Pull Request from: " + username +",\nRepository: "+repositoryName+",\nTarget Branch: "+targetBranch
                +",\nBase Branch:" +baseBranch + ",\nMessage: "+prMessage);

        String dateCreated = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss.SSS"));
        PullRequest newPR = new PullRequest(username,targetBranch,baseBranch,dateCreated, PullRequest.Status.OPEN);
        otherUser.AddPR(repositoryName,newPR);

        String message = "Pull Request was sent";
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
