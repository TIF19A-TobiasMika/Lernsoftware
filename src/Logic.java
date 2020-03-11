import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;

public class Logic {

    private final HashMap<String, ArrayList<Question>> categories;
    private final String[] mainMenuChoices;
    private final String[] statisticChoices;
    private final String[] gameModes;
    final static int maxAlternateAnswers = 4;

    public Logic() {
        categories = JsonHelper.ReturnQuestionsAndCategories();

        mainMenuChoices = new String[]{"Spielen", "Bearbeitungsmodus", "Statistiken", "Beenden"};
        statisticChoices = new String[]{"Allgemein", "Fragenspezifisch", "Kategoriespezifisch", "Statistiken zurücksetzten"};
        gameModes = new String[]{"Normal", "Ueberlebensmodus", "Wer wird Milionaer", "Fragen mit meisten Fehlern"};
    }

    public void runGame() {
        System.out.println("Hallo und herzlich willkommen zur Lernsoftware!");

        while (true) {
            mainMenuChoice();
        }
    }

    private void mainMenuChoice() {
        System.out.println();
        System.out.println("Sie befinden sich im Hauptmenue");
        var mainChoice = HelperClass.createChoiceMenuString("Folgende Aktionen stehen zur Auswahl:", mainMenuChoices);
        System.out.println(mainChoice);

        var userChoice = HelperClass.getInputInt("Was wollen Sie tun? ", 1, mainMenuChoices.length);

        switch (userChoice) {
            case 1:
                playMenu();
                break;
            case 2:
                editMenu();
                break;
            case 3:
                showStatistics();
                break;
            case 4:
                System.exit(0);
                break;
        }
    }

    private void playMenu() {
        System.out.println();
        System.out.println("Sie befinden sich im Quiz-Modus");

        int userChoice = HelperClass.simpleMenu("Welchen Spielmodus moechten sie spielen?", "Modus: ", gameModes);
        switch (userChoice) {
            case 1:
                playNormalQuiz();
                break;
            case 2:
                playSurvivalQuiz();
                break;
            case 3:
                playMillionaireQuiz();
                break;
            case 4:
                playMostWrongQuiz();
                break;
        }
    }

    private void playNormalQuiz() {
        ArrayList<Question> selectedQuestions = selectQuestions();
        while (selectedQuestions.size() <= 0) {
            System.out.println("Die ausgewaehlte(n) Kategorie(n) enthalten keine Fragen, bitte waehlen sie andere Kategorien\n");
            selectedQuestions = selectQuestions();
        }
        var numberOfQuestions = HelperClass.getInputInt("Wieviele Fragen wollen Sie beantworten (max. " + selectedQuestions.size() + " )? ", 1, selectedQuestions.size());

        ArrayList<Question> questions = HelperClass.generateRandomQuestions(numberOfQuestions, selectedQuestions);
        int correctQuestions = 0;
        for (int i = 0; i < questions.size(); i++) {
            System.out.println("Frage " + (i + 1) + ":");
            if (askQuestion(questions.get(i))) {
                correctQuestions++;
            }
        }
        System.out.println("Sie haben " + correctQuestions + " von " + numberOfQuestions + " Fragen richtig beantwortet!");
    }

    private void playSurvivalQuiz() {

    }

    private void playMillionaireQuiz() {

    }

    private void playMostWrongQuiz() {

    }

    private ArrayList<Question> selectQuestions() {
        ArrayList<Question> selectedQuestions = new ArrayList<>();
        String[] categoryNames = this.categories.keySet().toArray(String[]::new);
        String menuQuestion = "Aus welcher Kategorie sollen Fragen gestellt werden?" +
                "\nFuer einen Mix aus allen Kategorien schreiben Sie einfach '0'." +
                "\nFuer mehrere Kategorien auf einmal, geben sie deren Nummern kommgetrennt an (z.B. 2,3).";
        System.out.println(HelperClass.createChoiceMenuString(menuQuestion, categoryNames));

        var categoryIndex = HelperClass.getInputInts("Welche Kategorien (0 fuer Alle)? ", 0, categoryNames.length);


        if (categoryIndex.contains(0)) {
            for (ArrayList<Question> cat : categories.values()) {
                selectedQuestions.addAll(cat);
            }
        } else {
            for (Integer index : categoryIndex) {
                selectedQuestions.addAll(categories.get(categoryNames[index - 1]));
            }
        }
        return selectedQuestions;
    }

    private ArrayList<Question> selectAllQuestions() {
        ArrayList<Question> selectedQuestions = new ArrayList<>();
        for (ArrayList<Question> cat : categories.values()) {
            selectedQuestions.addAll(cat);
        }
        return selectedQuestions;
    }

    private boolean askQuestion(Question question) {
        // Check if alternate Answers are available for question
        if (question.getAlternateAnswers() != null) {
            //Get randomized answer array for question
            var answerArray = HelperClass.generateRandomAnswerArray(question.getAnswer(), question.getAlternateAnswers());

            var userAnswerIndex = HelperClass.simpleMenu(question.getQuestion(), "Ihre Antwort: ", answerArray);

            // Use user input index to compare user answer to correct answer
            if (question.getAnswer().equalsIgnoreCase(answerArray.get(userAnswerIndex - 1))) {
                System.out.println("Richtige Antwort!");
                question.addToCorrectAnswers(1);
                return true;
            } else {
                System.out.println("Falsche Antwort! Die richtige Antwort lautet: '" + question.getAnswer() + "'.");
                question.addToWrongAnswer(1);
            }
        } else {
            //No alternate answers available, so question gets printed to get user input
            System.out.println(question.getQuestion());
            var userAnswer = HelperClass.getInputText("Ihre Antwort: ");

            // Check if user answer matches correct answer (case insensitive)
            if (question.getAnswer().equalsIgnoreCase(userAnswer)) {
                System.out.println("Richtige Antwort!");
                question.addToCorrectAnswers(1);
                return true;
            } else {
                System.out.println("Falsche Antwort! Die richtige Antwort lautet: '" + question.getAnswer() + "'");
                question.addToWrongAnswer(1);
            }
        }
        return false;
    }

    private ArrayList<Question> GetQuestionListForCategory(String category) {
        // Standard Case, when category is selected
        if (!category.equals("")) return categories.get(category);

        //Gets all questions from all categories, when no category is specified
        ArrayList<Question> allQuestions = new ArrayList<>();

        for (String key : categories.keySet()) {
            allQuestions.addAll(categories.get(key));
        }
        return allQuestions;
    }

    //Einstiegs Punkt für den Editormodus
    private void editMenu() {
        //Stellt alle Kategorien als Auswahl zur Verfuegung
        ArrayList<String> menuOptions = new ArrayList<>(categories.keySet());
        menuOptions.add("Neue Kategorie Erstellen");
        menuOptions.add("Zurück");
        int userChoice = HelperClass.simpleMenu("Welche Kategorie möchten sie editieren?", ": ", menuOptions);
        if (userChoice == menuOptions.size()) {
            //Letzte Option ist Zurueck und geht damit ins Hauptmenu zurueck
            mainMenuChoice();
        } else if (userChoice == menuOptions.size() - 1) {
            //Vorletzte Option ist "Neue Kategorie erstellen"
            addCategory();
        } else {
            //Editor für die Ausgewaehlte Kategorie
            editCategory(menuOptions.get(userChoice - 1));
        }
    }

    private void editCategory(String category) {
        int userChoice = HelperClass.simpleMenu("Waehlen sie eine Option", ": ", "Frage hinzufuegen", "Fragen bearbeiten", "Katerogie umbenennen", "Katerogie loeschen", "Zurueck");
        switch (userChoice) {
            case 1:
                addQuestion(category);
                break;
            case 2:
                editQuestions(category);
                break;
            case 3:
                renameCategory(category);
                break;
            case 4:
                deleteCategory(category);
                break;
            case 5:
                editMenu();
        }
    }

    private void deleteCategory(String category) {
        if (HelperClass.getBoolean("Sind sie sicher, dass sie die Kategorie " + category + " loeschen wollen?")) {
            categories.remove(category);
            if (JsonHelper.removeCategoryFile(category)) {
                System.out.println("Katerogie wurde erfolgreich geloescht\n");
            } else {
                System.err.println("Fehler beim Loeschen!");
            }
        }
    }

    private void renameCategory(String category) {
        String newName = HelperClass.getInputText("Neuer Name für die Kategorie: ");
        if (HelperClass.getBoolean("Sind sie sicher, dass sie " + category + " in " + newName + " umbenennen wollen?")) {
            ArrayList<Question> tmp = categories.get(category);
            categories.remove(category);
            categories.put(newName, tmp);
            if (JsonHelper.renameQuestionsFile(category, newName, tmp)) {
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
        if (userChoice == questionStrings.size()) {
            editCategory(categoryName);
        } else {
            Question question = category.get(userChoice - 1);
            if (editQuestion(question, categoryName)) {
                if (JsonHelper.saveQuestionsToFile(category, categoryName)) {
                    System.out.println("Frage wurde bearbeitet und gespeichert!\n");
                } else {
                    System.err.println("Fehler beim Speichern!");
                }
            }
        }
    }

    //gibt true zurueck wenn etwas editiert wurde
    boolean editQuestion(Question question, String categoryName) {
        System.out.println("\n" + question.toString() + "\n");

        ArrayList<String> options = new ArrayList<>();
        options.add("Frage");
        options.add("Antwort");
        options.add("Alternative Antworten");
        options.add("Statistik Zurücksetzten");
        options.add("Loeschen");
        options.add("Abbrechen");

        while (true) {
            int userChoice = HelperClass.simpleMenu("Was willst du bearbeiten?", ": ", options);
            switch (userChoice) {
                case 1: //Frage Text editieren
                    System.out.println("Derzeitige Frage: " + question.getQuestion());
                    question.setQuestion(HelperClass.getInputText("Neuer Fragetext: "));
                    if (options.size() <= 6) {
                        options.add("Speichern");
                    }
                    break;
                case 2: //Antwort Text editieren
                    System.out.println("Derzeitige Antworten: " + question.getAnswer());
                    question.setAnswer(HelperClass.getInputText("Neue Antwort: "));
                    if (options.size() <= 6) {
                        options.add("Speichern");
                    }
                    break;
                case 3: //Alternative Antworten editieren
                    if (editAlternateAnswers(question)) {
                        if (options.size() <= 6) {
                            options.add("Speichern");
                        }
                    }
                    break;
                case 4: //Statistiken der Frage zuruecksetzen
                    question.resetStats();
                    break;
                case 5: //Frage loeschen
                    if (HelperClass.getBoolean("Sind sie sicher, dass sie diese Frage loeschen moechten?")) {
                        categories.get(categoryName).remove(question);
                        return true;
                    }
                    break;
                case 6: //Bearbeiten abbrechen
                    if (options.size() <= 6) {
                        return false;
                    } else {
                        if (HelperClass.getBoolean("Aenderungen gehen verloren, sind sie sicher, dass sie abbrechen wollen? ")) {
                            return false;
                        }
                    }
                    break;
                case 7: //Frage speichern
                    System.out.println(question);
                    if (HelperClass.getBoolean("Soll die Frage so gespeichert werden?")) {
                        return true;
                    }
                    break;
            }
        }
    }

    //gibt true zurueck wenn etwas editiert wurde
    boolean editAlternateAnswers(Question question) {
        ArrayList<String> options = new ArrayList<>();
        String menuTitle;
        if (question.getAlternateAnswers() == null) {
            menuTitle = "Waehlen sie eine der Optionen:";
            options.add("Alternative Antwort hinzufuegen");
            System.out.println("Dies ist eine Input-Frage");
        } else {
            menuTitle = "Waehlen sie die Alternative Antwort, welche sie bearbeiten moecheten oder eine der Optionen:";
            options.add("Alternative Antwort hinzufuegen\n------------Antworten---------------");
            options.addAll(question.getAlternateAnswers());
            int lastIndex = options.size() - 1;
            options.set(lastIndex, options.get(lastIndex) + "\n------------------------------------");
            options.add("In Input Frage umwandeln");
        }
        options.add("Fertig");

        int userChoice = HelperClass.simpleMenu(menuTitle, "?:", options);
        if (userChoice == options.size()) {
            return false;
        } else if (userChoice == 1) {
            if (question.getAlternateAnswers() != null && question.getAlternateAnswers().size() >= Logic.maxAlternateAnswers) {
                System.out.println("Maximale Anzahl alternativer Antworten erreicht, loeschen sie erst eine andere");
                return false;
            } else {
                question.addAlternateAnswer(HelperClass.getInputText("Geben sie eine alternative (falsche) Antwort ein: "));
                return true;
            }
        } else if (userChoice == options.size() - 1) {
            if (HelperClass.getBoolean("Sind sie sicher das sie die Frage in eine Input Frage umwandeln wollen?\n Dadurch werden alle Alternativen Antworten geloeschtl!")) {
                question.setAlternateAnswers(null);
                return true;
            }
        } else {
            int index = userChoice - 2;
            switch (HelperClass.simpleMenu("Aktuelle Antwort: " + question.getAlternateAnswer(index), "?:", "Bearbeiten", "Loeschen", "Abbrechen")) {
                case 1:
                    question.setAlternateAnswers(index, HelperClass.getInputText("Neuer alternative Antwort Text:"));
                    return true;
                case 2:
                    if (HelperClass.getBoolean("Bist du sicher?")) {
                        question.removeAlternateAnswer(index);
                    }
                    return true;
                case 3:
                    return false;
            }
        }
        return false;
    }


    private void addCategory() {
        System.out.println("\nNeue Kategorie erstellen:\n");
        String category = HelperClass.getInputText("Wie soll die Kategorie heissen? ");
        while (categories.containsKey(category)) {
            category = HelperClass.getInputText("Diese Kategorie existiert bereits.\nBitte andere Bezeichnung eingeben:");
        }
        int userChoice = HelperClass.simpleMenu("Kategorie " + category + " wird erstellt", ": ", "Fortsetzen und erste Frage zu " + category + " hinzufügen", "Abbrechen und zurück ins Hauptmenu");
        if (userChoice == 1) {
            categories.put(category, new ArrayList<>());
            addQuestion(category);
        } else {
            mainMenuChoice();
        }
    }

    private void addQuestion(String category) {
        System.out.println("\nNeue Frage zu " + category + " hinzufuegen:\n");
        String questionText = HelperClass.getInputText("Wie lautet ihre Frage? ");
        String correctAnswer = HelperClass.getInputText("Wie lautet die korrekte Antwort? ");
        ArrayList<String> alternateAnswers = null;

        System.out.println(HelperClass.createChoiceMenuString("\nGibt altenative Antwortmoeglichkeiten oder ist es eine Input-Frage?", "alternative Antworten", "Input-Frage"));
        int userChoice = HelperClass.getInputInt("Waehle einen Fragen Typ: ", 1, 2);
        if (userChoice == 1) {
            userChoice = 0;
            alternateAnswers = new ArrayList<>();
            while (userChoice != 2) {
                alternateAnswers.add(HelperClass.getInputText("\nAlternative (falsche) Antwortmoeglichkeit: "));

                if (alternateAnswers.size() < maxAlternateAnswers) {
                    System.out.println(HelperClass.createChoiceMenuString("", "Weitere Antwortmoeglichkeit", "Fertig"));
                    userChoice = HelperClass.getInputInt(": ", 1, 2);
                } else {
                    userChoice = 2;
                }
            }
        }

        Question question = new Question(questionText, correctAnswer, alternateAnswers, 0, 0);
        //userChoice = HelperClass.simpleMenu(, ": ", "Ja", "Nein");
        if (HelperClass.getBoolean("Wollen sie folgende Frage speichern?\n" + question.toString() + "\n")) {
            categories.get(category).add(question);
            if (JsonHelper.saveQuestionsToFile(categories.get(category), category)) {
                System.out.println("Frage wurde zu " + category + " hinzugefuegt und gespeichert!\n");
            } else {
                System.err.println("Fehler beim Speichern!");
            }
            userChoice = HelperClass.simpleMenu("Wollen sie...", ": ", "Eine weitere Frage zu " + category + " hinzufuegen", "Zurück ins Hauptmenu");
            if (userChoice == 1) {
                addQuestion(category);
            } else {
                mainMenuChoice();
            }
        } else {
            System.out.println("Frage wurde verworfen!");
        }
        mainMenuChoice();
    }

    private void showStatistics() {
        var globalStats = HelperClass.createGlobalStatValues(categories);
        System.out.println();

        System.out.println("Herzlich Willkommen im Statistik Bereich:");
        System.out.println(HelperClass.createChoiceMenuString("Welche Statistiken wollen Sie sehen?", statisticChoices));

        var userInput = HelperClass.getInputInt("Auswahl: ", 1, statisticChoices.length);

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
        } else if (userInput == 3) {
            String[] categoryOptions = categories.keySet().toArray(String[]::new);
            int choice = HelperClass.simpleMenu("Wählen sie eine Kategorie: ", ": ", categoryOptions);
            String category = categoryOptions[choice - 1];
            System.out.println("------ " + category + " ------");
            if (HelperClass.printCategoryStats(categories.get(category))) {
                for (Question q : categories.get(category)) {
                    q.printDetailedStats();
                }
            }
        } else if (userInput == 4) {
            for (ArrayList<Question> category : categories.values()) {
                for (Question q : category) {
                    q.resetStats();
                }
            }
        }


    }
}
