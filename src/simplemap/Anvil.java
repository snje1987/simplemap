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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import javax.imageio.ImageIO;

/**
 *
 * @author Yang Ming <yangming0116@163.com>
 */
public class Anvil{

    private byte[] buf = null;

    public static final int ChunkPerFile = 1024;
    public static final int IMGSIZE = 512;
    public static final int ShadowMax = 64;
    public static final double shadowFactor = 0.5;

    public void Draw(String from, String to){
        try{
            File file = new File(from);
            try (FileInputStream fis = new FileInputStream(file)) {
                buf = new byte[(int) file.length()];
                fis.read(buf);
            }

            BufferedImage img = new BufferedImage(IMGSIZE, IMGSIZE, BufferedImage.TYPE_4BYTE_ABGR);
            Point[][] pt = new Point[IMGSIZE][IMGSIZE];

            int offset;
            for(int i = 0; i < ChunkPerFile; i ++){
                offset =((buf[4 * i] & 0xFF) << 16 )+((buf[4 * i + 1] & 0xFF) << 8) + ((buf[4 * i + 2] & 0xFF));
                if(offset == 0){
                    continue;
                }
                if(!this.DrawChunk(offset, pt, i % 32 * 16, i / 32 * 16)){
                    break;
                }
            }
            for(int i = 0; i < IMGSIZE; i++){
                for(int j = 0; j < IMGSIZE; j++){
                    if(pt[i][j] == null){
                        continue;
                    }
                    int factor = 0;
                    if(i > 0 && pt[i - 1][j] != null && pt[i][j].height < pt[i - 1][j].height){
                        factor -= pt[i - 1][j].height - pt[i][j].height;
                    }
                    if(i < IMGSIZE - 1 && pt[i + 1][j] != null && pt[i][j].height < pt[i + 1][j].height){
                        factor += pt[i + 1][j].height - pt[i][j].height;
                    }
                    if(j > 0 && pt[i][j - 1] != null && pt[i][j].height < pt[i][j - 1].height){
                        factor -= pt[i][j - 1].height - pt[i][j].height;
                    }
                    if(j < IMGSIZE - 1 && pt[i][j + 1] != null && pt[i][j].height < pt[i][j + 1].height){
                        factor += pt[i][j + 1].height - pt[i][j].height;
                    }
                    if(factor == 0){
                        img.setRGB(i, j, pt[i][j].color.toInt());
                    }
                    else{
                        if(factor > 0){
                            img.setRGB(i, j, pt[i][j].color.changeBright((ShadowMax * (1 - Math.pow(shadowFactor, factor)))).toInt());
                        }
                        else{
                            img.setRGB(i, j, pt[i][j].color.changeBright((-1 * ShadowMax * (1 - Math.pow(shadowFactor, factor * -1)))).toInt());
                        }
                    }
                }
            }
            ImageIO.write(img, "PNG", new File(to));
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public boolean DrawChunk(int offset, Point[][] pt, int x, int z){
        offset *= 4096;
        int chunksize = ((buf[offset] & 0xFF) << 24 )+((buf[offset+ 1] & 0xFF) << 16) + ((buf[offset + 2] & 0xFF) << 8) + (buf[offset + 3] & 0xFF);

        int method = buf[offset + 4] & 0xFF;

        byte[] tmp = new byte[chunksize - 1];
        System.arraycopy(buf, offset + 5, tmp, 0, chunksize - 1);

        Chunk chunk = new Chunk();
        chunk.Load(tmp);
        return chunk.CalSurface(pt, x, z);
    }
}
