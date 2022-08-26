package com.csvparser;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class ParserTest {

    @Test
    void calculateScoreFromInputTest() throws Exception {
        String fileName = "test_input.csv";

        if (!Files.exists(Paths.get(fileName)))
            Files.createFile(Paths.get(fileName));

        PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(fileName)));
        writer.append("John,40\n");
        writer.append("Bob,10\n");
        writer.append("John,50\n");
        writer.append("Bob,10\n");
        writer.append("John,10\n");
        writer.append("Bob,10\n");
        writer.append("Alice,400\n");
        writer.append("Bob,10\n");
        writer.close();

        Map<String, Long> scores = Parser.calculateScoreFromInput(fileName);
        assertEquals(scores.size(), 3);
        assertEquals(scores.get("Bob"), 40);
        assertEquals(scores.get("Alice"), 400);
        assertEquals(scores.get("John"), 100);

    }

    @Test
    void stringToPascalCase() {
        assertEquals(Parser.toPascalCase("John Doe"), "JohnDoe");
        assertEquals(Parser.toPascalCase("John doe"), "JohnDoe");
        assertEquals(Parser.toPascalCase("john doe"), "JohnDoe");
        assertEquals(Parser.toPascalCase("John doe Bob"), "JohnDoeBob");
    }

    @Test
    void intToPascalCase() {
        assertEquals(Parser.toPascalCase("111 doe Bob"), "111DoeBob");
        assertEquals(Parser.toPascalCase("bob 11"), "Bob11");
        assertEquals(Parser.toPascalCase(" 000 11 "), "00011");
    }

    @Test
    void outputStatsToFileTest() throws Exception {
        Map<String, Long> data = new HashMap<>();
        data.put("Bob", 100L);
        data.put("Alice", 1000L);
        data.put("John", 400L);

        String fileName = "test_output.txt";
        if (!Files.exists(Paths.get(fileName)))
            Files.createFile(Paths.get(fileName));
        Parser.outputStatsToFile(fileName, data);

        String topAthlete = "";
        Long topScore = 0L;
        Integer totalAthlete = 0;
        Map<String, Long> res = new HashMap<>();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            if (line.isEmpty()) continue;
            String[] str = line.split(" ", -1);
            if (str[0].equals("Total") && str[1].equals("athlete:")) {
                totalAthlete = Integer.valueOf(str[2]);
                continue;
            }
            if (str[0].equals("Top") && str[1].equals("score:")) {
                topAthlete = str[3];
                topScore = Long.valueOf(str[str.length - 1]);
                continue;
            }
            res.put(str[0], Long.valueOf(str[str.length - 1]));
        }

        assertEquals(res.size(), data.size());
        assertEquals(res.get("Bob"), data.get("Bob"));
        assertEquals(res.get("Alice"), data.get("Alice"));
        assertEquals(res.get("John"), data.get("John"));
        assertEquals(topScore, data.get("Alice"));
        assertEquals(topAthlete, "Alice");
        assertEquals(totalAthlete, data.size());
    }

}
