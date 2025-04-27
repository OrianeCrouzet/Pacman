package Util;

import display.screen.Labyrinth;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.stream.Stream;

public final class FileHandler {

    private FileHandler() {}

    public static String[] readFile(String path){
        try (InputStream inputStream = Labyrinth.class.getClassLoader().getResourceAsStream(path)) {
            if (inputStream == null) {
                throw new IOException("File not found");
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            Stream<String> lines = reader.lines();
            if(lines != null){
                return lines.toArray(String[]::new);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String[0];
    }

    public static int[][] streamStringToIntDoubleArray(String[] mapString){
        int cols = mapString.length;
        int rows = mapString[0].chars().toArray().length;
        int[][] intArray = new int[cols][rows];
        for(int c = 0; c < mapString.length; c++){
            for (int r = 0; r < mapString[c].length(); r++) {
                intArray[c] = Arrays.stream(mapString[c].split("")).mapToInt(Integer::parseInt).toArray();
            }
        }

        return intArray;
    }

    public static int[][] turnMap(int[][] old){
        int col = old[0].length;
        int raw = old.length;

        int[][] newM = new int[col][raw];
        for (int c = 0; c < old.length; c++) {
            for (int r = 0; r < old[0].length; r++) {
                newM[r][c] = old[c][r];

            }

        }
        return newM;
    }








}
