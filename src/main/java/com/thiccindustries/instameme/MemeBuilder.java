package com.thiccindustries.instameme;

import java.awt.*;
import java.awt.image.BufferedImage;

public class MemeBuilder {
    private Template template;

    public MemeBuilder(){ template = null; }
    public MemeBuilder(Template t){ template = t; }
    public void SetTemplate(Template t){ template = t; }
    public Template GetTemplate() { return template; }

    public BufferedImage Build(String[] strings){
        BufferedImage meme = new BufferedImage(template.base.getWidth(), template.base.getHeight(), template.base.getType());

        Graphics2D memeGraphics = meme.createGraphics();

        //Copy base image
        memeGraphics.drawImage(template.base, 0, 0, null);

        //Print captions
        Caption captions[] = template.captions;

        for(int i = 0; i < captions.length; ++i){

            if(strings[i] == null || strings[i].equals(""))
                continue;

            int availWidth;
            int captionWidth = captions[i].GetWidth();
            if(captionWidth == -1)
                captionWidth = template.base.getWidth();

            if(captions[i].GetCentered()){
                /*If caption is centered, pos[0] is the center point of the text to center around
                 *Available with is the smallest of:
                 * *Max width of caption
                 * *distance to left edge
                 * *distance to right edge
                 */

                int centerPoint = captions[i].GetPosition()[0];

                int distanceToLeftEdge = (centerPoint * 2);
                int distanceToRightEdge = (template.base.getWidth() - centerPoint) * 2;

                availWidth = Math.min(Math.min(
                        captionWidth,
                        distanceToLeftEdge),
                        distanceToRightEdge);
            }else{
                /*Otherwise, available width is distance from point to right edge*/
                availWidth = template.base.getWidth() - captions[i].GetPosition()[0];
            }

            int fontSize = captions[i].GetFontSize();

            if(fontSize == -1)
                fontSize = 999;

            memeGraphics.setFont(new Font(captions[i].GetFontFamily(), captions[i].GetFontWeight(), fontSize));

            int textWidth = memeGraphics.getFontMetrics().stringWidth(strings[i]);

            //check if we need to shrink the fontsize
            //TODO: Repair this damage.
            while(textWidth > availWidth){
                fontSize--;
                memeGraphics.setFont(new Font(captions[i].GetFontFamily(), captions[i].GetFontWeight(), fontSize));
                textWidth = memeGraphics.getFontMetrics().stringWidth(strings[i]);
            }

            int x = captions[i].GetPosition()[0];

            if(captions[i].GetCentered())
                x -= textWidth / 2;

            memeGraphics.setColor(captions[i].GetColor());
            memeGraphics.drawString(strings[i], x, captions[i].GetPosition()[1]);
        }

        return meme;
    }
}
