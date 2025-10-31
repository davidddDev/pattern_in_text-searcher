import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;
import java.awt.*;

public class StepSearchGUI extends JFrame {

    private JTextPane textPane;
    private JTextArea inputTextArea;
    private JTextField patternField;
    private JButton nextStepButton, skipButton, resetButton, setTextButton;
    private JLabel statusLabel;

    private String text;
    private String pattern;

    private int textIndex = 0;
    private int patternIndex = 0;
    private int startPos = 0;

    private Object currentHighlight = null; // udržuje aktuální zvýraznění

    private Highlighter.HighlightPainter greenPainter = new DefaultHighlighter.DefaultHighlightPainter(new Color(144, 238, 144));
    private Highlighter.HighlightPainter redPainter = new DefaultHighlighter.DefaultHighlightPainter(Color.RED);

    public StepSearchGUI() {
        setTitle("Step Search Algorithm");
        setSize(950, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10,10));

        // Top panel: pattern + buttons
        patternField = new JTextField(25);
        patternField.setFont(new Font("Monospaced", Font.BOLD, 16));

        JLabel patternLabel = new JLabel("Pattern:");
        patternLabel.setFont(new Font("Arial", Font.BOLD, 16));

        nextStepButton = createButton("Next Step", new Color(70, 130, 180), Color.WHITE);
        skipButton = createButton("Skip to End", new Color(34, 139, 34), Color.WHITE);
        resetButton = createButton("Reset", new Color(220, 20, 60), Color.WHITE);
        setTextButton = createButton("Set Text", new Color(255, 140, 0), Color.WHITE);

        nextStepButton.addActionListener(e -> step());
        skipButton.addActionListener(e -> skipToEnd());
        resetButton.addActionListener(e -> resetAll());
        setTextButton.addActionListener(e -> setText());

        JPanel topPanel = new JPanel();
        topPanel.setBorder(new EmptyBorder(10,10,10,10));
        topPanel.add(patternLabel);
        topPanel.add(patternField);
        topPanel.add(nextStepButton);
        topPanel.add(skipButton);
        topPanel.add(resetButton);
        topPanel.add(setTextButton);

        add(topPanel, BorderLayout.NORTH);

        // Center panel: inputTextArea + textPane
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(new EmptyBorder(10,10,10,10));

        inputTextArea = new JTextArea(6, 70);
        inputTextArea.setLineWrap(true);
        inputTextArea.setWrapStyleWord(true);
        inputTextArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        inputTextArea.setBorder(BorderFactory.createTitledBorder("Input Text (editable until Set Text)"));
        JScrollPane inputScroll = new JScrollPane(inputTextArea);

        textPane = new JTextPane();
        textPane.setFont(new Font("Monospaced", Font.PLAIN, 16));
        textPane.setEditable(false);
        textPane.setBorder(BorderFactory.createTitledBorder("Highlighted Text"));
        JScrollPane textPaneScroll = new JScrollPane(textPane);
        textPaneScroll.setPreferredSize(new Dimension(900, 200));

        centerPanel.add(inputScroll);
        centerPanel.add(Box.createRigidArea(new Dimension(0,10)));
        centerPanel.add(textPaneScroll);

        add(centerPanel, BorderLayout.CENTER);

        statusLabel = new JLabel("Status: Ready", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 20));
        statusLabel.setOpaque(true);
        statusLabel.setBackground(Color.LIGHT_GRAY);
        statusLabel.setBorder(new EmptyBorder(5,5,5,5));
        add(statusLabel, BorderLayout.SOUTH);
    }

    private JButton createButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        return btn;
    }

    private void setText() {
        text = inputTextArea.getText();
        textPane.setText(text);
        textIndex = 0;
        patternIndex = 0;
        startPos = 0;
        textPane.getHighlighter().removeAllHighlights();
        currentHighlight = null;
        inputTextArea.setEditable(false);
        statusLabel.setText("Text nastaven, připraveno hledat");
    }

    private void step() {
        if (text == null || patternField.getText().isEmpty()) {
            statusLabel.setText("Zadej text a vzorek!");
            return;
        }

        pattern = patternField.getText();

        // Kontrola délky patternu
        if (pattern.length() > text.length()) {
            statusLabel.setText("Vzorek nesmí být delší než text!");
            return;
        }

        if (textIndex >= text.length()) {
            statusLabel.setText("Konec textu – hledání dokončeno");
            removeCurrentHighlight();
            return;
        }

        if (patternIndex < pattern.length() && textIndex < text.length()) {
            char t = text.charAt(textIndex);
            char p = pattern.charAt(patternIndex);

            removeCurrentHighlight(); // odstraní předchozí zvýraznění aktuálního znaku

            if (t == p) {
                highlightCurrentStep(textIndex, true); // zelené zvýraznění jen pro aktuální krok
                patternIndex++;
                textIndex++;
                statusLabel.setText("Shoda: '" + t + "'");
            } else {
                highlightCurrentStep(textIndex, false); // červené zvýraznění
                startPos++;
                textIndex = startPos;
                patternIndex = 0;
                statusLabel.setText("Neshoda – posun o jedno písmeno");
            }

            if (patternIndex == pattern.length()) {
                highlightFound(startPos, startPos + pattern.length()); // celý vzorek zeleně
                removeCurrentHighlight(); // odstraní aktuální zvýraznění
                statusLabel.setText("Vzorek nalezen – pokračuje hledání dalších výskytů");
                startPos++;
                textIndex = startPos;
                patternIndex = 0;
            }
        }
    }

    private void highlightCurrentStep(int index, boolean isMatch) {
        try {
            Highlighter highlighter = textPane.getHighlighter();
            Highlighter.HighlightPainter painter = isMatch ? greenPainter : redPainter;
            currentHighlight = highlighter.addHighlight(index, index + 1, painter);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private void removeCurrentHighlight() {
        if (currentHighlight != null) {
            textPane.getHighlighter().removeHighlight(currentHighlight);
            currentHighlight = null;
        }
    }

    private void highlightFound(int start, int end) {
        try {
            textPane.getHighlighter().addHighlight(start, end, greenPainter);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private void skipToEnd() {
        if (text == null || patternField.getText().isEmpty()) {
            statusLabel.setText("Zadej text a vzorek!");
            return;
        }

        pattern = patternField.getText();

        if (pattern.length() > text.length()) {
            statusLabel.setText("Vzorek nesmí být delší než text!");
            return;
        }

        while (textIndex < text.length()) {
            step();
        }
        removeCurrentHighlight();
        statusLabel.setText("Hledání dokončeno");
    }

    private void resetAll() {
        textIndex = 0;
        patternIndex = 0;
        startPos = 0;
        text = null;
        textPane.setText("");
        textPane.getHighlighter().removeAllHighlights();
        currentHighlight = null;
        patternField.setText("");
        inputTextArea.setText("");
        inputTextArea.setEditable(true);
        statusLabel.setText("Status: Ready");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            StepSearchGUI gui = new StepSearchGUI();
            gui.setVisible(true);
        });
    }
}
