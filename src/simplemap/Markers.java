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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.LinkedList;
import simplemap.json.jArray;
import simplemap.json.jObject;

/**
 *
 * @author Yang Ming <yangming0116@163.com>
 */

public class Markers{

    LinkedList<jObject> data;

    public Markers(){
        data = new LinkedList<>();
    }

    public void load(String path){
        jArray arr;
        try{
            arr = new jArray(new BufferedInputStream(new FileInputStream(path)));
        }
        catch(Exception ex){
            return;
        }
        int size = arr.size();
        for(int i = 0; i < size; i++){
            jObject obj = arr.getObject(i);
            if(obj == null){
                continue;
            }
            data.add(obj);
        }
    }

    public void save(String path){
        jArray json = new jArray();
        if(!data.isEmpty()){
            Iterator<jObject> it = data.iterator();
            while(it.hasNext()){
                json.Add(it.next());
            }
        }
        try{
            try(OutputStream out = new FileOutputStream(path)){
                //System.out.println(json.toStyleString("", ""));
                out.write(json.toStyleString("", "").getBytes("UTF-8"));
            }
        } catch (IOException ex) {
        }
    }

    public void add(jObject obj){
        data.add(obj);
    }

    public void append(Markers from, int x, int z){
        if(!from.data.isEmpty()){
            Iterator<jObject> it = from.data.iterator();
            while(it.hasNext()){
                jObject obj = it.next();
                if(Anvil.InFile(obj.getInt("x"), obj.getInt("z"), x, z)){
                    this.data.add(obj);
                }
            }
        }
    }
}
