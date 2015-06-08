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

/**
 *
 * @author Yang Ming <yangming0116@163.com>
 */
public class Color{
    public double r;
    public double g;
    public double b;
    public double alpha;
    public String name;
    public String tname;

    public Color(int color, int alpah){
        r = (color & 0x00FF0000) >> 16;
        g = (color & 0x0000FF00) >> 8;
        b = (color & 0x000000FF);
        this.alpha = alpah;
    }

    public Color(int color){
        r = (color & 0x00FF0000) >> 16;
        g = (color & 0x0000FF00) >> 8;
        b = (color & 0x000000FF);
        alpha = (color & 0xFF000000L) >> 24;
    }

    public void setColor(int color){
        r = (color & 0x00FF0000) >> 16;
        g = (color & 0x0000FF00) >> 8;
        b = (color & 0x000000FF);
        alpha = (color & 0xFF000000L) >> 24;
    }

    public void setColor(int color, int alpah){
        r = (color & 0x00FF0000) >> 16;
        g = (color & 0x0000FF00) >> 8;
        b = (color & 0x000000FF);
        this.alpha = alpah;
    }

    public int toInt(){
        return this.toInt(true);
    }

    public int toInt(boolean include_alpha){
        int ret;
        if(include_alpha){
            ret = ((((int)r & 0xFF) << 16) | (((int)g & 0xFF) << 8) | ( (int)b & 0xFF )) | (((int)alpha & 0xFF) << 24);
        }
        else{
            ret = ((((int)r & 0xFF) << 16) | (((int)g & 0xFF) << 8) | ((int)b & 0xFF ));
        }
        return ret;
    }

    public int getAlpha(){
        return (int)alpha;
    }

    public Color applyAlpha(int alpha){
        Color ret = new Color(0, 0);
        ret.r = r * alpha / 255;
        ret.b = b * alpha / 255;
        ret.g = g * alpha / 255;
        return ret;
    }

    public void merge(Color right){
        if(alpha >= 255){
            return;
        }
        double rfactor = (255 - alpha) * right.alpha / 255;
        if(rfactor <= 1){
            rfactor = 1;
        }
        r = r + right.r * rfactor / 255;
        g = g + right.g * rfactor / 255;
        b = b + right.b * rfactor / 255;
        r = (r > 255) ? 255  : r;
        g = (g > 255) ? 255  : g;
        b = (b > 255) ? 255  : b;
        alpha = (alpha + rfactor > 255) ? 255 : (alpha + rfactor);
    }
}
