import java.util.Comparator;

public class SortByWrongAnswers implements Comparator<Question>
{
    @Override
    public int compare(Question question1, Question question2) {
        return question2.getWrongAnswers() - question1.getWrongAnswers();
    }
}
