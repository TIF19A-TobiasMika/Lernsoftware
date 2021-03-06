import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class JsonHelper {

    static ArrayList<Question> getQuestionsFromFile(File file) {
        try {
            JsonReader jsonReader = new JsonReader(new FileReader(file));
            Gson gson = new Gson();
            ArrayList<Question> questions = gson.fromJson(jsonReader, new TypeToken<ArrayList<Question>>() {
            }.getType());
            jsonReader.close();
            return questions;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    static HashMap<String, ArrayList<Question>> returnQuestionsAndCategories() {
        File dir = new File("categories");
        if (dir.isDirectory()) {
            HashMap<String, ArrayList<Question>> categories = new HashMap<>();
            try {
                for (File file : Objects.requireNonNull(dir.listFiles())) {
                    if (file.isFile()) {
                        String filename = file.getName();
                        String extension;

                        int i = filename.lastIndexOf('.');
                        if (i >= 0) {
                            extension = filename.substring(i + 1);
                            //System.out.println(extension);
                            if (extension.equals("json")) {
                                ArrayList<Question> questions = getQuestionsFromFile(file);
                                if (questions != null) {
                                    String categoryName = filename.substring(0, filename.length() - (extension.length() + 1));
                                    //System.out.println(categoryName);
                                    categories.put(categoryName, questions);
                                }
                            }
                        }
                    }
                }
                return categories;
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("Error categories Folder not found");
            System.err.println("Plese create Folder " + dir.getAbsolutePath() + " with the category files");
            System.exit(1);
        }
        return null;
    }

    static void saveAllCategoriesToFile(HashMap<String, ArrayList<Question>> categories) {
        for (String key : categories.keySet()) {
            saveQuestionsToFile(categories.get(key), key);
        }
    }

    static boolean removeCategoryFile(String category) {
        File file = new File("categories/" + category + ".json");
        return file.delete();
    }

    static boolean saveQuestionsToFile(ArrayList<Question> questions, String category) {
        //questions.stream().forEach(System.out::println);
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        Gson gson = gsonBuilder.create();
        String json = gson.toJson(questions, new TypeToken<ArrayList<Question>>() {
        }.getType());
        //Write JSON file
        try (FileWriter file = new FileWriter("categories/" + category + ".json")) {
            file.write(json);
            file.flush();
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean renameQuestionsFile(String altName, String newName, ArrayList<Question> category) {
        if (saveQuestionsToFile(category, newName)) {
            if (!removeCategoryFile(altName)) {
                System.err.println("Alte Datei (" + altName + ".json) konnte nicht geloescht werden!");
            }
            return true;
        } else {
            return false;
        }
    }

    public static HighScores loadHighScores() {
        try {
            JsonReader jsonReader = new JsonReader(new FileReader(new File("highScores.json")));
            Gson gson = new Gson();
            HighScores highScores = gson.fromJson(jsonReader, new TypeToken<HighScores>() {
            }.getType());
            jsonReader.close();
            return highScores;
        } catch (IOException e) {
            e.printStackTrace();
            return new HighScores(new HashMap<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        }
    }

    static boolean saveHighScores(HighScores scores) {
        //questions.stream().forEach(System.out::println);
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        Gson gson = gsonBuilder.create();
        String json = gson.toJson(scores, new TypeToken<HighScores>() {
        }.getType());
        //Write JSON file
        try (FileWriter file = new FileWriter("highScores.json")) {
            file.write(json);
            file.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
