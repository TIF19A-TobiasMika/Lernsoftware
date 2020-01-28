public class Main {
    public static void main(String[] args) {
        Question q1 = new Question("Frage 1", "Antwort 1");
        System.out.println(HelperClass.createChoiceMenuString("Test", new String[] {"Option A", "Option B", "Option C"}));

    }
}
