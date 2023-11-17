package com.thiccindustries.jarvis;


import com.thiccindustries.instameme.Instameme;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.utils.FileUpload;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;

public class Bot extends ListenerAdapter {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("You have to provide a token as first argument!");
            System.exit(1);
        }
        // args[0] would be the token (using an environment variable or config file is preferred for security)
        // We don't need any intents for this bot. Slash commands work without any intents!
        JDA jda = JDABuilder.createLight(args[0], Collections.emptyList())
                .addEventListeners(new Bot())
                .setActivity(Activity.watching("\u200E"))
                .build();

        jda.updateCommands().addCommands(
                Commands.slash("jarvis", "{format},{string 1},{string n}")
                        .addOption(OptionType.STRING, "template", "Meme template", true)
                        .addOption(OptionType.STRING, "caption0", "Caption 1", false)
                        .addOption(OptionType.STRING, "caption1", "Caption 2", false)
                        .addOption(OptionType.STRING, "caption2", "Caption 3", false)
                        .addOption(OptionType.STRING, "caption3", "Caption 4", false)
                        .addOption(OptionType.STRING, "caption4", "Caption 5", false)
                        .addOption(OptionType.STRING, "caption5", "Caption 6", false)
                        .addOption(OptionType.STRING, "caption6", "Caption 7", false)
                        .addOption(OptionType.STRING, "caption7", "Caption 8", false),
                Commands.slash("jarvis-list", "List all formats available to J.A.R.V.I.S.")
        ).queue();
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if(!(event.getName().equals("jarvis") || event.getName().equals("jarvis-list")))
            return;

        if(event.getName().equals("jarvis-list")){
            System.out.println("here");
            File folder = new File("format");
            File[] formatlist = folder.listFiles();
            StringBuilder sb = new StringBuilder();
            sb.append("I can give you ");
            for(int i = 0; i < formatlist.length; ++i){
                if(!formatlist[i].isFile() || formatlist[i].getName().startsWith("."))
                    continue;

                sb.append("``");
                sb.append(formatlist[i].getName().substring(0, formatlist[i].getName().length() - 5));
                sb.append("``");
                if(i < formatlist.length - 2)
                    sb.append(", ");

                if(i == formatlist.length - 2)
                    sb.append(" and ");
            }
            sb.append(" formatted memes Sir.");

            event.reply(sb.toString()).setEphemeral(true).queue();
            return;
        }

        String template = event.getOption("template", OptionMapping::getAsString);

        String args[] = new String[10];

        args[0] = "temp.png";
        args[1] = "format/" + template + ".json";

        for(int i = 0; i < 8; ++i){
            args[i + 2] = event.getOption("caption" + i, OptionMapping::getAsString);
        }

        int result = Instameme.run(args);

        if(result != 0){
            String error = "Unknown error code. Consult console.";
            switch(result){
                case -1:
                    error = "Invalid syntax sent to IM. Consult console.";
                    break;
                case -2:
                    error = "I'm sorry Sir. I don't know about the ``" + template + "`` format.";
                    break;
                case -3:
                    error = "Unable to read image/captions given by " + template + ".json";
                    break;
                case -4:
                    error = "Unknown error while building meme. Consult console.";
                    break;
                case -5:
                    error = "Unable to write temporary file to disk. Consult console.";
                    break;
            }

            event.reply(error).setEphemeral(true).queue();
            return;
        }

        event.reply("Just a moment Sir.").setEphemeral(true).queue();

        FileUpload file = FileUpload.fromData(new File("temp.png"), "instameme.png");

        event.getChannel().sendMessage("").addFiles(file).queue();

        new File("temp.png").delete();
    }
}
