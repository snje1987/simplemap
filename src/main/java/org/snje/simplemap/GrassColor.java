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

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import javax.imageio.ImageIO;

/**
 *
 * @author Yang Ming <yangming0116@163.com>
 */

public class GrassColor{

    protected static GrassColor instance = null;
    protected static int[] grassBuffer = null;
    protected static final int width = 256;
    protected static final int height = 256;

    public static GrassColor getInstance(){
        if(instance == null){
            instance = new GrassColor();
            instance.load();
            instance.save();
        }
        return instance;
    }

    protected GrassColor(){
    }

    protected void load(){

        String path = System.getProperty("user.dir");
        if (!path.endsWith(File.separator)) {
            path += File.separator;
        }
        path += "grass.png";

        try {
            try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(path))) {
                this.load(in);
            }
        } catch (Exception ex) {
            grassBuffer = new int[width * height];
            Arrays.fill(grassBuffer, 0xFF7CBD6C);
        }
    }

    protected void load(InputStream is) throws Exception{
        BufferedImage buf = ImageIO.read(is);
        if(buf.getWidth() == width && buf.getHeight() == height){
            grassBuffer = new int[width * height];
            buf.getRGB(0, 0, width, height, grassBuffer, 0, width);
        }
        else{
            throw new Exception();
        }
    }

    protected void save(){
        String path = System.getProperty("user.dir");
        if (!path.endsWith(File.separator)) {
            path += File.separator;
        }
        path += "grass.png";
        try{
            try(OutputStream out = new FileOutputStream(path)){
                this.save(out);
            }
        } catch (IOException ex) {
        }
    }

    protected void save(OutputStream out) throws IOException{
        BufferedImage buf = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        buf.setRGB(0, 0, width, height, grassBuffer, 0, width);
        ImageIO.write(buf, "png", out);
    }

    public int getColor(double temperature, double humidity){

        temperature = temperature < 0.0D ? 0.0D : (temperature > 1.0D ? 1.0D : temperature);
        humidity = humidity < 0.0D ? 0.0D : (humidity > 1.0D ? 1.0D : humidity);
        humidity *= temperature;
        int tempIndex = (int)((1.0D - temperature) * 255.0D);
        int humIndex = (int)((1.0D - humidity) * 255.0D);
        int index = humIndex << 8 | tempIndex;

        return index > grassBuffer.length ? -65281 : grassBuffer[index];
    }

    public int getColor(int bid, int height){
        double temperature;
        double humidity;
        int tmp;

        BiomeInfo binfo = Biome.getInstance().getInfo(bid);
        temperature = binfo.temperature- (double)height*0.00166667f;
        humidity = binfo.rainfall;

        switch(bid){
        case Biome.Biome_Swampland:
            return 0x6a7039;
        case Biome.Biome_Roofed_Forest:
            tmp = this.getColor(temperature, humidity);
            return ((tmp & 0xfefefe) + 0x28340a) / 2;
        case Biome.Biome_Mesa:
        case Biome.Biome_Mesa_Plateau:
        case Biome.Biome_Mesa_Plateau_F:
            return  0x90814d;
        default:
            return this.getColor(temperature, humidity);
        }
    }
}
