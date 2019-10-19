package servlets.userInformationPageServlets;

import com.google.gson.Gson;
import engine.users.*;
import engine.users.constants.Constants;
import utils.ServletUtils;
import utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OtherUsersInformationServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        String currentUserName = SessionUtils.getUsername(request);
        Map<String, User> users = userManager.getUsers();

        List<SingleUserData> otherUsersData = new ArrayList<>();

        for (Map.Entry<String,User> entry : users.entrySet()) {
            User user = entry.getValue();
            if(!user.getUsername().equals(currentUserName)) {
                otherUsersData.add(createUserDataFromUser(user));
            }
        }

        try (PrintWriter out = response.getWriter()) {
            Gson gson = new Gson();
            String json = gson.toJson(otherUsersData);
            out.println(json);
            out.flush();
        }
    }

    private SingleUserData createUserDataFromUser(User user) {
        SingleUserData userData = new SingleUserData(user.getUsername());
        userData.getRepositoriesDataList().addAll(user.getRepositoriesDatas());

        return userData;
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
