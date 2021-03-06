<%-- 
    Homepage that diaplay the suggestion list and menu bar
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<link rel="stylesheet" href="css/style.css" type="text/css">
<html>
    <head>
        <script>
            function deleteFalse() {
                alert("He/She is on duty, cannot be fired");
            }

            function deleteTrue() {
                alert("Delete Successfully");
            }
        </script>
    </head>
    <body>

        <div class = "topnav">
            <a class = "HomeButton" onClick="displaymessage()" style="text-decoration:none" href="http://localhost:8080/LightMRE/HomePageServ">Home</a>
            <a class = "HomeButton" onClick="displaymessage()" style="text-decoration:none" href="#">Movies</a>
            <a class = "HomeButton" onClick="forwardToSearch()" style="text-decoration:none" href="http://localhost:8080/LightMRE/ListOfEmployees">Employees</a>
            <a class = "HomeButton" style="text-decoration:none" href="http://localhost:8080/LightMRE/QueryAllCustomers">Customers</a>
            <a class = "HomeButton" onClick="displaymessage()" style="text-decoration:none" href="SalesReport.jsp">Sales Report</a>
            <a class = "HomeButton" onClick="displaymessage()" style="text-decoration:none" href="http://localhost:8080/LightMRE/LogOut">Log Out</a>
        </div>


        <section>
            <!--for demo wrap-->
            <h1 id = "youMayLike">All Employees</h1>
            <div class="tbl-header">
                <table cellpadding="0" cellspacing="0" border="0">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>SSN</th>
                            <th>Start Date</th>
                            <th>Hourly Rate</th>
                            <th><button onclick="location.href = 'TopEmployee'">Who is the top?</button></th>
                            <th><button onclick="location.href = 'AddEmployee.jsp'">Add Employee</button></th>
                        </tr>
                    </thead>
                </table>
            </div>
            <div class="tbl-content">
                <table cellpadding="0" cellspacing="0" border="0" id="mytable">
                    <tbody>
                        <c:forEach items="${employeesList}" var="dList">
                            <tr>
                                <td contenteditable='true'>${dList.ID}</td>
                                <td contenteditable='true'>${dList.SSN}</td>
                                <td contenteditable='true'>${dList.date}</td>
                                <td contenteditable='true'>${dList.hourlyRate}</td>
                                <td><button type="button" onclick="editEmployee(this)">Edit</button></td>
                                <td><button type="button" onclick="deleteEmployee(this)">Delete</button></td>
                            </tr>
                        </c:forEach>

                    </tbody>
                </table>

            </div>


        </section>

        <!-- follow me template -->
        <div class="made-with-love">
            <img src="images/sbuLogoSmall.png" alt="Smiley face" height="208" width="252">
            </br>
            LightMRE
        </div>

        <c:choose>
            <c:when test="${deleteStatus =='false'}">
                <script> deleteFalse()</script> 
                <br />
            </c:when>    
            <c:when test="${deleteStatus =='true'}">
                <script> deleteTrue()</script>
                <br />
            </c:when>
            <c:otherwise>
            </c:otherwise>
        </c:choose>

        <script>
            function displaymessage() {
                window.location = "AboutUs.jsp"
            }

            function forwardToSearch() {
                window.location = "Search.jsp"
            }

            function rentIt() {
                var va = document.getElementById('movieName').value;
                alert(va);
            }


            function editEmployee(element) {

                // getting which col is selected, returned the name
                var name = document.getElementById("mytable").rows[element.parentNode.parentNode.rowIndex].cells[0].innerHTML;
                confirm("Edit " + name + "?");
            }
            function deleteEmployee(element) {

                // getting which col is selected, returned the name
                var id = document.getElementById("mytable").rows[element.parentNode.parentNode.rowIndex].cells[0].innerHTML;
                confirm("Are you sure you want to delete " + id + "?");
                location.href = "ListOfEmployees?EmployeeId=" + id;
            }

            function editEmployee(element) {

                // getting which col is selected, returned the name
                var id = document.getElementById("mytable").rows[element.parentNode.parentNode.rowIndex].cells[0].innerHTML;
                confirm("Are you sure you want to edit " + id + "?");
                location.href = "EditEmployeeServlet?EmployeeId=" + id;
            }
        </script>
    </body>
</html>