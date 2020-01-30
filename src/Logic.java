import java.util.ArrayList;
import java.util.HashMap;

public class Logic {

    private HashMap<String, ArrayList<Question>> questions;
    private String [] MainMenuChoices;

    public Logic()
    {
        //questions = JsonHelper.ReturnQuestionsandCategories(); //NotYetImplemented

        questions = new HashMap<>()
        {
            {
                put("Geographie", new ArrayList<>() {
                    {
                        add( new Question("In welchem Land liegt Paris?", "Frankreich", new String[]{"UK", "Somewhere", "Else"}));
                        add( new Question("In welchem Land liegt London?", "UK", new String[]{"US", "Somewhere", "Else"}));
                    }
                });
                put("Geschichte", new ArrayList<>(){
                    {
                        add( new Question("Wie lange dauerte der 30-jaehrige Krieg?", "30 Jahre", new String[]{"UK", "Somewhere", "Else"}));
                        add( new Question("Wie heisst Angela Merkel mit Vornamen?", "Angela", new String[]{"UK", "Somewhere", "Else"}));
                    }}
                );
            }
        };
        MainMenuChoices = new String[] {"Spielen", "Fragen hinzufuegen", "Statistiken", "Beenden"};
    }

    public void RunGame()
    {
        System.out.println("Hallo und herzlich willkommen zur Lernsoftware!");

        while(true)
        {
            MainMenuChoice();
        }
    }

    private void MainMenuChoice()
    {
        System.out.println();
        System.out.println("Sie befinden sich im Hauptmenue");
        var mainchoice = HelperClass.createChoiceMenuString("Folgende Aktionen stehen zur Auswahl:", MainMenuChoices);
        System.out.println(mainchoice);

        var userChoice = HelperClass.GetInputInt("Was wollen Sie tun? ", 1,4);

        switch(userChoice)
        {
            case 1:
                PlayQuiz();
                break;
            case 2:
                AddQuestion();
                break;
            case 3:
                ShowStatistics();
                break;
            case 4:
                System.exit(0);
        }
    }

    private void PlayQuiz()
    {
        var categories = questions.keySet().toArray(new String[questions.size()]);

        System.out.println();
        System.out.println("Sie befinden sich im Quiz-Modus");
        System.out.println(HelperClass.createChoiceMenuString("Aus welcher Kategorie sollen Fragen gestellt werden?\nFuer einen Mix aus allen Kategorien schreiben Sie einfach '0'.", categories));

        var categoryIndex = HelperClass.GetInputInt("Welche Kategorie (0 fuer Alle)? ", 0, categories.length);
        categoryIndex = categoryIndex == 0 ? 0 : categoryIndex-1;

        var numberOfQuestions = HelperClass.GetInputInt("Wieviele Fragen wollen Sie beantworten (max. 10)? ", 1, 10);

        AskQuestions(categoryIndex == 0 ? "" : categories[categoryIndex], numberOfQuestions);
    }

    private void AskQuestions(String category, int numberOfQuestions)
    {
        Question[] questions = HelperClass.GenerateRandomQuestions(numberOfQuestions, GetQuestionListForCategory(category));
        System.out.println();

        for(int i = 0; i<numberOfQuestions; i++)
        {
            System.out.println("Frage " + (i+1) + ":");
            System.out.println(questions[i].question);
        }
    }

    private ArrayList<Question> GetQuestionListForCategory(String category)
    {
        if(!category.equals("")) return questions.get(category);

        ArrayList<Question> allQuestions = new ArrayList<>();

        for (String key :questions.keySet())
        {
            allQuestions.addAll(questions.get(key));
        }
        return allQuestions;
    }

    private void AddQuestion()
    {

    }

    private void ShowStatistics()
    {
        System.out.println("This funtion is not implemented yet!");
    }
    private void ResetQuestions()
    {
        questions = null;
        questions = JsonHelper.ReturnQuestionsAndCategories();
    }


}
