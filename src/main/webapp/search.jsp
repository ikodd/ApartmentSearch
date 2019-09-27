<%--
  Created by IntelliJ IDEA.
  User: ik
  Date: 18.09.2019
  Time: 15:05
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>


<c:forEach var="apartment" items="${apartmentRange}" varStatus="counter">
    <c:choose>
        <c:when test="${counter.count < 2}">
            <c:set var="areaLow" value="${apartment.area}"/>
            <c:set var="numRoomsLow" value="${apartment.numRooms}"/>
            <c:set var="priceLow" value="${apartment.price}"/>
        </c:when>
        <c:otherwise>
            <c:set var="areaTop" value="${apartment.area}"/>
            <c:set var="numRoomsTop" value="${apartment.numRooms}"/>
            <c:set var="priceTop" value="${apartment.price}"/>
        </c:otherwise>
    </c:choose>
</c:forEach>
<c:set var="isDelete" value="${isDelete}"/>

<html>
<head>

    <title>Apartment search</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.0/css/bootstrap.min.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.0/js/bootstrap.min.js"></script>

    </head>
<body>
<div class="container">
<c:if test="${view eq 'searchForm'}">
    <h1>Apartment search</h1>
    <form action="/manage" method="post">
        <input type="hidden" name="command" value="find">
        <table class="table">
            <tr>
                <td rowspan="2">District</td>
                <td colspan="2">Area range</td>
                <td colspan="2">Number of rooms range</td>
                <td colspan="2">Price range</td>
            </tr>
            <tr>
                <td>From</td>
                <td>To</td>
                <td>From</td>
                <td>To</td>
                <td>From</td>
                <td>To</td>
            </tr>
            <tr>
                <td>
                    <select name="districtId">
                        <option value="0">Any</option>
                        <c:forEach var="district" items="${districts}">
                            <option value="${district.id}">${district.name}</option>
                        </c:forEach>
                    </select>
                </td>
                <td><input type="number" name="areaLow" min="${areaLow}" max="${areaTop}" value="${areaLow}" required></td>
                <td><input type="number" name="areaTop" min="${areaLow}" max="${areaTop}" value="${areaTop}" required></td>
                <td><input type="number" name="numRoomsLow" min="${numRoomsLow}" max="${numRoomsTop}" value="${numRoomsLow}" required></td>
                <td><input type="number" name="numRoomsTop" min="${numRoomsLow}" max="${numRoomsTop}" value="${numRoomsTop}" required></td>
                <td><input type="number" name="priceLow" min="${priceLow}" max="${priceTop}" value="${priceLow}" required></td>
                <td><input type="number" name="priceTop" min="${priceLow}" max="${priceTop}" value="${priceTop}"required></td>
            </tr>
        </table><br>
        <input type="submit" value="Search">
    </form><br><br>
    </div>
</c:if>

<c:if test="${view eq 'searchResult'}">
    <div class="container">
    Result of the request:<br>

        <table class="table">
            <tr>
               <td>#</td><td>District</td><td>Address</td><td>Area</td><td>Number of rooms</td><td>Price</td>
            </tr>
    <c:forEach var="apartment" items="${apartments}" varStatus="counter">
        <tr><td>${counter.count}</td>
                <td>
                    <c:forEach var="district" items="${districts}">
                        <c:if test="${district.id eq apartment.districtId}">${district.name}</c:if>
                    </c:forEach>
                </td>
            <td>${apartment.address}</td><td>${apartment.area}</td><td>${apartment.numRooms}</td><td>${apartment.price}</td>
            </tr>
            </c:forEach>
        </table><br>
    </div>
</c:if>

<c:if test="${view eq 'addApartForm'}">
    <div class="container">
<h1>Add apartment</h1>

    <form action="/manage" method="post">
        <input type="hidden" name="command" value="add">
    <table>
        <tr>
            <td>District</td><td>Address</td><td>Area</td><td>Number of rooms</td><td>Price</td>
        </tr>
        <tr><td>
            <select name="districtId">
            <c:forEach var="district" items="${districts}">
                    <option value="${district.id}">${district.name}</option>
            </c:forEach>
            </select></td>
            <td><input type="text" name="address" required></td>
            <td><input type="number" name="area" min="${areaLow}" max="${areaTop}" value="${areaLow}" required></td>
            <td><input type="number" name="numRooms" min="${numRoomsLow}" max="${numRoomsTop}" value="${numRoomsLow}" required></td>
            <td><input type="number" name="price" min="${priceLow}" max="${priceTop}" value="${priceLow}" required></td>
        </tr>
    </table>
        <input type="submit">
    </form>
    </div>
</c:if>

<c:if test="${view eq 'list'}">
    <div class="container">
    <h1>Apartment list</h1>
    <c:if test="${isDelete ne true}"><c:set var="disabled" value="disabled"/></c:if>
 <c:if test="${isDelete eq true}">
     <c:set var="disabled" value=""/>
     <form action="/manage" method="post">
     <input type="hidden" name="command" value="delete">
 </c:if>
    <table class="table">
        <tr>
            <td>#</td><td>Address</td><td>Area</td><td>Number of rooms</td><td>Price</td><td>Selected</td>
        </tr>
    <c:forEach var="apartment" items="${apartments}" varStatus="counter">
        <tr>
            <td>${counter.count}</td><td>${apartment.address}</td>
            <td>${apartment.area}</td><td>${apartment.numRooms}</td>
            <td>${apartment.price}</td><td>
            <input type="checkbox" name="${apartment.id}" ${disabled}>
        </td>
        </tr>
    </c:forEach>
    </table>
     <c:if test="${isDelete eq true}">
         <input type="submit" value="Delete">
         </form>
     </c:if>
    </div>
</c:if>

<c:if test="${view eq 'deleteResult'}">
    <div class="container">
    <h2>All records listed below have been deleted:</h2>
    <table class="table">
        <tr>
            <td>#</td><td>id</td><td>Address</td><td>Status</td>
        </tr>
        <c:forEach var="apartment" items="${apartments}" varStatus="counter">
            <tr>
            <td>${counter.count}</td><td>${apartment.id}</td><td>${apartment.address}</td><td>Deleted</td>
            </tr>
        </c:forEach>
    </table>
</c:if>

<footer>
    <a href="/manage?command=list">Apartment list</a>
    <a href="/manage?command=search_form">Search form</a>
    <a href="/manage?command=add_apartment_form">Add apartment</a>
    <a href="/manage?command=list&delete_form=true">Delete apartment record</a>
</footer>
    </div>
</body>
</html>
