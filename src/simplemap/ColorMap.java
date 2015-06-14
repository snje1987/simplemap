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

import java.awt.Graphics2D;
import java.awt.Image;
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
import java.util.Set;
import javax.imageio.ImageIO;
import simplemap.json.jArray;
import simplemap.json.jException;
import simplemap.json.jObject;

/**
 *
 * @author Yang Ming <yangming0116@163.com>
 */

public class ColorMap{

    public static boolean defcolor = false;

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

    public Color getColor(int id){
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
        }
        return color;
    }

    public void genColor(String path){

        if (!path.endsWith(File.separator)) {
            path += File.separator;
        }

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
                    ncolor = this.makeColor(path + tmp.getString("path"));
                    //val.setColor(ncolor);
                    System.out.println(val.getString("name"));
                }
                catch(IOException ex){
                }
            }
        }
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

    protected int makeColor(String fname) throws IOException{
        File file = new File(fname);
        if(!file.exists() || !file.isFile()){
            System.out.println(fname);
            throw new IOException();
        }
        BufferedImage i;
        i = ImageIO.read(file);
        Image tmp = i.getScaledInstance(1, 1, Image.SCALE_SMOOTH);
        BufferedImage o = new BufferedImage(tmp.getWidth(null), tmp.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = o.createGraphics();
        g.drawImage(tmp, null, null);
        g.dispose();
        return o.getRGB(0, 0);
    }
}
