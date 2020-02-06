public class Question {
    private String question;
    private String answer;
    private String[] alternateAnswers;
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

    public String getAnswer() {
        return answer;
    }

    public String[] getAlternateAnswers() {
        return alternateAnswers;
    }

    public Question(String question, String answer, String[] alternateAnswers, int correctAnswers, int wrongAnswers) {
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
        if(alternateAnswers != null) {
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
}
