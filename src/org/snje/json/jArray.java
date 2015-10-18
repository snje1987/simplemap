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
package org.snje.json;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;

/**
 *
 * @author Yang Ming <yangming0116@163.com>
 */
public class jArray extends jTag{

    ArrayList<jTag> data = null;

    public jArray(InputStream in) throws jException{
        data = new ArrayList<>();
        jTag tag;
        int ch;
        try{
            ch = in.read();
            if(ch != '['){
                throw new jException();
            }
            while(true){
                in.mark(1);
                ch = in.read();
                in.reset();
                switch(ch){
                case '{':
                    tag = new jObject(in);
                    data.add(tag);
                    break;
                case '[':
                    tag = new jArray(in);
                    data.add(tag);
                    break;
                case ',':
                case ' ':
                case '\t':
                case '\n':
                case '\r':
                    in.read();
                    break;
                case ']':
                    in.read();
                    return;
                case '\"':
                    tag = new jString(in);
                    data.add(tag);
                    break;
                default:
                    if(ch == '-' || ch >= '0' && ch <= '9'){
                        tag = new jNumber(in);
                        data.add(tag);
                    }
                    else{
                        throw new jException();
                    }
                }
            }
        }
        catch(Exception ex){
            throw new jException();
        }
    }

    public jArray(){
        data = new ArrayList<>();
    }

    public int size(){
        return data.size();
    }

    public jObject getObject(int index){
        if(index < 0 || index >= data.size()){
            return null;
        }
        return data.get(index).toClass(jObject.class);
    }

    public jArray getArray(int index){
        if(index < 0 || index >= data.size()){
            return null;
        }
        return data.get(index).toClass(jArray.class);
    }

    public void Add(jTag item){
        data.add(item);
    }

    @Override
    public String toString(){
        StringBuilder buf = new StringBuilder();
        buf.append("[");
        int size = data.size();
        if(!data.isEmpty()){
            for(int i = 0; i < size - 1; i++){
                buf.append(data.get(i).toString());
                buf.append(",");
            }
            buf.append(data.get(size - 1).toString());
        }
        buf.append("]");
        return buf.toString();
    }

    @Override
    public String toStyleString(String prefix, String repeat){
        StringBuilder buf = new StringBuilder();
        buf.append(prefix);
        buf.append("[\n");
        int size = data.size();
        if(!data.isEmpty()){
            for(int i = 0; i < size - 1; i++){
                buf.append(data.get(i).toStyleString(repeat + "    ", repeat + "    "));
                buf.append(",\n");
            }
            buf.append(data.get(size - 1).toStyleString(repeat + "    ", repeat + "    "));
            buf.append("\n");
        }
        buf.append(prefix);
        buf.append("]");
        return buf.toString();
    }

}
