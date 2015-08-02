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
package simplemap.json;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedHashMap;
/**
 *
 * @author Yang Ming <yangming0116@163.com>
 */
public class jObject extends jTag{

    LinkedHashMap<String, jTag> data = null;

    public jObject(InputStream in) throws jException{
        data = new LinkedHashMap<>();
        jTag tag;
        int ch;
        try{
            ch = in.read();
            if(ch != '{'){
                throw new jException();
            }
            while(true){
                in.mark(1);
                ch = in.read();
                in.reset();
                if(ch == '}'){
                    in.read();
                    return;
                }
                else if(ch == ',' || ch == ' ' || ch == '\n' || ch == '\r'){
                    in.read();
                }
                else{
                    String name = ReadName(in);
                    tag = ReadTag(in);
                    data.put(name, tag);
                }
            }
        }
        catch(Exception e){
            throw new jException();
        }
    }

    public jObject(){
        data = new LinkedHashMap<>();
    }

    private String ReadName(InputStream in) throws IOException{
        int ch;
        ByteArrayOutputStream buf = new ByteArrayOutputStream(1024);
        ch = in.read();
        while(ch != ':'){
            if(ch != ' ' && ch != '\n' && ch != '\r' && ch != '\"'){
                buf.write(ch);
            }
            ch = in.read();
        }
        return new String(buf.toByteArray(), "UTF-8");
    }

    private jTag ReadTag(InputStream in) throws IOException, jException{
        in.mark(1);
        int ch = in.read();
        in.reset();

        while(ch == ' ' || ch == '\n' || ch == '\r'){
            in.read();
            in.mark(1);
            ch = in.read();
            in.reset();
        }

        switch(ch){
        case '{':
            return new jObject(in);
        case '[':
            return new jArray(in);
        case '\"':
            return new jString(in);
        default:
            if(ch == '-' || ch >= '0' && ch <= '9'){
                return new jNumber(in);
            }
            else{
                throw new jException();
            }
        }
    }

    public jObject getObject(String key){
        if(!data.containsKey(key)){
            return null;
        }
        return data.get(key).toClass(jObject.class);
    }

    public jArray getArray(String key){
        if(!data.containsKey(key)){
            return null;
        }
        return data.get(key).toClass(jArray.class);
    }

    public int getInt(String key){
        if(!data.containsKey(key)){
            return 0;
        }
        return data.get(key).toClass(jNumber.class).getInt();
    }

    public double getDouble(String key){
        if(!data.containsKey(key)){
            return 0;
        }
        return data.get(key).toClass(jNumber.class).getDouble();
    }

    public String getString(String key){
        if(!data.containsKey(key)){
            return "";
        }
        return data.get(key).toClass(jString.class).getString();
    }

    public boolean hasKey(String key){
        return data.containsKey(key);
    }

    public void Add(String name, jTag item){
        data.put(name, item);
    }

    @Override
    public String toString(){
        StringBuilder buf = new StringBuilder();
        buf.append("{");
        if(!data.isEmpty()){
            Iterator<String> it = data.keySet().iterator();
            String key;
            jTag val;
            key = it.next();
            val = data.get(key);
            while(it.hasNext()){
                buf.append(key);
                buf.append(":");
                buf.append(val.toString());
                buf.append(",");
                key = it.next();
                val = data.get(key);
            }
            buf.append(key);
            buf.append(":");
            buf.append(val.toString());
        }
        buf.append("}");
        return buf.toString();
    }

    @Override
    public String toStyleString(String prefix, String repeat){
        StringBuilder buf = new StringBuilder();
        buf.append(prefix);
        buf.append("{\n");
        if(!data.isEmpty()){
            Iterator<String> it = data.keySet().iterator();
            String key;
            jTag val;
            key = it.next();
            val = data.get(key);
            while(it.hasNext()){
                buf.append(repeat);
                buf.append("    \"");
                buf.append(key);
                buf.append("\":");
                buf.append(val.toStyleString("", repeat + "    "));
                buf.append(",\n");
                key = it.next();
                val = data.get(key);
            }
            buf.append(repeat);
            buf.append("    \"");
            buf.append(key);
            buf.append("\":");
            buf.append(val.toStyleString("", repeat + "    "));
            buf.append("\n");
        }
        buf.append(repeat);
        buf.append("}");
        return buf.toString();
    }
}
