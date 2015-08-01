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

    public Color(int color, int alpah){
        r = (color & 0x00FF0000) >> 16;
        g = (color & 0x0000FF00) >> 8;
        b = (color & 0x000000FF);
        this.alpha = alpah;
    }

    public Color(int r, int g, int b, int a){

        this.r = r;
        this.g = g;
        this.b = b;
        this.alpha = a;

        this.r = (this.r > 255) ? 255  : this.r;
        this.g = (this.g > 255) ? 255  : this.g;
        this.b = (this.b > 255) ? 255  : this.b;
        this.alpha = (this.alpha > 255) ? 255  : this.alpha;

        this.r = (this.r < 0) ? 0  : this.r;
        this.g = (this.g < 0) ? 0  : this.g;
        this.b = (this.b < 0) ? 0  : this.b;
        this.alpha = (this.alpha < 0) ? 0  : this.alpha;
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
        if(right.alpha <= 0.01){
            return;
        }
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

    public Color changeBright(double offset){
        r = r + offset;
        b = b + offset;
        g = g + offset;

        r = (r > 255) ? 255  : r;
        g = (g > 255) ? 255  : g;
        b = (b > 255) ? 255  : b;

        r = (r < 0) ? 0  : r;
        g = (g < 0) ? 0  : g;
        b = (b < 0) ? 0  : b;

        return this;
    }

    public void mutiply(int multiplier){
        this.r *= (double)(multiplier >> 16 & 255) / 255.0F;
        this.g *= (double)(multiplier >> 8 & 255) / 255.0F;
        this.b *= (double)(multiplier & 255) / 255.0F;
    }

    public String toString(){
        return String.format("[%d %d %d %d]", (int)r, (int)g, (int)b, (int)alpha);
    }
}
