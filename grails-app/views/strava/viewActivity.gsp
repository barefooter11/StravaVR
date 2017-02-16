<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="site"/>
    <title>StravaVR</title>

    <asset:javascript src="viewActivity.js"/>
</head>

<body>

<div class="container">
    <h2>${activity.name}</h2>
    <hr>

    <canvas id="glCanvas" width="900" height="600">
        Your browser doesn't appear to support the
        <code>&lt;canvas&gt;</code> element.
    </canvas>

</div>

<script id="2d-vertex-shader" type="notjs">

      // an attribute will receive data from a buffer
      attribute vec4 a_position;

      // all shaders have a main function
      void main() {

        // gl_Position is a special variable a vertex shader
        // is responsible for setting
        gl_Position = a_position;
      }

</script>

<script id="2d-fragment-shader" type="notjs">

      // fragment shaders don't have a default precision so we need
      // to pick one. mediump is a good default
      precision mediump float;

      void main() {
        // gl_FragColor is a special variable a fragment shader
        // is responsible for setting
        gl_FragColor = vec4(1, 0, 0.5, 1); // return redish-purple
      }

</script>

</body>
</html>