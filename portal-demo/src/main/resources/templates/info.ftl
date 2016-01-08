<#import "/spring.ftl" as spring />
<#assign xhtmlCompliant = true in spring>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>${serviceName}: Info</title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css"
          integrity="sha384-1q8mTJOASx8j1Au+a5WDVnPi2lkFfwwEAa8hDDdjZlpLegxhjVME1fgjWPGmkzs7" crossorigin="anonymous"/>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap-theme.min.css"
          integrity="sha384-fLW2N01lMqjakBkx3l/M9EahuwpSfeNvV63J5ezn3uZzapT0u7EYsXMjQV+0En5r" crossorigin="anonymous"/>
</head>
<body>
<div class="container">
    <nav class="navbar navbar-default">
        <div class="container-fluid">
            <div class="navbar-header">
                <a class="navbar-brand" href="#">${serviceName}</a>
            </div>
            <form class="navbar-form navbar-right" action="<@spring.url '/sso/logout' />" method="post">
                <input type="hidden"
                       name="${_csrf.parameterName}"
                       value="${_csrf.token}"/>
                <button type="submit" class="btn btn-default">Logout</button>
            </form>
        </div>
    </nav>
    <p class="lead">Welcome, ${token.name}</p>

    <div class="panel panel-default">
        <div class="panel-heading">User information</div>
        <table class="table">
            <tbody>
            <tr>
                <td>Email</td>
                <td>${token.email}</td>
            </tr>
            <tr>
                <td>Given Name</td>
                <td>${token.givenName}</td>
            </tr>
            <tr>
                <td>Family Name</td>
                <td>${token.familyName}</td>
            </tr>
            <tr>
                <td>Full Name</td>
                <td>${token.name}</td>
            </tr>
            </tbody>
        </table>
    </div>

    <div class="panel panel-default">
        <div class="panel-heading">Custom Claims</div>

        <table class="table">
            <thead>
                <tr>
                    <th>Claim</th>
                    <th>Value</th>
                </tr>
            </thead>
            <tbody>
            <#list claims?keys as claimName>
                <tr>
                    <td>${claimName}</td>
                    <td>${claims[claimName]}</td>
                </tr>
            </#list>
            </tbody>
        </table>
    </div>
</div>
</body>
</html>
