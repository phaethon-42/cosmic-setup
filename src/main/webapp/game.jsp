<!DOCTYPE html>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Cosmic Encounter</title>

    <c:set var="revealIsSafe" value="true"/>
    <c:forEach items="${game.players}" var="player">
        <c:if test="${empty player.chosenRace}">
            <c:set var="revealIsSafe" value="false"/>
        </c:if>
    </c:forEach>

    <script type="text/javascript">
        function safeToReveal() {
            if (<c:out value="${revealIsSafe}"/>) {
                return true;
            } else {
                return confirm('Not all players have made their choices, are you sure you want to reveal everything? This will make all choices final.')
            }
        }
    </script>
</head>
<body>
    <c:if test="${empty game}">
        <c:redirect url="/game/start"/>
    </c:if>

    <c:choose>
        <c:when test="${empty player}">
            <c:choose>
                <c:when test="${game.revealed}">
                    <div>Sorry, the choices have been revealed. You can't join anymore</div>
                </c:when>
                <c:otherwise>
                    <form:form method="post" modelAttribute="playerInput" action="/game/addPlayer">
                        <span>Enter player name</span>
                        <form:input path="name" type="text" />
                        <form:errors path="name" />
                        <input type="submit" value="Join"/>
                    </form:form>
                </c:otherwise>
            </c:choose>
        </c:when>
        <c:otherwise>
            <span>Welcome <c:out value="${player.name}"/></span><br>
            <span>Choose one of the following races:<br>
            <c:forEach items="${player.races}" var="raceName">
                <div> <c:out value="${raceName}"/> <c:if test="${raceName eq player.chosenRace}"> (CHOSEN)</c:if> </div>
                <c:if test="${not game.revealed}">
                    <form:form method="post" modelAttribute="raceInput" action="/game/chooseRace">
                        <form:input path="name" type="hidden" value="${raceName}" />
                        <form:errors path="name" />
                        <input type="submit" value="Choose"/>
                    </form:form>
                </c:if>
            </c:forEach>

            <form:form method="post" modelAttribute="playerInput" action="/game/quit">
                <form:input path="name" type="hidden" value="${player.name}" />
                <input type="submit" value="Quit game"/>
            </form:form>
        </c:otherwise>
    </c:choose>

    <c:if test="${not empty game}">
        <div>Other players in the game:</div>
        <c:forEach items="${game.players}" var="otherPlayer">
            <div><c:out value="${otherPlayer.name}"/></div>
            <c:if test="${game.revealed}">
                <div><c:out value="${otherPlayer.name}"/>'s choices were</div>
                <c:forEach items="${otherPlayer.races}" var="raceName">
                    <div> <c:out value="${raceName}"/> </div>
                </c:forEach>
                <div>Final choice: <c:out value="${otherPlayer.chosenRace}"/>
            </c:if>
        </c:forEach>
        <c:if test="${not game.revealed}">
            <form:form method="post" action="/game/reveal" onSubmit="return safeToReveal()">
                <input type="submit" value="Reveal choices"/>
            </form:form>
        </c:if>

        <form:form method="post" action="/game/restart">
            <input type="submit" value="Start a new game"/>
        </form:form>
    </c:if>
</body>
</html>