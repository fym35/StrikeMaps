package eu.konggdev.strikemaps.map.style.management;

public class StyleManagementMetadata {
    public boolean modified;
    public boolean doUpdates;
    public boolean autoUpdate;

    public String source;
    public String sourceHash;

    public StyleManagementMetadata() { }

    public StyleManagementMetadata(boolean modified, boolean doUpdates, boolean autoUpdate, String source, String sourceHash) {
        this.modified = modified;
        this.doUpdates = doUpdates;
        this.autoUpdate = autoUpdate;
        this.source = source;
        this.sourceHash = sourceHash;
    }
}
