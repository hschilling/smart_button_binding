package frc.robot;

import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import java.util.Map;
import java.util.function.Supplier;
import java.util.LinkedHashMap;

/**
 * Represents information about a button including its position and functionality.
 */
class ButtonInfo {
    private String usage;
    private final Supplier<Trigger> mapping;
    private final int row;
    private final int column;

    /**
     * Creates a new ButtonInfo instance.
     * @param usage Brief description of the button's function
     * @param mapping The trigger supplier for this button
     * @param row Row position of the button
     * @param column Column position of the button
     * @throws IllegalArgumentException if any parameters are invalid
     */
    public ButtonInfo(String usage, Supplier<Trigger> mapping, int row, int column) {
        if (row < 0 || column < 0) {
            throw new IllegalArgumentException("Row and column must be non-negative");
        }
        this.usage = usage;
        this.mapping = mapping;
        this.row = row;
        this.column = column;
    }

    public String getUsage() {
        return usage;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }

    public Supplier<Trigger> getMapping() {
        return mapping;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    @Override
    public String toString() {
        return String.format("ButtonInfo[usage=%s, position=(%d,%d)]", usage, row, column);
    }
}
public class ButtonBinder {
    private CommandXboxController controller;
    private Map<String, ButtonInfo> buttonInfo;

    private final int DASHBOARD_WIDGET_WIDTH_IF_USED = 3;
    private final int DASHBOARD_WIDGET_WIDTH_IF_NOT_USED = 2;
    
    public ButtonBinder(CommandXboxController controller) {
        this.controller = controller;
        this.buttonInfo = new LinkedHashMap<>();
        initializeButtonMappings();
    }

    /**
     * Initialize all available button mappings.
     * Add new buttons here to extend functionality.
     */
    private void initializeButtonMappings() {
        // Face buttons
        buttonInfo.put("a", new ButtonInfo("", controller::a, 0,0));
        buttonInfo.put("b", new ButtonInfo("", controller::b, 1,0));
        buttonInfo.put("x", new ButtonInfo("", controller::x, 2,0));
        buttonInfo.put("y", new ButtonInfo("", controller::y, 3,0));
        
        buttonInfo.put("leftBumper", new ButtonInfo("", controller::leftBumper, 0,4));
        buttonInfo.put("rightBumper", new ButtonInfo("", controller::rightBumper, 1,4));
        buttonInfo.put("leftTrigger", new ButtonInfo("", controller::leftTrigger, 2,4));
        buttonInfo.put("rightTrigger", new ButtonInfo("", controller::rightTrigger, 3,4));
        buttonInfo.put("leftStick", new ButtonInfo("", controller::leftStick, 4,4));
        buttonInfo.put("rightStick", new ButtonInfo("", controller::rightStick, 5,4));

        buttonInfo.put("start", new ButtonInfo("", controller::start, 0,8));
        buttonInfo.put("back", new ButtonInfo("", controller::back, 1,8));
    }

    /**
     * Get a button trigger with specified usage.
     * @param buttonName The name of the button to bind
     * @param usage The intended usage for this button
     * @return Trigger for the requested button
     * @throws IllegalStateException if button is already bound
     * @throws IllegalArgumentException if button name is invalid
     */
    public Trigger getButton(String buttonName, String usage) {
        // Validate button exists
        if (!buttonInfo.containsKey(buttonName)) {
            throw new IllegalArgumentException("Invalid button name: " + buttonName + 
                "\nValid buttons are: " + String.join(", ", buttonInfo.keySet()));
        }

        // Check if button is already used
        if (buttonInfo.get(buttonName).getUsage().length() > 0 ) {
            throw new IllegalStateException("Button '" + buttonName + 
                "' has already been bound. Existing usage: " + buttonInfo.get(buttonName).getUsage());
        }

        // Store the usage and return the trigger
        buttonInfo.get(buttonName).setUsage(usage);;
        return buttonInfo.get(buttonName).getMapping().get();
    }

    /**
     * Generates a detailed report of button usage.
     * @return A string with a line-by-line report of each button's status
     */
    public String getButtonUsageReport() {
        StringBuilder report = new StringBuilder();

        for (Map.Entry<String, ButtonInfo> entry : buttonInfo.entrySet()) {
            report.append("Button '").append(entry.getKey()).append("': ");
            // System.out.println(entry.getKey() + ": " + entry.getValue());
            if (entry.getValue().getUsage().length() > 0) {
                report.append(entry.getValue().getUsage());
            } else {
                report.append("Unused");
            }
            
            report.append("\n");
        }
        return report.toString();
    }

    /**
     * Create dashboard widgets showing what buttons are assingned to what functions.
     * @param buttonsTabName The name of the dashboard tab where these widgets will be placed
     * @return void
     */
    public void makeStatusDashboardWidgets(String buttonsTabName) {
        ShuffleboardTab buttonsTab = Shuffleboard.getTab(buttonsTabName);
        int width ;

        for (Map.Entry<String, ButtonInfo> entry : buttonInfo.entrySet()) {
            if (isButtonUsed(entry.getKey())) {
                width = DASHBOARD_WIDGET_WIDTH_IF_USED;
            } else {
                width = DASHBOARD_WIDGET_WIDTH_IF_NOT_USED;
            }
                buttonsTab.add(entry.getKey(), getButtonStatus(entry.getKey()))
                .withSize(width, 1)
                .withPosition(entry.getValue().getColumn(), entry.getValue().getRow());
        }
    }

    /**
     * Indicates if a button has been bound.
     * @param buttonName The name of the button
     * @return None
     * @throws IllegalArgumentException if button name is invalid
     */
    private Boolean isButtonUsed(String buttonName) {
        if (!buttonInfo.containsKey(buttonName)) {
            throw new IllegalArgumentException("Invalid button name: " + buttonName);
        }

        if (buttonInfo.get(buttonName).getUsage().length() > 0 ) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns the status of a specific button.
     * @param buttonName The name of the button to check
     * @return A string describing the button's status (usage or unused)
     * @throws IllegalArgumentException if the button name is not valid
     */
    public String getButtonStatus(String buttonName) {
        // Validate the button name
        if (!buttonInfo.containsKey(buttonName)) {
            throw new IllegalArgumentException("Invalid button name: " + buttonName);
        }

        // Check if the button has been used
        if (buttonInfo.get(buttonName).getUsage().length() > 0 ) {
            return buttonInfo.get(buttonName).getUsage();
        }

        return "Unused";
    }

}