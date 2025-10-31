public class StringSearcher {
    private static final String GREEN = "\u001B[32m"; // ANSI kody
    private static final String RESET = "\u001B[0m";

    private String text;    // text, ve kterem se hleda
    private String pattern; // hledany vzor

    public StringSearcher(String text, String pattern) {
        this.text = text;
        this.pattern = pattern;
    }

    public void findAllOccurrences() {
        boolean found = false;              // vzor neni zatim nalezen
        StringBuilder sb = new StringBuilder(text); // pouzije se pro upravu textu
        int offset = 0; // posun kvuli vlozenym barvam

        // prochazi text po znacich
        for (int i = 0; i <= text.length() - pattern.length(); i++) {
            int j;
            // porovnava znaky textu a vzoru
            for (j = 0; j < pattern.length(); j++) {
                if (text.charAt(i + j) != pattern.charAt(j)) break;
            }
            // pokud vsechny znaky odpovidaji, nasel vyskyt a zabarvi to zelene
            if (j == pattern.length()) {
                found = true;
                int start = i + offset;
                sb.insert(start, GREEN); // vlozi zacatek barvy
                offset += GREEN.length();
                sb.insert(start + pattern.length() + GREEN.length(), RESET); // vlozi konec barvy
                offset += RESET.length();

                System.out.println("✅ Nalezen vyskyt na pozici: " + i);
            }
        }

        // vypise vysledny text
        if (found) {
            System.out.println("\nText s vyznacenymi vyskyt y:");
            System.out.println(sb.toString());
        } else {
            System.out.println("\n❌ Vzorek nebyl nalezen v textu.");
        }
    }
}
