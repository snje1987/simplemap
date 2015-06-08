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

    public void Draw(String from, String to){
        try{
            File file = new File(from);
            try (FileInputStream fis = new FileInputStream(file)) {
                buf = new byte[(int) file.length()];
                fis.read(buf);
            }

            BufferedImage img = new BufferedImage(512, 512, BufferedImage.TYPE_4BYTE_ABGR);

            int[] colors;

            int offset;
            for(int i = 0; i < ChunkPerFile; i ++){
                offset =((buf[4 * i] & 0xFF) << 16 )+((buf[4 * i + 1] & 0xFF) << 8) + ((buf[4 * i + 2] & 0xFF));
                if(offset == 0){
                    continue;
                }
                colors = this.DrawChunk(offset, img);
                if(colors == null){
                    break;
                }
                img.setRGB(i % 32 * 16, i / 32 * 16, 16, 16, colors, 0, 16);
                if(i > 3){
                    //break;
                }
            }
            ImageIO.write(img, "PNG", new File(to));
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public int[] DrawChunk(int offset, BufferedImage img){
        offset *= 4096;
        int chunksize = ((buf[offset] & 0xFF) << 24 )+((buf[offset+ 1] & 0xFF) << 16) + ((buf[offset + 2] & 0xFF) << 8) + (buf[offset + 3] & 0xFF);

        int method = buf[offset + 4] & 0xFF;

        byte[] tmp = new byte[chunksize - 1];
        System.arraycopy(buf, offset + 5, tmp, 0, chunksize - 1);

        Chunk chunk = new Chunk();
        chunk.Load(tmp);
        return chunk.CalSurface();
    }
}
