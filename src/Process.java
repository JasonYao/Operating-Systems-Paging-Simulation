public class Process
{
    // Process object attributes
    private int processID;
    private double A;
    private double B;
    private double C;
    private int numberOfFaults;
    private int currentWord;
    private int currentReferenceNumber;
    private int numberOfEvictions;
    private boolean isFinished;
    private int totalResidencyTime;


    public Process(int processID, double a, double b, double c, int numberOfFaults, int currentWord,
                   int currentReferenceNumber, int numberOfEvictions, boolean isFinished, int totalResidencyTime) {
        this.processID = processID;
        A = a;
        B = b;
        C = c;
        this.numberOfFaults = numberOfFaults;
        this.currentWord = currentWord;
        this.currentReferenceNumber = currentReferenceNumber;
        this.numberOfEvictions = numberOfEvictions;
        this.isFinished = isFinished;
        this.totalResidencyTime = totalResidencyTime;
    } // End of the process object constructor

    public int getNumberOfEvictions() {
        return numberOfEvictions;
    }

    public void setNumberOfEvictions(int numberOfEvictions) {
        this.numberOfEvictions = numberOfEvictions;
    }

    public int getCurrentReferenceNumber() {

        return currentReferenceNumber;
    }

    public void setCurrentReferenceNumber(int currentReferenceNumber) {
        this.currentReferenceNumber = currentReferenceNumber;
    }

    /**
     * Sets the next referenced word, along with the finished flag if it is reached
     * @param randomNumber The random number given from the file
     * @param PROCESS_SIZE The global process size in words
     * @param NUMBER_OF_REFERENCES_PER_PROCESS The number of references per process
     */
    public void setNextReferencedWord(int randomNumber, int PROCESS_SIZE, int NUMBER_OF_REFERENCES_PER_PROCESS)
    {
        double A = getA();
        double B = getB();
        double C = getC();
        double quotient = randomNumber / (Integer.MAX_VALUE + 1d);

        if (quotient < A)
        {
            // Execute case 0: w+1 mod S with probability A
            setCurrentWord((getCurrentWord() + 1) % PROCESS_SIZE);
        } // End of case 0
        else if (quotient < (A + B))
        {
            // Execute case 1: w-5 mod S with probability B (needs to be w-5+S, since % != modulo, % == remainder)
            setCurrentWord((getCurrentWord() - 5 + PROCESS_SIZE) % PROCESS_SIZE);
        } // End of case 1
        else if (quotient < (A + B + C))
        {
            // Execute case 2: w+4 mod S with probability C
            setCurrentWord((getCurrentWord() + 4) % PROCESS_SIZE);
        } // End of case 2
        else if (quotient >= (A + B + C ))
        {
            // Execute case 3: a random value in [0,S) each with probability (1-A-B-C)/S
            setCurrentWord(randomNumber % PROCESS_SIZE);
        } // End of case 3
        else
        {
            // Error handling
            System.err.println("Error: could not set next referenced word");
            System.exit(1);
        }

        int newCurrentReferenceNumber = getCurrentReferenceNumber() + 1;
        if (newCurrentReferenceNumber > NUMBER_OF_REFERENCES_PER_PROCESS)
            setFinished(true);
        setCurrentReferenceNumber(newCurrentReferenceNumber);
    } // End of the get next referenced word method


    public int getCurrentPage(int pageSize)
    {return getCurrentWord() / pageSize;}

    public int getNumberOfFaults() {
        return numberOfFaults;
    }

    public void setNumberOfFaults(int numberOfFaults) {
        this.numberOfFaults = numberOfFaults;
    }

    public int getCurrentWord() {
        return currentWord;
    }

    public void setCurrentWord(int currentWord) {
        this.currentWord = currentWord;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }

    public int getTotalResidencyTime() {
        return totalResidencyTime;
    }

    public void setTotalResidencyTime(int totalResidencyTime) {
        this.totalResidencyTime = totalResidencyTime;
    }

    public int getProcessID() {
        return processID;
    }

    public double getA() {
        return A;
    }

    public double getB() {
        return B;
    }

    public double getC() {
        return C;
    }

} // End of the process class
