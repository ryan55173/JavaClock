import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class Clock implements Runnable {

    // Static objects
    private static final int WIDTH = 800, HEIGHT = 480; // Pi screen dimensions
    private static final Border BUTTON_BORDER = BorderFactory.createRaisedBevelBorder();
    private static final int BUTTON_HEIGHT = 60;
    private static final Font BUTTON_FONT = new Font("arial", Font.BOLD, 28);
    private static final Font DIGITAL_CLOCK_FONT = new Font("arial", Font.BOLD, 96);
    private static final Cursor BLANK_CURSOR = Toolkit.getDefaultToolkit().createCustomCursor(
            new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB),
            new Point(0, 0),
            "blankCursor"
    );
    private static final Font OUTPUT_TEXT_FONT = new Font("arial", Font.PLAIN, 18);
    private static final int OPTIONS_WIDTH = 400, OPTIONS_HEIGHT = 200;
    // Objects
    private Color colorOne;
    private Color colorTwo;
    private Color colorThree;
    private JFrame window;
    private Thread thread;
    private boolean isRunning = false;
    private JLabel clockText;
    private JDialog optionsDialog;
    private JDialog textOutDialog;
    private JDialog colorSelectionDialog;


    // Constructor and initialization
    Clock(boolean testRun) {
        initConfig();
        initWindow();
        createComponents();
        if (testRun) {
            displayTestWindow();
        } else {
            displayWindow();
        }
    }
    private void initConfig() {
        ClockSettings clockSettings = new ClockSettings();
        this.colorOne = clockSettings.colorOne();
        this.colorTwo = clockSettings.colorTwo();
        this.colorThree = clockSettings.colorThree();
    }
    // Window creation
    private void initWindow() {
        Dimension winDim = new Dimension(WIDTH, HEIGHT);
        this.window = new JFrame();
        this.window.setTitle("ClockApp");
        this.window.setMinimumSize(winDim);
        this.window.setPreferredSize(winDim);
        this.window.setMaximumSize(winDim);
        this.window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.window.setLocationRelativeTo(null);
        // Init icons
        ImageIcon catIcon = new ImageIcon("data\\angryCat.ico");
        this.window.setIconImage(catIcon.getImage());
    }
    private void createComponents() {
        // Button panel creation
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(colorOne);
        GridLayout buttonLayout = new GridLayout(1, 5);
        buttonPanel.setLayout(buttonLayout);
        buttonPanel.setBorder(null);
        // Create buttons
        JButton exitButton = panelButton("EXIT");
        exitButton.addActionListener(e -> exitButtonPressed());
        JButton optionsButton = panelButton("OPTIONS");
        optionsButton.addActionListener(e -> optionsButtonPressed());
        // Add to button panel
        buttonPanel.add(optionsButton);
        for (int i = 1; i < (buttonLayout.getColumns() - 1); i++) {
            JPanel emptyPanel = blankPanel();
            buttonPanel.add(emptyPanel);
        }
        buttonPanel.add(exitButton);

        // Main panel creation
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(colorOne);
        mainPanel.setBorder(null);
        this.clockText = new JLabel();
        this.clockText.setBorder(null);
        this.clockText.setBackground(colorOne);
        this.clockText.setForeground(colorThree);
        this.clockText.setFont(DIGITAL_CLOCK_FONT);
        this.clockText.setCursor(null);
        this.clockText.setHorizontalAlignment(0); // Centers the text
        // Add to main panel
        mainPanel.add(this.clockText, BorderLayout.CENTER);

        // Add top panels to window
        this.window.add(buttonPanel, BorderLayout.SOUTH);
        buttonPanel.setPreferredSize(new Dimension(WIDTH, BUTTON_HEIGHT));
        this.window.add(mainPanel);
    }
    private void displayWindow() {
        // Change to fullscreen and set visible
        this.window.setUndecorated(true);
        GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gDev = environment.getDefaultScreenDevice();
        gDev.setFullScreenWindow(this.window);
        this.window.setVisible(true);
        this.window.getContentPane().setCursor(BLANK_CURSOR);
        // Start main thread
        start();
    }
    private void displayTestWindow() {
        this.window.setVisible(true);
//        Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
//                new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB),
//                new Point(0, 0),
//                "blankCursor");
        this.window.getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        // Start main thread
        start();
    }


    // Methods, Start and stop methods are for implementing Runnable interface
    private synchronized void start() {
        if (this.isRunning) {
            return;
        }
        thread = new Thread(this);
        thread.start();
        this.isRunning = true;
    }
    private synchronized void stop() {
        if (!this.isRunning) {
            return;
        }
        try {
            this.thread.join();
        } catch (InterruptedException e) {
            System.out.println("Error: " + e.getMessage());
        }
        this.isRunning = false;
    }
    public void run() {
        while (this.isRunning) {
            updateClock();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        stop();
    }
    private void updateClock() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String formattedString = dtf.format(now);
        this.clockText.setText(formattedString);
    }
    private void exitButtonPressed() {
        System.exit(0); // Get the hell out of this program
        // this.window.dispose(); // Closes and disposes of window resources
        // this.window.setVisible(false); // Sets window invisible
    }
    private void optionsButtonPressed() {
        // Create and open options JDialog
        this.optionsDialog = new JDialog(this.window);
        this.optionsDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.optionsDialog.setLayout(new BorderLayout());
        this.optionsDialog.setMinimumSize(new Dimension(OPTIONS_WIDTH, OPTIONS_HEIGHT));

        // Components
        // Buttons
        JButton closeButton = panelButton("Close");
        closeButton.addActionListener(e -> optionsExit());
        JButton displayButton = panelButton("Display");
        displayButton.addActionListener(e -> displaySettings());
        JButton alarmsButton = panelButton("Alarms");
        alarmsButton.addActionListener(e -> alarmsOptions());
        JButton bluetoothButton = panelButton("Bluetooth");
        bluetoothButton.addActionListener(e -> bluetoothDevices());
        // Button panel
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setBackground(colorOne);
        buttonsPanel.setBorder(null);
        buttonsPanel.setLayout(new GridLayout(0, 1));
        buttonsPanel.add(displayButton);
        buttonsPanel.add(alarmsButton);
        buttonsPanel.add(bluetoothButton);

        // Add to dialog
        this.optionsDialog.add(closeButton, BorderLayout.SOUTH);
        this.optionsDialog.add(buttonsPanel);
        // Show dialog
        this.optionsDialog.setUndecorated(true);
        this.optionsDialog.setLocationRelativeTo(null);
        this.optionsDialog.setVisible(true);
    }
    private void optionsExit() {
        this.optionsDialog.dispose();
    }
    private void displaySettings() {
        // Mimic options dialog
        this.colorSelectionDialog = new JDialog();
        this.colorSelectionDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.colorSelectionDialog.setLayout(new BorderLayout());
        this.colorSelectionDialog.setMinimumSize(new Dimension(OPTIONS_WIDTH, OPTIONS_HEIGHT));

        // Components
        JButton closeButton = panelButton("Close");
        closeButton.addActionListener(e -> displaySettingsExit());
        JButton selectOne = panelButton("Color One");
        selectOne.addActionListener(e -> selectColorOne());
        JButton selectTwo = panelButton("Color Two");
        selectTwo.addActionListener(e -> selectColorTwo());
        JButton selectThree = panelButton("Color Three");
        selectThree.addActionListener(e -> selectColorThree());
        // Color panel
        JPanel colorsPanel = new JPanel();
        colorsPanel.setBackground(this.colorOne);
        colorsPanel.setBorder(null);
        colorsPanel.setLayout(new GridLayout(0, 1));
        colorsPanel.add(selectOne);
        colorsPanel.add(selectTwo);
        colorsPanel.add(selectThree);

        // Add to dialog
        this.colorSelectionDialog.add(closeButton, BorderLayout.SOUTH);
        this.colorSelectionDialog.add(colorsPanel);

        // Show dialog
        this.colorSelectionDialog.setUndecorated(true);
        this.colorSelectionDialog.setLocationRelativeTo(null);
        this.colorSelectionDialog.setVisible(true);
        // Dispose last dialog
        this.optionsDialog.dispose();
    }
    private void displaySettingsExit() {
        this.colorSelectionDialog.dispose();
    }
    private void selectColorOne() {
        System.out.println("Select One");
        // TODO: This
    }
    private void selectColorTwo() {
        System.out.println("Select Two");
        // TODO: This
    }
    private void selectColorThree() {
        System.out.println("Select Three");
        // TODO: This
    }

    private void alarmsOptions() {
        // TODO: This for alarms settings
        System.out.println("TODO: Program alarms options and storage...");
    }
    private void bluetoothDevices() {
        // TODO: This for bluetooth connections
        System.out.println("TODO: Program bluetooth audio stream for alarms or on action...");
    }
    private void textOut(String outputText) {
        // Sends a text output to swing application
        this.textOutDialog = new JDialog();
        this.textOutDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.textOutDialog.setLayout(new BorderLayout());
        this.textOutDialog.setMinimumSize(new Dimension(OPTIONS_WIDTH, OPTIONS_HEIGHT));

        // Components
        JButton closeButton = panelButton("Close");
        closeButton.addActionListener(e -> closeTextOut());
        JTextPane outputTextPane = new JTextPane();
        outputTextPane.setBorder(BorderFactory.createLineBorder(colorTwo, 3));
        outputTextPane.setBackground(colorOne);
        outputTextPane.setForeground(colorThree);
        outputTextPane.setEditable(false);
        outputTextPane.setCaret(null);
        outputTextPane.setFont(OUTPUT_TEXT_FONT);
        outputTextPane.setText(outputText);

        // Add components
        this.textOutDialog.add(outputTextPane);
        this.textOutDialog.add(closeButton, BorderLayout.SOUTH);
        this.textOutDialog.setLocationRelativeTo(null);
        this.textOutDialog.setUndecorated(true);
        this.textOutDialog.setVisible(true);
    }
    private void closeTextOut() {
        this.textOutDialog.dispose();
    }
    private JButton panelButton(String buttonText) {
        // Creates a panel button with text
        JButton button = new JButton();
        button.setText(buttonText);
        button.setBackground(colorTwo);
        button.setForeground(colorThree);
        button.setBorder(BUTTON_BORDER);
        button.setFont(BUTTON_FONT);
        button.setFocusPainted(false);
        return button;
    }
    private JButton panelButton(ImageIcon imageIcon) {
        // Creates a panel button with an icon
        JButton button = new JButton();
        // TODO: This needs fixed
        Image readImg = imageIcon.getImage();
        button.setIcon(new ImageIcon(readImg));
        button.setBackground(colorTwo);
        button.setForeground(colorThree);
        button.setBorder(BUTTON_BORDER);
        button.setFont(BUTTON_FONT);
        button.setFocusPainted(false);
        return button;
    }
    private JPanel blankPanel() {
        JPanel bPan = new JPanel();
        bPan.setBackground(colorOne);
        bPan.setBorder(null);
        return bPan;
    }

}
