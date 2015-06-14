/*
 * Copyright (C) 2015 Yang Ming <yangming0116@163.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package simplemap;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 * @author Yang Ming <yangming0116@163.com>
 */
public class World{
    public static boolean regen = true;
    public static boolean update = false;
    public static int cx = 0;
    public static int cy = 0;
    public static int width = 512;
    public static int height = 512;
    public static int scale = 0;

    public static int mx_begin = 0;
    public static int mx_end = 0;
    public static int mz_begin = 0;
    public static int mz_end = 0;

    protected String imgdir = "files" + File.separator;
    protected String dest;
    protected BufferedOutputStream svg;

    public void Draw(String src, String dest){
        if(!src.endsWith(File.separator)){
            src += File.separator;
        }
        if(!dest.endsWith(File.separator)){
            dest += File.separator;
        }
        this.dest = dest;

        src += "region" + File.separator;

        File dir;
        dir = new File(dest + imgdir);
        if(dir.exists()){
            if(!dir.isDirectory()){
                System.out.println("目标路径无法使用");
                return;
            }
        }
        else{
            if(!dir.mkdirs()){
                System.out.println("建立目录出错");
                return;
            }
        }

        try{
            svg = new BufferedOutputStream(new FileOutputStream(this.dest + "map.svg"));
        }
        catch(Exception ex){
            return;
        }

        this.svgHeader();

        dir = new File(src);
        if(!dir.exists() || !dir.isDirectory()){
            System.out.println("存档不存在");
            return;
        }

        String[] files = dir.list();
        if(files == null){
            System.out.println("存档不存在");
            return;
        }

        Markers oMarker = new Markers();
        Markers nMarker = new Markers();

        oMarker.load(this.dest + imgdir + "markers.json");

        for(String file : files){
            this.DrawFile(src + file, nMarker, oMarker);
        }

        nMarker.save(this.dest + imgdir + "markers.json");

        buildUI();

        try{
            svg.write(String.format("</svg><svg id=\"layer\" x=\"0\" y=\"0\" width=\"%d\" height=\"%d\" viewBox=\"0 0 %d %d\"><text id=\"pos\" x=\"%d\" y=\"%d\" style=\"font-size:14px\">1:%d </text>\n",width, height, width, height, 10, height - 20, scale).getBytes());
            svg.write(String.format("<text id=\"btn1\" x=\"%d\" y=\"%d\" style=\"font-size:14px\">放大</text>\n", 10, 30).getBytes());
            svg.write(String.format("<text id=\"btn2\" x=\"%d\" y=\"%d\" style=\"font-size:14px\">缩小</text>\n</svg></svg>\n", 10, 60).getBytes());
        }
        catch(IOException ex){
            return;
        }

        try{
            svg.close();
        }
        catch(IOException ex){
        }
    }

    protected boolean buildUI(){
        try{
            try(BufferedReader is=new BufferedReader(new InputStreamReader(SimpleMap.class.getResourceAsStream("/ui.js")))){
                try(FileOutputStream os = new FileOutputStream(this.dest + imgdir + "ui.js")){
                    String s = is.readLine();
                    s += '\n';
                    os.write(s.getBytes("UTF-8"));

                    String marker_fname = this.dest + imgdir + "markers.json";
                    File tmp = new File(marker_fname);
                    if(tmp.exists()){
                        os.write("var markers=".getBytes("UTF-8"));
                        try(BufferedReader reader = new BufferedReader(new FileReader(tmp))){
                            s = reader.readLine();
                            String tmp1;
                            tmp1 = reader.readLine();
                            while(tmp1 != null){
                                s += '\n';
                                os.write(s.getBytes("UTF-8"));
                                s = tmp1;
                                tmp1 = reader.readLine();
                            }
                            os.write(s.getBytes("UTF-8"));
                        }
                        os.write(";\n".getBytes("UTF-8"));
                    }

                    s = is.readLine();
                    while(s != null){
                        s += '\n';
                        os.write(s.getBytes("UTF-8"));
                        s = is.readLine();
                    }
                }
            }
        }
        catch(Exception e){
            return false;
        }
        return true;
    }

    protected void svgHeader(){
        StringBuilder buf = new StringBuilder();
        buf.append("<?xml version=\"1.0\" standalone=\"no\"?>\n");
        buf.append(String.format("<svg width=\"%d\" height=\"%d\" scale=\"%d\" cx=\"%d\" cy=\"%d\" preserveAspectRatio=\"xMidYMid meet\" version=\"1.1\" baseProfile=\"full\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:ev=\"http://www.w3.org/2001/xml-events\" onload=\"init(evt)\">\n", width, height, scale, cx, cy));
        buf.append("<script type=\"text/javascript\" xlink:href=\"files/ui.js\"></script>");
        buf.append("<svg id=\"map\" x=\"0\" y=\"0\" width=\"100%\" height=\"100%\">");
        try{
            svg.write(buf.toString().getBytes());
        }
        catch(IOException ex){
        }
    }

    public void DrawFile(String file, Markers nMarker, Markers oMarker){
        try{
            String fname = file.substring(file.lastIndexOf(File.separator) + 1);
            String[] tmp = fname.split("\\.");

            int x = Integer.parseInt(tmp[1]);
            int z = Integer.parseInt(tmp[2]);

            boolean needDraw = regen;

            if(needDraw && mx_end > mx_begin){
                if(mx_end < x * 512 || mx_begin > x * 512 + 512){
                    needDraw = false;
                }
            }

            if(needDraw && mz_end > mz_begin){
                if(mz_end < z * 512 || mz_begin > z * 512 + 512){
                    needDraw = false;
                }
            }

            String pngname = tmp[1] + "." + tmp[2] + ".png";

            if(update){
                File from = new File(file);
                File to = new File(dest + imgdir + pngname);
                if(to.exists() && to.lastModified() > from.lastModified()){
                    needDraw = false;
                }
            }

            if(needDraw){
                //System.out.println("正在处理：" + fname);
                Anvil draw = new Anvil();
                draw.Draw(file, dest + imgdir + pngname, nMarker);
            }
            else{
                //System.out.println("已经跳过：" + fname);
                nMarker.append(oMarker, x, z);
            }

            String tag = String.format("<image x=\"%d\" y=\"%d\" width=\"512\" height = \"512\" xlink:href=\"files/%s\" />\n", x * 512, z * 512, pngname);

            svg.write(tag.getBytes());
        }
        catch(Exception e){
        }
    }

}
