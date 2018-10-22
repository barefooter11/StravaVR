<!DOCTYPE html>
<html>
<head>
    <title>${activity.getName()}</title>

    <asset:javascript src="jquery-2.2.0.min.js"/>
    <script src="https://aframe.io/releases/0.5.0/aframe.min.js" ></script>
    <asset:javascript src="aframe-gamepad-controls.min.js"/>
    <asset:javascript src="viewActivity.js"/>

</head>

<body>

<a-scene>

    <a-assets>
        <img id="groundTexture" crossorigin="anonymous" src="https://maps.googleapis.com/maps/api/staticmap?maptype=terrain&size=400x400&scale=2&path=weight:3%7Ccolor:orange%7Cenc:${activity.map.polyline}&key=AIzaSyCzC7_fx0o7ZKQZiQGjjMXHtPM_BDF2VVY">
    </a-assets>

    <g:each var="point" in="${points}">
        <a-box id="${point['distance']}" color="${point['speedColor']}" position="${point['x']} ${point['y']} ${point['z']}" scale="0.01 0.01 0.01"></a-box>
    </g:each>

    <!-- Ground Plane -->
    <a-plane src="#groundTexture" rotation="-90 0 0" width="10.1" height="10.1" color="#9aa7bc"></a-plane>

    <a-text value="${activity.getName()}" color="#BBB" position="-0.9 1 -20" scale="3 3 3"></a-text>

    <a-entity camera gamepad-controls look-controls></a-entity>

</a-scene>

</body>
</html>
