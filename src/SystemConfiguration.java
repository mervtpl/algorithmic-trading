public class SystemConfiguration {

    // Singleton instance
    private static SystemConfiguration instance;

    // Shared system settings
    private double riskLimit;
    private double thresholdValue;
    private String dataFilePath;
    private String outputDirectory;
    private String activeStrategy;

    // Private constructor
    private SystemConfiguration() {

        // Default values
        this.riskLimit = 5.0;
        this.thresholdValue = 100.0;

        this.dataFilePath = "src/market_data.csv";
        this.outputDirectory = "src/output/";
        this.activeStrategy = "SHORT_TERM";
    }

    // Global access point
    public static SystemConfiguration getInstance() {

        if (instance == null) {
            instance = new SystemConfiguration();
        }
        return instance;
    }

    // Getters & Setters

    public double getRiskLimit() {
        return riskLimit;
    }

    public void setRiskLimit(double riskLimit) {
        this.riskLimit = riskLimit;
    }

    public double getThresholdValue() {
        return thresholdValue;
    }

    public void setThresholdValue(double thresholdValue) {
        this.thresholdValue = thresholdValue;
    }

    public String getDataFilePath() {
        return dataFilePath;
    }

    public void setDataFilePath(String dataFilePath) {
        this.dataFilePath = dataFilePath;
    }

    public String getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory(String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public String getActiveStrategy() {
        return activeStrategy;
    }

    public void setActiveStrategy(String activeStrategy) {
        this.activeStrategy = activeStrategy;
    }
}
