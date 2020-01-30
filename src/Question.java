public class Question {
    String question;
    String answer;
    String[] alternateAnswers;

    public Question(String question, String answer, String[] alternateAnswers) {
        this.question = question;
        this.answer = answer;
        this.alternateAnswers = alternateAnswers;
    }

    @Override
    public String toString() {
        StringBuilder tmp = new StringBuilder("Question: ");
        tmp.append(question);
        tmp.append("\nAnswer: ");
        tmp.append(answer);
        tmp.append("\nAlternate Answers: ");
        for (String str : alternateAnswers) {
            tmp.append("\n  ");
            tmp.append(str);
        }
        return tmp.toString();
    }
}
