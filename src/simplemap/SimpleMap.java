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
public class SimpleMap{

    protected static final int ACT_PIXMAP = 0;
    protected static final int ACT_GENCOLOR = 1;
    protected static final int ACT_UPDATE = 2;

    protected static int index = 0;
    protected static String[] args = null;
    protected static int action = ACT_PIXMAP;
    protected static String src = "";
    protected static String dest = "";

    public static void main(String[] args){
        SimpleMap.args = args;
        if(!parseCmd()){
            System.exit(1);
        }
        World world;
        switch(action){
        case ACT_UPDATE:
            World.update = true;
        case ACT_PIXMAP:
            if(src.length() == 0 || dest.length() == 0){
                printHelp();
                System.exit(1);
            }
            world = new World();
            world.Draw(src, dest);
            break;
        case ACT_GENCOLOR:
            if(src.length() == 0){
                printHelp();
                System.exit(1);
            }
            ColorMap.getInstance().genColor(src);
            break;
        }
    }

    protected static void printHelp(){
        System.out.println("SimpleMap [选项1] [选项1值1] [选项1值2] ... [选项2] [选项2值1]...");
        System.out.println("选项包括:");
        System.out.println("-act");
        System.out.println("设置程序要进行的动作，可能值有：\npixmap 用存档生成像素地图，使用此项需要指定-src和－dest\ngencolor 用材质包生成颜色表，使用此项需要指定-src");
        System.out.println("-src");
        System.out.println("设置源数据路径，生成地图时为存档路径，生成颜色表时为材质包路径");
        System.out.println("-dest");
        System.out.println("设置输出目标路径，生成地图时为图片存储路径");
        System.out.println("-defcolor");
        System.out.println("不使用自定义颜色表，只使用默认颜色表");
    }

    protected static boolean parseCmd(){
        try{
            while(haveNext()){
                String opt = args[index ++];
                String tmp;
                switch(opt){
                case "-act":
                    tmp = nextString();
                    switch(tmp){
                    case "pixmap":
                        action = ACT_PIXMAP;
                        break;
                    case "update":
                        action = ACT_UPDATE;
                        break;
                    case "gencolor":
                        action = ACT_GENCOLOR;
                    }
                    break;
                case "-src":
                    src = nextString();
                    break;
                case "-dest":
                    dest = nextString();
                    break;
                case "-defcolor":
                    ColorMap.defcolor = true;
                    break;
                case "-noregen":
                    World.regen = false;
                    break;
                case "-center":
                    World.cx = nextInt();
                    World.cy = nextInt();
                    break;
                case "-wsize":
                    World.width = nextInt();
                    World.height = nextInt();
                    break;
                case "-mx":
                    World.mx_begin = nextInt();
                    World.mx_end = nextInt();
                    break;
                case "-mz":
                    World.mz_begin = nextInt();
                    World.mz_end = nextInt();
                    break;
                }
            }
        }
        catch(Exception e){
            printHelp();
            return false;
        }
        return true;
    }

    protected static boolean haveNext(){
        return index < args.length;
    }

    protected static String nextString(){
        return args[index ++];
    }

    protected static int nextInt(){
        return Integer.parseInt(args[index ++]);
    }
}
