import java.util.*;

public class HelperClass {

    static final Scanner scanner = new Scanner(System.in);

    static String createChoiceMenuString(String title, String... options) {
        StringBuilder sb = new StringBuilder();
        sb.append(title);
        sb.append("\n");
        for (int i = 0; i < options.length; i++) {
            sb.append(i + 1);
            sb.append(") ");
            sb.append(options[i]);
            sb.append('\n');
        }
        return sb.toString();
    }

    static String createChoiceMenuString(String title, ArrayList<String> options) {
        StringBuilder sb = new StringBuilder();
        sb.append(title);
        sb.append("\n");
        for (int i = 0; i < options.size(); i++) {
            sb.append(i + 1);
            sb.append(") ");
            sb.append(options.get(i));
            sb.append('\n');
        }
        return sb.toString();
    }

    static int simpleMenu(String title, String inputQuestion, String... options) {
        System.out.print(createChoiceMenuString(title, options));
        return getInputInt(inputQuestion, 1, options.length);
    }

    static int simpleMenu(String title, String inputQuestion, ArrayList<String> options) {
        System.out.print(createChoiceMenuString(title, options));
        return getInputInt(inputQuestion, 1, options.size());
    }

    static String getInputText(String question) {
        System.out.print(question);
        return scanner.nextLine();
    }

    static int getInputInt(String question) {
        System.out.print(question);

        while (true) {
            var input = scanner.nextLine();

            try {
                return Integer.parseInt(input);
            } catch (Exception e) {
                System.out.print("Ungueltige Eingabe! Bitte geben Sie eine Zahl ein: ");
            }
        }
    }

    // Overloaded method GetInputInt with value boundaries
    static int getInputInt(String question, int startBoundary, int endBoundary) {
        System.out.print(question);

        while (true) {
            var input = scanner.nextLine();

            try {
                var intInput = Integer.parseInt(input);

                if (startBoundary > intInput || endBoundary < intInput) {
                    System.out.print("Bitte geben Sie eine Nummer von " + startBoundary + " bis " + endBoundary + " ein: ");
                    continue;
                }

                return intInput;
            } catch (Exception e) {
                System.out.print("Ungueltige Eingabe! Bitte geben Sie eine Zahl ein: ");
            }
        }
    }

    static List<Question> generateRandomQuestions(int amount, ArrayList<Question> questions) {
        Collections.shuffle(questions);
        return questions.subList(0, amount);
    }

    static List<Question> generateSortedRandomQuestions(int amount, ArrayList<Question> q) {
        q.sort((question1, question2) -> question2.getWrongAnswersPercent() - question1.getWrongAnswersPercent());
        List<Question> questions = q.subList(0, amount);
        Collections.shuffle(questions);
        return questions;
    }

    static ArrayList<String> generateRandomAnswerArray(String answer, List<String> alternateAnswers) {
        // Combine correct answer and (if existing) alternate Answers
        ArrayList<String> answerList = new ArrayList<>();
        answerList.add(answer);
        answerList.addAll(alternateAnswers);

        // Randomize answer order
        Collections.shuffle(answerList);

        return answerList;
    }

    static int[] createGlobalStatValues(HashMap<String, ArrayList<Question>> questions) {
        int[] globalStats = new int[]{0, 0, 0};

        // for each category of questions
        for (String key : questions.keySet()) {
            //for each question in category add number of wrong/correct answers to globalStats
            for (int i = 0; i < questions.get(key).size(); i++) {
                var tempQuestion = questions.get(key).get(i);
                globalStats[0] = globalStats[0] + tempQuestion.getWrongAnswers();
                globalStats[1] = globalStats[1] + tempQuestion.getCorrectAnswers();
            }
        }

        // Calculate total amount of answered questions
        globalStats[2] = globalStats[0] + globalStats[1];

        return globalStats;
    }

    public static boolean printCategoryStats(ArrayList<Question> questions) {
        System.out.println("Fragen in der Kategorie: " + questions.size());
        int correctAnswers = 0;
        int wrongAnswers = 0;
        for (Question q : questions) {
            correctAnswers += q.getCorrectAnswers();
            wrongAnswers += q.getWrongAnswers();
        }
        if (correctAnswers + wrongAnswers > 0) {
            System.out.println(String.format("Diese wurden Insgesamt %d Mal beantwortet, davon %d%% Mal richtig", (correctAnswers + wrongAnswers), (correctAnswers * 100) / (correctAnswers + wrongAnswers)));
            System.out.println(String.format("%dx Richtig beantwortet, %dx Falsch Beantwortet\n", correctAnswers, wrongAnswers));
            return true;
        } else {
            System.out.println("Bisher wurde noch keine davon beantwortet\n");
            return false;
        }
    }

    public static boolean getBoolean(String s) {
        System.out.println(s);
        while (true) {
            System.out.print("(Ja|Nein): ");
            String input = scanner.nextLine().toLowerCase();
            if (input.equals("ja") || input.equals("j") || input.equals("yes") || input.equals("y")) {
                return true;
            }
            if (input.equals("nein") || input.equals("n") || input.equals("no")) {
                return false;
            }
        }

    }

    public static Set<Integer> getInputInts(String question, int startBoundary, int endBoundary) {
        System.out.print(question);

        outerLoop:
        while (true) {
            String input = scanner.nextLine();
            String[] splitInputs = input.split(",");
            Set<Integer> inputInts = new HashSet<>();

            for (String splitInput : splitInputs) {
                try {
                    int val = Integer.parseInt(splitInput);
                    if (startBoundary > val || endBoundary < val) {
                        System.out.print("Bitte geben Sie nur Nummer von " + startBoundary + " bis " + endBoundary + " ein: ");
                        continue outerLoop;
                    }
                    inputInts.add(val);
                } catch (Exception e) {
                    System.out.print("Ungueltige Eingabe! Bitte geben Sie nur (kommagetrennte) Zahlen ein: ");
                }
            }
            return inputInts;
        }
    }

    public static Question[] createMillionaireLevels(ArrayList<Question> selectedQuestions, int levelNum) {
        selectedQuestions.sort(Comparator.comparingInt(Question::getWrongAnswersPercent));
        double questionsPerLevel = (double) selectedQuestions.size() / levelNum;
        //System.out.println("Total: " + selectedQuestions.size() + " Per Level: " + questionsPerLevel);
        Question[] levels = new Question[levelNum];
        for (int i = 0; i < 15; i++) {
            int startIndex = (int) (i * questionsPerLevel);
            int endIndex = (int) ((i + 1) * questionsPerLevel);
            List<Question> levelQuestions = selectedQuestions.subList(startIndex, endIndex);
            levels[i] = levelQuestions.get(Logic.random.nextInt(levelQuestions.size()));
            /*System.out.print("Level: " + i + " StartI: " + startIndex + " EndI: " + endIndex + "[");
            for(Question q : levelQuestions) {
                System.out.print("," + q.getQuestion() + " (" + q.getWrongAnswersPercent() + ")");
            }
            System.out.println("] Picked: " + levels[i].getQuestion());*/
        }
        return levels;
    }
}
