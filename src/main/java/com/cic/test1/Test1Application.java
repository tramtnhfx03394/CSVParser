package com.cic.test1;

import com.cic.test1.entity.AthleteScore;
import com.cic.test1.repository.AthleteScoreRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SpringBootApplication
@Log4j2
public class Test1Application {
    public static void main(String[] args) throws Exception {
		ConfigurableApplicationContext context = SpringApplication.run(Test1Application.class, args);

        Map<String, Long> m = calculateScoreFromInput();

		AthleteScoreRepository repository = context.getBean(AthleteScoreRepository.class);

		List<AthleteScore> athleteScoreList  = m.entrySet().stream().map(x -> AthleteScore.builder()
                .athleteName(x.getKey())
                .totalScore(x.getValue())
                .build()).collect(Collectors.toList());

		repository.saveAll(athleteScoreList);
    }

    // calculate each athlete total score from input
    public static Map<String, Long> calculateScoreFromInput() throws Exception {
        Map<String, Long> m = new HashMap<>();

        try {
            InputStream inputStream = Test1Application.class.getResourceAsStream("/test.csv");
            InputStreamReader in = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader bufferedReader = new BufferedReader(in);
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
        } catch (Exception e) {
            throw new Exception(e);
        }

        return m;
    }

    //process strings from input
    public static String[] processInput(String input) {
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

    public static boolean isANumber(char c) {
        return '0' <= c && c <= '9';
    }
}
