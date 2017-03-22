<!DOCTYPE html>
<html>
<head>
    <title>${activity.getName()}</title>

    <asset:javascript src="jquery-2.2.0.min.js"/>
    <asset:javascript src="viewActivity.js"/>
    <script src="https://aframe.io/releases/0.5.0/aframe.min.js" ></script>

</head>

<body>

<a-scene>

    <g:each var="point" in="${points}">
        <a-box id="${point['distance']}" color="${point['speedColor']}" position="${point['x']} ${point['y']} ${point['z']}" scale="0.01 0.01 0.01"></a-box>
    </g:each>

    <a-text value="${activity.getName()}" color="#BBB" position="-0.9 1 -20" scale="3 3 3"></a-text>

</a-scene>

</body>
</html>
