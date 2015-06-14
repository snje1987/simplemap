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
import java.io.InputStream;

/**
 *
 * @author Yang Ming <yangming0116@163.com>
 */
public class jString extends jTag{

    protected String data;

    public jString(InputStream in) throws jException{
        int ch;
        ByteArrayOutputStream buf = new ByteArrayOutputStream(1024);
        try{
            ch = in.read();
            if(ch != '\"'){
                throw new jException();
            }
            ch = in.read();
            while(ch != '\"'){
                buf.write(ch);
                ch = in.read();
            }
            data = new String(buf.toByteArray(), "UTF-8");
        }
        catch(Exception ex){
            throw new jException();
        }
    }

    public jString(String str){
        data = str;
    }

    public String getString(){
        return data;
    }

    @Override
    public String toString(){
        return String.format("\"%s\"", data);
    }

    @Override
    public String toStyleString(String prefix, String repeat){
        return String.format("%s\"%s\"", prefix, data);
    }

}
