public class HelperClass {

    static String createChoiceMenuString(String title, String[] options) {
        StringBuilder sb = new StringBuilder();
        sb.append(title);
        sb.append("\n");
        for (int i = 0; i < options.length; i++) {
            sb.append(i+1);
            sb.append(") ");
            sb.append(options[i]);
            sb.append('\n');
        }
        return  sb.toString();
    }




}
