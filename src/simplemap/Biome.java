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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import simplemap.json.jArray;
import simplemap.json.jException;
import simplemap.json.jNumber;
import simplemap.json.jObject;
import simplemap.json.jString;

/**
 *
 * @author Yang Ming <yangming0116@163.com>
 */

public class Biome{

    public static boolean usedef = false;
    protected static Biome instance = null;

    protected static BiomeInfo defbiome = new BiomeInfo("Ocean");

    protected HashMap<Integer, BiomeInfo> map;

    public static Biome getInstance(){
        if(instance == null){
            instance = new Biome();
            instance.load();
            instance.save();
        }
        return instance;
    }

    protected Biome(){
        map = new LinkedHashMap<>();
    }

    protected void load(){

        try{
            try(BufferedInputStream is=new BufferedInputStream(SimpleMap.class.getResourceAsStream("/biome.json"))){
                this.load(is);
            }
        }
        catch(IOException ex){
            return;
        }

        if(usedef){
            return;
        }

        String path = System.getProperty("user.dir");
        if (!path.endsWith(File.separator)) {
            path += File.separator;
        }
        path += "biome.json";

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
            int id = obj.getInt("id");
            map.put(id, new BiomeInfo(obj.getString("name"), obj.getDouble("temperature"), obj.getDouble("rainfall")));
        }
    }

    protected void save(){
        String path = System.getProperty("user.dir");
        if (!path.endsWith(File.separator)) {
            path += File.separator;
        }
        path += "biome.json";
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
            BiomeInfo tmp;
            jObject val;
            while(it.hasNext()){
                key = it.next();
                tmp = map.get(key);
                val = new jObject();
                val.Add("id", new jNumber(key));
                val.Add("temperature", new jNumber(tmp.temperature));
                val.Add("rainfall", new jNumber(tmp.rainfall));
                val.Add("name", new jString(tmp.name));
                json.Add(val);
            }
        }
        return json;
    }

    public BiomeInfo getInfo(int id){
        if(map.containsKey(id)){
            return map.get(id);
        }
        else{
            return defbiome;
        }
    }
}
