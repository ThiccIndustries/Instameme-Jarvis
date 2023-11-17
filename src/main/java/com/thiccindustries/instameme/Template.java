package com.thiccindustries.instameme;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.json.*;

public class Template {
    public final Caption[] captions;
    public final BufferedImage base;
    public Template(Caption[] captions, BufferedImage base){
        this.captions = captions;
        this.base = base;
    }

    public Template(JsonObject obj){
        int captionCount = obj.getInt("count");
        JsonArray captionJsonArray = obj.getJsonArray("captions");

        captions = new Caption[captionCount];
        for(int i = 0; i < captionCount; ++i){
            captions[i] = new Caption(captionJsonArray.getJsonObject(i));
        }

        String baseImageFilePath = obj.getString("image");

        BufferedImage bufferedImage = null;
        try{
            bufferedImage = ImageIO.read(new File("image/" + baseImageFilePath));
        }catch(IOException e){e.printStackTrace(); }

        base = bufferedImage;
    }
}
