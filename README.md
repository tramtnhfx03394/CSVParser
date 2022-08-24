# CSVParser

## Main idea
- An application that reads data from the CSV file, processes data, caculates and maps it into database
- CSV file contains 2 columns: athlete name and score so the data read from each line will be split by this rule: The right column will be the score so start scanning from right to left, the first comma we encounter will be the delimter of name and score so the string will be split into 2 parts and put into a hashmap with the key will be the name of the athlete. If we run into a character first, that's mean the score is null, so the whole string will be put into map as key and 0 for the value
- After done calculate the total score of each athlete, the data will be put in the database

## Comment On CSV File
- Alot of duplicate name, row and empty line
- Names contain characters that's not usually seen in name like '"' or ','
- Big score number
