package com.thiccindustries.instameme;

import javax.imageio.ImageIO;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class Instameme {

    public static int run(String[] args){
        if(args.length < 3){
            PrintUsage();
            return -1; //too few args
        }

        String outputFilePath = args[0];
        String templateString = args[1];
        String strings[] = Arrays.copyOfRange(args, 2, args.length);

        JsonObject templateJson = readJsonFile(templateString);

        if(templateJson == null){
            System.out.println("invalid or missing json");
            return -2;
        }

        Template template = new Template(templateJson);

        if(template.base == null){
            System.out.println("Failed to read image");
            return -3;
        }

        MemeBuilder builder = new MemeBuilder(template);

        //Build meme
        BufferedImage outimage = builder.Build(strings);

        if(outimage == null) {
            System.err.println("Failed to build meme");
            return -4;
        }

        File outFile = new File(outputFilePath);

        try {
            ImageIO.write(outimage, "png", outFile);
        } catch (IOException e) {
            //e.printStackTrace();
            System.err.println("Failed to write image to file");
            return -5;
        }

        return 0;
    }

    public static void main(String[] args){
        run(args);
    }

    public static void PrintUsage(){
        System.out.println("Usage: java -jar Instameme {output} {format} {string 1} {string 2} ... {string n}");
    }

    public static JsonObject readJsonFile(String path){
        try {
            String jsonString = new String(Files.readAllBytes(Paths.get(path)));

            JsonReader reader = Json.createReader(new StringReader(jsonString));
            JsonObject obj = reader.readObject();

            if(obj == null){
                System.err.println("Failed to parse json file!");
            }

            System.out.println("Format loaded: ");
            System.out.println(obj);
            return obj;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
