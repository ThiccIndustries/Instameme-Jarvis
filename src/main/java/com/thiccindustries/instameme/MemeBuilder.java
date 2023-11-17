package com.thiccindustries.instameme;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class MemeBuilder {

    private final static boolean debug = false;

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
        Caption[] captions = template.captions;

        for(int i = 0; i < captions.length; ++i){

            if(strings[i] == null || strings[i].isEmpty())
                continue;

            int[] size = captions[i].GetSize();

            //Fix size -1
            if(size[0] == -1) size[0] = template.base.getWidth();
            if(size[1] == -1) size[1] = template.base.getHeight();

            int[] pos = AlignedPosToAbsolutePos(new int[]{template.base.getWidth(), template.base.getHeight()}, size, captions[i].GetAlignment(), captions[i].GetBoxAlignment(), captions[i].GetPosition());
            int[] bounds = AlignedBoundsToAbsoluteBounds(new int[]{template.base.getWidth(), template.base.getHeight()}, captions[i].GetAlignment(), pos, size);

            System.out.println("{" + pos[0] + ", " + pos[1] + "}, " + "{" + bounds[0] + ", " + bounds[1] + "}, ");

            int fontSize;
            {
                int maxFontSize = captions[i].GetFontSize();
                if (maxFontSize == -1) maxFontSize = 999;

                fontSize = CalculateMaximumFontSize(bounds, strings[i], captions[i].GetFontFamily(), captions[i].GetFontWeight(), maxFontSize);
            }

            System.out.println(fontSize);

            Font f = new Font(captions[i].GetFontFamily(), captions[i].GetFontWeight(), fontSize);

            String[] lines = BreakLines(strings[i], bounds[0], f);

            System.out.println("need " + lines.length + " lines for caption " + i);

            int lineHeightOffset = 0;

            //TODO: 1 and 2 feel very magic-numbery and i don't like it
            switch(captions[i].GetTextAlignment()[1]){
                case 0:
                    lineHeightOffset = fontSize;
                    break;
                case 1:
                    lineHeightOffset = -((lines.length - 3) * fontSize) / 2;
                    break;
                case 2:
                    lineHeightOffset = -((lines.length - 2) * fontSize);
                    break;
            }

            if(debug) {
                memeGraphics.setColor(Color.GREEN);
                memeGraphics.drawRect(pos[0], pos[1], bounds[0], bounds[1]);
                memeGraphics.setColor(Color.BLUE);
                memeGraphics.drawRect(0, 0, template.base.getWidth() / 2, template.base.getHeight() / 2);
                memeGraphics.drawRect(template.base.getWidth() / 2 - 1, 0, template.base.getWidth() / 2, template.base.getHeight() / 2);
                memeGraphics.drawRect(template.base.getWidth() / 2 - 1, template.base.getHeight() / 2 - 1, template.base.getWidth() / 2, template.base.getHeight() / 2);
                memeGraphics.drawRect(0, template.base.getHeight() / 2 - 1, template.base.getWidth() / 2, template.base.getHeight() / 2);
            }

            for (String line : lines) {

                System.out.println("draw line: " + line);
                int[] texPos = GetAbsoluteTextPosition(pos, bounds, captions[i].GetTextAlignment(), line, f);

                System.out.println("{" + texPos[0] + ", " + texPos[1] + "}");

                //draw line
                memeGraphics.setFont(f);

                if(captions[i].GetShadow()){
                    memeGraphics.setColor(captions[i].GetShadowColor());
                    memeGraphics.drawString(line, texPos[0]-2, texPos[1] + lineHeightOffset);
                    memeGraphics.drawString(line, texPos[0]+2, texPos[1] + lineHeightOffset);
                    memeGraphics.drawString(line, texPos[0], texPos[1]-2 + lineHeightOffset);
                    memeGraphics.drawString(line, texPos[0], texPos[1]+2 + lineHeightOffset);
                }

                memeGraphics.setColor(captions[i].GetColor());
                memeGraphics.drawString(line, texPos[0], texPos[1] + lineHeightOffset);


                lineHeightOffset += fontSize;
            }

            if(template.overlay != null)
                memeGraphics.drawImage(template.overlay, 0, 0, null);
        }

        return meme;
    }

    public static int[] AlignedPosToAbsolutePos(int[] image, int[] caption, int[] alignment, int[] box00, int[] alignedPos){
        //Convert aligned position to absolute position

        int[] positionOffsets = new int[2];
        int[] absPos = new int[2];

        switch(box00[0]){
            case 0:
                positionOffsets[0] = 0;
                break;
            case 1:
                positionOffsets[0] = caption[0] / 2;
                break;
            case 2:
                positionOffsets[0] = caption[0];
                break;
        }

        switch(box00[1]){
            case 0:
                positionOffsets[1] = 0;
                break;
            case 1:
                positionOffsets[1] = caption[1] / 2;
                break;
            case 2:
                positionOffsets[1] = caption[1];
                break;
        }

        switch(alignment[0]){
            case 0:
                absPos[0] =  alignedPos[0] - positionOffsets[0];
                break;
            case 1:
                absPos[0] = (image[0] / 2) - positionOffsets[0];
                break;
            case 2:
                absPos[0] = image[0] + alignedPos[0] - positionOffsets[0];
                break;
        }

        switch(alignment[1]){
            case 0:
                absPos[1] = alignedPos[1] - positionOffsets[1];
                break;
            case 1:
                absPos[1] = (image[1] / 2) + alignedPos[1] - positionOffsets[1];
                break;
            case 2:
                absPos[1] = image[1] + alignedPos[1] - positionOffsets[1];
                break;
        }

        return absPos;
    }

    public static int[] AlignedBoundsToAbsoluteBounds(int[] image, int[] alignment, int[] pos, int[] size){

        int[] bounds = new int[2]; //available width, available height

        bounds[0] = size[0];
        bounds[1] = size[1];

        return bounds;
    }

    public static int[] GetAbsoluteTextPosition(int[] pos, int[] bounds, int[] alignment, String s, Font f){
        int abspos[] = new int[2];

        switch(alignment[0]){
            case 0:
                abspos[0] = pos[0];
                break;
            case 1:
                abspos[0] = pos[0] + (bounds[0] / 2) - (GetWidthFromFontSize(s, f) / 2);
                break;
            case 2:
                abspos[0] = pos[0] + bounds[0] - GetWidthFromFontSize(s, f);
                break;
        }

        switch(alignment[1]){
            case 0:
                abspos[1] = pos[1];
                break;

            case 1:
                //if vertically centered we need line count so we can offset it correctly
                abspos[1] = pos[1] + bounds[1] / 2 - (int)(f.getSize() * 1.333 / 2);
                break;

            case 2:
                abspos[1] = pos[1] + bounds[1] - (int)(f.getSize() * 1.333);
                break;
        }

        return abspos;
    }

    //TODO: this is the worst thing ever probably
    public static int CalculateMaximumFontSize(int[] bounds, String s, String fontFamily, int fontWeight, int maxFont) {

        int fontSize = maxFont + 1;

        int numLines;
        do {
            fontSize--;
            Font f = new Font(fontFamily, fontWeight, fontSize);
            numLines = BreakLines(s, bounds[0], f).length;
        }
        while((numLines * fontSize) + fontSize > bounds[1]); //first practical use of do-while?

        return fontSize;
    }

    public static int GetWidthFromFontSize(String s, Font font){
        return new Canvas().getFontMetrics(font).stringWidth(s); //java moment
    }

    public static String[] BreakLines(String s, int width, Font f){
        String words[] = s.split(" ");
        String lines[] = new String[words.length];

        for(int i = 0; i < lines.length; ++i){
            lines[i] = "";
        }

        int lineCount = 0;
        int i = 0;

        while(i < words.length){
            lines[lineCount] += words[i] + " ";
            i++;

            if(GetWidthFromFontSize((lines[lineCount]), f) > width){
                i--;
                System.out.println(lines[lineCount]);
                lines[lineCount] = lines[lineCount].substring(0, lines[lineCount].length() - words[i].length() - 2);
                lineCount++;
            }
        }

        String finalLines[] = new String[lineCount + 1];
        for(int j = 0; j < lineCount + 1; ++j){
            finalLines[j] = lines[j];
        }

        return finalLines;
    }

}
