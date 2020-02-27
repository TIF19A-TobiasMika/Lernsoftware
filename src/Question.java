import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Question {
    private String question;
    private String answer;
    private ArrayList<String> alternateAnswers;
    private int correctAnswers;
    private int wrongAnswers;

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(int correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public int getWrongAnswers() {
        return wrongAnswers;
    }

    public void setWrongAnswers(int wrongAnswers) {
        this.wrongAnswers = wrongAnswers;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public void setAlternateAnswers(ArrayList<String> alternateAnswers) {
        this.alternateAnswers = alternateAnswers;
    }

    public void removeAlternateAnswer(int index) {
        this.alternateAnswers.remove(index);
        if(this.alternateAnswers.size() <= 0) {
            this.alternateAnswers = null;
        }
    }

    public void setAlternateAnswers(int index, String alternateAnswer) {
        this.alternateAnswers.set(index, alternateAnswer);
    }

    public String getAnswer() {
        return answer;
    }

    public ArrayList<String> getAlternateAnswers() {
        return alternateAnswers;
    }

    public String getAlternateAnswer(int index) {
        return alternateAnswers.get(index);
    }

    public Question(String question, String answer, ArrayList<String> alternateAnswers, int correctAnswers, int wrongAnswers) {
        this.question = question;
        this.answer = answer;
        this.alternateAnswers = alternateAnswers;
        this.correctAnswers = correctAnswers;
        this.wrongAnswers = wrongAnswers;
    }

    public Question(String question, String answer, int correctAnswers, int wrongAnswers) {
        this.question = question;
        this.answer = answer;
        this.alternateAnswers = null;
        this.correctAnswers = correctAnswers;
        this.wrongAnswers = wrongAnswers;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Frage: ");
        sb.append(question);
        sb.append("\nAntwort: ");
        sb.append(answer);
        if (alternateAnswers != null) {
            sb.append("\nAlternative Antworten: ");
            for (String str : alternateAnswers) {
                sb.append("\n  ");
                sb.append(str);
            }
        } else {
            sb.append("\nInput Frage");
        }
        return sb.toString();
    }

    public void printStats() {
        System.out.println("Frage: " + question);
        if (correctAnswers + wrongAnswers > 0) {
            System.out.println(String.format("Insgesamt %d Mal beantwortet, davon %d%% Mal richtig\n", (correctAnswers + wrongAnswers), (correctAnswers * 100) / (correctAnswers + wrongAnswers)));
            //System.out.println(String.format("%dx Richtig beantwortet, %dx Falsch Beantwortet", correctAnswers, wrongAnswers));
        } else {
            System.out.println("Diese Frage wurde noch nie beantwortet\n");
        }
    }

    public void resetStats() {
        setCorrectAnswers(0);
        setWrongAnswers(0);
    }

    public void printDetailedStats() {
        System.out.println("Frage: " + question);
        System.out.println("Richtige Antwort: " + answer);
        if (correctAnswers + wrongAnswers > 0) {
            System.out.println(String.format("Insgesamt %d Mal beantwortet, davon %d%% Mal richtig", (correctAnswers + wrongAnswers), (correctAnswers * 100) / (correctAnswers + wrongAnswers)));
            System.out.println(String.format("%dx Richtig beantwortet, %dx Falsch Beantwortet\n", correctAnswers, wrongAnswers));
        } else {
            System.out.println("Diese Frage wurde noch nie beantwortet\n");
        }
    }

    public void addAlternateAnswer(String getInputText) {
        if(this.alternateAnswers == null) {
            this.alternateAnswers = new ArrayList<>();
        }
        this.alternateAnswers.add(getInputText);
    }

    public void addToCorrectAnswers(int i) {
        correctAnswers += i;
    }

    public void addToWrongAnswer(int i) {
        wrongAnswers += i;
    }
}