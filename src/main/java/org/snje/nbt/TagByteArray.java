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
package org.snje.nbt;

/**
 *
 * @author Yang Ming <yangming0116@163.com>
 */
public class TagByteArray extends Tag{
    public byte[] data = null;

    public TagByteArray(String name, byte[] data){
        this.name = name;
        this.data = new byte[data.length];
        System.arraycopy(data, 0, this.data, 0, data.length);
    }

    public byte[] GetData(){
        return data;
    }

    @Override
    public String toString(){
        return this.toString("", "");
    }

    @Override
    public String toString(String prefix, String repeat){
        return String.format("%s─%s[ByteArray]:%d\n", prefix, name, data.length);
    }

    @Override
    public byte GetType(){
        return NBT.TAG_Byte_Array;
    }
}
