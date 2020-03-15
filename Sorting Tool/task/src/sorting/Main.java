package sorting;

import java.io.*;
import java.util.*;

public class Main {
    public static void main(final String[] args) {
        HashMap<String, String> options = makeOptions(args);
        if (options.containsKey("errors")) {
            System.out.println(options.get("errors"));
            return;
        }

        Scanner scanner = new Scanner(System.in);
        String inputFile = options.getOrDefault("-inputFile", "");
        File file = new File(inputFile);

        if (!inputFile.equals("")) {
            try {
                scanner = new Scanner(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return;
            }
        }

        String dataType = options.getOrDefault("-dataType", "line");
        String sortType = options.getOrDefault("-sortingType", "natural");
        String outFile = options.getOrDefault("-outputFile", "");
        sortInputData(dataType, sortType, scanner, outFile);
        scanner.close();


    }

    static void sortInputData(String dataType, String sortType, Scanner scanner, String outFile) {
        HandleData dataHandler;
        if (dataType.equals("long")) {
            dataHandler = new HandleData(sortType, scanner, outFile);
        } else if (dataType.equals("line")) {
            dataHandler = new HandleLineData(sortType, scanner, outFile);
        } else {
            dataHandler = new HandleWordData(sortType, scanner, outFile);
        }
        dataHandler.handle();
    }

    static HashMap<String, String> makeOptions(String[] args) {
        List<String> argsCopy = new ArrayList<>(Arrays.asList(args));
        HashMap<String, String> options = new HashMap<>();

        while (argsCopy.size() > 0) {
            String key = argsCopy.remove(0);
            try {
                String value = argsCopy.remove(0);
                options.put(key, value);
            } catch (IndexOutOfBoundsException exc) {
                if (key.equals("-sortingType")) {
                    options.put("errors", "No sorting type defined!");
                } else {
                    options.put("errors", "No data type defined!");
                }

            }

        }
        if (options.containsKey("-inputFile")) {
            if (!options.containsKey("-outputFile")) {
                options.put("errors", "There is no output file out.txt");
            }
        }
        return options;

    }


    static <key, value extends Comparable<? super value>> SortedSet<Map.Entry<key, value>> entriesSortedByValues(Map<key, value> map) {
        SortedSet<Map.Entry<key, value>> sortedEntries = new TreeSet<>(
                (Map.Entry<key, value> element, Map.Entry<key, value> element2) -> {
                    int result = element.getValue().compareTo(element2.getValue());
                    return result != 0 ? result : 1; // Special fix to preserve items with equal values
                }
        );
        sortedEntries.addAll(map.entrySet());
        return sortedEntries;
    }

}


class HandleData {
    protected int totalNumberSeen = 0;
    protected String sortType;
    protected String dataType = "numbers";
    protected Scanner scanner;
    protected TreeMap<Object, Integer> sortedUserInputCount = new TreeMap<>();
    protected String outFile;
    protected Boolean useWriter = false;
    protected FileWriter writer;

    /*

     */
    public HandleData() {
    }

    public HandleData(String sortType, Scanner scanner, String outFile) {
        this.sortType = sortType;
        this.scanner = scanner;
        this.outFile = outFile;
        if (!outFile.equals("")) {
            this.useWriter = true;
            try {
                this.writer = new FileWriter(outFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    protected boolean hasUserInput() {
        return scanner.hasNextLong();

    }

    protected Object getUserInput() {
        return scanner.nextLong();
    }

    protected void outputMessage(String message) {
        if (useWriter) {
            try {
                writer.write(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.print(message);
        }
    }

    public void handle() {

        while (hasUserInput()) {
            Object num_word_or_line = getUserInput();
            int numberCount = sortedUserInputCount.getOrDefault(num_word_or_line, 0);
            sortedUserInputCount.put(num_word_or_line, numberCount + 1);
            totalNumberSeen++;
        }
        outputMessage(String.format("Total %s: %s.\n", dataType, totalNumberSeen));

        if (sortType.equals("natural")) {
            System.out.print("Sorted data: ");
            for (Object key : sortedUserInputCount.keySet()) {
                int repeatNumber = sortedUserInputCount.get(key);
                for (int i = 0; i < repeatNumber; i++) {
                    outputMessage(String.format("%s ", key));
                }
            }
            return;
        }
        SortedSet<Map.Entry<Object, Integer>> sorted = sorting.Main.entriesSortedByValues(sortedUserInputCount);

        for (Map.Entry<Object, Integer> number : sorted) {
            int numberCount = number.getValue();
            double percentOfTotal = ((double) numberCount / (double) totalNumberSeen) * 100;
            outputMessage(String.format("%s: %s time(s), %s%%.\n", number.getKey(), numberCount, (int) percentOfTotal));

        }

    }

}

class HandleWordData extends HandleData {
    public HandleWordData(String sorType, Scanner scanner, String outFile) {
        super(sorType, scanner, outFile);
        this.dataType = "words";
    }

    @Override
    protected boolean hasUserInput() {
        return scanner.hasNext();
    }

    @Override
    protected Object getUserInput() {
        return scanner.next();
    }
}

class HandleLineData extends HandleData {
    public HandleLineData(String sorType, Scanner scanner, String outFile) {
        super(sorType, scanner, outFile);
        this.dataType = "lines";
    }

    @Override
    protected boolean hasUserInput() {
        return scanner.hasNextLine();
    }

    @Override
    protected Object getUserInput() {
        return scanner.nextLine();

    }
}