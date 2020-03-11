import java.util.*;
import java.util.stream.Collectors;

public class Logic {

    private final HashMap<String, ArrayList<Question>> categories;
    private final String[] mainMenuChoices;
    private final String[] statisticChoices;
    private final String[] gameModes;
    final static int maxAlternateAnswers = 4;
    final static int survivalLives = 3;
    private final int[] millionaireLevels;
    static Random random = new Random();;

    public Logic() {
        categories = JsonHelper.ReturnQuestionsAndCategories();

        mainMenuChoices = new String[]{"Spielen", "Bearbeitungsmodus", "Statistiken", "Beenden"};
        statisticChoices = new String[]{"Allgemein", "Fragenspezifisch", "Kategoriespezifisch", "Statistiken zurücksetzten"};
        gameModes = new String[]{"Normal", "Ueberlebensmodus", "Wer wird Millionaer", "Fragen mit meisten Fehlern"};
        millionaireLevels = new int[]{50, 100, 200, 300, 500, 1000, 2000, 4000, 8000, 16000, 32000, 64000, 125000, 500000, 1000000};
    }

    private String[] categoriesToSave;

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

    //Einstiegspunkt fuer den Spielmodus
    private void playMenu() {
        System.out.println();
        System.out.println("Sie befinden sich im Quiz-Modus");

        int userChoice = HelperClass.simpleMenu("Welchen Spielmodus moechten sie spielen?", "Modus: ", gameModes);
        switch (userChoice) {
            case 1:
                playQuiz();
                break;
            case 2:
                playSurvivalQuiz();
                break;
            case 3:
                playMillionaireQuiz();
                break;
            case 4:
                playQuiz(true);
                break;
        }
    }

    //Speicher die Kategorien aus categoriesToSave
    private void saveCategories() {
        for (String category : categoriesToSave) {
            JsonHelper.saveQuestionsToFile(categories.get(category), category);
        }
    }

    private void playQuiz() {
        playQuiz(false);
    }

    //Normaler Quizmodus mit frei waehlbarer Kategorie und Anzahl an Fragen
    private void playQuiz(boolean mostWrong) {
        //Eine Liste von Fragen aus einer oder mehrerne vom Nutzer gewaehlten Kategorien.
        ArrayList<Question> selectedQuestions = selectQuestions();
        while (selectedQuestions.size() <= 0) {
            System.out.println("Die ausgewaehlte(n) Kategorie(n) enthalten keine Fragen, bitte waehlen sie andere Kategorien\n");
            selectedQuestions = selectQuestions();
        }
        var numberOfQuestions = HelperClass.getInputInt("Wieviele Fragen wollen Sie beantworten (max. " + selectedQuestions.size() + " )? ", 1, selectedQuestions.size());

        List<Question> questions;
        if (mostWrong) {
            //Sortiert die Liste nach den am meisten Falsch beantworteten Fragen und gibt eine Liste mit numberOfQuestions Fragen zurueck
            questions = HelperClass.generateSortedRandomQuestions(numberOfQuestions, selectedQuestions);
        } else {
            //gibt eine Liste mit numberOfQuestions Fragen zurueck
            questions = HelperClass.generateRandomQuestions(numberOfQuestions, selectedQuestions);
        }

        int correctQuestions = 0;
        for (int i = 0; i < questions.size(); i++) {
            System.out.println("\nFrage " + (i + 1) + ":");
            if (askQuestion(questions.get(i))) {
                correctQuestions++;
            }
        }
        System.out.println("Sie haben " + correctQuestions + " von " + numberOfQuestions + " Fragen richtig beantwortet!");

        //Speichert die Fragen in den Kategorien aus categoriesToSave (fuer die Statistik)
        saveCategories();
    }

    /*Ueberlebens Modus, es werden solange Fragen gestellt bis der Nutzer keine Leben mehr hat.
    Bei Falsch beantworteten Fragen verliert der Nutzer ein Leben*/
    private void playSurvivalQuiz() {
        ArrayList<Question> selectedQuestions = selectAllQuestions();
        if (selectedQuestions.size() <= 0) {
            System.out.println("Es sind keine Fragen vorhanden! Bitte fuegen sie erst welche hinzu.");
            return;
        }
        int lives = survivalLives;
        int correctQuestions = 0;
        survivalLoop:
        while (true) {
            //Bringt die Fragen in eine zufaellige Reihenfolge
            Collections.shuffle(selectedQuestions);
            for (int i = 0; i < selectedQuestions.size(); i++) {
                System.out.println("\nFrage " + (i + 1) + ":");
                if (askQuestion(selectedQuestions.get(i))) {
                    correctQuestions++;
                } else {
                    lives--;
                    if (lives <= 0) {
                        System.out.println("\nDas war leider ihr letztes Leben. Vielen Dank fuers Spielen");
                        break survivalLoop;
                    } else {
                        System.out.println("\nSie haben noch " + lives + " Leben\n");
                    }
                }
            }
            System.out.println("Glueckwunsch sie sind einmal alle Fragen durch, ab jetzt wiederholen sich Fragen");
        }
        System.out.println("Sie haben " + correctQuestions + " Fragen richtig beantwortet!");
        //Speichert alle Fragen bzw. deren neue Statistik
        JsonHelper.saveAllCategoriesToFile(categories);
    }

    private void playMillionaireQuiz() {
        ArrayList<Question> selectedQuestions = selectAllChoiceQuestions(2);

        if (selectedQuestions.size() < millionaireLevels.length) {
            System.out.println("Es sind zu wenige Fragen vorhanden! Bitte fuegen sie mehr hinzu.");
            return;
        }
        System.out.println("Wilkommen bei wer wird Millinaer, baentworten sie alle " + millionaireLevels.length + " Fragen richtig um zu gewinnen.");
        System.out.println("Außerdem haben sie einen 50/50 Joker und einen Publikums Joker");
        //A Qustion for each Level, sorted so that Level 1 gets the Question with the least wrong Answers and the last Level that with the most
        Question[] levelQuestions = HelperClass.createMillionaireLevels(selectedQuestions, millionaireLevels.length);
        //Liste noch vorfuegbarer Joker
        ArrayList<String> jokers = new ArrayList<>();
        jokers.add("50/50 Joker");
        jokers.add("Publikums Joker");
        boolean audienceJoker = false;

        //Loop for each Level
        questionLoop:
        for (int i = 0; i < millionaireLevels.length; i++) {
            audienceJoker = false;
            Question levelQuestion = levelQuestions[i];
            System.out.println("\n" + millionaireLevels[i] + "€ Frage:");
            // Check if alternate Answers are available for question
            if (levelQuestion.getAlternateAnswers() != null) {
                //Get randomized answer array for question
                var answerArray = HelperClass.generateRandomAnswerArray(levelQuestion.getAnswer(), levelQuestion.getAlternateAnswers());

                while (jokers.size() > 0) {
                    System.out.println(HelperClass.createChoiceMenuString(levelQuestion.getQuestion(), answerArray));

                    System.out.println("Verfügbare Joker:");
                    for (int j = 1; j <= jokers.size(); j++) {
                        System.out.println(String.format("%d) %s", j + answerArray.size(), jokers.get(j - 1)));
                    }
                    var userAnswerIndex = HelperClass.getInputInt("Ihre Antwort, oder waehlen sie einen Joker: ", 1, answerArray.size() + jokers.size());

                    if (userAnswerIndex > answerArray.size()) {
                        String joker = jokers.get(userAnswerIndex - answerArray.size() - 1);
                        System.out.println("Sie haben den " + joker + " gewaehlt!");
                        if (joker.equals("50/50 Joker")) {
                            var alternateAnswersArray = levelQuestion.getAlternateAnswers();
                            Collections.shuffle(alternateAnswersArray);
                            answerArray = HelperClass.generateRandomAnswerArray(levelQuestion.getAnswer(), alternateAnswersArray.subList(0, alternateAnswersArray.size() / 2));
                            if(audienceJoker) {
                                addPercentages(answerArray, levelQuestion.getAnswer());
                            }
                            jokers.remove("50/50 Joker");
                            System.out.println("\nEinige der falschen Antworten wurden entfernt");
                        } else if (joker.equals("Publikums Joker")) {
                            jokers.remove("Publikums Joker");
                            audienceJoker = true;
                            addPercentages(answerArray, levelQuestion.getAnswer());
                            System.out.println("\nDas Publikum hat abgestimmt, die Ergebnisse (in %) stehen neben den Fragen");
                        }
                    } else {
                        // Use user input index to compare user answer to correct answer
                        String pickedAnswer = answerArray.get(userAnswerIndex - 1);
                        if(audienceJoker) {
                            pickedAnswer = pickedAnswer.split(",")[0];
                        }
                        if (levelQuestion.getAnswer().equalsIgnoreCase(pickedAnswer)) {
                            System.out.println("Richtige Antwort!");
                            levelQuestion.addToCorrectAnswers(1);
                            continue questionLoop;
                        } else {
                            System.out.println("Falsche Antwort! Die richtige Antwort lautet: '" + levelQuestion.getAnswer() + "'.");
                            levelQuestion.addToWrongAnswer(1);
                            System.out.println("Damit haben sie leider Verloren, bis zum naechsten Mal");

                            return;
                        }
                    }
                }
                var userAnswerIndex = HelperClass.simpleMenu(levelQuestion.getQuestion(), "Ihre Antwort: ", answerArray);

                // Use user input index to compare user answer to correct answer
                String pickedAnswer = answerArray.get(userAnswerIndex - 1);
                if(audienceJoker) {
                    pickedAnswer = pickedAnswer.split(",")[0];
                }
                if (levelQuestion.getAnswer().equalsIgnoreCase(pickedAnswer)) {
                    System.out.println("Richtige Antwort!");
                    levelQuestion.addToCorrectAnswers(1);
                } else {
                    System.out.println("Falsche Antwort! Die richtige Antwort lautet: '" + levelQuestion.getAnswer() + "'.");
                    System.out.println("Damit haben sie leider Verloren, bis zum naechsten Mal");
                    levelQuestion.addToWrongAnswer(1);
                    return;
                }
            } else {
                System.err.println("Hier sollten keine Input Fragen vorkommen!");
            }
        }
        System.out.println("Herrzlichen Glueckwunsch sie haben alle " + millionaireLevels.length + " Fragen Richtig beantwortet und die 1.000.000€ gewonnen!");
        JsonHelper.saveAllCategoriesToFile(categories);
    }

    //Adds "Random" Percentages to the Answers. Needed for the the Audiece Joker
    private void addPercentages(ArrayList<String> answerArray, String correctQuestion) {
        //Creates an Array and fills it with Rnadom Numbers between 10 und 100
        int[] percentages = new int[answerArray.size()];
        int percentagesSum = 0;
        for (int k = 0; k < answerArray.size(); k++) {
            percentages[k] = 10 + random.nextInt(91);
            percentagesSum += percentages[k];
        }
        int sum = 0;
        //Makes it so that the sum auf all Numebers in the Array equal 100
        for (int k = 0; k < answerArray.size(); k++) {
            percentages[k] = (percentages[k]*100)/percentagesSum;
            sum += percentages[k];
        }
        //Sorts the Array
        Arrays.sort(percentages);
        //Picks the largest or second largest Number for the correct Answer
        int correctIndex = percentages.length - (1 + random.nextInt(2));
        int answerIndex = answerArray.size()-1;
        //Adds the rounding Errors to the correct Percentage
        percentages[correctIndex] += (100 - sum);
        //Matches the Questions to a Percantage
        for (int j = 0; j < answerArray.size(); j++) {
            String answer = answerArray.get(j);
            if(answer.equals(correctQuestion)) {
                answerIndex = j;
                answerArray.set(j, String.format("%s,    %d%%", answer, percentages[correctIndex]));
            } else if(j == correctIndex) {
                answerArray.set(j, String.format("%s,    %d%%", answer, percentages[answerIndex]));
            } else {
                answerArray.set(j, String.format("%s,    %d%%", answer, percentages[j]));
            }
        }
    }

    //Gibt eien Liste mit allen Multiple Choice Fragen, welche die Mnidestanzahl an alternativen Antworten haben, zurueck
    private ArrayList<Question> selectAllChoiceQuestions(int minAltChoices) {
        ArrayList<Question> selectedQuestions = new ArrayList<>();
        for (ArrayList<Question> cat : categories.values()) {
            for (Question q : cat) {
                if (q.getAlternateAnswers() != null && q.getAlternateAnswers().size() >= minAltChoices) {
                    selectedQuestions.add(q);
                }
            }
        }
        return selectedQuestions;
    }

    /*Laesst den Nutzer Kategorien auswaehlen und gibt dann
     * alle Fragen in den gewahlten Kategorien zurueck*/
    private ArrayList<Question> selectQuestions() {
        ArrayList<Question> selectedQuestions = new ArrayList<>();
        String[] categoryNames = this.categories.keySet().toArray(String[]::new);
        String menuQuestion = "\nAus welcher Kategorie sollen Fragen gestellt werden?" +
                "\nFuer einen Mix aus allen Kategorien schreiben Sie einfach '0'." +
                "\nFuer mehrere Kategorien auf einmal, geben sie deren Nummern kommgetrennt an (z.B. 2,3).";
        System.out.println(HelperClass.createChoiceMenuString(menuQuestion, categoryNames));

        //Ein Set mit den vom Nutzer gewaehlten Kategoreien
        Set<Integer> categoryIndex = HelperClass.getInputInts("Welche Kategorien (0 fuer Alle)? ", 0, categoryNames.length);

        //Wenn das categoryIndex Set 0 enthaelt sollen alle Fragen genommen werden
        if (categoryIndex.contains(0)) {
            for (ArrayList<Question> cat : categories.values()) {
                selectedQuestions.addAll(cat);
            }
            //Nach dem Spiel muessen alle Kategorien gespeichert werden
            categoriesToSave = categories.keySet().toArray(String[]::new);
        } else {
            //Liste der zu speichernden Kategorien
            categoriesToSave = new String[categoryIndex.size()];
            int saveIndex = 0;
            //(i - 1) entspricht dem index in categoryNames
            for (Integer i : categoryIndex) {
                //Fuegt die Fragen aus der ensprechende Kategorie zu selectedQuestions hinzu
                selectedQuestions.addAll(categories.get(categoryNames[i - 1]));
                //Fuegt die dem index ensprechende Kategorie zur Speicherliste hinzu
                categoriesToSave[saveIndex] = categoryNames[i - 1];
                saveIndex++;
            }
        }
        return selectedQuestions;
    }

    //Gibt eien Liste mit allen Fragen zurueck
    private ArrayList<Question> selectAllQuestions() {
        ArrayList<Question> selectedQuestions = new ArrayList<>();
        for (ArrayList<Question> cat : categories.values()) {
            selectedQuestions.addAll(cat);
        }
        return selectedQuestions;
    }

    //Fragt eine Frage ab, gibt zurueck ob diese richtig beantwortet wurde
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

    //Untermenu zum editieren einer Kategorie
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

    //laesst den Nutzer eine Kategorie loeschen
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

    //Laesst den Nutzer eine Kategorie umbenennen
    private void renameCategory(String category) {
        String newName = HelperClass.getInputText("Neuer Name für die Kategorie: ");
        while (categories.keySet().contains(newName)) {
            System.out.println("Es gibt bereits eine Katergorie mit diesem Namen!");
            newName = HelperClass.getInputText("Neuer Name für die Kategorie: ");
        }
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

    //Laesst den Nutzer eine Frage aus der Kategorie zu editieren auswaehlen
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

    /*Laesst den Nutzer eine bestimmte Frage editieren
    gibt true zurueck wenn etwas editiert wurde*/
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

    //Laesst den Nutzer eine neue Kategorie erstellen
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

    //laesst den Nutzer eine neue Frage zur angegebenen Kategorie hinzufuegen
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

    //Untermenu fuer Statistiken
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
            JsonHelper.saveAllCategoriesToFile(categories);
        }


    }
}
