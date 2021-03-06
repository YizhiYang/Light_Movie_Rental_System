
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DBWorks;

import com.mysql.jdbc.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 *
 * @author MATT
 */
public class DBConnection {

    private Connection conn;

    public DBConnection() {

        conn = null;
    }

    public boolean valid(HttpServletRequest request) throws ClassNotFoundException, SQLException {

        if (connectDB() == false) {
            return false;
        }

        //if (request.getParameter("name").isEmpty()) {
        //return false;
        //}
        return true;
    }

    public boolean connectDB() throws ClassNotFoundException, SQLException {

        Class.forName("com.mysql.jdbc.Driver");
        String url = "jdbc:mysql://localhost:3306/moviedb";
        String username = "java";
        String password = "12345";
        boolean result = true;

        try {
            conn = (Connection) DriverManager.getConnection(url, username, password);
            result = true;
        } catch (SQLException ex) {
            result = false;
        }
        return result;
    }

    //Get recommendation result based on movie and actor
    //Type and Actor should based on the current user's favor
    public ResultSet getRecommendation(String type, String actor) throws SQLException {

        Statement stmt = null;
        stmt = conn.createStatement();
        String sql = "SELECT Name, Type, Rating, DistrFee FROM Movie";
        ResultSet rs = stmt.executeQuery(sql);

        return rs;
    }

    // query based on the name of the movie
    public ResultSet queryMovie(String name) {

        try {
            PreparedStatement stmt = null;
            stmt = conn.prepareStatement("SELECT Name, Type, Rating, DistrFee FROM Movie WHERE Name LIKE ?");
            stmt.setString(1, "%" + name + "%");
            ResultSet rs = stmt.executeQuery();
            return rs;

        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    // query based on the type of movie.
    public ResultSet queryMovieByType(String name) {

        try {
            PreparedStatement stmt = null;
            stmt = conn.prepareStatement("SELECT Name, Type, Rating, DistrFee FROM Movie WHERE Type LIKE ?");
            stmt.setString(1, "%" + name + "%");
            ResultSet rs = stmt.executeQuery();
            return rs;

        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public ResultSet queryMovieByActor(String name) {

        try {
            PreparedStatement stmt = null;
            stmt = conn.prepareStatement("SELECT Movie.Name, Movie.Type, Movie.Rating, Movie.DistrFee FROM Movie, Actor, AppearedIn"
                    + " WHERE AppearedIn.MovieId = Movie.Id"
                    + " AND AppearedIn.ActorId = Actor.Id"
                    + " AND Actor.Name LIKE ?");
            stmt.setString(1, "%" + name + "%");
            ResultSet rs = stmt.executeQuery();
            return rs;

        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public ResultSet queryAllMovie() throws SQLException {

        Statement stmt = null;
        stmt = conn.createStatement();
        String sql = "SELECT Name, Type, Rating, DistrFee, NumCopies FROM Movie";
        ResultSet rs = stmt.executeQuery(sql);

        return rs;

    }

    public ResultSet queryUserSuggestedMovies(String username) throws SQLException {
        try {
            int accId = getAccId(username);
            PreparedStatement stmt = null;
            stmt = conn.prepareStatement("SELECT DISTINCT "
                    + "    moviedb.movie.Name, moviedb.movie.Type, moviedb.movie.Rating, moviedb.movie.DistrFee "
                    + "FROM "
                    + "    moviedb.movie "
                    + "WHERE "
                    + "    movie.Type IN (SELECT  "
                    + "            MAX(moviedb.movie.Type) "
                    + "        FROM "
                    + "            moviedb.rental, "
                    + "            moviedb.order, "
                    + "            moviedb.movie "
                    + "        WHERE "
                    + "            moviedb.rental.AccountId = ? "
                    + "                AND moviedb.rental.OrderId = moviedb.order.Id "
                    + "                AND rental.MovieId = moviedb.movie.Id) "
                    + "        AND moviedb.movie.Id NOT IN (SELECT  "
                    + "            moviedb.movie.Id "
                    + "        FROM "
                    + "            moviedb.movie, "
                    + "            moviedb.rental "
                    + "        WHERE "
                    + "            moviedb.movie.Id = rental.MovieId "
                    + "                AND rental.AccountId = ?)");
            stmt.setInt(1, accId);
            stmt.setInt(2, accId);
            ResultSet rs = stmt.executeQuery();
            return rs;
        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    // close the connection to the DB
    public void close() {
        try {
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // return the list of all employee
    public ResultSet queryAllEmployees() throws SQLException {

        Statement stmt = null;
        stmt = conn.createStatement();
        String sql = "SELECT Id, SSN, StartDate, HourlyRate FROM Employee";
        ResultSet rs = stmt.executeQuery(sql);
        return rs;
    }

    public ResultSet queryAllCustomers() throws SQLException {

        Statement stmt = null;
        stmt = conn.createStatement();
        String sql = "SELECT Id, Email, Rating, CreditCardNumber FROM Customer";
        ResultSet rs = stmt.executeQuery(sql);
        return rs;
    }

    public boolean deleteMovie(String moiveName) {

        try {
            PreparedStatement stmt = null;
            stmt = conn.prepareStatement("DELETE FROM Movie WHERE Name = ?");
            stmt.setString(1, moiveName);
            stmt.executeUpdate();
            return true;

        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public boolean deleteEmployee(String id) {
        try {
            PreparedStatement stmt = null;
            stmt = conn.prepareStatement("DELETE FROM Employee WHERE Id = ?");
            stmt.setString(1, id);
            stmt.executeUpdate();
            return true;

        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    /*
     * Add a customer information
     * Add information in Location(if not exist), Person, Customer, and Account table
     * TESTED
     */
    public boolean addCustomer(String zp, String city, String state, String ssn, String lastName, String firstName,
            String address, String telephone, String email, String creditCardNumber,
            String dO, String accountType, String username, String password) {
        try {
            //parse variables from String
            int zipCode = Integer.parseInt(zp);

            //int rating = Integer.parseInt(RT);
            java.util.Date gg = new SimpleDateFormat("yyyy-MM-dd").parse(dO);
            Date dateOpened = new Date(gg.getTime());
            //Adding new zipcodes
            PreparedStatement stmt = null;
            stmt = conn.prepareStatement("INSERT IGNORE INTO Location(ZipCode, City, State) VALUES (?, ?, ?)");
            stmt.setInt(1, zipCode);
            stmt.setString(2, city);
            stmt.setString(3, state);
            stmt.executeUpdate();

            stmt = conn.prepareStatement("INSERT INTO Person(SSN, LastName, FirstName, Address, ZipCode, Telephone)"
                    + " VALUES(?, ?, ?, ?, ?, ?)");
            stmt.setString(1, ssn);
            stmt.setString(2, lastName);
            stmt.setString(3, firstName);
            stmt.setString(4, address);
            stmt.setInt(5, zipCode);
            stmt.setString(6, telephone);
            stmt.executeUpdate();

            stmt = conn.prepareStatement("INSERT INTO Customer(Id, Email, Rating, CreditCardNumber) VALUES(?, ?, ?, ?)");
            stmt.setString(1, ssn);
            stmt.setString(2, email);
            stmt.setInt(3, 0);
            stmt.setString(4, creditCardNumber);
            stmt.executeUpdate();

            stmt = conn.prepareStatement("SELECT MAX(Id) FROM Account");
            ResultSet rs = stmt.executeQuery();
            int Id = 1;
            while (rs.next()) {
                Id = rs.getInt(1);
            }
            Id = Id + 1;
            stmt = conn.prepareStatement("INSERT INTO Account(Id, DateOpened, Type, Customer,username,password) VALUES (?, ?, ?, ?,?,?)");
            stmt.setInt(1, Id);
            stmt.setDate(2, dateOpened);
            stmt.setString(3, accountType);
            stmt.setString(4, ssn);
            stmt.setString(5, username);
            stmt.setString(6, password);
            stmt.executeUpdate();

            return true;
        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (ParseException ex2) {
            return false;
        }

    }

    /*
     * Add Movie with actors in it
     * Takes in the info of movie and actors, actors info are packed inside of array. 
     * Ex:
       t.addMovie("FIGHTTTT", "Drama", "0", "100.00", "5", "JOE,gag,123,as","2,2,2,2","M,F,M,F");
     * TESTED
     */
    public boolean addMovie(String Name, String Type, String Rt, String DistrFee, String NC, String AN, String AA, String AG) {
        try {
            PreparedStatement stmt = null;

            //add Movie
            stmt = conn.prepareStatement("SELECT MAX(Id) FROM Movie");
            ResultSet rs = stmt.executeQuery();
            int Id = 1;
            while (rs.next()) {
                Id = rs.getInt(1);
            }
            Id = Id + 1;

            int Rating = Integer.parseInt(Rt);
            int NumCopies = Integer.parseInt(NC);

            //add information of movie
            stmt = conn.prepareStatement("INSERT INTO Movie(Id, Name, Type, Rating, DistrFee, NumCopies) VALUES (?,?,?,?,?,?)");
            stmt.setInt(1, Id);
            stmt.setString(2, Name);
            stmt.setString(3, Type);
            stmt.setInt(4, Rating);

            BigDecimal fee = new BigDecimal(DistrFee);
            stmt.setBigDecimal(5, fee);

            stmt.setInt(6, NumCopies);

            stmt.executeUpdate();

            //add information of Actor in movie and AppearedIn
            String[] ActorName = AN.split(",");
            String[] ActorAge = AA.split(",");
            String[] ActorGender = AG.split(",");
            int numOfActor = ActorName.length;
            if (ActorName != null && ActorAge != null && ActorGender != null) {
                for (int i = 0; i < numOfActor; i++) {
                    //update actor
                    stmt = conn.prepareStatement("SELECT MAX(Id) FROM Actor");
                    rs = stmt.executeQuery();
                    int ActorId = 1;
                    while (rs.next()) {
                        ActorId = rs.getInt(1);
                    }
                    ActorId = ActorId + 1;

                    int age = Integer.parseInt(ActorAge[i].replaceAll("\\s", ""));

                    stmt = conn.prepareStatement("INSERT INTO Actor(Id, Name, Age, `M/F`) VALUES (?,?,?,?)");
                    stmt.setInt(1, ActorId);
                    stmt.setString(2, ActorName[i]);
                    stmt.setInt(3, age);
                    stmt.setString(4, ActorGender[i].replaceAll("\\s", ""));

                    stmt.executeUpdate();
                    //update appearedIn
                    stmt = conn.prepareStatement("INSERT INTO AppearedIn(ActorId, MovieId, Rating) VALUES (?,?,?)");
                    stmt.setInt(1, ActorId);
                    stmt.setInt(2, Id);
                    stmt.setInt(3, 0);

                    stmt.executeUpdate();
                }
            }
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    /* 
     * Add an employee with all information
     * TESTED
     */
    public boolean addEmployee(String SSN, String LastName, String FirstName, String Address, String city, String state, String Zp, String Telephone,
            String Sd, String Hr, String username, String password, String isM) {

        try {
            PreparedStatement stmt = null;

            int Zipcode = Integer.parseInt(Zp);
            int HourlyRate = Integer.parseInt(Hr);
            int isManager = Integer.parseInt(isM);

            java.util.Date gg = new SimpleDateFormat("yyyy-MM-dd").parse(Sd);
            Date StartDate = new Date(gg.getTime());
            //update address
            stmt = conn.prepareStatement("INSERT IGNORE INTO Location(ZipCode, City, State) VALUES (?, ?, ?)");
            stmt.setInt(1, Zipcode);
            stmt.setString(2, city);
            stmt.setString(3, state);
            stmt.executeUpdate();
            //update person
            stmt = conn.prepareStatement("INSERT INTO Person(SSN, LastName, FirstName, Address, Zipcode, Telephone) VALUES (?,?,?,?,?,?)");
            stmt.setString(1, SSN);
            stmt.setString(2, LastName);
            stmt.setString(3, FirstName);
            stmt.setString(4, Address);
            stmt.setInt(5, Zipcode);
            stmt.setString(6, Telephone);
            stmt.executeUpdate();

            //get the newest ID in employee
            stmt = conn.prepareStatement("SELECT MAX(Id) FROM Employee");
            ResultSet rs = stmt.executeQuery();
            int Id = 1;
            while (rs.next()) {
                Id = rs.getInt(1);
            }
            Id = Id + 1;
            //update employee
            stmt = conn.prepareStatement("INSERT INTO Employee(Id, SSN, StartDate, HourlyRate,username,password,isManager) VALUES (?,?,?,?,?,?,?)");
            stmt.setInt(1, Id);
            stmt.setString(2, SSN);
            stmt.setDate(3, StartDate);
            stmt.setInt(4, HourlyRate);
            stmt.setString(5, username);
            stmt.setString(6, password);
            stmt.setInt(7, isManager);
            stmt.executeUpdate();

            return true;
        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (ParseException ex2) {
            return false;
        }
    }


    /*
     * Edit information of an employee
     * EX;t.editEmployee("3","442-55-6666","gggA","as","1234","Heaven","Sky","0","987-654-3211","2009-10-10","10","employee3","employee","0");
     * TESTED
     */
    public boolean editEmployee(String Id, String SSN, String LastName, String FirstName, String Address, String city, String state, String Zipcode, String Telephone,
            String StartDate, String HourlyRate, String username, String password, String isM) {
        //String SSN, String LastName, String FirstName, String Address, String city, String state, String Zp, String Telephone, 
        //String Sd, String Hr,String username, String password, String isM
        try {
            PreparedStatement stmt = null;
            //check if the employee exist
            int id = Integer.valueOf(Id);
            stmt = conn.prepareStatement("SELECT * FROM Employee WHERE Id = ?");
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            //this employe id exist?
            if (rs.next()) {
                int zc = Integer.parseInt(Zipcode);

                java.util.Date gg = new SimpleDateFormat("yyyy-MM-dd").parse("2017-04-30");
                Date sd = new Date(gg.getTime());
                int hr = Integer.parseInt(HourlyRate);

                //add new zipcode if didn't exist
                stmt = conn.prepareStatement("INSERT IGNORE INTO Location(ZipCode, City, State) VALUES (?, ?, ?)");
                stmt.setInt(1, zc);
                stmt.setString(2, city);
                stmt.setString(3, state);
                stmt.executeUpdate();

                stmt = conn.prepareStatement("UPDATE Person, Employee SET LastName = ?, FirstName = ?, Address = ?, zipcode = ?, telephone = ?, Person.SSN = ? WHERE Person.SSN = Employee.SSN AND Employee.Id = ?");
                stmt.setString(1, LastName);
                stmt.setString(2, FirstName);
                stmt.setString(3, Address);
                stmt.setInt(4, zc);
                stmt.setString(5, Telephone);
                stmt.setString(6, SSN);
                stmt.setString(7, Id);
                stmt.executeUpdate();

                int iM = Integer.parseInt(isM);
                //update employee table
                stmt = conn.prepareStatement("UPDATE Employee SET StartDate = ?, HourlyRate = ?, username = ?, password = ?, isManager = ?, SSN = ? WHERE Id = ?");
                stmt.setDate(1, sd);
                stmt.setInt(2, hr);
                stmt.setString(3, username);
                stmt.setString(4, password);
                stmt.setInt(5, iM);
                stmt.setString(6, SSN);
                stmt.setInt(7, id);
                stmt.executeUpdate();

                return true;
            }
            return false;
        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (ParseException e2) {
            return false;
        }
    }

    public boolean recordOrder(Date orderTime, Date orderReturnDate, int accountId, int rentalCusRepId, int rentalOrderId, String rentalMovieId) {
        try {
            PreparedStatement stmt = null;
            stmt = conn.prepareStatement("SELECT MAX(Id) FROM Order");
            ResultSet rs = stmt.executeQuery();
            int Id = ((Number) rs.getObject(1)).intValue() + 1;

            stmt = conn.prepareStatement("INSERT INTO Order(Id, DateTime,ReturnDate) VALUES (?,?,?)");
            stmt.setInt(1, Id);
            stmt.setDate(2, orderTime);
            stmt.setDate(3, orderReturnDate);
            stmt.executeUpdate();

            stmt = conn.prepareStatement("INSERT INTO Rental(AccountId,CustRepId,OrderId,MovieId) VALUES (?,?,?,?)");
            stmt.setInt(1, accountId);
            stmt.setInt(2, rentalCusRepId);
            stmt.setInt(3, rentalOrderId);
            stmt.setString(4, rentalMovieId);
            stmt.executeUpdate();

            stmt = conn.prepareStatement("INSERT INTO MovieQ(AccountId, MovieId) VALUES (?,?");
            stmt.setInt(1, accountId);
            stmt.setString(2, rentalMovieId);
            stmt.executeUpdate();

            return true;
        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    /*
     * Delete Customer
     * This method will check if that user exist before deleting, to avoid deletion of employee
     * TESTED
     */
    public boolean deleteCustomer(String SSN) {
        try {
            PreparedStatement stmt = null;
            stmt = conn.prepareStatement("SELECT Id FROM Customer WHERE Id = ?");
            stmt.setString(1, SSN);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                stmt = conn.prepareStatement("DELETE FROM Account WHERE Customer = ?");
                stmt.setString(1, SSN);
                stmt.executeUpdate();

                stmt = conn.prepareStatement("DELETE FROM Customer WHERE Id = ?");
                stmt.setString(1, SSN);
                stmt.executeUpdate();

                stmt = conn.prepareStatement("DELETE FROM Person WHERE SSN = ?");
                stmt.setString(1, SSN);
                stmt.executeUpdate();
            } else {
                return false;
            }
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    /*
     * get all the customer mailing list with Last Name, First Name, Address, and city
     * 6 outputs in total
     * TESTED
     */
    public ResultSet getCustomerMailing() {
        try {
            PreparedStatement stmt = null;
            stmt = conn.prepareStatement(
                    " SELECT "
                    + "Person.LastName, Person.FirstName, Person.Address, Location.City,"
                    + " Location.State, Location.ZipCode "
                    + "FROM "
                    + " Person, Location, Customer "
                    + "WHERE "
                    + "      Customer.Id = Person.SSN AND Person.ZipCode = Location.ZipCode");
            ResultSet rs = stmt.executeQuery();
            return rs;
        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public boolean existingCustomer(String username, String password) {
        try {
            PreparedStatement stmt = null;
            stmt = conn.prepareStatement("SELECT * FROM Account WHERE username = ? AND password = ?");
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next() == false) {
                return false;
            }
            return true;

        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public int existingEmployee(String username, String password) throws SQLException {
        try {
            PreparedStatement stmt = null;
            stmt = conn.prepareStatement("SELECT * FROM employee WHERE username = ? AND password = ?");
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next() == false) {
                return 0;
            } else if (rs.getBoolean("isManager") == true) {
                return 2;
            }
            return 1;
        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
    }

    /*
     * Check whether the username is taken or not
     * Return 1 if username is not taken
     *        -1 if username is taken
     * TESTED
     */
    public int existingUsername(String username) {
        try {
            PreparedStatement stmt = null;
            stmt = conn.prepareStatement("SELECT * FROM employee WHERE username = ?");
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next() == false) {
                stmt = conn.prepareStatement("SELECT * FROM Account WHERE username = ?");
                stmt.setString(1, username);
                rs = stmt.executeQuery();
                if (rs.next() == false) {
                    return 1;
                }
                return -1;
            } else {
                return -1;
            }

        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
    }

    /*
     * edit Customer information
    ex: t.editCustomer("555-55-6555", "HLLO", "MOTO", "SSR", "1", "Heaven2", "Sky", "542-542-1234", "HACK@N.com", "0","123-123-123", "2010-10-10", "unlimited-2","gaga", "taata");
     * TESTED ; need discussion
     */
    public boolean editCustomer(String SSN, String lastName, String firstName,
            String address, String city, String state, String zp, String telephone, String email, String RT, String creditCardNumber,
            String accountType, String username, String password) {
        try {
            PreparedStatement stmt = null;
            //check if the customer exist         

            int zipCode = Integer.parseInt(zp);
            int rating = Integer.parseInt(RT);

            //update location if it's new  
            stmt = conn.prepareStatement("INSERT IGNORE INTO Location(ZipCode, City, State) VALUES (?, ?, ?)");
            stmt.setInt(1, zipCode);
            stmt.setString(2, city);
            stmt.setString(3, state);
            stmt.executeUpdate();
            //update the person 
//                stmt = conn.prepareStatement("UPDATE Person SET LastName = ?, FirstName = ?, Address = ?, ZipCode = ?, Telephone = ? WHERE SSN = ?");
//                stmt.setString(1, lastName);
//                stmt.setString(2, firstName);
//                stmt.setString(3, address);
//                stmt.setInt(4, zipCode);
//                stmt.setString(5, telephone);
//                stmt.setString(6, SSN);
//                stmt.executeUpdate();

            //update customer
            stmt = conn.prepareStatement("UPDATE Customer SET Email = ?, Rating = ?, CreditCardNumber = ? WHERE Id = ?");
            stmt.setString(1, email);
            stmt.setInt(2, rating);
            stmt.setString(3, creditCardNumber);
            stmt.setString(4, SSN);
            stmt.executeUpdate();

            //update
            stmt = conn.prepareStatement("UPDATE Account Set Type = ?, username = ?, password = ? WHERE Customer = ?");

            stmt.setString(1, accountType);
            stmt.setString(2, username);
            stmt.setString(3, password);
            stmt.setString(4, SSN);
            stmt.executeUpdate();
            return true;

        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    /*
     * Add Actor
     * TESTED
     */
    public boolean addActor(String Name, String Age, String mf) {
        try {
            PreparedStatement stmt = null;
            //split actors attributes
            String[] aName = Name.split(",");
            String[] aAge = Age.split(",");
            String[] amf = mf.split(",");
            if (aName != null && aAge != null && amf != null) {
                for (int i = 0; i < aName.length; i++) {
                    //add actors          
                    stmt = conn.prepareStatement("SELECT MAX(Id) FROM Actor");
                    ResultSet rs = stmt.executeQuery();
                    int ActorId = 1;
                    while (rs.next()) {
                        ActorId = rs.getInt(1);
                    }
                    ActorId = ActorId + 1;

                    int age = Integer.parseInt(aAge[i]);

                    stmt = conn.prepareStatement("INSERT INTO Actor(Id, Name, Age, `M/F`) VALUES (?,?,?,?)");
                    stmt.setInt(1, ActorId);
                    stmt.setString(2, aName[i]);
                    stmt.setInt(3, age);
                    stmt.setString(4, amf[i]);

                    stmt.executeUpdate();
                }
            }
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    /*
     * Edit Actor
     * TESTED
     */
    public boolean editActor(String Id, String Name, String Age, String mf) {
        try {
            PreparedStatement stmt = null;
            //split actors attributes
            stmt = conn.prepareStatement("UPDATE Actor SET Name = ?, Age = ?, `M/F` = ? WHERE Id = ?");
            stmt.setString(1, Name);
            stmt.setInt(2, Integer.parseInt(Age));
            stmt.setString(3, mf);
            stmt.setString(4, Id);
            stmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    /*
     * Delete Actor
     * TESTED
     */
    public boolean deleteActor(String Id) {
        try {
            PreparedStatement stmt = null;
            //split actors attributes
            stmt = conn.prepareStatement("DELETE FROM AppearedIn WHERE ActorId = ?");
            stmt.setInt(1, Integer.parseInt(Id));
            stmt.executeUpdate();
            stmt = conn.prepareStatement("DELETE FROM Actor WHERE Id = ?");
            stmt.setInt(1, Integer.parseInt(Id));
            stmt.executeUpdate();

            return true;
        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    /*
     * Get a list of most actively rented movies
     * TESTED
     */
    public ResultSet queryMovieMostRented() {
        try {
            PreparedStatement stmt = null;
            stmt = conn.prepareStatement("SELECT m.Id, m.Name, COUNT(r.MovieId) AS count FROM Rental r, Movie m WHERE r.MovieId = m.id GROUP BY r.MovieId DESC;");

            ResultSet rs = stmt.executeQuery();
            return rs;

        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /*
     * Get a list of most active customer based on amount of movie rented
     * TESTED
     */
    public ResultSet queryCustomerMostActive() {
        try {
            PreparedStatement stmt = null;
            stmt = conn.prepareStatement("SELECT r.AccountId, p.LastName, p.FirstName, COUNT(AccountId) As count FROM Rental r, Customer c, Person p, Account a WHERE r.AccountId = a.id AND p.SSN = c.Id AND a.Customer = c.Id GROUP BY r.AccountId DESC;");

            ResultSet rs = stmt.executeQuery();
            return rs;

        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public int getAccId(String username) {
        try {
            PreparedStatement stmt = null;
            stmt = conn.prepareStatement("SELECT Id from Account WHERE username = ?");
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("Id");
            }
            return -1;
        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
    }

    public String getCustId(String username) {
        try {
            PreparedStatement stmt = null;
            stmt = conn.prepareStatement("SELECT Customer.Id FROM Account, Customer WHERE "
                    + "Account.Customer = Customer.Id AND Account.username = ?");
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("Id");
            }
            return null;
        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public ResultSet queryRatableMovies(String username) {
        try {
            int accId = getAccId(username);
            String custId = getCustId(username);
            PreparedStatement stmt = null;
            stmt = conn.prepareStatement("SELECT \n"
                    + "    moviedb.movie.Id, moviedb.movie.Name\n"
                    + "FROM\n"
                    + "    moviedb.movie\n"
                    + "WHERE\n"
                    + "    moviedb.movie.Id IN (SELECT \n"
                    + "            moviedb.rental.MovieId\n"
                    + "        FROM\n"
                    + "            moviedb.rental,\n"
                    + "            moviedb.order\n"
                    + "        WHERE\n"
                    + "            moviedb.rental.OrderId = moviedb.order.Id\n"
                    + "                AND moviedb.rental.AccountId = ?\n"
                    + "                AND moviedb.order.ReturnDate IS NOT NULL)\n"
                    + "        AND moviedb.movie.Id NOT IN (SELECT \n"
                    + "            moviedb.rating.MovieId\n"
                    + "        FROM\n"
                    + "            moviedb.rating\n"
                    + "        WHERE\n"
                    + "            moviedb.rating.CustomerId = ?);");
            stmt.setInt(1, accId);
            stmt.setString(2, custId);
            ResultSet rs = stmt.executeQuery();
            return rs;
        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public boolean addRating(String MovieId, String username, String rating) {
        try {
            int movId = Integer.parseInt(MovieId);
            int custId = getAccId(username);
            int rat = Integer.parseInt(rating);
            PreparedStatement stmt = null;
            stmt = conn.prepareStatement("INSERT INTO Rating(MovieId, CustomerId, Rating) VALUES (?, ?, ?)");
            stmt.setInt(1, movId);
            stmt.setInt(2, custId);
            stmt.setInt(3, rat);
            stmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public boolean updateMovieRatings(String MovieId) {
        try {
            int movId = Integer.parseInt(MovieId);
            PreparedStatement stmt = null;
            stmt = conn.prepareStatement("UPDATE moviedb.movie,\n"
                    + "    moviedb.rating \n"
                    + "SET \n"
                    + "    moviedb.movie.Rating = (SELECT \n"
                    + "            AVG(moviedb.rating.Rating)\n"
                    + "        FROM\n"
                    + "            moviedb.rating\n"
                    + "        WHERE\n"
                    + "            moviedb.rating.MovieId = ?);");
            stmt.setInt(1, movId);
            stmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public ResultSet queryOrderHistory(String username) {
        try {
            int custId = getAccId(username);
            PreparedStatement stmt = null;
            stmt = conn.prepareStatement("SELECT \n"
                    + "    moviedb.order.Id,\n"
                    + "    moviedb.order.DateTime,\n"
                    + "    moviedb.order.ReturnDate\n"
                    + "FROM\n"
                    + "    moviedb.rental,\n"
                    + "    moviedb.order\n"
                    + "WHERE\n"
                    + "    moviedb.rental.AccountId = ?\n"
                    + "        AND moviedb.rental.OrderId = moviedb.order.Id;");
            stmt.setInt(1, custId);
            ResultSet rs = stmt.executeQuery();
            return rs;

        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public ResultSet queryCurrentlyHeldMovies(String username) {
        try {
            int accId = getAccId(username);
            PreparedStatement stmt = null;
            stmt = conn.prepareStatement("SELECT \n"
                    + "    moviedb.movie.Name, moviedb.movie.Type, moviedb.movie.Rating, moviedb.rental.OrderId\n"
                    + "FROM\n"
                    + "    moviedb.movie,\n"
                    + "    moviedb.rental\n"
                    + "WHERE\n"
                    + "    moviedb.rental.AccountId = ?\n"
                    + "        AND moviedb.rental.MovieId = moviedb.movie.Id\n"
                    + "        AND moviedb.rental.OrderId IN (SELECT \n"
                    + "            moviedb.order.Id\n"
                    + "        FROM\n"
                    + "            moviedb.order\n"
                    + "        WHERE\n"
                    + "            moviedb.order.ReturnDate IS NULL);");
            stmt.setInt(1, accId);
            ResultSet rs = stmt.executeQuery();
            return rs;

        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public ResultSet queryMovieQueue(String username) {
        try {
            int accId = getAccId(username);
            PreparedStatement stmt = null;
            stmt = conn.prepareStatement("SELECT \n"
                    + "    moviedb.movie.Name\n"
                    + "FROM\n"
                    + "    moviedb.movieq,\n"
                    + "    moviedb.movie\n"
                    + "WHERE\n"
                    + "    moviedb.movieq.MovieId = moviedb.movie.Id\n"
                    + "        AND moviedb.movieq.AccountId = ?;");
            stmt.setInt(1, accId);
            ResultSet rs = stmt.executeQuery();
            return rs;

        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public ResultSet queryAccountSettings(String username) {
        try {
            int accId = getAccId(username);
            PreparedStatement stmt = null;
            stmt = conn.prepareStatement("SELECT \n"
                    + "    moviedb.account.*, moviedb.person.*, moviedb.customer.*\n"
                    + "FROM\n"
                    + "    moviedb.account,\n"
                    + "    moviedb.customer,\n"
                    + "    moviedb.person\n"
                    + "WHERE\n"
                    + "    moviedb.account.Id = ? AND\n"
                    + "    moviedb.account.Customer = moviedb.customer.Id AND\n"
                    + "    moviedb.customer.Id = moviedb.person.SSN;");
            stmt.setInt(1, accId);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            return rs;

        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public ResultSet queryBestSellers() {
        try {
            PreparedStatement stmt = null;
            stmt = conn.prepareStatement("SELECT \n"
                    + "    moviedb.movie.Name, moviedb.movie.Type, moviedb.movie.Rating, moviedb.movie.DistrFee\n"
                    + "FROM\n"
                    + "    moviedb.movie,\n"
                    + "    moviedb.rental\n"
                    + "WHERE\n"
                    + "    moviedb.movie.Id = moviedb.rental.MovieId\n"
                    + "GROUP BY moviedb.movie.Name\n"
                    + "ORDER BY COUNT(moviedb.rental.MovieId) DESC;");
            ResultSet rs = stmt.executeQuery();
            return rs;

        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /*
     * Get the Customer Representative with the most oversaw transaction;
     * TESTED
     */
    public ResultSet queryCustRepOversawTrans() {
        try {
            PreparedStatement stmt = null;
            stmt = conn.prepareStatement("SELECT r.CustRepId, p.LastName, p.FirstName,COUNT(r.MovieId) AS count FROM moviedb.Rental r, moviedb.Employee e, moviedb.Person p WHERE e.Id = r.CustRepId And p.SSN = e.SSN GROUP BY r.CustRepId ORDER BY COUNT(r.MovieId) DESC;");

            ResultSet rs = stmt.executeQuery();
            return rs;

        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /*
     * Get the monthly Sales Report
     * TESTED 
     */
    public double queryMonthlySalesReport(String month, String year) {
        try {
            String Startdates = year.replaceAll("\\s+", "") + "-" + month.replaceAll("\\s+", "") + "-" + "01";
            String EndDates = year.replaceAll("\\s+", "") + "-" + month.replaceAll("\\s+", "") + "-";
            switch (Integer.parseInt(month.replaceAll("\\s+", ""))) {
                case 1:
                case 3:
                case 5:
                case 7:
                case 8:
                case 10:
                case 12:
                    EndDates += "31";
                    break;
                case 2:
                    boolean g = true;
                    if (Integer.parseInt(year) % 4 == 0) {
                        if (Integer.parseInt(year) % 100 == 0) {
                            if (Integer.parseInt(year) % 400 == 0) {
                                EndDates += "29";
                                g = false;
                            }
                        }
                    }
                    if (g) {
                        EndDates += "28";
                    }
                    break;
                case 4:
                case 6:
                case 9:
                case 11:
                    EndDates += "30";
                    break;
            }
            PreparedStatement stmt = null;
            stmt = conn.prepareStatement("SELECT SUM(DistrFee) FROM `Order` o, Movie m, Rental r WHERE o.DateTime BETWEEN DATE(?) AND DATE(?) AND r.OrderId = o.Id AND m.Id = r.MovieId;");
            stmt.setString(1, Startdates);
            stmt.setString(2, EndDates);

            ResultSet rs = stmt.executeQuery();
            double ans2 = -1;
            if (rs.next()) {
                String ans = rs.getString(1);
                if (ans == null) {
                    ans2 = 0;
                } else {
                    ans2 = Double.parseDouble(ans.replaceAll("\\s", ""));
                }
            }
            return ans2;

        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
    }

    public boolean checkIfAvailable(String movieId) {
        try {
            PreparedStatement stmt = null;
            stmt = conn.prepareStatement("SELECT movie.NumCopies FROM movie WHERE movie.Id = ?;");
            stmt.setString(1, movieId);
            ResultSet rs = stmt.executeQuery();
            int numCopies = -1;
            if (rs.next()) {
                numCopies = rs.getInt("NumCopies");
            }
            if (numCopies > 0) {
                return true;
            }
            return false;
        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public int addRental(String username, String movieName) {
        try {
            int ordId = getNumberOfOrders() + 1;
            int accId = getAccId(username);
            String movieId = getMovieId(movieName);
            int cusRepId = getNumberOfEmployees();
            if (!checkIfAvailable(movieId)) {
                return 2;
            }
            PreparedStatement stmt = null;
            stmt = conn.prepareStatement("INSERT INTO moviedb.order(Id, DateTime, ReturnDate) VALUES (?, NOW(), NULL)");
            stmt.setInt(1, ordId);
            stmt.executeUpdate();
            stmt = conn.prepareStatement("INSERT INTO moviedb.rental(AccountId, CustRepId, OrderId, MovieId) VALUES (?, ?, ?, ?)");
            stmt.setInt(1, accId);
            stmt.setInt(2, cusRepId);
            stmt.setInt(3, ordId);
            stmt.setString(4, movieId);
            stmt.executeUpdate();
            stmt = conn.prepareStatement("UPDATE movie SET NumCopies = NumCopies - 1 WHERE movie.Id = ?;");
            stmt.setString(1, movieId);
            stmt.executeUpdate();
            return 1;
        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
            return 3;
        }
    }

    public String getMovieId(String movieName) {
        try {
            PreparedStatement stmt = null;
            stmt = conn.prepareStatement("SELECT Movie.Id FROM Movie WHERE Movie.Name = ?");
            stmt.setString(1, movieName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("Movie.Id");
            }
            return null;
        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public int getNumberOfEmployees() {
        try {
            PreparedStatement stmt = null;
            stmt = conn.prepareStatement("SELECT COUNT(*) FROM moviedb.employee");
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return (int) (Math.random() * rs.getInt("COUNT(*)") + 1);
            }
            return -1;
        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
    }

    public int getNumberOfOrders() {
        try {
            PreparedStatement stmt = null;
            stmt = conn.prepareStatement("SELECT COUNT(*) FROM moviedb.order");
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("COUNT(*)");
            }
            return -1;
        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
    }

    public boolean addReturnRental(String movieName, String orderId) {
        try {
            PreparedStatement stmt = null;
            String movieId = getMovieId(movieName);
            int ordId = Integer.parseInt(orderId);
            stmt = conn.prepareStatement("UPDATE moviedb.order SET ReturnDate = CURDATE() WHERE moviedb.order.Id = ?;");
            stmt.setInt(1, ordId);
            stmt.executeUpdate();
            stmt = conn.prepareStatement("UPDATE movie SET NumCopies = NumCopies + 1 WHERE movie.Id = ?;");
            stmt.setString(1, movieId);
            stmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public ResultSet getEmployeeForEdit(String Id) {
        try {
            PreparedStatement stmt = null;
            stmt = conn.prepareStatement("SELECT Employee.Id, Person.SSN, Person.LastName, Person.FirstName, Person.Address, Location.City, Location.State, Person.ZipCode, Person.Telephone, Employee.StartDate, Employee.HourlyRate, Employee.username, Employee.password, Employee.isManager\n"
                    + "FROM moviedb.Person, moviedb.Location, moviedb.Employee\n"
                    + "WHERE Employee.Id = ? AND Person.SSN = Employee.SSN AND Person.Zipcode = Location.Zipcode\n"
                    + "ORDER BY Person.SSN");
            stmt.setString(1, Id);
            ResultSet rs = stmt.executeQuery();
            return rs;
        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public boolean editMovie(String Id, String Name, String Type, String Rt, String DistrFee, String NC, String AN, String AA, String AG) {
        try {
            PreparedStatement stmt = null;
            stmt = conn.prepareStatement("UPDATE `moviedb`.Movie SET Movie.Name = ?, Movie.Type = ?, Movie.Rating = ?, Movie.DistrFee = ?, Movie.NumCopies = ? WHERE Movie.Id = ?");
            int Rating = Integer.parseInt(Rt);
            int NumCopies = Integer.parseInt(NC);

            stmt.setString(1, Name);
            stmt.setString(2, Type);
            stmt.setInt(3, Rating);

            BigDecimal fee = new BigDecimal(DistrFee);
            stmt.setBigDecimal(4, fee);
//
            stmt.setInt(5, NumCopies);
            stmt.setString(6, Id);

            stmt.executeUpdate();

            String[] ActorName = AN.split(",");
            String[] ActorAge = AA.split(",");
            String[] ActorGender = AG.split(",");
            int numOfActor = ActorName.length;
            if (ActorName != null && ActorAge != null && ActorGender != null) {
                for (int i = 0; i < numOfActor; i++) {
                    //update actor

                    int age = Integer.parseInt(ActorAge[i].replaceAll("\\s", ""));

                    stmt = conn.prepareStatement("UPDATE Actor, Movie, AppearedIn SET Actor.Name = ?, Actor.Age = ?, Actor.`M/F` = ? WHERE Movie.Id = ? AND Movie.Id = AppearedIn.movieId AND AppearedIn.actorId = actor.Id ");

                    if (ActorName[i].charAt(0) == ' ') {
                        ActorName[i] = ActorName[i].substring(1);
                    }

                    stmt.setString(1, ActorName[i]);
                    stmt.setInt(2, age);
                    stmt.setString(3, ActorGender[i].replaceAll("\\s", ""));
                    stmt.setString(4, Id);
                    stmt.executeUpdate();
                }
            }
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public String[] getMovieForEdit(String Name) {
        try {
            String ans[] = new String[9];
            //int Id, String name, String Type, int Rating, double distrFee, int NumOfCopies, String ActorName, String ActorAge, String ActorGender
            PreparedStatement stmt = null;
            stmt = conn.prepareStatement("SELECT Movie.Id, Movie.Name, Movie.Type, Movie.Rating, Movie.distrFee, Movie.NumCopies\n"
                    + "FROM `moviedb`.Movie\n"
                    + "WHERE Name LIKE ?");
            stmt.setString(1, "%" + Name + "%");
            ResultSet rs1 = stmt.executeQuery();

            stmt = conn.prepareStatement("SELECT Actor.Name\n"
                    + "FROM Actor, appearedIn, movie\n"
                    + "WHERE Actor.Id = appearedIn.actorId AND appearedIn.movieId = movie.Id AND movie.Name LIKE ?");
            stmt.setString(1, "%" + Name + "%");
            ResultSet rs2 = stmt.executeQuery();

            stmt = conn.prepareStatement("SELECT Actor.Age\n"
                    + "FROM Actor, appearedIn, movie\n"
                    + "WHERE Actor.Id = appearedIn.actorId AND appearedIn.movieId = movie.Id AND movie.Name LIKE ?");
            stmt.setString(1, "%" + Name + "%");
            ResultSet rs3 = stmt.executeQuery();

            stmt = conn.prepareStatement("SELECT Actor.`M/F`\n"
                    + "FROM Actor, appearedIn, movie\n"
                    + "WHERE Actor.Id = appearedIn.actorId AND appearedIn.movieId = movie.Id AND movie.Name LIKE ?");
            stmt.setString(1, "%" + Name + "%");
            ResultSet rs4 = stmt.executeQuery();
            int i = 0;
            if (!rs1.next()) {
                return null;
            }
            int gg = 1;

            ans[i++] = rs1.getString(gg++);
            ans[i++] = rs1.getString(gg++);
            ans[i++] = rs1.getString(gg++);
            ans[i++] = rs1.getString(gg++);
            ans[i++] = rs1.getString(gg++);
            ans[i++] = rs1.getString(gg++);
            //System.out.println(rs1.getString(gg-1));

            //handle movie actor name
            if (rs2 == null) {
                ans[i++] = null;
            } else {
                String gaga = "";
                int itr = 1;
                while (rs2.next()) {
                    gaga += rs2.getString(itr) + ", ";
                }
                if (!gaga.equals("")) {
                    gaga = gaga.substring(0, gaga.length() - 2);
                    ans[i++] = gaga;
                } else {
                    ans[i++] = null;
                }
            }

            if (rs3 == null) {
                ans[i++] = null;
            } else {
                String gaga = "";
                int itr = 1;
                while (rs3.next()) {
                    gaga += rs3.getString(itr) + ", ";
                }
                if (!gaga.equals("")) {
                    gaga = gaga.substring(0, gaga.length() - 2);
                    ans[i++] = gaga;
                } else {
                    ans[i++] = null;
                }
            }
            if (rs4 == null) {
                ans[i++] = null;
            } else {
                String gaga = "";
                int itr = 1;
                while (rs4.next()) {
                    gaga += rs4.getString(itr) + ", ";
                }
                if (!gaga.equals("")) {
                    gaga = gaga.substring(0, gaga.length() - 2);
                    ans[i++] = gaga;
                } else {
                    ans[i++] = null;
                }
            }

            return ans;
        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public String getActors(String movieName) {
        try {
            PreparedStatement stmt = null;
            stmt = conn.prepareStatement("SELECT Actor.Name\n"
                    + "FROM Actor, appearedIn, movie\n"
                    + "WHERE Actor.Id = appearedIn.actorId AND appearedIn.movieId = movie.Id AND movie.Name LIKE ?");
            stmt.setString(1, "%" + movieName + "%");
            ResultSet rs2 = stmt.executeQuery();
            String gaga = "";
            if (rs2 == null) {
                gaga = null;
            } else {

                int itr = 1;
                while (rs2.next()) {
                    gaga += rs2.getString(itr) + ", ";
                }
                if (!gaga.equals("")) {
                    gaga = gaga.substring(0, gaga.length() - 2);

                } else {
                    gaga = null;
                }
            }

//            String movieId = getMovieId(movieName);
//            stmt = conn.prepareStatement("SELECT COUNT(actor.name) \n"
//                    + "FROM actor, appearedin, movie\n"
//                    + "WHERE movie.Id = ? AND movie.Id = appearedIn.MovieId\n"
//                    + "AND appearedIn.ActorId = actor.Id;");
//            stmt.setString(1, movieId);
//            int size = 0;
//            ResultSet rs = stmt.executeQuery();
//            if (rs.next()) {
//                size = rs.getInt("COUNT(actor.name)");
//            }
//            stmt = conn.prepareStatement("SELECT actor.name \n"
//                    + "FROM actor, appearedin, movie\n"
//                    + "WHERE movie.Id = ? AND movie.Id = appearedIn.MovieId\n"
//                    + "AND appearedIn.ActorId = actor.Id;");
//            stmt.setString(1, movieId);
//            rs = stmt.executeQuery();
//            String[] actors = new String[size];
//            int i = 0;
//            while (rs.next()) {
//                actors[i] = rs.getString("actor.name");
//                i++;
//            }
//            return actors;
            return gaga;
        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public ResultSet getAccountForEdit(String username) {
        /*
        String SSN, String lastName, String firstName,
            String address, String zp, String city, String state, String telephone, String email, String RT, String creditCardNumber,
            String dO, String accountType, String username, String password
         */
        try {
            PreparedStatement stmt = null;
            stmt = conn.prepareStatement("SELECT Person.lastname, Person.Firstname, Person.Address, Location.city, Location.State, Person.Zipcode, Person.telephone, Customer.email,  Customer.CreditCardNumber, Account.Type, Account.password\n"
                    + "FROM Person, Customer, Account, Location\n"
                    + "WHERE Person.SSN = Customer.Id AND Account.Customer = Person.SSN AND Location.Zipcode = Person.ZipCode AND Account.username = ? ");
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            return rs;

        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
//    public ResultSet getAccountForEdit(String username) {
//        /*
//        String SSN, String lastName, String firstName,
//            String address, String zp, String city, String state, String telephone, String email, String RT, String creditCardNumber,
//            String dO, String accountType, String username, String password
//         */
//        try {
//            PreparedStatement stmt = null;
//            stmt = conn.prepareStatement("SELECT Person.Lastname, Person.Firstname, Person.Address, Location.City, Location.State, Person.ZipCode, Person.Telephone, Customer.Email,  Customer.CreditCardNumber, Account.Type, Account.password\n"
//                    + "FROM Person, Customer, Account, Location\n"
//                    + "WHERE Person.SSN = Customer.Id AND Account.Customer = Person.SSN AND Location.Zipcode = Person.ZipCode AND Account.username = ? ");
//            stmt.setString(1, username);
//            ResultSet rs = stmt.executeQuery();
//            return rs;
//
//        } catch (SQLException ex) {
//            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
//            return null;
//        }
//    }

    public ResultSet queryMovieRentalbyName(String name) {
        try {
            PreparedStatement stmt = null;
            stmt = conn.prepareStatement("SELECT DISTINCT m.Name, p.LastName, p.FirstName FROM Movie m, Rental r, Person p, Account a WHERE m.id = r.MovieId AND m.Name LIKE ? AND r.AccountId = a.Id AND a.Customer = p.SSN GROUP BY m.Name;");
            stmt.setString(1, "%" + name + "%");

            ResultSet rs = stmt.executeQuery();
            rs.next();
            return rs;

        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public ResultSet queryMovieRentalbyType(String name) {
        try {
            PreparedStatement stmt = null;
            stmt = conn.prepareStatement("SELECT m.Name, m.Type, p.LastName, p.FirstName FROM Movie m, Rental r, Person p, Account a WHERE m.id = r.MovieId AND m.Type LIKE ? AND r.AccountId = a.Id AND a.Customer = p.SSN ;");
            stmt.setString(1, "%" + name + "%");

            ResultSet rs = stmt.executeQuery();
            rs.next();
            return rs;

        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public ResultSet queryMovieRentalbyCustomer(String FirstLast) {
        try {
            PreparedStatement stmt = null;
            String[] names = FirstLast.split(" ");
            String LastName = names[1];
            String FirstName = names[0];
            stmt = conn.prepareStatement("SELECT m.Name, p.LastName, p.FirstName FROM Movie m, Rental r, Person p, Account a WHERE m.id = r.MovieId AND p.LastName = ? AND p.FirstName = ? AND r.AccountId = a.Id AND a.Customer = p.SSN ;");
            stmt.setString(1, LastName);
            stmt.setString(2, FirstName);

            ResultSet rs = stmt.executeQuery();
            rs.next();
            return rs;

        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public ResultSet getCustomerForEdit(String SSN) {
        /*
        String SSN, String lastName, String firstName,
            String address, String zp, String city, String state, String telephone, String email, String RT, String creditCardNumber,
            String dO, String accountType, String username, String password
         */
        try {
            PreparedStatement stmt = null;
            stmt = conn.prepareStatement("SELECT Person.SSN, Person.lastname, Person.Firstname, Person.Address, Location.city, "
                    + "Location.State, Person.Zipcode, Person.telephone, Customer.email, Customer.Rating, Customer.CreditCardNumber, "
                    + "Account.DateOpened, Account.Type, Account.username, Account.password\n"
                    + "FROM Person, Customer, Account, Location\n"
                    + "WHERE Person.SSN = Customer.Id AND Account.Customer = Person.SSN AND Location.Zipcode = Person.ZipCode AND Person.SSN = ? ");
            stmt.setString(1, SSN);
            ResultSet rs = stmt.executeQuery();
            //rs.next();
            return rs;

        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

}
