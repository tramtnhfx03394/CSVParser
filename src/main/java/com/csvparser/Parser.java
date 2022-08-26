package com.csvparser;

import com.csvparser.entity.AthleteScore;
import com.csvparser.repository.AthleteScoreRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SpringBootApplication
@Log4j2
public class Parser {
    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext context = SpringApplication.run(Parser.class, args);
        if (args.length > 2) {
            Map<String, Long> m = calculateScoreFromInput(args[0]);

            AthleteScoreRepository repository = context.getBean(AthleteScoreRepository.class);

            List<AthleteScore> athleteScoreList = m.entrySet().stream().map(x -> AthleteScore.builder()
                    .athleteName(toPascalCase(x.getKey()))
                    .totalScore(x.getValue())
                    .build()).collect(Collectors.toList());

            repository.saveAll(athleteScoreList);

            outputStatsToFile(args[2], m);
        }
    }

    // calculate each athlete total score from input
    public static Map<String, Long> calculateScoreFromInput(String filePath) throws Exception {
        Map<String, Long> m = new HashMap<>();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            if (line.isEmpty()) continue;
            String[] values = processInput(line);
            if (m.containsKey(values[0])) {
                m.put(values[0], m.get(values[0]) + Long.parseLong(values[1]));
            } else {
                m.put(values[0], Long.valueOf(values[1]));
            }
        }
        log.info(m);
        return m;
    }

    //process strings from input
    private static String[] processInput(String input) {
        int splitPoint = 0;
        String[] res = new String[2];
        for (int i = input.length() - 1; i >= 0; i--) {
            if (input.charAt(i) == ',') {
                splitPoint = i;
                break;
            }
            if (!isANumber(input.charAt(i))) {
                res[0] = input.substring(0, i + 1);
                res[1] = "0";
                return res;
            }
        }

        res[0] = input.substring(0, splitPoint);
        res[1] = input.substring(splitPoint + 1);
        return res;
    }

    private static boolean isANumber(char c) {
        return '0' <= c && c <= '9';
    }

    public static String toPascalCase(String str) {
        str = str.trim();
        List<Character> res = new ArrayList<>();
        res.add(Character.toUpperCase(str.charAt(0)));

        for (int i = 1; i < str.length(); i++) {
            if (str.charAt(i) == ' ') {
                continue;
            }
            if (str.charAt(i - 1) == ' ') {
                res.add(Character.toUpperCase(str.charAt(i)));
                continue;
            }
            res.add(str.charAt(i));
        }
        return res.stream()
                .map(String::valueOf)
                .collect(Collectors.joining());
    }

    public static void outputStatsToFile(String fileName, Map<String, Long> data) throws Exception {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName)));
        long highScore = 0;
        String topAthlete = "";

        for (String key : data.keySet()) {
            if (data.get(key) >= highScore) {
                highScore = data.get(key);
                topAthlete = key;
            }
            writer.append(key).append(" with total score: ").append(String.valueOf(data.get(key)));
            writer.newLine();
        }

        writer.append("Total athlete: ").append(String.valueOf(data.size()));
        writer.newLine();
        writer.append("Top score: Athlete ").append(topAthlete).append(" with ").append(String.valueOf(highScore));
        writer.close();
    }

}
