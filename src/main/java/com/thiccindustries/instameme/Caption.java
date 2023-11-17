package com.thiccindustries.instameme;

import javax.json.JsonObject;
import javax.json.JsonArray;
import java.awt.*;

public class Caption {

    private final String fontFamily;
    private final int fontWeight; /*  Font.PLAIN 0
                                Font.BOLD 1
                                Font.ITALIC 2 */
    private final int fontSize;

    private final int[] position;
    private final int[] size;
    private final int[] alignment;
    private final int[] boxAlignment;
    private final int[] textAlignment;

    private final boolean shadow;

    private final Color shadowColor;
    private final Color color;

    public Caption(JsonObject obj){
        String ff = "Times New Roman";
        int fw = 0;
        int fs = -1;

        Color c = Color.WHITE;

        if(obj.containsKey("fontFamily"))
            ff = obj.getString("fontFamily");

        if(obj.containsKey("fontWeight"))
            fw = obj.getInt("fontWeight");

        if(obj.containsKey("fontSize"))
            fs = obj.getInt("fontSize");

        fontFamily = ff;
        fontSize = fs;
        fontWeight = fw;

        position = new int[]{0, 0};
        size = new int[]{-1, -1};
        alignment = new int[]{0, 0};
        boxAlignment = new int[]{0, 0};
        textAlignment = new int[]{0, 0};

        if(obj.containsKey("pos")) {
            JsonArray positionArray = obj.getJsonArray("pos");
            position[0] = positionArray.getInt(0);
            position[1] = positionArray.getInt(1);
        }

        if(obj.containsKey("align")){
            JsonArray array = obj.getJsonArray("align");
            alignment[0] = array.getInt(0);
            alignment[1] = array.getInt(1);
        }
        System.out.println(" { " + alignment[0] + ", " + alignment[1] + "} ");

        boxAlignment[0] = alignment[0];
        boxAlignment[1] = alignment[1];

        if(obj.containsKey("boxAlign")){
            JsonArray array = obj.getJsonArray("boxAlign");
            boxAlignment[0] = array.getInt(0);
            boxAlignment[1] = array.getInt(1);
        }

        System.out.println(" { " + boxAlignment[0] + ", " + boxAlignment[1] + "} ");

        textAlignment[0] = boxAlignment[0];
        textAlignment[1] = boxAlignment[1];

        if(obj.containsKey("textAlign")){
            JsonArray array = obj.getJsonArray("textAlign");
            textAlignment[0] = array.getInt(0);
            textAlignment[1] = array.getInt(1);
        }

        System.out.println(" { " + textAlignment[0] + ", " + textAlignment[1] + "} ");

        if(obj.containsKey("size")){
            JsonArray positionArray = obj.getJsonArray("size");
            size[0] = positionArray.getInt(0);
            size[1] = positionArray.getInt(1);
        }

        if(obj.containsKey("color"))
            c = Color.decode(obj.getString("color"));

        color = c;

        boolean s = false;
        if(obj.containsKey("shadow"))
            s = obj.getBoolean("shadow");

        shadow = s;

        Color sc = Color.black;
        if(obj.containsKey("shadowColor"))
            sc = Color.decode(obj.getString("shadowColor"));

        shadowColor = sc;
    }

    public int[] GetPosition(){ return position; }
    public int[] GetSize(){ return size; }
    public String GetFontFamily(){ return fontFamily; }
    public int GetFontWeight(){ return fontWeight; }
    public int GetFontSize(){ return fontSize; }
    public int[] GetAlignment(){ return alignment; }
    public int[] GetBoxAlignment(){ return boxAlignment; }
    public int[] GetTextAlignment(){ return textAlignment; }
    public boolean GetShadow(){ return shadow; }
    public Color GetColor(){ return color; }
    public Color GetShadowColor(){ return shadowColor; }

}

