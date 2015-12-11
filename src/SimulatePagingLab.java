/**
 * This lab is to simulate different paging replacement policies
 */
public class SimulatePagingLab
{
    // Global simulation flags
    private static boolean IS_RANDOM;   // Whether or not to show the random integer generation
    private static int MACHINE_SIZE;    // M: Machine size in words
    private static int PAGE_SIZE;       // P: Page size in words
    private static int PROCESS_SIZE;    // S: The size of a process (references are to virtual addresses 0..S-1)
    private static int JOB_MIX_NUMBER;  // J: The job mix, that determines A, B, & C below
    private static int NUMBER_OF_PREFERENCES_PER_PROCESS;   // N: the number of references for each process
    private static String REPLACEMENT_ALGORITHM;            // R: The replacement policy lifo, random, or lru
    private static boolean IS_VERBOSE;                      // Whether or not to show debugging steps
    private static boolean GLOBAL_QUANTUM;                  // Global initial quantum value for round robin (driver)

    /**
     * [DONE] Begins the paging simulation
     * @param args The commandline parameters passed in
     */
    public static void main(String[] args)
    {
        // Initial checks & settings
        validateInput(args);
        setFlags(args);

        // Actually begins the program
        printInitialState();
        beginSimulation();
        printOutput();
    } // End of the simulate paging main method

    /***** Start of application methods *****/

    /**
     * [DONE] [Application Method] Calls the correct simulation to run based on the input replacement policy
     */
    private static void beginSimulation()
    {
        switch (REPLACEMENT_ALGORITHM)
        {
            case "lru":
            {
                simulateLRU();
                break;
            }
            case "lifo":
            {
                simulateLIFO();
                break;
            }
            case "random":
            {
                simulateRandom();
                break;
            }
            default:
                System.err.println("Error: Invalid replacement algorithm was input, please try again");
                System.exit(1);
        }
    } // End of the begin simulation method

    /***** Start of simulation methods *****/

    /**
     * [Simulation method] Simulates using the page replacement policy: least recently used
     */
    private static void simulateLRU()
    {
        //TODO finish

    } // End of the simulate LRU method
    /**
     * [Simulation method] Simulates using the page replacement policy: least recently used
     */
    private static void simulateLIFO()
    {
        //TODO finish

    } // End of the simulate LIFO method
    /**
     * [Simulation method] Simulates using the page replacement policy: least recently used
     */
    private static void simulateRandom()
    {
        //TODO finish

    } // End of the simulate random method

    /***** Start of helper methods *****/

    /**
     * [DONE] [Helper Method] Validates all the input of the commandline
     * @param args The commandline arguments
     */
    private static void validateInput(String[] args)
    {
        try
        {
            //testInput();
            // Checks for input size
            if ((args.length != 7) && (args.length != 8))
                throw new InvalidInputException();

            // Checks for input types
            int offset = 0;
            if (args.length == 8)
                offset = 1;
            for (int i = offset; i < 5 + offset; ++i)
                Integer.parseInt(args[i]);
        } //
        catch (InvalidInputException | NumberFormatException e)
        {
            System.err.println("Error: Invalid input detected, please check your inputs");
            System.exit(1);
        }
    } // End of the validateInput method

    /**
     * [DONE] [Helper method] Initialises all global flags to the values passed in through the commandline
     * @param args The commandline arguments
     */
    private static void setFlags(String[] args)
    {
        int offset = 0;
        if (args[0].equals("--show-random"))
        {
            offset = 1;
            IS_RANDOM = true;
        }
        MACHINE_SIZE = Integer.parseInt(args[offset]);
        PAGE_SIZE = Integer.parseInt(args[1 + offset]);
        PROCESS_SIZE = Integer.parseInt(args[2 + offset]);
        JOB_MIX_NUMBER = Integer.parseInt(args[3 + offset]);
        NUMBER_OF_PREFERENCES_PER_PROCESS = Integer.parseInt(args[4 + offset]);
        REPLACEMENT_ALGORITHM = args[5 + offset];
        IS_VERBOSE = !args[6 + offset].equals("0");
        //testFlags();
    } // End of the setflags method

    /***** Start of print methods *****/

    /**
     * [DONE] [Print Method] Prints the initial state of all the global flags
     */
    private static void printInitialState()
    {testFlags();} // End of the print initial state method

    /**
     * [Print Method] Prints the summary output for the simulation
     */
    private static void printOutput()
    {
        //TODO finish this up
        // Prints which process had faults, and the average residency (for those processes)

        // prints the total number of faults and the global average residency

    } // End of the print output method

    /***** Start of test methods *****/
    /**
     * [DONE] [Test Method] Shows the values of the global flags set for this run
     */
    private static void testFlags()
    {
        System.out.printf("Show random is: %b.\n", IS_RANDOM);
        System.out.printf("The machine size is: %d.\n", MACHINE_SIZE);
        System.out.printf("The page size is: %d.\n", PAGE_SIZE);
        System.out.printf("The process size is: %d.\n", PROCESS_SIZE);
        System.out.printf("The job mix number is: %d.\n", JOB_MIX_NUMBER);
        System.out.printf("The number of preferences per process is: %d.\n", NUMBER_OF_PREFERENCES_PER_PROCESS);
        System.out.printf("The replacement algorithm is: %s.\n", REPLACEMENT_ALGORITHM);
        System.out.printf("Show verbose is: %b.\n",IS_VERBOSE);
    } // End of the test flags method
} // End of the simulate paging lab class
