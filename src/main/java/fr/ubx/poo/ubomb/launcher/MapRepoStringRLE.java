package fr.ubx.poo.ubomb.launcher;

import java.io.Reader;
import java.io.Writer;
import java.io.IOException;
import java.io.*;
import fr.ubx.poo.ubomb.launcher.MapLevel;

public class MapRepoStringRLE implements MapRepo {
    final char EOL = 'x';
    
    private String compression(MapLevel mapLevel){
        StringBuilder s = new StringBuilder();
        int count = 1;
        for(int i = 0; i < mapLevel.width(); i++){
            for (int j = 0; j < mapLevel.height(); j++){
                if (j+1 < mapLevel.height()){
                    if (mapLevel.get(i, j).getCode() == mapLevel.get(i, j+1).getCode()){
                        count++;
                    } else {
                        s.append(mapLevel.get(i, j).getCode());
                        if (count == 2){
                            s.append(mapLevel.get(i, j).getCode());
                            count = 1;
                        } else if (count > 2){
                            s.append(Integer.toString(count));
                            count = 1;
                        }
                    }
                } else {
                    s.append(mapLevel.get(i, j).getCode());
                    if (count == 2){
                        s.append(mapLevel.get(i, j).getCode());
                        count = 1;
                    } else if (count > 2){
                        s.append(Integer.toString(count));
                        count = 1;
                    }
                }

            }
            s.append("x");
        }
        return s.toString();
    }

    private String decompression(String string){
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < string.length(); i++){
            if (string.charAt(i) > '2' && string.charAt(i) <= '9'){
                for(int k = 1; k < (int) string.charAt(i) - '0'; k++) {
                    s.append(string.charAt(i - 1));
                }
            } else {
                s.append(string.charAt(i));
            }
        }
        return s.toString();
    }

    @Override
    public MapLevel load(String string) {
        int width = 0;
        int height = 0;
        string = decompression(string);
        for (int i = 0; i < string.length();i++){
            if (string.charAt(i) == EOL){
                width++;
            }
            if (string.charAt(i) == EOL && width == 1){
                height = i;
            }
        }
        if (width == 0){
            MapException Exception = new MapException("Missing eol character");
        }
        MapLevel mapLevel = new MapLevel(width, height);

        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){
                if (Entity.fromCode(string.charAt(j+i+(i*width))) == null){
                    MapException Exception = new MapException("Invalid character");
                }
                mapLevel.set(i, j, Entity.fromCode(string.charAt(j+i+(i*width))));
            }
        }
        return mapLevel;
    }

    @Override
    public String export(MapLevel mapLevel) {
        String s = new String();
        s = compression(mapLevel);
        return s;
    }

    public MapLevel load(Reader in) throws IOException{
        StringBuilder s = new StringBuilder();
        int r;
        while ((r = in.read()) != -1){
            s.append((char) r);
        }
        return load(s.toString());
    }

    public void export(MapLevel mapLevel, Writer ou) throws IOException{
        ou.write(export(mapLevel));
        ou.flush();
        return;
    }

}