import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class HighScores {
    HashMap<Integer, Integer> millionaireScores;
    ArrayList<Integer> normalModeScores;
    ArrayList<Integer> hardModeScores;
    ArrayList<Integer> survivalScores;

    public HighScores(HashMap<Integer, Integer> millionaireScores, ArrayList<Integer> normalModeScores, ArrayList<Integer> hardModeScores, ArrayList<Integer> survivalScores) {
        this.millionaireScores = millionaireScores;
        this.normalModeScores = normalModeScores;
        this.hardModeScores = hardModeScores;
        this.survivalScores = survivalScores;
    }

    public void resetAll() {
        millionaireScores.clear();
        normalModeScores.clear();
        hardModeScores.clear();
        survivalScores.clear();
    }

    public int getMillionaireGameCount() {
        int count = 0;
        for(int i : millionaireScores.values()) {
            count += i;
        }
        return count;
    }

    public long getMillionaireSum() {
        long sum = 0;
        for(int key : millionaireScores.keySet()) {
            sum += (key * millionaireScores.get(key));
        }
        return sum;
    }

    public long getMillionaireAverage() {
        long sum = 0;
        int games = 0;
        for(int key : millionaireScores.keySet()) {
            int i =  millionaireScores.get(key);
            games += i;
            sum += (key * i);
        }
        return sum / games;
    }

    public void addHardScore(int score) {
        hardModeScores.add(score);
    }

    public void addNormalScore(int score) {
        normalModeScores.add(score);
    }

    public void addSurvivalScore(int score) {
        survivalScores.add(score);
    }

    public long arrayListSum(ArrayList<Integer> list) {
        long sum = 0;
        for(int i : list) {
            sum += i;
        }
        return sum;
    }

    public void addToMillionaireScores(int level) {
        var score = millionaireScores.get(level);
        if(score == null) {
            score = 0;
        }
        millionaireScores.put(level, score+1);
    }

    public void printStats() {
        System.out.println("\n" + this.toString());
    }

    public void printNormalStats() {
        StringBuilder sb = new StringBuilder("Normaler Modus:\n");
        sb.append(modeBasicsString(normalModeScores));
        sb.append("% der Fragen Richtig\n");
        sb.append(String.format("  Durchschnittlich %d%% der Fragen Richtig", calcAverage(normalModeScores)));
        System.out.println(sb.toString());
    }

    public void printHardStats() {
        StringBuilder sb = new StringBuilder("Meisten Fehler Modus:\n");
        sb.append(modeBasicsString(hardModeScores));
        sb.append("% der Fragen Richtig\n");
        sb.append(String.format("  Durchschnittlich %d%% der Fragen Richtig", calcAverage(hardModeScores)));
        System.out.println(sb.toString());
    }

    public void printSurvivalStats() {
        StringBuilder sb = new StringBuilder("Ueberlebensmodus:\n");
        sb.append(modeBasicsString(survivalScores));
        sb.append(" Fragen Richtig\n");
        sb.append(String.format("  Durchschnittlich %d Fragen geschafft\n", calcAverage(survivalScores)));
        sb.append(String.format("  Insgesammt %d Fragen beantwortet", arrayListSum(survivalScores)));
        System.out.println(sb.toString());
    }

    public void printMillionaireStats() {
        StringBuilder sb = new StringBuilder("Wer wird Millionaer Scores:\n");
        sb.append(String.format("  Anzahl Spiele: %d\n", getMillionaireGameCount()));
        sb.append(String.format("  Hoester Gewinn: %d€\n", Collections.max(millionaireScores.keySet())));
        sb.append(String.format("  Gesammter Gewinn: %d€\n", getMillionaireSum()));
        sb.append(String.format("  Durchschnittlicher Gewinn: %d€\n", getMillionaireAverage()));
        System.out.println(sb.toString());
    }

    int calcAverage(ArrayList<Integer> scores) {
        return (int) (arrayListSum(scores) / scores.size());
    }

    String modeBasicsString(ArrayList<Integer> scores) {
        return String.format("  Anzahl Spiele: %d\n  High Score: %d", scores.size(), Collections.max(scores));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("HighScores:\n");
        sb.append(String.format("Insgesammt gespielte Spiele: %d\n", survivalScores.size() + hardModeScores.size() + normalModeScores.size() + getMillionaireGameCount()));
        sb.append("\nNormal:\n");
        if(normalModeScores.size() > 0) {
            sb.append(modeBasicsString(normalModeScores));
            sb.append("%der Fragen Richtig\n");
        } else {
            sb.append("Modus noch nicht gespielt");
        }
        sb.append("\nMeisten Fehler:\n");
        if(hardModeScores.size() > 0) {
            sb.append(modeBasicsString(hardModeScores));
            sb.append("%der Fragen Richtig\n");
        } else {
            sb.append("Modus noch nicht gespielt");
        }
        sb.append("\nUeberlebensmodus:\n");
        if(survivalScores.size() > 0) {
            sb.append(modeBasicsString(survivalScores));
            sb.append(" Fragen Richtig\n");
        } else {
            sb.append("Modus noch nicht gespielt");
        }
        sb.append("\nWer wird Millionaer:\n");
        if(millionaireScores.size() > 0) {
            sb.append(String.format("  Anzahl Spiele: %d\n", getMillionaireGameCount()));
            sb.append(String.format("  Hoester Gewinn: %d€", Collections.max(millionaireScores.keySet())));
        } else {
            sb.append("Modus noch nicht gespielt");
        }
        return sb.toString();
    }

    public void reset(int choice) {
        switch (choice) {
            case 1: resetAll(); break;
            case 2: normalModeScores.clear(); break;
            case 3: survivalScores.clear(); break;
            case 4: millionaireScores.clear(); break;
            case 5: hardModeScores.clear(); break;
        }
    }
}
