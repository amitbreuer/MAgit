package servlets.repositoryInformationPageServlets;

import com.google.gson.Gson;
import engine.Branch;
import engine.Repository;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PossibleBranchesForPRServlet extends HttpServlet {
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        String username = SessionUtils.getUsername(request);
        User user = userManager.getUser(username);
        Map<String, Branch> branches = user.getMagitManager().GetCurrentRepository().getBranches();
        List<String> targetBranchOptions = new ArrayList<>();
        List<String> baseBranchOptions = new ArrayList<>();
        for(Map.Entry<String,Branch> entry : branches.entrySet()) {
            Branch branch = entry.getValue();
            if (branch.getIsRTB()) {
                targetBranchOptions.add(branch.getName());
                baseBranchOptions.add(branch.getName());
            }
            if(branch.getIsRB()) {
                baseBranchOptions.add(branch.getName());
            }
        }
        List<List<String>> possibleBranchForPR = new ArrayList<>();
        possibleBranchForPR.add(targetBranchOptions);
        possibleBranchForPR.add(baseBranchOptions);
        Gson gson = new Gson();
        String json = gson.toJson(possibleBranchForPR);
        try (PrintWriter out = response.getWriter()) {
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
