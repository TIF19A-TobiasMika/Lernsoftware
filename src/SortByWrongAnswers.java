import java.util.Comparator;

public class SortByWrongAnswers implements Comparator<Question>
{
    @Override
    public int compare(Question question1, Question question2) {
        return question2.wrongAnswers - question1.wrongAnswers;
    }
}
