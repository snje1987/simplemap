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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author Yang Ming <yangming0116@163.com>
 */

public class ColorMap{

    public static boolean defcolor = false;

    protected static ColorMap instance = null;
    protected HashMap<Integer, Color> map;
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
        if(map.containsKey(id)){
            color = map.get(id);
        }
        else{
            if(map.containsKey(id & 0x00000FFF)){
                color = map.get(id & 0x00000FFF);
            }
        }
        if(color.toInt() == 0xFF000000){
            if(!alert.contains(id)){
                System.out.println(String.format(
                        "缺少颜色：%d %d\n",
                        (id & 0x00000FFF),
                        ((id & 0x0000F000) >> 12)
                ));
                alert.add(id);
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
            Color val;
            int ncolor;
            while(it.hasNext()){
                key = it.next();
                val = map.get(key);
                if(val.tname.equals("none")){
                    continue;
                }
                try{
                    ncolor = this.makeColor(path + val.tname);
                    val.setColor(ncolor);
                    System.out.println(val.name);
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
            try(InputStream is=SimpleMap.class.getResourceAsStream("/color.txt")){
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
        path += "color.txt";

        try {
            try (InputStream in = new FileInputStream(path)) {
                this.load(in);
            }
        } catch (Exception ex) {
        }
    }

    protected void load(InputStream is){
        Scanner sc = new Scanner(is);
        Color tmp;
        while(sc.hasNext()){
            int block_id = sc.nextInt();
            int data = sc.nextInt();
            int color = sc.nextInt(16);
            int alpha =sc.nextInt(16);
            block_id = (block_id & 0x00000FFF) | ((data & 0x0000000F) << 12);
            tmp = new Color(color, alpha);
            tmp.name = sc.next();
            tmp.tname = sc.next();
            map.put(block_id, tmp);
            sc.nextLine();
        }
    }

    protected void save(){
        String path = System.getProperty("user.dir");
        if (!path.endsWith(File.separator)) {
            path += File.separator;
        }
        path += "color.txt";
        try{
            try(OutputStream out = new FileOutputStream(path)){
                this.save(out);
            }
        } catch (IOException ex) {
        }
    }

    protected void save(OutputStream out) throws IOException{
        if(!map.isEmpty()){
            Iterator<Integer> it = map.keySet().iterator();
            int key;
            Color val;
            while(it.hasNext()){
                key = it.next();
                val = map.get(key);
                out.write(String.format(
                        "%d %d %06X %02X %s %s\n",
                        (key & 0x00000FFF),
                        ((key & 0x0000F000) >> 12),
                        val.toInt(false),
                        val.getAlpha(),
                        val.name,
                        val.tname
                ).getBytes());
            }
        }
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
