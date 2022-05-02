package at.fhv.sysarch.lab2.homeautomation.environment;

public enum Weather {
    SUNNY("sunny"),
    RAINY("rainy"),
    FOGGY("foggy");

    private final String friendlyName;

    Weather(String name) {
        this.friendlyName = name;
    }

    public String getFriendlyName() {
        return friendlyName;
    }
}
