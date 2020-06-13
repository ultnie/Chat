package ru.nsu.g.amaseevskii.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class ReadConfig {
    public static HashMap<String, Integer> readConfig() {
        InputStream conf = ReadConfig.class.getResourceAsStream("ServerConfig");
        BufferedReader br = new BufferedReader(new InputStreamReader(conf));
        String line = null;
        HashMap<String, Integer> confmap = new HashMap<>();
        try {
            while ((line = br.readLine()) != null) {
                String[] split = line.split("[= ]");
                if (split.length == 2 && split[1].matches("\\d+"))
                    confmap.put(split[0], Integer.parseInt(split[1]));
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return confmap;
    }
}
