import java.util.Scanner;

public class HelperClass {

    static Scanner scanner = new Scanner(System.in);

    static String createChoiceMenuString(String title, String[] options) {
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
                System.out.print("Invalid input. Please write a number.");
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

                if(startBoundary> intInput || endBoundary < intInput) continue;

                return intInput;
            }
            catch (Exception e)
            {
                System.out.print("Invalid input. Please write a number.");
                continue;
            }
        }
    }

}
