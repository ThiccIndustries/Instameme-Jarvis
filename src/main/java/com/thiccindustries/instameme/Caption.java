package com.thiccindustries.instameme;

import javax.json.JsonObject;
import javax.json.JsonArray;
import java.awt.*;

public class Caption {
    private final int[] position;
    private final int width;
    private String fontFamily;
    private int fontWeight; /*  Font.PLAIN 0
                                Font.BOLD 1
                                Font.ITALIC 2 */
    private int fontSize;
    private boolean centered;
    private Color color;

    public Caption(int[] position, int width){
        this.position = position;
        this.width = width;
    }

    public Caption(JsonObject obj){
        fontFamily = obj.getString("fontFamily");
        fontWeight = obj.getInt("fontWeight");
        fontSize = obj.getInt("fontSize");

        width = obj.getInt("maxWidth");
        centered = obj.getBoolean("centered");

        JsonArray positionArray = obj.getJsonArray("pos");

        position = new int[2];

        position[0] = positionArray.getInt(0);
        position[1] = positionArray.getInt(1);

        color = Color.decode(obj.getString("color"));
    }

    public int[] GetPosition(){ return position; }
    public int GetWidth(){ return width; }
    public String GetFontFamily(){ return fontFamily; }
    public int GetFontWeight(){ return fontWeight; }
    public int GetFontSize(){ return fontSize; }
    public boolean GetCentered(){ return centered; }
    public Color GetColor(){ return color; }

}

