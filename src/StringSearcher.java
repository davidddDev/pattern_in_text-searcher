import java.util.Scanner;

public class StringSearcher {
    private String text;
    private String vzor;
    private int pozice = 0;
    private int indexVzoru = 0;
    private Scanner sc = new Scanner(System.in);

    public StringSearcher(String text, String vzor) {
        this.text = text;
        this.vzor = vzor;
    }
    public void start() {
        while (true) {
            zobrazStav();
            System.out.println("Možnosti: [v]před, [z]pět, ");
        }
    }
}
