package servlets.repositoryInformationPageServlets;

import com.google.gson.Gson;
import constants.Constants;
import engine.Folder;
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
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PushNoneRTBToRRServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        UserManager userManager = ServletUtils.getUserManager(getServletContext());

        String username = SessionUtils.getUsername(request);
        User user = userManager.getUser(username);
        String RRName = user.getMagitManager().GetCurrentRepository().getRemoteRepositoryname();
        String RRPath = user.getMagitManager().GetCurrentRepository().getRemoteRepositoryPath().toString();
        String RRUser = RRPath.substring(12);// cut the user directory prefix

        for (RepositoryData repositoryData :userManager.getUser(RRUser).getRepositoriesData()){
            if (repositoryData.getName().equals(RRName)){
                repositoryData.setNumberOfBranches(repositoryData.getNumberOfBranches()+1);
            }
        }
        try {
            user.getMagitManager().PushNoneRTBToRR();
            Gson gson = new Gson();
            String json = gson.toJson("push executed successfully");

            try (PrintWriter out = response.getWriter()) {
                out.println(json);
                out.flush();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Gson gson = new Gson();
            String json = gson.toJson(e.getMessage());

            try (PrintWriter out = response.getWriter()) {
                out.println(json);
                out.flush();
            }
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
