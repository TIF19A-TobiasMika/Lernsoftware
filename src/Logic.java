import java.util.ArrayList;
import java.util.HashMap;

public class Logic {

    private HashMap<String, ArrayList<Question>> questions;
    private String [] MainMenuChoices;

    public Logic()
    {
        questions = JsonHelper.ReturnQuestionsandCategories();
        MainMenuChoices = new String[] {"Spielen", "Fragen hinzufuegen", "Statistiken", "Beenden"};
    }

    public void RunGame()
    {
        System.out.println("Hallo und herzlich willkommen zur Lernsoftware!");

        MainMenuChoice();
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

    }

    private void AddQuestion()
    {

    }

    private void ShowStatistics()
    {

    }
    private void ResetQuestions()
    {
        questions = null;
        questions = JsonHelper.ReturnQuestionsandCategories();
    }


}
