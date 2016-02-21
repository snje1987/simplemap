var styles = {};

styles['dot'] = function(doc, map, x, z, scale, arg1, arg2, arg3){
    var tmp;
    var args2 = arg2.split(',');
    var args3 = arg3.split(',');

    var sw = 1;
    if(args3.length > 3){
        sw = args3[3];
    }

    tmp = "font-weight:bold;stroke:" + args3[1] + ";stroke-width:" + (sw * scale) + "px;fill:" + args3[0] + ";";

    var dot = doc.createElementNS("http://www.w3.org/2000/svg","circle");
    dot.setAttribute("cx",x);
    dot.setAttribute("cy",z);
    dot.setAttribute("r",parseInt(args3[2]) * scale);
    dot.setAttribute("style",tmp);
    map.appendChild(dot);

    var txt = doc.createElementNS("http://www.w3.org/2000/svg","text");
    txt.appendChild(doc.createTextNode(arg1));
    var fsize = parseInt(args2[2]) * scale;

    sw = 0.4;
    if(args2.length > 3){
        sw = args2[3];
    }

    txt.setAttribute("x",x);
    txt.setAttribute("y",z + fsize +  parseInt(args3[2]) * scale + scale);
    txt.setAttribute("height",fsize);
    txt.setAttribute("text-anchor","middle");
    txt.setAttribute("style","font-weight:bold;stroke:" + args2[1] + ";stroke-width:" + (scale * sw) + "px;font-size:" + fsize + "px;fill:" + args2[0] + ";");

    map.appendChild(txt);
};