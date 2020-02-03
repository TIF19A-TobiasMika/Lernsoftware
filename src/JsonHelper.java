import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class JsonHelper {


    static ArrayList<Question> getQuestionsFromFile(String path) {
        JsonReader jsonReader = null;
        try {
            jsonReader = new JsonReader(new FileReader(path));
            Gson gson = new Gson();
            return gson.fromJson(jsonReader, new TypeToken<ArrayList<Question>>() {}.getType());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    static ArrayList<Question> getQuestionsFromFile(File file) {
        try {
            JsonReader jsonReader = new JsonReader(new FileReader(file));
            Gson gson = new Gson();
            return gson.fromJson(jsonReader, new TypeToken<ArrayList<Question>>() {
            }.getType());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    static HashMap<String, ArrayList<Question>> ReturnQuestionsAndCategories() {
        File dir = new File("categories");
        if (dir.isDirectory()) {
            HashMap<String, ArrayList<Question>> categories = new HashMap<>();
            for (File file : dir.listFiles()) {
                String filename = file.getName();
                filename = filename.substring(0, filename.length()-5);
                System.out.println(filename);
                categories.put(filename, getQuestionsFromFile(file));
            }
            return categories;
        }
        return null;
    }

    static void saveAllCategoriesToFile(HashMap<String, ArrayList<Question>> categories) {
        for(String key : categories.keySet()) {
            saveQuestionsToFile(categories.get(key), key);
        }
    }

    static boolean saveQuestionsToFile(ArrayList<Question> questions, String name) {
        //questions.stream().forEach(System.out::println);
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        Gson gson = gsonBuilder.create();
        String json = gson.toJson(questions, new TypeToken<ArrayList<Question>>(){}.getType());
        //Write JSON file
        try (FileWriter file = new FileWriter("categories/" + name + ".json")) {
            file.write(json);
            file.flush();
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}
