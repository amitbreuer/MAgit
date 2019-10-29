package servlets.repositoryInformationPageServlets;

import com.google.gson.Gson;
import engine.Blob;
import engine.Folder;
import engine.users.User;
import engine.users.UserManager;
import org.apache.commons.codec.digest.DigestUtils;
import utils.ServletUtils;
import utils.SessionUtils;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AddFileServlet extends HttpServlet {
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        String currentUserName = SessionUtils.getUsername(request);
        String fixedFilePath = ServletUtils.getFixedFilePathForAddedFile(request.getParameter("createFile-path")
                ,currentUserName);

        String fileName = request.getParameter("createFile-name") + ".txt";
        String content = request.getParameter("createFile-content");
        User currentUser = userManager.getUser(currentUserName);

        String message = fileName + " was added";
        Blob newBlob = new Blob(content);
        Folder.ComponentData newFolderComponent = new Folder.ComponentData(fileName, DigestUtils.sha1Hex(newBlob.toString())
                ,newBlob,currentUserName, LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss.SSS")));

        List<Object> data = new ArrayList<>();

        try {
            currentUser.getMagitManager().createTextFile(fixedFilePath + File.separator + fileName,content);
        } catch (IOException e) {
            message = e.getMessage();
        } finally {
            try (PrintWriter out = response.getWriter()) {
                data.add(message);
                data.add(newFolderComponent);
                Gson gson = new Gson();
                String json = gson.toJson(data);
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
