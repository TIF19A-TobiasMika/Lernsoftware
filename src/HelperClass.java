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
                var intInput = Integer.parseInt(input);
                return intInput;
            }
            catch (Exception e)
            {
                System.out.print("Invalid input. Please write a number: ");
            }
        }
    }

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
                    System.out.print("Please enter a number from " + startBoundary + " to " + endBoundary + ": ");
                    continue;
                }

                return intInput;
            }
            catch (Exception e)
            {
                System.out.print("Invalid input. Please write a number: ");
            }
        }
    }

    static Question [] GenerateRandomQuestions(int amount, ArrayList<Question> questionCollection)
    {
        if(amount> questionCollection.size()) return null;

        Question [] randomQuestionArray = new Question[amount];

        Collections.shuffle(questionCollection);

        for(var i = 0; i < amount; i++)
        {
            randomQuestionArray[i] = questionCollection.get(i);
        }

        return randomQuestionArray;
    }

    static String [] GenerateRandomAnswerArray(String answer, String [] alternateAnswers)
    {
        ArrayList<String> answerList = new ArrayList<>();
        answerList.add(answer);

        answerList.addAll(Arrays.asList(alternateAnswers));

        Collections.shuffle(answerList);

        return answerList.toArray(new String[ alternateAnswers.length+1]);
    }

    static int[] CreateGlobalStatValues(HashMap<String, ArrayList<Question>> questions)
    {
        int [] globalStats = new int[] {0, 0, 0};

        for (String key : questions.keySet())
        {
            for(int i = 0; i<questions.get(key).size(); i++)
            {
                var tempQuestion = questions.get(key).get(i);
                globalStats[0] = globalStats[0] + tempQuestion.wrongAnswers;
                globalStats[1] = globalStats[1] + tempQuestion.correctAnswers;
            }
        }

        globalStats[2] = globalStats[0] + globalStats[1];

        return globalStats;
    }

}
