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
        JsonReader jsonReader = null;
        try {
            jsonReader = new JsonReader(new FileReader(file));
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
                return categories;
            }
        }
        return null;
    }

    static void saveQuestionsToFile(ArrayList<Question> questions, String path) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        Gson gson = gsonBuilder.create();
        String json = gson.toJson(questions);
        //System.out.println(json);
        //Write JSON file
        try (FileWriter file = new FileWriter(path)) {

            file.write(json);
            file.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
