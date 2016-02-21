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
package org.snje.simplemap;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipFile;
import javax.imageio.ImageIO;
import org.snje.json.jArray;
import org.snje.json.jException;
import org.snje.json.jNumber;
import org.snje.json.jObject;

/**
 *
 * @author Yang Ming <yangming0116@163.com>
 */

public class ColorMap{

    public static boolean defcolor = false;

    protected static final String prefix = "assets/minecraft/textures/";
    protected static ColorMap instance = null;
    protected HashMap<Integer, jObject> map;
    protected Set<Integer> alert;

    public static ColorMap getInstance(){
        if(instance == null){
            instance = new ColorMap();
            instance.load();
            instance.save();
        }
        return instance;
    }

    public Color getColor(int id, int bid, int height){
        Color color = new Color(0, (byte) 0xFF);
        jObject obj = null;
        if(map.containsKey(id)){
            obj = map.get(id).getObject("color");
        }
        else{
            if(map.containsKey(id & 0x00000FFF)){
                obj = map.get(id & 0x00000FFF).getObject("color");
            }
        }

        if(obj == null){
            if(!alert.contains(id)){
                System.out.println(String.format(
                        "缺少颜色：%d %d\n",
                        (id & 0x00000FFF),
                        ((id & 0x0000F000) >> 12)
                ));
                alert.add(id);
            }
        }
        else{
            if(obj.getString("type").equals("rgba")){
                color = new Color(obj.getInt("r"), obj.getInt("g"), obj.getInt("b"), obj.getInt("a"));
            }
            else if(obj.getString("type").equals("grass")){
                color = new Color(obj.getInt("r"), obj.getInt("g"), obj.getInt("b"), obj.getInt("a"));
                color.mutiply(GrassColor.getInstance().getColor(bid, height));
            }
            else if(obj.getString("type").equals("foliage")){
                color = new Color(obj.getInt("r"), obj.getInt("g"), obj.getInt("b"), obj.getInt("a"));
                color.mutiply(FoliageColor.getInstance().getColor(bid, height));
            }
            else if(obj.getString("type").equals("water")){
                color = new Color(obj.getInt("r"), obj.getInt("g"), obj.getInt("b"), obj.getInt("a"));
                if(bid == Biome.Biome_Swampland){
                    color.mutiply(0xE0FFAE);
                }
            }
        }
        return color;
    }

    public void genColor(List<String> srcs){
        for(String path : srcs){
            System.err.println("正在处理：" + path);
            this.genColor(path);
        }
    }

    public void genColor(String path){

        if(!map.isEmpty()){
            Iterator<Integer> it = map.keySet().iterator();
            int key;
            jObject val;
            int ncolor;
            while(it.hasNext()){
                key = it.next();
                val = map.get(key);
                jObject tmp = val.getObject("texture");
                if(tmp == null){
                    continue;
                }
                try{
                    ncolor = this.makeColor(path, tmp);
                    Color c = new Color(ncolor);
                    jObject cObj = val.getObject("color");
                    cObj.Add("r", new jNumber((int)c.r));
                    cObj.Add("g", new jNumber((int)c.g));
                    cObj.Add("b", new jNumber((int)c.b));
                    cObj.Add("a", new jNumber((int)c.alpha));
                    //System.out.println(val.getString("name") + "=>" + c.toString());
                }
                catch(IOException ex){
                }
            }
        }

        String dir = System.getProperty("user.dir");
        if (!dir.endsWith(File.separator)) {
            dir += File.separator;
        }

        this.getMap(path, prefix + "colormap/foliage.png", dir + "foliage.png");
        this.getMap(path, prefix + "colormap/grass.png", dir + "grass.png");

        this.save();
    }

    protected ColorMap(){
        map = new LinkedHashMap<>();
        alert = new HashSet<>();
    }

    protected void load(){

        try{
            try(BufferedInputStream is=new BufferedInputStream(SimpleMap.class.getResourceAsStream("/color.json"))){
                this.load(is);
            }
        }
        catch(IOException ex){
            return;
        }

        if(defcolor){
            return;
        }

        String path = System.getProperty("user.dir");
        if (!path.endsWith(File.separator)) {
            path += File.separator;
        }
        path += "color.json";

        try {
            try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(path))) {
                this.load(in);
            }
        } catch (Exception ex) {
        }
    }

    protected void load(InputStream is){
        jArray arr;
        try{
            arr = new jArray(is);
        }
        catch(jException ex){
            return;
        }
        int size = arr.size();
        for(int i = 0; i < size; i++){
            jObject obj = arr.getObject(i);
            if(obj == null){
                continue;
            }
            int block_id = obj.getInt("id");
            int data = obj.getInt("data");
            block_id = (block_id & 0x00000FFF) | ((data & 0x0000000F) << 12);
            map.put(block_id, obj);
        }
    }

    protected void save(){
        String path = System.getProperty("user.dir");
        if (!path.endsWith(File.separator)) {
            path += File.separator;
        }
        path += "color.json";
        try{
            try(OutputStream out = new FileOutputStream(path)){
                this.save(out);
            }
        } catch (IOException ex) {
        }
    }

    protected void save(OutputStream out) throws IOException{
        jArray json = ToJson();
        out.write(json.toStyleString("", "").getBytes(Charset.forName("UTF-8")));
    }

    protected jArray ToJson(){
        jArray json = new jArray();
        if(!map.isEmpty()){
            Iterator<Integer> it = map.keySet().iterator();
            int key;
            jObject val;
            while(it.hasNext()){
                key = it.next();
                val = map.get(key);
                json.Add(val);
            }
        }
        return json;
    }

    protected void getMap(String path, String fname, String to){

        InputStream in = null;
        try{
            if(path.endsWith(".zip") || path.endsWith(".jar")){
                ZipFile zip = new ZipFile(path);
                in = zip.getInputStream(zip.getEntry(fname));
            }
            else{
                if (!path.endsWith(File.separator)) {
                    path += File.separator;
                }
                in = new BufferedInputStream(new FileInputStream(path + fname));
            }

            byte[] buf = new byte[1024];
            int count;
            try(OutputStream out = new FileOutputStream(to)){
                while((count = in.read(buf)) != -1){
                    out.write(buf, 0, count);
                }
            }
            in.close();
        }
        catch(IOException | NullPointerException e){

        }
    }

    protected int makeColor(String path, jObject texture) throws IOException{

        InputStream in = null;

        String fname = prefix + texture.getString("path");

        if(path.endsWith(".zip") || path.endsWith(".jar")){
            ZipFile zip = new ZipFile(path);
            in = zip.getInputStream(zip.getEntry(fname));
        }
        else{
            if (!path.endsWith(File.separator)) {
                path += File.separator;
            }
            in = new BufferedInputStream(new FileInputStream(path + fname));
        }

        BufferedImage i;
        i = ImageIO.read(in);
        in.close();

        if(texture.hasKey("x")){
            return avgColor(i, texture.getInt("x"), texture.getInt("y"), texture.getInt("w"), texture.getInt("h"), texture.getInt("bw"), texture.getInt("bh"), texture.getInt("base"));
        }
        else{
            return avgColor(i, 0, 0, i.getWidth(), i.getHeight(), i.getWidth(), i.getHeight(), i.getWidth());
        }
    }

    protected int avgColor(BufferedImage img, int x, int y, int width, int height, int bw, int bh, int base){
        double r = 0, g = 0, b = 0, a = 0;
        int color = 0;
        int count = 0;
        int iwidth = img.getWidth();
        int iheight = img.getHeight();

        if(base != iwidth){
            x = x * iwidth / base;
            y = y * iwidth / base;
            width = width * iwidth / base;
            height = height * iwidth / base;
            bw = bw * iwidth / base;
            bh = bh * iwidth / base;
        }

        for(int i = 0; i < width; i++){
            if(x + i >= iwidth){
                break;
            }
            for(int j = 0; j < height; j++){
                if(y + j >= iheight){
                    break;
                }
                color = img.getRGB(x + i, y + j);
                a += ((color >> 24) & 255);
                r += ((color >> 16) & 255);
                g += ((color >> 8) & 255);
                b += (color & 255);
            }
        }
        count = bw * bh;
        return (new Color((int)(r / count), (int)(g / count), (int)(b / count), (int)(a / count))).toInt();
    }
}
