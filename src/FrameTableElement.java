public class FrameTableElement
{
    private int processNumber;
    private int pageNumber;
    private boolean isLoaded;
    private boolean isModified;
    private int timeAdded;
    private int frameTableIndex;
    private boolean isActive;

    public FrameTableElement(int processNumber, int pageNumber, boolean isLoaded, boolean isModified, int timeAdded,
                             int frameTableIndex, boolean isActive) {
        this.processNumber = processNumber;
        this.pageNumber = pageNumber;
        this.isLoaded = isLoaded;
        this.isModified = isModified;
        this.timeAdded = timeAdded;
        this.frameTableIndex = frameTableIndex;
        this.isActive = isActive;
    }

    public boolean isActive() { return isActive; }

    public boolean isModified() { return isModified; }

    public int getFrameTableIndex() {
        return frameTableIndex;
    }

    public int getProcessNumber() {
        return processNumber;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    public void setLoaded(boolean loaded) {
        isLoaded = loaded;
    }

    public int getTimeAdded() {
        return timeAdded;
    }
} // End of the frame table element inner class