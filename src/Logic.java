import java.util.ArrayList;
import java.util.HashMap;

public class Logic {

    private HashMap<String, ArrayList<Question>> categories;
    private String[] MainMenuChoices;
    private String[] StatisticChoices;

    public Logic() {
        categories = JsonHelper.ReturnQuestionsAndCategories();

        MainMenuChoices = new String[]{"Spielen", "Fragen hinzufuegen", "Statistiken", "Beenden"};
        StatisticChoices = new String[]{"Allgemein", "Fragenspezifisch", "Kategoriespezifisch", "Statistiken zurücksetzten"};
    }

    public void RunGame() {
        System.out.println("Hallo und herzlich willkommen zur Lernsoftware!");

        while (true) {
            MainMenuChoice();
        }
    }

    private void MainMenuChoice() {
        System.out.println();
        System.out.println("Sie befinden sich im Hauptmenue");
        var mainchoice = HelperClass.createChoiceMenuString("Folgende Aktionen stehen zur Auswahl:", MainMenuChoices);
        System.out.println(mainchoice);

        var userChoice = HelperClass.GetInputInt("Was wollen Sie tun? ", 1, 5);

        switch (userChoice) {
            case 1:
                PlayQuiz();
                break;
            case 2:
                AddQuestionMenu();
                break;
            case 3:
                ShowStatistics();
                break;
            case 4:
                System.exit(0);
                break;
            case 5:
                JsonHelper.saveAllCategoriesToFile(categories);
        }
    }

    private void PlayQuiz() {
        var categoryNames = this.categories.keySet().toArray(String[]::new);

        System.out.println();
        System.out.println("Sie befinden sich im Quiz-Modus");
        System.out.println(HelperClass.createChoiceMenuString("Aus welcher Kategorie sollen Fragen gestellt werden?\nFuer einen Mix aus allen Kategorien schreiben Sie einfach '0'.", categoryNames));

        var categoryIndex = HelperClass.GetInputInt("Welche Kategorie (0 fuer Alle)? ", 0, categoryNames.length);

        var numberOfQuestions = HelperClass.GetInputInt("Wieviele Fragen wollen Sie beantworten (max. 10)? ", 1, 10);

        AskQuestions(categoryIndex == 0 ? "" : categoryNames[categoryIndex - 1], numberOfQuestions);
    }

    private void AskQuestions(String category, int numberOfQuestions) {
        Question[] questions = HelperClass.GenerateRandomQuestions(numberOfQuestions, GetQuestionListForCategory(category));

        if (questions == null) {
            System.out.println("This category doesn't hold that many questions!");
            return;
        }

        System.out.println();

        int correctQuestions = 0;
        for (int i = 0; i < numberOfQuestions; i++) {
            System.out.println("Frage " + (i + 1) + ":");

            if (questions[i].getAlternateAnswers() != null) {
                var answerArray = HelperClass.GenerateRandomAnswerArray(questions[i].getAnswer(), questions[i].getAlternateAnswers());
                var question = HelperClass.createChoiceMenuString(questions[i].getQuestion(), answerArray);
                System.out.println(question);
                var userAnswerIndex = HelperClass.GetInputInt("Ihre Antwort: ", 1, answerArray.length);

                if (questions[i].getAnswer().equalsIgnoreCase(answerArray[userAnswerIndex - 1])) {
                    System.out.println("Richtige Antwort!");
                    correctQuestions++;
                    questions[i].addToCorrectAnswers(1);
                } else {
                    System.out.println("Falsche Antwort! Die richtige Antwort lautet: '" + questions[i].getAnswer() + "'.");
                    questions[i].addToWrongAnswer(1);
                }
            } else {
                System.out.println(questions[i].getQuestion());
                var userAnswer = HelperClass.GetInputText("Ihre Antwort: ");

                if (questions[i].getAnswer().equalsIgnoreCase(userAnswer)) {
                    System.out.println("Richtige Antwort!");
                    correctQuestions++;
                    questions[i].addToCorrectAnswers(1);
                } else {
                    System.out.println("Falsche Antwort! Die richtige Antwort lautet: '" + questions[i].getAnswer() + "'");
                    questions[i].addToWrongAnswer(1);
                }
            }

            System.out.println();
        }

        if (!category.equals("")) {
            JsonHelper.saveQuestionsToFile(categories.get(category), category);
        } else {
            JsonHelper.saveAllCategoriesToFile(categories);
        }
        System.out.println("Sie haben " + correctQuestions + " von " + numberOfQuestions + " Fragen richtig beantwortet!");
    }

    private ArrayList<Question> GetQuestionListForCategory(String category) {
        if (!category.equals("")) return categories.get(category);

        ArrayList<Question> allQuestions = new ArrayList<>();

        //var tempKeyIndex = 0;
        for (String key : categories.keySet()) {
            allQuestions.addAll(categories.get(key));

            /*for(int i = 0; i< categories.get(key).size(); i++)
            {
                allQuestions.get(tempKeyIndex).statCategory = key;
                tempKeyIndex++;
            }*/
        }
        return allQuestions;
    }

    private void AddQuestionMenu() {
        ArrayList<String> menuOptions = new ArrayList<>(categories.keySet());
        menuOptions.add("Neue Katerogie Erstellen");
        menuOptions.add("Zurück");

        System.out.println(HelperClass.createChoiceMenuString("Zu welcher Kategorie soll die neue Frage hinzugefügt werden?", menuOptions));
        var userChoice = HelperClass.GetInputInt("Wählen sie eine Option:  ", 1, menuOptions.size());

        if (userChoice == menuOptions.size()) {
            MainMenuChoice();
        } else if (userChoice == menuOptions.size() - 1) {
            addCategorie();
        } else {
            AddQuestion(menuOptions.get(userChoice - 1));
        }

        JsonHelper.saveAllCategoriesToFile(categories);
    }

    private void addCategorie() {
        System.out.println("\nNeue Kategorie erstellen:\n");
        String categorie = HelperClass.GetInputText("Wie soll die Kategorie heissen? ");
        while (categories.containsKey(categorie)) {
            categorie = HelperClass.GetInputText("Diese Katerogie existirt bereits.\nBitte andere Bezeichnung eingeben:");
        }
        int userChoice = HelperClass.simpleMenu("Katerogie " + categorie + " wird erstellt", ": ", "Fortsetzen und erste Frage zu " + categorie + " hinzufügen", "Abbrechen und zurück ins Hauptmenu");
        if (userChoice == 1) {
            categories.put(categorie, new ArrayList<>());
            AddQuestion(categorie);
        } else {
            MainMenuChoice();
        }
    }

    private void AddQuestion(String categorie) {
        System.out.println("\nNeue Frage zu " + categorie + " hinzufügen:\n");
        String questionText = HelperClass.GetInputText("Wie lautet ihre Frage? ");
        String correctAnswer = HelperClass.GetInputText("Wie lautet die korrekte Antwort? ");
        String[] alternateAnswers = null;

        System.out.println(HelperClass.createChoiceMenuString("\nGibt altenative Antwortmöglichkeiten oder ist es eine Input-Frage?", "alternative Antworten", "Input-Frage"));
        int userChoice = HelperClass.GetInputInt("Wähle einen Fragen Typ: ", 1, 2);
        if (userChoice == 1) {
            userChoice = 0;
            ArrayList<String> alternateAnswersTMP = new ArrayList<>();
            while (userChoice != 2) {
                alternateAnswersTMP.add(HelperClass.GetInputText("\nAlternative (falsche) Antwortmöglichkeit: "));

                if (alternateAnswersTMP.size() < 4) {
                    System.out.println(HelperClass.createChoiceMenuString("", "Weitere Antwortmöglichkeit", "Fertig"));
                    userChoice = HelperClass.GetInputInt(": ", 1, 2);
                } else {
                    userChoice = 2;
                }
            }
            alternateAnswers = alternateAnswersTMP.toArray(String[]::new);
        }

        Question question = new Question(questionText, correctAnswer, alternateAnswers, 0, 0);
        userChoice = HelperClass.simpleMenu("Wollen sie folgende Frage speichern?\n" + question.toString() + "\n", ": ", "Ja", "Nein");
        if (userChoice == 1) {
            categories.get(categorie).add(question);
            if (JsonHelper.saveQuestionsToFile(categories.get(categorie), categorie)) {
                System.out.println("Frage wurde zu " + categorie + " hinzugefügt und gespeichert!\n");
            } else {
                System.err.println("Fehler beim Speichern!");
            }
        } else {
            System.out.println("Frage wurde verworfen!");
        }
        userChoice = HelperClass.simpleMenu("Wollen sie...", ": ", "Eine weitere Frage zu " + categorie + " hinzufügen", "Zurück ins Hauptmenu");
        if (userChoice == 1) {
            AddQuestion(categorie);
        } else {
            MainMenuChoice();
        }
    }

    private void ShowStatistics() {
        var globalStats = HelperClass.CreateGlobalStatValues(categories);
        System.out.println();

        System.out.println("Herzlich Willkommen im Statistik Bereich:");
        System.out.println(HelperClass.createChoiceMenuString("Welche Statistiken wollen Sie sehen?", StatisticChoices));

        var userInput = HelperClass.GetInputInt("Auswahl: ", 1, 3);

        if (userInput == 1) {
            if (globalStats[2] <= 0) {
                System.out.println("Noch keine Fragen beantwortet");
                return;
            }
            System.out.println("Fragen insgesamt: " + globalStats[2]);
            System.out.println("Davon richtig beantwortet: " + globalStats[1] + " (in Prozent: " + (((double) (globalStats[1]) / globalStats[2]) * 100) + "%)");
            System.out.println("Davon falsch beantwortet: " + globalStats[0] + " (in Prozent: " + (((double) (globalStats[0]) / globalStats[2]) * 100) + "%)");
        } else if (userInput == 2) {
            for (String category : categories.keySet()) {
                System.out.println("------ " + category + " ------");
                for (Question q : categories.get(category)) {
                    q.printStats();
                }
            }
        } else if(userInput == 3) {
            String[] categoryOptions = categories.keySet().toArray(String[]::new);
            int choice = HelperClass.simpleMenu("Wählen sie eine Kategorie: ", ": ", categoryOptions);
            String category = categoryOptions[choice-1];
            System.out.println("------ " + category + " ------");
            if(HelperClass.printCategoryStats(categories.get(category))) {
                for (Question q : categories.get(category)) {
                    q.printDetailedStats();
                }
            }
        } else if(userInput == 4) {
            for(ArrayList<Question> catergory : categories.values()) {
                for(Question q : catergory) {
                    q.resetStats();
                }
            }
        }


    }

    private void reloadQuestions() {
        categories = null;
        categories = JsonHelper.ReturnQuestionsAndCategories();
    }


}
