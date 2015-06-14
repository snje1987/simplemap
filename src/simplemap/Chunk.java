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
import simplemap.nbt.TagByte;
import simplemap.nbt.TagByteArray;
import simplemap.nbt.TagCompound;
import simplemap.nbt.TagList;
import simplemap.nbt.TagString;

/**
 *
 * @author Yang Ming <yangming0116@163.com>
 */
public class Chunk{

    public static final int MAX_VCHUNK = 16;
    public static final int CHUNK_WIDTH = 16;
    public static double shadow = 0.2;

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
        comp = nbt.root.toClass(TagCompound.class);
        if(comp == null){
            return false;
        }

        comp = comp.Get("Level", TagCompound.class);
        if(comp == null){
            return false;
        }

        list = comp.Get("TileEntities", TagList.class);
        if(list != null && !GetMarker(list)){
            return false;
        }

        list = comp.Get("Sections", TagList.class);
        if(list == null){
            return false;
        }

        if(!this.InitData(list)){
            return false;
        }
        return true;
    }

    protected boolean GetMarker(TagList list){
        TagCompound comp;
        TagString tagString;
        int len = list.Size();
        for(int i = 0; i < len; i++){
            comp = list.Get(i, TagCompound.class);
            if(comp == null){
                return false;
            }
            tagString = comp.Get("id", TagString.class);
            if(tagString == null || !tagString.GetData().equals("Sign")){
                continue;
            }
            //System.out.println(comp.toString());
        }
        return true;
    }

    public boolean CalSurface(Point[][] pts, int x, int z){
        int i = 0, j = 0;
        for(i = 0; i < CHUNK_WIDTH; i++){
            for(j = 0; j < CHUNK_WIDTH; j++){
                pts[x + j][z + i] = this.GetColor(j, i);
            }
        }
        return true;
    }

    protected Point GetColor(int x, int z){
        Block block = new Block();
        Color tmp;
        Color ret = new Color(0, 0);
        boolean find = false;
        int i = 0, j = 0;
        int h = 0;
        for(i = MAX_VCHUNK - 1; i >= 0; i--){
            if(blocks[i].length <= 0){
                continue;
            }
            for(j = CHUNK_WIDTH - 1; j >= 0; j--){
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
                if(h == 0 && ret.getAlpha() >= 1){
                    h = i * CHUNK_WIDTH + j;
                }
                if(ret.getAlpha() >= 255){
                    find = true;
                    break;
                }
            }
            if(find == true){
                break;
            }
        }
        return new Point(ret, (short)h);
    }

    protected static byte Shift(byte[] arr, int index){
        if(arr.length * 2 <= index){
            return 0;
        }
        return (byte) (index % 2 == 0 ? arr[index / 2] & 0x0F : (arr[index / 2] >> 4) & 0x0F);
    }

    protected boolean InitData(TagList list){
        TagCompound comp;
        TagByteArray array;
        TagByte by;
        byte y;
        int len = list.Size();
        for(int i = 0; i < len; i++){
            comp = list.Get(i, TagCompound.class);
            if(comp == null){
                return false;
            }

            by = comp.Get("Y", TagByte.class);
            if(by == null){
                return false;
            }

            y = by.GetData();

            array = comp.Get("Data", TagByteArray.class);
            if(array == null){
                return false;
            }
            data[y] = array.GetData();

            array = comp.Get("Add", TagByteArray.class);
            if(array != null){
                add[y] = array.GetData();
            }

            array = comp.Get("Blocks", TagByteArray.class);
            if(array == null){
                return false;
            }
            blocks[y] = array.GetData();
        }
        return true;
    }
}
