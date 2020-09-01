import java.awt.*;
import java.io.*;
import java.util.Properties;

public class ClockSettings {

    // Static objects
    private static final String PROPERTIES_PATH = "data\\config.properties";
    // Objects
    private Color colorOne = Color.BLACK;
    private Color colorTwo = Color.BLUE;
    private Color colorThree = Color.RED;
    private File propertiesFile;
    private Properties properties;


    // Constructor and initialization
    ClockSettings() {
        initConfig();
    }
    private void initConfig() {
        this.propertiesFile = new File(PROPERTIES_PATH);
        if (!this.propertiesFile.exists()) {
            // Create properties with defaults
            createPropertiesFile();
        } else {
            // Properties exists
            readPropertiesFile();
        }
    }
    private void createPropertiesFile() {
        this.properties = new Properties();
        // Set properties to object
        this.properties.setProperty("colorOne", "BLACK");
        this.properties.setProperty("colorTwo", "BLUE");
        this.properties.setProperty("colorThree", "RED");
        // Write to properties file
        try {
            this.properties.store(new FileOutputStream(this.propertiesFile), null);
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    private void readPropertiesFile() {
        // TODO: Read properties from file
        this.properties = new Properties();
        try {
            FileReader reader = new FileReader(this.propertiesFile);
            this.properties.load(reader);
        } catch (IOException e ) {
            System.out.println("Error: " + e.getMessage());
        }
        if (this.properties == null) {
            createPropertiesFile();
            return;
        }
        // Read properties
        String colorOneString = this.properties.getProperty("colorOne");
        String colorTwoString = this.properties.getProperty("colorTwo");
        String colorThreeString = this.properties.getProperty("colorThree");
        // Turn property strings in to objects
        this.colorOne = readColorString(colorOneString);
        this.colorTwo = readColorString(colorTwoString);
        this.colorThree = readColorString(colorThreeString);
    }
    private Color readColorString(String colorString) {
        Color returnColor;
        switch (colorString) {
            case ("BLUE"):
                returnColor = Color.BLUE;
                break;
            case ("RED"):
                returnColor = Color.RED;
                break;
            default:
                returnColor = Color.BLACK;
                break;
        }
        return returnColor;
    }

    // Getters and setters
    public Color colorOne() {
        return this.colorOne;
    }
    public Color colorTwo() {
        return this.colorTwo;
    }
    public Color colorThree() {
        return this.colorThree;
    }

}
