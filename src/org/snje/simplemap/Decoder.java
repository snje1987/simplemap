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
package org.snje.simplemap;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;
import java.util.zip.ZipException;
import org.snje.nbt.NBT;

/**
 *
 * @author Yang Ming <yangming0116@163.com>
 */
public class Decoder{
    public static void decode(String fname){
        File file = new File(fname);
        byte[] data = null;
        try{
            int i = 1024;
            byte[] buf = new byte[i];
            try(ByteArrayOutputStream o = new ByteArrayOutputStream(1024)){
                try{
                    try(InflaterInputStream iis = new InflaterInputStream(new FileInputStream(file))){
                        while ((i = iis.read(buf, 0, i)) > 0) {
                            o.write(buf, 0, i);
                        }
                    }
                }
                catch(ZipException ex){
                    i = 1024;
                    try{
                        try(GZIPInputStream zis = new GZIPInputStream(new FileInputStream(file))){
                            while ((i = zis.read(buf, 0, i)) > 0) {
                                o.write(buf, 0, i);
                            }
                        }
                    }
                    catch(ZipException e){
                        i = 1024;
                        try(BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))){
                            while ((i = bis.read(buf, 0, i)) > 0) {
                                o.write(buf, 0, i);
                            }
                        }
                    }
                }
                data = o.toByteArray();
            }
        }
        catch(FileNotFoundException ex){
            ex.printStackTrace();
            System.err.println("文件不存在");
            return;
        }
        catch(IOException ex){
            ex.printStackTrace();
            System.err.println("文件读取失败");
            return;
        }

        NBT nbt = new NBT();
        if(!nbt.Decode(data)){
            System.err.println("解码失败");
            return;
        }
        System.out.println(nbt.toString());
    }
}
