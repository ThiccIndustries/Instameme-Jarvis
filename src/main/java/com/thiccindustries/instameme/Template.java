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
    public final BufferedImage overlay;


    public Template(JsonObject obj){

        if(!obj.containsKey("captions")){
            captions = null;
            base = null;
            overlay = null;
            return;
        }

        JsonArray captionJsonArray = obj.getJsonArray("captions");

        int captionCount = captionJsonArray.size();

        captions = new Caption[captionCount];
        for(int i = 0; i < captionCount; ++i){
            captions[i] = new Caption(captionJsonArray.getJsonObject(i));
        }

        String baseImageFilePath = "jarvis.png";
        if(obj.containsKey("image"))
            baseImageFilePath = obj.getString("image");

        BufferedImage bufferedImage = null;
        try{
            bufferedImage = ImageIO.read(new File("image/" + baseImageFilePath));
        }catch(IOException e){e.printStackTrace(); }

        base = bufferedImage;

        BufferedImage overlayImage = null;
        if(obj.containsKey("overlay")){
            String overlayImagePath = obj.getString("overlay");
            try{
                overlayImage = ImageIO.read(new File("image/" + overlayImagePath));
            }catch(IOException e){e.printStackTrace(); }
        }

        overlay = overlayImage;
    }

}
