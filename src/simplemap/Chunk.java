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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.InflaterInputStream;
import simplemap.nbt.NBT;
import simplemap.nbt.Tag;
import simplemap.nbt.TagByte;
import simplemap.nbt.TagByteArray;
import simplemap.nbt.TagCompound;
import simplemap.nbt.TagList;

/**
 *
 * @author Yang Ming <yangming0116@163.com>
 */
public class Chunk{

    public static final int MAX_VCHUNK = 16;
    public static final int CHUNK_WIDTH = 16;

    byte[][] data;
    byte[][] add;
    byte[][] blocks;
    ColorMap map;

    public Chunk(){
        data = new byte[MAX_VCHUNK][0];
        add = new byte[MAX_VCHUNK][0];
        blocks = new byte[MAX_VCHUNK][0];
        map = ColorMap.getInstance();
    }

    public boolean Load(byte[] data){
        ByteArrayInputStream bi = new ByteArrayInputStream(data);
        InflaterInputStream iis = new InflaterInputStream(bi);
        ByteArrayOutputStream o = new ByteArrayOutputStream(1024);

        try {
            int i = 1024;
            byte[] buf = new byte[i];
            while ((i = iis.read(buf, 0, i)) > 0) {
                o.write(buf, 0, i);
            }
        } catch (IOException e) {
            return false;
        }

        NBT nbt = new NBT();
        if(!nbt.Decode(o.toByteArray())){
            return false;
        }
        TagCompound comp;
        TagList list;
        Tag tag;
        comp = nbt.root.toClass(TagCompound.class);
        if(comp == null){
            return false;
        }
        tag = comp.Get("Level");
        if(tag == null){
            return false;
        }
        comp = tag.toClass(TagCompound.class);
        if(comp == null){
            return false;
        }
        tag = comp.Get("Sections");
        if(tag == null){
            return false;
        }
        list = tag.toClass(TagList.class);
        if(list == null){
            return false;
        }

        return this.InitData(list);
    }

    public int[] CalSurface(){
        int[] colors = new int[CHUNK_WIDTH * CHUNK_WIDTH];
        int len = colors.length;
        for(int i = 0; i < len; i++){
            colors[i] = this.GetColor(i % CHUNK_WIDTH, i / CHUNK_WIDTH);
        }
        return colors;
    }

    protected int GetColor(int x, int z){
        Block block = new Block();
        Color tmp;
        Color ret = new Color(0, 0);
        boolean find = false;
        for(int i = MAX_VCHUNK - 1; i >= 0; i--){
            if(blocks[i].length <= 0){
                continue;
            }
            for(int j = CHUNK_WIDTH - 1; j >= 0; j--){
                int pos = x + z * CHUNK_WIDTH + j * CHUNK_WIDTH * CHUNK_WIDTH;
                byte blockid_a = blocks[i][pos];
                byte blockid_b = Chunk.Shift(add[i], pos);
                block.block_id = (short) ((blockid_a & 0xFF) | (blockid_b << 8));
                if(block.block_id == 110){
                    int k;
                    k = 0;
                }
                if(block.block_id == 0){
                    continue;
                }
                if(block.block_id == 6){
                    block.block_id = (short) (block.block_id | ((Chunk.Shift(data[i], pos) & 0x7F) << 12));
                }
                else if(block.block_id >= 8 && block.block_id <= 11){
                }
                else{
                    block.block_id = (short) (block.block_id | (Chunk.Shift(data[i], pos) << 12));
                }
                tmp = map.getColor(block.block_id);
                ret.merge(tmp);
                if(ret.getAlpha() >= 255){
                    find = true;
                    break;
                }
                else{
                    int k = 0;
                    k++;
                }
            }
            if(find == true){
                break;
            }
        }
        return ret.toInt();
    }

    protected static byte Shift(byte[] arr, int index){
        if(arr.length * 2 <= index){
            return 0;
        }
        return (byte) (index % 2 == 0 ? arr[index / 2] & 0x0F : (arr[index / 2] >> 4) & 0x0F);
    }

    protected boolean InitData(TagList list){
        TagCompound comp;
        Tag tag;
        TagByteArray array;
        TagByte by;
        byte y;
        int len = list.Size();
        for(int i = 0; i < len; i++){
            tag = list.Get(i);
            comp = tag.toClass(TagCompound.class);
            if(comp == null){
                return false;
            }

            tag = comp.Get("Y");
            if(tag == null){
                return false;
            }

            by = tag.toClass(TagByte.class);
            if(by == null){
                return false;
            }

            y = by.GetData();

            tag = comp.Get("Data");
            if(tag == null){
                return false;
            }
            array = tag.toClass(TagByteArray.class);
            if(array == null){
                return false;
            }
            data[y] = array.GetData();

            tag = comp.Get("Add");
            if(tag != null){
                array = tag.toClass(TagByteArray.class);
                if(array == null){
                    return false;
                }
                add[y] = array.GetData();
            }

            tag = comp.Get("Blocks");
            if(tag == null){
                return false;
            }
            array = tag.toClass(TagByteArray.class);
            if(array == null){
                return false;
            }
            blocks[y] = array.GetData();
        }
        return true;
    }
}
