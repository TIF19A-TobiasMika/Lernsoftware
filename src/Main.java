import java.io.FileNotFoundException;
import java.util.ArrayList;

public class Main
{
    public static void main(String[] args)
    {
        try {
            System.out.println(JsonHelper.ReturnQuestionsAndCategories().keySet());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        /*
        ArrayList<Question> questions = new ArrayList<>();
        questions.add(new Question("Frage 1", "Antwort 1", new String[] {"Answer 2", "Answer 3"}));
        questions.add(new Question("Frage 2", "Antwort 11", new String[] {"Answer 22"}));
        JsonHelper.saveQuestionsToFile(questions, "categories/test.json");

        try {
            for (Question q : JsonHelper.getQuestionsFromFile("categories/test.json")) {
                System.out.println(q);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
*/
    }
}
