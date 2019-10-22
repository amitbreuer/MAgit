package servlets.userInformationPageServlets;

import com.google.gson.Gson;
import constants.Constants;
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
import java.text.SimpleDateFormat;
import java.util.Date;

public class ForkServlet extends HttpServlet {
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        UserManager userManager = ServletUtils.getUserManager(getServletContext());

        String username = SessionUtils.getUsername(request);
        String otherUserName = request.getParameter(Constants.OTHER_USERNAME);
        String otherUserRepositoryName = request.getParameter(Constants.OTHER_USER_REPOSITORY_NAME);
        userManager.getUser(otherUserName).AddMessage(username+" has forked your repository-"+otherUserRepositoryName+"\nAt :"+userManager.getDate());
        String message = "The Fork was executed successfully";
        User user = userManager.getUser(username);

        try {
            user.getMagitManager().Fork(username,otherUserName,otherUserRepositoryName);
            user.CreateRepositoryDataForNewRepository(otherUserRepositoryName);
        } catch (Exception e) {
            message = e.getMessage();
        } finally {
            try (PrintWriter out = response.getWriter()) {
                Gson gson = new Gson();
                String json = gson.toJson(message);
                out.println(json);
                out.flush();
            }
        }

    }

    private String getDate() {

        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss:SSS");
        Date date = new Date();
        return formatter.format(date);

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
