<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="site"/>
    <title>StravaVR</title>
</head>

<body>

<div class="container">
    <h2>${athlete.firstname + " " + athlete.lastname}</h2>
    <hr>
    <div class="list-group">
    <g:each var="activity" in="${activities}">
        <g:link action="viewActivity" id="${activity.id}" class="list-group-item">${activity.name} - ${Math.round(activity.distance / 16.09344)/100} Miles - ${Math.round(activity.total_elevation_gain * 3.28084)} Vertical Feet</g:link>
    </g:each>
    </div>
</div>

</body>
</html>