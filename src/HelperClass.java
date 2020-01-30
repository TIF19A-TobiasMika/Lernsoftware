import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Scanner;

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
                continue;
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
                continue;
            }
        }
    }

    static Question [] GenerateRandomQuestions(int amount, ArrayList<Question> questionCollection)
    {
        Question [] randomQuestionArray = new Question[amount];
        var randomQuestions = questionCollection;

        Collections.shuffle(randomQuestions);

        for(int i = 0; i < amount; i++)
        {
            randomQuestionArray[i] = randomQuestions.get(i);
        }

        return randomQuestionArray;
    }

    static String [] GenerateRandomAnswerArray(String answer, String [] alternateAnswers)
    {
        ArrayList<String> answerList = new ArrayList<>();
        answerList.add(answer);

        for(int i = 0; i<alternateAnswers.length; i++)
        {
            answerList.add(alternateAnswers[i]);
        }

        Collections.shuffle(answerList);

        return answerList.toArray(new String[ alternateAnswers.length+1]);
    }

}
