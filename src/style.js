var styles = {};

styles['dot'] = function(doc, map, x, z, scale, arg1, arg2){
    var tmp;
    var args2 = arg2.split(',');
    var stroke = args2[1];

    tmp = "font-weight:bold;stroke:" + stroke + ";stroke-width:" + (scale) + "px;fill:" + args2[0] + ";";

    var dot = doc.createElementNS("http://www.w3.org/2000/svg","circle");
    dot.setAttribute("cx",x);
    dot.setAttribute("cy",z);
    dot.setAttribute("r",parseInt(args2[3]) * scale);
    dot.setAttribute("style",tmp);
    map.appendChild(dot);

    var txt = doc.createElementNS("http://www.w3.org/2000/svg","text");
    txt.appendChild(doc.createTextNode(arg1));
    var fsize = parseInt(args2[2]) * scale;

    txt.setAttribute("x",x);
    txt.setAttribute("y",z + fsize +  parseInt(args2[3]) * scale + scale);
    txt.setAttribute("height",fsize);
    txt.setAttribute("text-anchor","middle");
    txt.setAttribute("style","font-weight:bold;stroke:" + stroke + ";stroke-width:" + (scale * 0.4) + "px;font-size:" + fsize + "px;fill:" + args2[0] + ";");

    map.appendChild(txt);
};