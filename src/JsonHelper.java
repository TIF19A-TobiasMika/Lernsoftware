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

    static HashMap<String, ArrayList<Question>> ReturnQuestionsAndCategories() {
        File dir = new File("categories");
        if (dir.isDirectory()) {
            HashMap<String, ArrayList<Question>> categories = new HashMap<>();
            try {
                for (File file : Objects.requireNonNull(dir.listFiles())) {
                    String filename = file.getName();
                    filename = filename.substring(0, filename.length() - 5);
                    //System.out.println(filename);
                    categories.put(filename, getQuestionsFromFile(file));
                }
                return categories;
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
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
}
