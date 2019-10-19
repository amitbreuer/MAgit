package servlets.userInformationPageServlets;

import com.google.gson.Gson;
import constants.Constants;
import engine.MagitManager;
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

import static constants.Constants.USERNAME;

public class NewRepositoryServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String fileContent = request.getParameter("file");
        Gson gson = new Gson();
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        String currentUserName = SessionUtils.getUsername(request);
        User user = userManager.getUser(currentUserName);

        try {
            user.getMagitManager().ValidateAndLoadXMLRepositoryFromUploadedFile(currentUserName,fileContent);
            out.println(gson.toJson("The repository was uploaded successfully"));
            String repositoryName = user.getMagitManager().getRepositoryName();
            user.CreateRepositoryDataForNewRepository(repositoryName);
        } catch (Exception e) {
            out.println(gson.toJson("error: "+ e.getMessage()+".\n the repository was not loaded") );
        }
    }

}
