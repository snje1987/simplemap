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

import java.io.UnsupportedEncodingException;

class TAG_End_Exception extends Exception{
}
class End_Of_Input extends Exception{
}
/**
 *
 * @author Yang Ming <yangming0116@163.com>
 */
public class NBT{

    public static final byte TAG_End = 0;
    //1 byte / 8 bits, signed
    public static final byte TAG_Byte = 1;
    //2 bytes / 16 bits, signed, big endian
    public static final byte TAG_Short = 2;
    //4 bytes / 32 bits, signed, big endian
    public static final byte TAG_Int = 3;
    //8 bytes / 64 bits, signed, big endian
    public static final byte TAG_Long = 4;
    //4 bytes / 32 bits, signed, big endian, IEEE 754-2008, binary32
    public static final byte TAG_Float = 5;
    //8 bytes / 64 bits, signed, big endian, IEEE 754-2008, binary64
    public static final byte TAG_Double = 6;
    //TAG_Int's payload size, then size TAG_Byte's payloads.
    public static final byte TAG_Byte_Array = 7;
    //TAG_Short's payload length, then a UTF-8 string with size length.
    public static final byte TAG_String = 8;
    //TAG_Byte's payload tagId, then TAG_Int's payload size, then size tags' payloads, all of type tagId.
    public static final byte TAG_List = 9;
    //Fully formed tags, followed by a TAG_End.
    public static final byte TAG_Compound = 10;
    //TAG_Int's payload size, then size TAG_Int's payloads.
    public static final byte TAG_Int_Array = 11;

    protected int offset = 0 ;
    protected byte[] data = null;

    public Tag root = null;

    public NBT(){

    }

    public boolean Decode(byte[] data){
        this.offset = 0;
        this.data = data;
        try{
            this.root = this.DecodeF();
            if(root == null){
                return false;
            }
            else{
                return true;
            }
        }
        catch(TAG_End_Exception | End_Of_Input ex){
            return false;
        }
        finally{
            this.data = null;
        }
    }

    @Override
    public String toString(){
        return this.toString("", "");
    }

    public String toString(String prefix, String repeat){
        return root.toString(prefix, repeat);
    }

    protected Tag DecodeF() throws TAG_End_Exception, End_Of_Input{
        if(offset >= data.length){
            throw new End_Of_Input();
        }
        byte type = data[offset];
        offset ++;
        return this.DecodeAny(type, false);
    }

    protected Tag DecodeAny(byte type, boolean noname) throws TAG_End_Exception{
        switch(type){
        case TAG_End:
            throw new TAG_End_Exception();
        case TAG_Byte:
            return this.DecodeByte(noname);
        case TAG_Short:
            return this.DecodeShort(noname);
        case TAG_Int:
            return this.DecodeInt(noname);
        case TAG_Long:
            return this.DecodeLong(noname);
        case TAG_Float:
            return this.DecodeFloat(noname);
        case TAG_Double:
            return this.DecodeDouble(noname);
        case TAG_Byte_Array:
            return this.DecodeByteArray(noname);
        case TAG_String:
            return this.DecodeString(noname);
        case TAG_List:
            return this.DecodeList(noname);
        case TAG_Compound:
            return this.DecodeCompound(noname);
        case TAG_Int_Array:
            return this.DecodeIntArray(noname);
        default:
            return null;
        }
    }

    protected String GetString(){
        int NameLen = this.GetShort();
        byte[] NameByte = new byte[NameLen];
        System.arraycopy(data, offset, NameByte, 0, NameLen);
        String NameString;
        try{
            NameString = new String(NameByte, "UTF-8");
        }
        catch(UnsupportedEncodingException ex){
            NameString = "";
        }
        offset += NameLen;
        return NameString;
    }

    protected Integer GetInt(){
        int ret = ((data[offset] & 0xFF) << 24) | ((data[offset + 1] & 0xFF) << 16) | ((data[offset + 2] & 0xFF) << 8) | (data[offset + 3] & 0xFF);
        offset += 4;
        return ret;
    }

    protected Long GetLong(){
        int high = this.GetInt();
        int low = this.GetInt();

        long l1 = (high & 0x00000000ffffffffL)<<32;
        long l2 = low & 0x00000000ffffffffL;

        return l1|l2;
    }

    protected short GetShort(){
        short ret = (short) (((data[offset] & 0xFF) << 8) | (data[offset + 1] & 0xFF));
        offset += 2;
        return ret;
    }

    protected TagByte DecodeByte(boolean noname){
        String NameString;
        if(noname){
            NameString = "";
        }
        else{
            NameString = this.GetString();
        }
        offset ++;
        return new TagByte(NameString, data[offset - 1]);
    }

    protected TagShort DecodeShort(boolean noname){
        String NameString;
        if(noname){
            NameString = "";
        }
        else{
            NameString = this.GetString();
        }
        short val = this.GetShort();
        return new TagShort(NameString, val);
    }

    protected TagInt DecodeInt(boolean noname){
        String NameString;
        if(noname){
            NameString = "";
        }
        else{
            NameString = this.GetString();
        }
        int val = this.GetInt();
        return new TagInt(NameString, val);
    }

    protected TagLong DecodeLong(boolean noname){
        String NameString;
        if(noname){
            NameString = "";
        }
        else{
            NameString = this.GetString();
        }
        long val = this.GetLong();
        return new TagLong(NameString, val);
    }

    protected TagFloat DecodeFloat(boolean noname){
        String NameString;
        if(noname){
            NameString = "";
        }
        else{
            NameString = this.GetString();
        }
        int tmp = this.GetInt();
        float val = Float.intBitsToFloat(tmp);
        return new TagFloat(NameString, val);
    }

    protected TagDouble DecodeDouble(boolean noname){
        String NameString;
        if(noname){
            NameString = "";
        }
        else{
            NameString = this.GetString();
        }
        long tmp = this.GetLong();
        double val = Double.longBitsToDouble(tmp);
        return new TagDouble(NameString, val);
    }

    protected TagByteArray DecodeByteArray(boolean noname){
        String NameString;
        if(noname){
            NameString = "";
        }
        else{
            NameString = this.GetString();
        }
        int size = this.GetInt();
        byte[] val = new byte[size];
        System.arraycopy(data, offset, val, 0, size);
        offset += size;
        return new TagByteArray(NameString, val);
    }

    protected TagString DecodeString(boolean noname){
        String NameString;
        if(noname){
            NameString = "";
        }
        else{
            NameString = this.GetString();
        }
        String val = this.GetString();
        return new TagString(NameString, val);
    }

    protected TagList DecodeList(boolean noname){
        String NameString;
        if(noname){
            NameString = "";
        }
        else{
            NameString = this.GetString();
        }

        TagList list = new TagList(NameString);

        byte tagId = data[offset];
        offset ++;
        int size = this.GetInt();
        int i;
        boolean ret = true;
        Tag item;

        for(i = 0; i < size; i++){
            try{
                item = this.DecodeAny(tagId, true);
                if(item == null){
                    ret = false;
                    break;
                }
                else{
                    list.Add(item);
                }
            }
            catch(TAG_End_Exception ex){
                ret = false;
                break;
            }
        }

        if(ret){
            return list;
        }
        else{
            return null;
        }

    }

    protected TagCompound DecodeCompound(boolean noname){
        String NameString;
        if(noname){
            NameString = "";
        }
        else{
            NameString = this.GetString();
        }

        TagCompound comp = new TagCompound(NameString);

        boolean ret;
        Tag item;

        while(true){
            try{
                item = this.DecodeF();
                if(item == null){
                    ret = false;
                    break;
                }
                else{
                    comp.Add(item);
                }
            }
            catch(TAG_End_Exception ex){
                ret = true;
                break;
            }
            catch(End_Of_Input ex){
                ret = false;
                break;
            }
        }
        if(ret){
            return comp;
        }
        else{
            return null;
        }
    }

    protected TagIntArray DecodeIntArray(boolean noname){
        String NameString;
        if(noname){
            NameString = "";
        }
        else{
            NameString = this.GetString();
        }
        int size = this.GetInt();
        byte[] val = new byte[size * 4];
        System.arraycopy(data, offset, val, 0, size * 4);
        offset += size * 4;
        return new TagIntArray(NameString, val);
    }
}
