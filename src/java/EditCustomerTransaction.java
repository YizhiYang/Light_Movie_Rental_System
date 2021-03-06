/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import DBWorks.DBConnection;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author huang
 */
public class EditCustomerTransaction extends HttpServlet {

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
            out.println("<title>Servlet EditCustomerTransaction</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet EditCustomerTransaction at " + request.getContextPath() + "</h1>");
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
        processRequest(request, response);
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
            DBConnect.connectDB();
            String id = request.getParameter("SSN");
            String lastName = request.getParameter("lastname");
            String firstName = request.getParameter("FirstName");
            String telephone = request.getParameter("telephone");
           
            String address = request.getParameter("address");
            String city = request.getParameter("city");
            String state = request.getParameter("state");
            String zip = request.getParameter("zipcode");
            String email = request.getParameter("email");
            String rating = request.getParameter("Rating");
            String CreditCardNumber = request.getParameter("CreditCardNumber");
            String username = request.getParameter("username");
            String userpassword = request.getParameter("password");
            String type = request.getParameter("Type");
           
//                public boolean editEmployee(String Id, String SSN, String LastName, String FirstName, String Address, String city, String state, String Zipcode, String Telephone,
//            String StartDate, String HourlyRate, String username, String password, String isM) {
//(String SSN, String lastName, String firstName,
//            String address,  String city, String state, String zp,String telephone, String email, String RT, String creditCardNumber,
//            String accountType, String username, String password)
            if(DBConnect.editCustomer(id, lastName, firstName, address, city, state, zip, telephone, email, rating, CreditCardNumber, type, username, userpassword))
            {
                RequestDispatcher dispatcher
                        = request.getRequestDispatcher("DisplayAllCustomers.jsp");
                dispatcher.forward(request, response);
            }
            //if(zip == null)
            //processRequest(request, response);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(AddEmployee.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(AddEmployee.class.getName()).log(Level.SEVERE, null, ex);
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
