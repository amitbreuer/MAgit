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
import java.util.List;

public class MessagesServlet extends HttpServlet {
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        String currentUserName = SessionUtils.getUsername(request);
        int messagesVersionFromRequest = ServletUtils.getIntParameter(request, Constants.MESSAGES_VERSION_PARAMETER);
        if (messagesVersionFromRequest == Constants.INT_PARAMETER_ERROR) {
            return;
        }
        List<String> messages;
        User currentUser = userManager.getUser(currentUserName);
        int userMessagesVersion;
            userMessagesVersion = currentUser.getMessagesVersion();
            messages = currentUser.getMessages(messagesVersionFromRequest);

        MessagesAndVersion mav = new MessagesAndVersion(messages, userMessagesVersion);

        try (PrintWriter out = response.getWriter()) {
            Gson gson = new Gson();
            String json = gson.toJson(mav);
            out.println(json);
            out.flush();
        }
    }

    private static class MessagesAndVersion {

        final private List<String> messages;
        final private int version;

        public MessagesAndVersion(List<String> messages, int version) {
            this.messages = messages;
            this.version = version;
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
