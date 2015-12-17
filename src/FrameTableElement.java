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

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isModified() {
        return isModified;
    }

    public void setModified(boolean modified) {
        isModified = modified;
    }

    public int getFrameTableIndex() {
        return frameTableIndex;
    }

    public void setFrameTableIndex(int frameTableIndex) {
        this.frameTableIndex = frameTableIndex;
    }

    public int getProcessNumber() {
        return processNumber;
    }

    public void setProcessNumber(int processNumber) {
        this.processNumber = processNumber;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
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

    public void setTimeAdded(int timeAdded) {
        this.timeAdded = timeAdded;
    }
} // End of the frame table element inner class