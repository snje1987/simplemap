function init(evt) {
    var root, svgRoot, map;
    var width, height, scale, cx, cy;
    var scalelist = [1,2,4,8,16,32,64,128];
    var sx, sy, draging;
    function init(evt) {
        root = evt.target.ownerDocument;
        svgRoot = root.rootElement;
        map = root.getElementById("map");

        width = parseInt(svgRoot.getAttribute("width"));
        height = parseInt(svgRoot.getAttribute("height"));
        scale = parseInt(svgRoot.getAttribute("scale"));
        cx = parseInt(svgRoot.getAttribute("cx"));
        cy = parseInt(svgRoot.getAttribute("cy"));

        if(scale > scalelist.length - 1){
            scale = scalelist.length - 1;
        }
        if(scale < 0){
            scale = 0;
        }

        draw_marker();
        movemap(cx, cy);

        svgRoot.onmousemove = ondrag;
        svgRoot.onmousedown = drag_start;
        svgRoot.onmouseup = drag_end;

        root.getElementById("btn1").onclick = scalel;
        root.getElementById("btn2").onclick = scales;
    }

    function draw_marker(){
        var omarkers = map.getElementById('marker');
        var nmarkers = root.createElementNS("http://www.w3.org/2000/svg",'g');
        nmarkers.setAttribute('id','marker');
        nmarkers.setAttribute('x','0');
        nmarkers.setAttribute('y','0');
        nmarkers.setAttribute('width','100%');
        nmarkers.setAttribute('height','100%');
        var len = markers.length;
        for(var i = 0; i < len; i++){
            var func = styles[markers[i]['style']];
            func(root, nmarkers, markers[i]['x'],markers[i]['z'],scalelist[scale],markers[i]['arg1'],markers[i]['arg2'],markers[i]['arg3']);
        }
        map.replaceChild(nmarkers, omarkers);
    }

    function setcoord(x, y){
        x = parseInt((cx + (x - width / 2) * scalelist[scale]));
        y = parseInt((cy + (y - height / 2) * scalelist[scale]));
        var chx = x >> 4;
        var chy = y >> 4;
        root.getElementById("pos").firstChild.nodeValue = "1:" + scalelist[scale] + " " + x + "," + y + " [" + chx + "," + chy + "]";
    }

    function movemap(cx, cy){
        if(cx % scalelist[scale] !== 0){
            cx = cx - cx % scalelist[scale];
        }
        if(cy % scalelist[scale] !== 0){
            cy = cy - cy % scalelist[scale];
        }
        map.setAttribute("viewBox", parseInt((cx - width * scalelist[scale] / 2)) + "," + parseInt((cy - height * scalelist[scale] / 2)) + "," + (width * scalelist[scale]) + "," + (height * scalelist[scale]));
    }
    function scalel(evt) {
        if (scale > 0) {
            scale --;
            var x = evt.clientX;
            var y = evt.clientY;
            setcoord(x, y);
            draw_marker();
            movemap(cx, cy);
        }
    }
    function scales(evt) {
        if (scale < scalelist.length - 1) {
            scale ++;
            var x = evt.clientX;
            var y = evt.clientY;
            setcoord(x, y);
            draw_marker();
            movemap(cx, cy);
        }
    }
    function drag_start(evt) {
        sx = evt.clientX;
        sy = evt.clientY;
        draging = 1;
    }
    function drag_end(evt) {
        svgRoot = evt.target.ownerDocument.rootElement;
        var x = evt.clientX;
        var y = evt.clientY;
        cx = cx - (x - sx) * scalelist[scale];
        cy = cy - (y - sy) * scalelist[scale];
        draging = 0;
    }
    function ondrag(evt) {
        var x = evt.clientX;
        var y = evt.clientY;
        if (draging === 1) {
            movemap(cx - (x - sx) * scalelist[scale], cy - (y - sy) * scalelist[scale]);
        }
        else{
            setcoord(x, y);
        }
    }

    init(evt);
}