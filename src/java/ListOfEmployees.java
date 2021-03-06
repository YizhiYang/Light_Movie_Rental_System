/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import Beans.Employee;
import Beans.Recommendation;
import DBWorks.DBConnection;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author MATT
 */
public class ListOfEmployees extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet ListOfEmployees</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ListOfEmployees at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            DBConnection DBConnect = new DBConnection();
            DBConnect.connectDB();
            if (request.getParameter("EmployeeId") != null) {
                boolean result = DBConnect.deleteEmployee(request.getParameter("EmployeeId"));
                request.setAttribute("deleteStatus", result);
            }
            ResultSet rs = null;
            String url = "Employees.jsp";
            ArrayList list = new ArrayList();
            rs = DBConnect.queryAllEmployees();

            while (rs.next()) {
                Employee employee = new Employee();
                employee.setID(rs.getInt("Id"));
                employee.setSSN(rs.getString("SSN"));
                employee.setDate(rs.getDate("StartDate"));
                employee.setHourlyRate(rs.getInt("HourlyRate"));
                list.add(employee);
            }
            request.setAttribute("employeesList", list);
            DBConnect.close();

            RequestDispatcher dispatcher
                    = request.getRequestDispatcher(url);
            dispatcher.forward(request, response);
            //this.processRequest(request, response);

        } catch (SQLException ex) {
            Logger.getLogger(HomePageServ.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(HomePageServ.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {

            DBConnection DBConnect = new DBConnection();
            if (DBConnect.connectDB() == false) {
                processRequest(request, response);
            }
            ResultSet rs = null;

            rs = DBConnect.queryAllEmployees();

            ArrayList resultList = new ArrayList();
            while (rs.next()) {
                Employee employee = new Employee();
                employee.setID(rs.getInt("Id"));
                employee.setSSN(rs.getString("SSN"));
                employee.setDate(rs.getDate("StartDate"));
                employee.setHourlyRate(rs.getInt("HourlyRate"));
                resultList.add(employee);
            }
            request.setAttribute("employeesList", resultList);

            DBConnect.close();
            String url = "Employees.jsp";
            RequestDispatcher dispatcher
                    = request.getRequestDispatcher(url);
            dispatcher.forward(request, response);
            //processRequest(request, response);

        } catch (SQLException ex) {
            Logger.getLogger(SearchResult.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(SearchResult.class.getName()).log(Level.SEVERE, null, ex);
        }
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
