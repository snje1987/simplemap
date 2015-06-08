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

import java.util.ArrayList;

/**
 *
 * @author Yang Ming <yangming0116@163.com>
 */
public class TagList extends Tag{
    ArrayList<Tag> data = null;

    public TagList(String name){
        this.name = name;
        this.data = new ArrayList<>();
    }

    public void Add(Tag item){
        data.add(item);
    }

    public Tag Get(int index){
        if(index >= 0 && index < data.size()){
            return data.get(index);
        }
        return null;
    }

    public int Size(){
        return data.size();
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
        buf.append("[List]\n");
        int size = data.size();
        if(!data.isEmpty()){
            for(int i = 0; i < size - 1; i++){
                buf.append(data.get(i).toString(repeat + "  ├", repeat + "  │"));
            }
            buf.append(data.get(size - 1).toString(repeat + "  └", repeat + "    "));
        }
        return buf.toString();
    }

    @Override
    public byte GetType(){
        return NBT.TAG_List;
    }
}