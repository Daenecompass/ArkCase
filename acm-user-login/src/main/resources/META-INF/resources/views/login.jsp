<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>ACM: Armedia Case Management</title>

    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="Author" content="Armedia LLC"/>
    <meta name="viewport" content="width=1024"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1"/>


    <!--<link rel="stylesheet" href="${request.contextPath}/app/resources/styles/blueprint/screen.css" type="text/css" media="screen, projection" />-->
    <!--<link rel="stylesheet" href="${request.contextPath}/app/resources/styles/blueprint/print.css" type="text/css" media="print" />-->
    <link rel="stylesheet" href="<c:url value="/resources/styles/acm/basic.css"/>" type="text/css"
          media="screen, projection"/>
    <link rel="stylesheet" href="<c:url value="/resources/styles/acm/forms.css"/>" type="text/css"
          media="screen, projection"/>
    <link rel="stylesheet" href="<c:url value="/resources/styles/acm/messages.css"/>" type="text/css" media="screen, projection"/>
    <!--[if lt IE 8]>
        <link rel="stylesheet" href="<c:url value="/resources/styles/blueprint/ie.css"/>" type="text/css" media="screen, projection" />
    <![endif]-->
    <ui:insert name="headIncludes"/>
</head>
<body>

    <div class="span-10 login">
        <div class="login-box main-content">
            <c:if test="${not empty param.login_error}">
                <div class="error">
                    Your login attempt was not successful, try again.<br />
                    Reason: ${sessionScope.SPRING_SECURITY_LAST_EXCEPTION.message}
                </div>
            </c:if>

            <div class="section">
                <div class="message info full">Enter your username and password.</div>
                <form name="loginForm" action="<%= request.getContextPath()%>/j_spring_security_check" method="post" class="clearfix">
                    <p>
                        <c:if test="${not empty param.login_error}">
                            <c:set var="username" value="${sessionScope.SPRING_SECURITY_LAST_USERNAME}"/>
                        </c:if>

                        <input type="text" name="j_username" value="${username}" placeholder="Username" class="full"/>
                    </p>
                    <p>
                        <input type="password" name="j_password" placeholder="Password" class="full"/>
                    </p>
                    <p>
                        <button class="button button-gray fr" type="submit">Login</button>
                        <!--<input name="submit" type="submit" value="Login" />-->
                    </p>
                </form>
            </div>
        </div>
    </div>
</body>
</html>
