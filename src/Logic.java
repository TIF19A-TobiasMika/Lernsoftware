import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

public class Logic {

    private HashMap<String, ArrayList<Question>> categories;
    private String[] MainMenuChoices;
    private String[] StatisticChoices;

    public Logic() {
        categories = JsonHelper.ReturnQuestionsAndCategories();

        MainMenuChoices = new String[]{"Spielen", "Bearbeitungsmodus", "Statistiken", "Beenden"};
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

        var userChoice = HelperClass.GetInputInt("Was wollen Sie tun? ", 1, MainMenuChoices.length);

        switch (userChoice) {
            case 1:
                PlayQuiz();
                break;
            case 2:
                editMenu();
                break;
            case 3:
                ShowStatistics();
                break;
            case 4:
                System.exit(0);
                break;
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
            System.out.println("Diese Kategorie enthaelt keine oder weniger Fragen als Sie eingegeben haben.");
            return;
        }

        System.out.println();

        int correctQuestions = 0;
        for (int i = 0; i < numberOfQuestions; i++) {
            System.out.println("Frage " + (i + 1) + ":");

            // Check if alternate Answers are available for question
            if (questions[i].getAlternateAnswers() != null)
            {
                //Get randomized answer array for question
                var answerArray = HelperClass.GenerateRandomAnswerArray(questions[i].getAnswer(), questions[i].getAlternateAnswers());

                // Generate question output, print question and get answer index as user input
                var question = HelperClass.createChoiceMenuString(questions[i].getQuestion(), answerArray);
                System.out.println(question);
                var userAnswerIndex = HelperClass.GetInputInt("Ihre Antwort: ", 1, answerArray.length);

                // Use user input index to compare user answer to correct answer
                if (questions[i].getAnswer().equalsIgnoreCase(answerArray[userAnswerIndex - 1])) {
                    System.out.println("Richtige Antwort!");
                    correctQuestions++;
                    questions[i].addToCorrectAnswers(1);
                } else {
                    System.out.println("Falsche Antwort! Die richtige Antwort lautet: '" + questions[i].getAnswer() + "'.");
                    questions[i].addToWrongAnswer(1);
                }
            } else
                {
                //No alternate answers available, so question gets printed to get user input
                System.out.println(questions[i].getQuestion());
                var userAnswer = HelperClass.GetInputText("Ihre Antwort: ");

                // Check if user answer matches correct answer (case insensitive)
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

        // Saving stat values for questions
        if (!category.equals("")) {
            JsonHelper.saveQuestionsToFile(categories.get(category), category);
        } else {
            JsonHelper.saveAllCategoriesToFile(categories);
        }

        // Print game stats when all questions are answered
        System.out.println("Sie haben " + correctQuestions + " von " + numberOfQuestions + " Fragen richtig beantwortet!");
    }

    private ArrayList<Question> GetQuestionListForCategory(String category)
    {
        // Standard Case, when category is selected
        if (!category.equals("")) return categories.get(category);

        //Gets all questions from all categories, when no category is specified
        ArrayList<Question> allQuestions = new ArrayList<>();

        for (String key : categories.keySet()) {
            allQuestions.addAll(categories.get(key));
        }
        return allQuestions;
    }

    private void editMenu() {
        ArrayList<String> menuOptions = new ArrayList<>(categories.keySet());
        menuOptions.add("Neue Kategorie Erstellen");
        menuOptions.add("Zurück");
        int userChoice = HelperClass.simpleMenu("Welche Kategorie möchten sie editieren?", ": ", menuOptions);
        if (userChoice == menuOptions.size()) {
            MainMenuChoice();
        } else if (userChoice == menuOptions.size() - 1) {
            addCategorie();
        } else {
            editCategory(menuOptions.get(userChoice - 1));
        }
    }

    private void editCategory(String category) {
        int userChoice = HelperClass.simpleMenu("Waehlen sie eine Option", ": ", "Frage hinzufuegen", "Fragen bearbeiten", "Katerogie umbenennen", "Katerogie loeschen", "Zurueck");
        switch (userChoice) {
            case 1: addQuestion(category); break;
            case 2: editQuestions(category); break;
            case 3: renameCategory(category); break;
            case 4: deleteCategory(category); break;
            case 5: editMenu();
        }
    }

    private void deleteCategory(String category) {
        if(HelperClass.getBoolean("Sind sie sicher, dass sie die Kategorie " + category + " loeschen wollen?")) {
            categories.remove(category);
            if (JsonHelper.removeCategoryFile(category)) {
                System.out.println("Katerogie wurde erfolgreich geloescht\n");
            } else {
                System.err.println("Fehler beim Loeschen!");
            }
        }
    }

    private void renameCategory(String category) {
        String newName = HelperClass.GetInputText("Neuer Name für die Kategorie: ");
        if(HelperClass.getBoolean("Sind sie sicher, dass sie " + category + " in " + newName + " umbenennen wollen?")) {
            ArrayList<Question> tmp = categories.get(category);
            categories.remove(category);
            categories.put(newName, tmp);
            if(JsonHelper.renameQuestionsFile(category, newName, tmp)) {
                System.out.println("Kategorie " + category + " wurde erfolgreich in " + newName + " umbennant");
            } else {
                System.err.println("Datei konnte nicht umbennant werden, Namensaenderung ist nicht permanent");
            }
        }
    }

    private void editQuestions(String categoryName) {
        ArrayList<Question> category = categories.get(categoryName);
        ArrayList<String> questionStrings = category.stream().map(Question::getQuestion).collect(Collectors.toCollection(ArrayList::new));
        questionStrings.add("Zurueck");
        /*String[] questionStrings = new String[category.size()+1];
        for (int i = 0; i < category.size(); i++) {
            questionStrings[i] = category.get(i).getAnswer();
        }*/

        int userChoice = HelperClass.simpleMenu("Waehle eine Frage", ": ", questionStrings);
        if(userChoice == questionStrings.size()) {
            editCategory(categoryName);
        } else {
            Question question = category.get(userChoice-1);
            if(editQuestion(question)) {
                if (JsonHelper.saveQuestionsToFile(category, categoryName)) {
                    System.out.println("Frage wurde bearbeitet und gespeichert!\n");
                } else {
                    System.err.println("Fehler beim Speichern!");
                }
            }
        }
    }

    private boolean editQuestion(Question question) {
        System.out.println(question);

        ArrayList<String> options = new ArrayList<>();
        options.add("Frage");
        options.add("Antwort");
        options.add("Alternative Antworten");
        options.add("Statistik Zurücksetzten");
        options.add("Abbrechen");

        while (true) {
            int userChoice = HelperClass.simpleMenu("Was willst du bearbeiten?", ": ", options);
            if (userChoice == 1) {
                System.out.println("Derzeitige Frage: " + question.getQuestion());
                question.setQuestion(HelperClass.GetInputText("Neuer Fragetext: "));
                if (options.size() <= 5) {
                    options.add("Speichern");
                }
            } else if (userChoice == 2) {
                System.out.println("Derzeitige Antworten: " + question.getAnswer());
                question.setAnswer(HelperClass.GetInputText("Neue Antwort: "));
                if (options.size() <= 5) {
                    options.add("Speichern");
                }
            } else if (userChoice == 3) {
                //TODO: Edit Alternate Answers
            } else if (userChoice == 4) {
                question.resetStats();
            } else if (userChoice == 5) {
                if (options.size() <= 5) {
                    return false;
                } else {
                    if (HelperClass.getBoolean("Aenderungen gehen verloren, sind sie sicher, dass sie abbrechen wollen? ")) {
                        return false;
                    }
                }
            } else if (userChoice == 6) {
                System.out.println(question);
                if(HelperClass.getBoolean("Soll die Frage so gespeichert werden?")) {
                    return true;
                }
            }
        }


    }

    private void addCategorie() {
        System.out.println("\nNeue Kategorie erstellen:\n");
        String categorie = HelperClass.GetInputText("Wie soll die Kategorie heissen? ");
        while (categories.containsKey(categorie)) {
            categorie = HelperClass.GetInputText("Diese Kategorie existiert bereits.\nBitte andere Bezeichnung eingeben:");
        }
        int userChoice = HelperClass.simpleMenu("Kategorie " + categorie + " wird erstellt", ": ", "Fortsetzen und erste Frage zu " + categorie + " hinzufügen", "Abbrechen und zurück ins Hauptmenu");
        if (userChoice == 1) {
            categories.put(categorie, new ArrayList<>());
            addQuestion(categorie);
        } else {
            MainMenuChoice();
        }
    }

    private void addQuestion(String category) {
        System.out.println("\nNeue Frage zu " + category + " hinzufuegen:\n");
        String questionText = HelperClass.GetInputText("Wie lautet ihre Frage? ");
        String correctAnswer = HelperClass.GetInputText("Wie lautet die korrekte Antwort? ");
        String[] alternateAnswers = null;

        System.out.println(HelperClass.createChoiceMenuString("\nGibt altenative Antwortmoeglichkeiten oder ist es eine Input-Frage?", "alternative Antworten", "Input-Frage"));
        int userChoice = HelperClass.GetInputInt("Waehle einen Fragen Typ: ", 1, 2);
        if (userChoice == 1) {
            userChoice = 0;
            ArrayList<String> alternateAnswersTMP = new ArrayList<>();
            while (userChoice != 2) {
                alternateAnswersTMP.add(HelperClass.GetInputText("\nAlternative (falsche) Antwortmoeglichkeit: "));

                if (alternateAnswersTMP.size() < 4) {
                    System.out.println(HelperClass.createChoiceMenuString("", "Weitere Antwortmoeglichkeit", "Fertig"));
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
            categories.get(category).add(question);
            if (JsonHelper.saveQuestionsToFile(categories.get(category), category)) {
                System.out.println("Frage wurde zu " + category + " hinzugefuegt und gespeichert!\n");
            } else {
                System.err.println("Fehler beim Speichern!");
            }
        } else {
            System.out.println("Frage wurde verworfen!");
        }
        userChoice = HelperClass.simpleMenu("Wollen sie...", ": ", "Eine weitere Frage zu " + category + " hinzufuegen", "Zurück ins Hauptmenu");
        if (userChoice == 1) {
            addQuestion(category);
        } else {
            MainMenuChoice();
        }
    }

    private void ShowStatistics() {
        var globalStats = HelperClass.CreateGlobalStatValues(categories);
        System.out.println();

        System.out.println("Herzlich Willkommen im Statistik Bereich:");
        System.out.println(HelperClass.createChoiceMenuString("Welche Statistiken wollen Sie sehen?", StatisticChoices));

        var userInput = HelperClass.GetInputInt("Auswahl: ", 1, StatisticChoices.length);

        if (userInput == 1) {
            if (globalStats[2] <= 0) {
                System.out.println("Noch keine Fragen beantwortet");
                return;
            }
            System.out.println("Fragen insgesamt: " + globalStats[2]);
            System.out.println("Davon richtig beantwortet: " + globalStats[1] + " (in Prozent: " + (Math.round(((double) (globalStats[1]) / globalStats[2]) * 100)) + "%)");
            System.out.println("Davon falsch beantwortet: " + globalStats[0] + " (in Prozent: " + (Math.round(((double) (globalStats[0]) / globalStats[2]) * 100)) + "%)");
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
}
