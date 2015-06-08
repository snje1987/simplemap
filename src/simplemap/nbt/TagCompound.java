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
package simplemap.nbt;

import java.util.HashMap;
import java.util.Iterator;

/**
 *
 * @author Yang Ming <yangming0116@163.com>
 */
public class TagCompound extends Tag{
    HashMap<String, Tag> data = null;
    public TagCompound(String name){
        this.name = name;
        data = new HashMap<>();
    }

    public void Add(Tag item){
        data.put(item.name, item);
    }

    public Tag Get(String name){
        if(data.containsKey(name)){
            return data.get(name);
        }
        return null;
    }

    @Override
    public String toString(){
        return this.toString("", "");
    }

    @Override
    public String toString(String prefix, String repeat){
        StringBuilder buf = new StringBuilder();
        buf.append(prefix);
        if(data.isEmpty()){
            buf.append("──");
        }
        else{
            buf.append("─┬");
        }
        buf.append(name);
        buf.append("[Compound]\n");
        if(!data.isEmpty()){
            Iterator<String> it = data.keySet().iterator();
            String key;
            Tag val;
            key = it.next();
            val = data.get(key);
            while(it.hasNext()){
                buf.append(val.toString(repeat + "  ├", repeat + "  │"));
                key = it.next();
                val = data.get(key);
            }
            buf.append(val.toString(repeat + "  └", repeat + "    "));
        }
        return buf.toString();
    }

    @Override
    public byte GetType(){
        return NBT.TAG_Compound;
    }
}
