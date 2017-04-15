<%-- 
    Homepage that diaplay the suggestion list and menu bar
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<link rel="stylesheet" href="css/style.css" type="text/css">
<html>
    <head>
    </head>
    <body>

        <form name="submitForm" method="POST" action="http://localhost:8080/LightMRE/ListOfEmployees">
            <div class = "topnav">
                <a class = "HomeButton" onClick="displaymessage()" style="text-decoration:none" href="#">Home</a>
                <a class = "HomeButton" onClick="displaymessage()" style="text-decoration:none" href="#">Movies</a>
                <a class = "HomeButton" onClick="forwardToSearch()" style="text-decoration:none" href="javascript:document.submitForm.submit()">Employees</a>
                <a class = "HomeButton" onClick="displaymessage()" style="text-decoration:none" href="#">About Us</a>
            </div>
            </form>
        <section>
            <!--for demo wrap-->
            <h1 id = "youMayLike">All Movies</h1>
            <div class="tbl-header">
                <table cellpadding="0" cellspacing="0" border="0">
                    <thead>
                        <tr>
                            <th>Movies</th>
                            <th>Type</th>
                            <th>Actor</th>
                            <th>Rating</th>
                            <th>Price</th>
                            <th></th>
                            <th></th>
                        </tr>
                    </thead>
                </table>
            </div>
            <div class="tbl-content">
                <table cellpadding="0" cellspacing="0" border="0" id="mytable">
                    <tbody>
                        <c:forEach items="${allMovie}" var="dList">
                            <tr>
                                <td contenteditable='true' id = "movieName">${dList.name}</td>
                                <td contenteditable='true'>${dList.type}</td>
                                <td contenteditable='true'>${dList.price}</td>
                                <td contenteditable='true'>${dList.rating}</td>
                                <td contenteditable='true'>${dList.price}</td>
                                <td><button type="button" onclick="editMovie(this)">Edit</button></td>
                                <td><button type="button" onclick="deleteMovie(this)">Delete</button></td>
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

        <script>
            function displaymessage() {
                window.location = "AboutUs.jsp"
            }

            function forwardToSearch() {
                window.location = "Search.jsp"
            }

            function editMovie(element) {

                // getting which col is selected, returned the name
                var name = document.getElementById("mytable").rows[element.parentNode.parentNode.rowIndex].cells[0].innerHTML;
                confirm("Edit " + name + "?");
            }
            function deleteMovie(element) {

                // getting which col is selected, returned the name
                var name = document.getElementById("mytable").rows[element.parentNode.parentNode.rowIndex].cells[0].innerHTML;
                confirm("Are you sure you want to remove " + name + "?");
            }


        </script>
    </body>
</html>