import java.util.ArrayList;

public class FrameTable
{
    private ArrayList<FrameTableElement> frameTable;

    public FrameTable(ArrayList<FrameTableElement> frameTable) {
        this.frameTable = frameTable;
    }

    public ArrayList<FrameTableElement> getFrameTable() {
        return frameTable;
    }



    // Start of Frame table methods
    public boolean isHit(int processID, int pageNumber)
    {
        for (int i = 0; i < frameTable.size(); ++i)
        {
            if ((getFrameTable().get(i).getProcessNumber() == processID)
                    && (getFrameTable().get(i).getPageNumber() == pageNumber)
                    && (getFrameTable().get(i).isActive()))
                return true;
        }
        return false;
    } // End of the isHit method

    public int getHit(int processID, int pageNumber)
    {
        for (int i = 0; i < frameTable.size(); ++i)
        {
            if ((getFrameTable().get(i).getProcessNumber() == processID)
                    && (getFrameTable().get(i).getPageNumber() == pageNumber)
                    && (getFrameTable().get(i).isActive()))
                return i;
        }
        return -1;
    } // End of the getHit method

    public boolean isFull(int NUMBER_OF_PAGES)
    {
        int numberOfActivePages = 0;

        for (FrameTableElement currentElement : frameTable)
        {
            if (currentElement.isActive())
                ++numberOfActivePages;
        }
        return numberOfActivePages == NUMBER_OF_PAGES;
    } // End of the is full method
} // End of the frame table class
