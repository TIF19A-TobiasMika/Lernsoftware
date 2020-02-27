import java.util.*;

public class HelperClass {

    static Scanner scanner = new Scanner(System.in);

    static String createChoiceMenuString(String title, String... options) {
        StringBuilder sb = new StringBuilder();
        sb.append(title);
        sb.append("\n");
        for (int i = 0; i < options.length; i++) {
            sb.append(i+1);
            sb.append(") ");
            sb.append(options[i]);
            sb.append('\n');
        }
        return  sb.toString();
    }

    static String createChoiceMenuString(String title, ArrayList<String> options) {
        StringBuilder sb = new StringBuilder();
        sb.append(title);
        sb.append("\n");
        for (int i = 0; i < options.size(); i++) {
            sb.append(i+1);
            sb.append(") ");
            sb.append(options.get(i));
            sb.append('\n');
        }
        return  sb.toString();
    }

    static int simpleMenu(String title, String inputQuestion, String... options) {
        System.out.print(createChoiceMenuString(title, options));
        return GetInputInt(inputQuestion, 1, options.length);
    }

    static int simpleMenu(String title, String inputQuestion, ArrayList<String> options) {
        System.out.print(createChoiceMenuString(title, options));
        return GetInputInt(inputQuestion, 1, options.size());
    }

    static String GetInputText(String question)
    {
        System.out.print(question);
        return scanner.nextLine();
    }

    static int GetInputInt(String question)
    {
        System.out.print(question);

        while(true)
        {
            var input = scanner.nextLine();

            try
            {
                return Integer.parseInt(input);
            }
            catch (Exception e)
            {
                System.out.print("Ungueltige Eingabe! Bitte geben Sie eine Zahl ein: ");
            }
        }
    }

    // Overloaded method GetInputInt with value boundaries
    static int GetInputInt(String question, int startBoundary, int endBoundary)
    {
        System.out.print(question);

        while(true)
        {
            var input = scanner.nextLine();

            try
            {
                var intInput = Integer.parseInt(input);

                if(startBoundary> intInput || endBoundary < intInput)
                {
                    System.out.print("Bitte geben Sie eine Nummer von " + startBoundary + " bis " + endBoundary + " ein: ");
                    continue;
                }

                return intInput;
            }
            catch (Exception e)
            {
                System.out.print("Ungueltige Eingabe! Bitte geben Sie eine Zahl ein: ");
            }
        }
    }

    static Question [] GenerateRandomQuestions(int amount, ArrayList<Question> questionCollection)
    {
        // Prevent Exception if there aren't enough questions in the selected category
        if(amount> questionCollection.size()) return null;

        Question [] randomQuestionArray = new Question[amount];

        // Sort questions by wrong answers descending
        Collections.sort(questionCollection, new SortByWrongAnswers());

        // Add questions for output according to requested amount
        for(var i = 0; i < amount; i++)
        {
            randomQuestionArray[i] = questionCollection.get(i);
        }

        return randomQuestionArray;
    }

    static String [] GenerateRandomAnswerArray(String answer, ArrayList<String> alternateAnswers)
    {
        // Combine correct answer and (if existing) alternate Answers
        ArrayList<String> answerList = new ArrayList<>();
        answerList.add(answer);
        answerList.addAll(alternateAnswers);

        // Randomize answer order
        Collections.shuffle(answerList);

        return answerList.toArray(new String[ alternateAnswers.size()+1]);
    }

    static int[] CreateGlobalStatValues(HashMap<String, ArrayList<Question>> questions)
    {
        int [] globalStats = new int[] {0, 0, 0};

        // for each category of questions
        for (String key : questions.keySet())
        {
            //for each question in category add number of wrong/correct answers to globalStats
            for(int i = 0; i<questions.get(key).size(); i++)
            {
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
        if(correctAnswers + wrongAnswers > 0) {
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
            if(input.equals("ja") || input.equals("j") || input.equals("yes") || input.equals("y")) {
                return true;
            }
            if(input.equals("nein") || input.equals("n") || input.equals("no")) {
                return false;
            }
        }

    }
}
