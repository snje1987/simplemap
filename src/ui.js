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

        movemap(cx, cy);

        svgRoot.onmousemove = ondrag;
        svgRoot.onmousedown = drag_start;
        svgRoot.onmouseup = drag_end;

        root.getElementById("btn1").onclick = scalel;
        root.getElementById("btn2").onclick = scales;
    }

    function movemap(cx, cy){
        map.setAttribute("viewBox", (cx - width * scalelist[scale] / 2) + " " + (cy - height * scalelist[scale] / 2) + " " + (width * scalelist[scale]) + " " + (height * scalelist[scale]));
    }

    function scalel(evt) {
        if (scale > 0) {
            scale --;
            movemap(cx, cy);
            var x = evt.clientX;
            var y = evt.clientY;
            root.getElementById("pos").firstChild.nodeValue = "1:" + scalelist[scale] + " 坐标：" + (cx + (x - width) * scalelist[scale] / 2) + "  " + (cy + (y - height) * scalelist[scale] / 2);
        }
    }
    function scales(evt) {
        if (scale < scalelist.length - 1) {
            scale ++;
            movemap(cx, cy);
            var x = evt.clientX;
            var y = evt.clientY;
            root.getElementById("pos").firstChild.nodeValue = "1:" + scalelist[scale] + " 坐标：" + parseInt((cx + (x - width / 2) * scalelist[scale])) + "  " + parseInt((cy + (y - height / 2) * scalelist[scale]));
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
            root.getElementById("pos").firstChild.nodeValue = "1:" + scalelist[scale] + " 坐标：" + parseInt((cx + (x - width / 2) * scalelist[scale])) + "  " + parseInt((cy + (y - height / 2) * scalelist[scale]));
        }
    }

    init(evt);
}