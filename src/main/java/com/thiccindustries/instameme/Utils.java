package com.thiccindustries.instameme;

import javax.json.*;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public class Utils {
    public static JsonObject LoadJsonFileRecursive(String path){
        try{

            String jsonString = new String(Files.readAllBytes(Paths.get(path)));
            JsonReader reader = Json.createReader(new StringReader(jsonString));

            System.out.println(jsonString);

            JsonObject obj = reader.readObject();

            if(obj == null){
                System.err.println("Invalid/Missing JSON File: " + path);
                return null;
            }

            //Why is jsonobject immutable
            //if it is, why does it have put?
            //huh?

            //Add original file to job
            JsonObjectBuilder job = Json.createObjectBuilder();
            for(Map.Entry<String, JsonValue> originalEntry : obj.entrySet()){
                job.add(originalEntry.getKey(), originalEntry.getValue());
            }

            //Recurse
            if(obj.containsKey("include")){
                job.remove("include");

                //Load included file
                JsonObject obj2 = LoadJsonFileRecursive("format/" + obj.getString("include"));

                if(obj2 == null){
                    System.err.println("Failed to include JSON file: " + path);
                    return null;
                }

                //Merge unoverrided entries
                for(Map.Entry<String, JsonValue> newEntry : obj2.entrySet()){
                    if(!obj.containsKey(newEntry.getKey())) {
                        job.add(newEntry.getKey(), newEntry.getValue());
                    }
                }
            }

            System.out.println("Utils: " + obj.toString());
            return job.build();

        }catch(Exception e){
            System.err.println("Uncaught error:");
            e.printStackTrace();
            return null;
        }
    }
}
