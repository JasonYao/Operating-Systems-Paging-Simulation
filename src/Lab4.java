import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Lab4
{
    // Global simulation flags
    private static boolean IS_RANDOM;   // Whether or not to show the random integer generation
    private static int MACHINE_SIZE;    // M: Machine size in words
    private static int PAGE_SIZE;       // P: Page size in words
    private static int PROCESS_SIZE;    // S: The size of a process (references are to virtual addresses 0..S-1)
    private static int JOB_MIX_NUMBER;  // J: The job mix, that determines A, B, & C below
    private static int NUMBER_OF_REFERENCES_PER_PROCESS;   // N: the number of references for each process
    private static String REPLACEMENT_ALGORITHM;            // R: The replacement policy lifo, random, or lru
    private static boolean IS_VERBOSE;                      // Whether or not to show debugging steps
    private static int GLOBAL_QUANTUM = 3;                  // Global initial quantum value for round robin (driver)
    private static int TOTAL_NUMBER_OF_PAGES;
    private static int GLOBAL_EVICTIONS = 0;


    private static String RANDOM_NUMBER_FILE_PATH = "random-numbers";
    // Global simulation
    private static FrameTable FRAME_TABLE;
    private static ArrayList<Process> PROCESS_CONTAINER;
    private static int CURRENT_TIME = 0;

    /* Eviction Policy: least recently used globals */

    /* Eviction Policy: random globals */
    private static int randomReplacementNumber = 0;
    private static boolean wasEvictedRandom = false;

    /* Eviction Policy: last-in, first-out global */

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

    private static void beginSimulation()
    {
        PROCESS_CONTAINER = getJobMix();
        File randomNumberFile;
        Scanner lineScanner = null;

        try
        {
            randomNumberFile = new File(RANDOM_NUMBER_FILE_PATH);
            lineScanner = new Scanner(randomNumberFile);

            while (!areAllProcessesDone())
            {
                for (int i = 0 ; i < PROCESS_CONTAINER.size(); ++i)
                {
                    for (int currentReference = 0; currentReference < GLOBAL_QUANTUM; ++currentReference)
                    {
                        // Deals with completed processes
                        if (PROCESS_CONTAINER.get(i).isFinished())
                            break;

                        // Prints out the commonality
                        if (IS_VERBOSE)
                        {
                            System.out.printf("%d references word %d (page %d) at time %d: ", i + 1, PROCESS_CONTAINER.get(i)
                                    .getCurrentWord(), PROCESS_CONTAINER.get(i).getCurrentPage(PAGE_SIZE), CURRENT_TIME);
                        }
                        // Deals with a page hit
                        if (FRAME_TABLE.isHit(i, PROCESS_CONTAINER.get(i).getCurrentPage(PAGE_SIZE)))
                        {
                            int hitIndex = FRAME_TABLE.getHit(i, PROCESS_CONTAINER.get(i).getCurrentPage(PAGE_SIZE));
                            FRAME_TABLE.getFrameTable().get(hitIndex).setLoaded(true);
                            // Page hit
                            if (IS_VERBOSE)
                            {
                                System.out.printf("Hit in frame %d\n", FRAME_TABLE.getHit(
                                        i, PROCESS_CONTAINER.get(i).getCurrentPage(PAGE_SIZE)));
                            }
                        } // End of dealing with page hit
                        else
                        {
                           // Page miss
                            if (IS_VERBOSE)
                                System.out.print("Fault, ");

                            PROCESS_CONTAINER.get(i).setNumberOfFaults(PROCESS_CONTAINER.get(i).getNumberOfFaults() + 1);
                            if (FRAME_TABLE.isFull(TOTAL_NUMBER_OF_PAGES))
                            {
                                System.out.printf("Page table was full, "); //TODO remove after
                                // Page table was full

                                // Evicts a frame table element based on R: replacement policy
                                FrameTableElement evictedFrame = evict(lineScanner);

                                if (evictedFrame == null)
                                {
                                    System.err.println("Error: no frame was evicted");
                                    System.exit(1);
                                }
                                int evictedPage = evictedFrame.getPageNumber();
                                int evictedFrameIndex = evictedFrame.getFrameTableIndex();

                                // Updates the eviction number
                                PROCESS_CONTAINER.get(evictedFrame.getProcessNumber()).setNumberOfEvictions(
                                        PROCESS_CONTAINER.get(evictedFrame.getProcessNumber()).getNumberOfEvictions() + 1);

                                // Adds the residency time
                                PROCESS_CONTAINER.get(evictedFrame.getProcessNumber())
                                        .setTotalResidencyTime(PROCESS_CONTAINER.get(evictedFrame.getProcessNumber())
                                                .getTotalResidencyTime() + CURRENT_TIME - evictedFrame.getTimeAdded());

                                // Replaces the old frame with the new one
                                FrameTableElement frameTableElementToBeAdded = new FrameTableElement(
                                        i, PROCESS_CONTAINER.get(i).getCurrentPage(PAGE_SIZE), false, false,
                                        CURRENT_TIME, evictedFrameIndex, true);

                                FRAME_TABLE.getFrameTable().set(evictedFrameIndex, frameTableElementToBeAdded);

                                if (IS_VERBOSE)
                                    System.out.printf("evicting page %d of %d from frame %d\n",
                                            evictedPage, evictedFrame.getProcessNumber() + 1, evictedFrameIndex);
                            } // End of dealing with page miss: page table was full, page was evicted
                            else
                            {
                                System.out.printf("Page table was not full, "); //TODO remove after
                                // when page is brought in, OS resets R = M = 0 (R == referenced, M == modified)
                                if (FRAME_TABLE.isFull(TOTAL_NUMBER_OF_PAGES))
                                {
                                    System.err.println("Error: Table is full but should not be");
                                    System.exit(1);
                                }
                                else
                                {
                                    // Page table has free frames
                                    FrameTableElement highestFreeFrame = null;
                                    int indexOdHighestFreeFrame = FRAME_TABLE.getFrameTable().size() - 1;
                                    for ( ; indexOdHighestFreeFrame >= 0; --indexOdHighestFreeFrame)
                                    {
                                        if (!FRAME_TABLE.getFrameTable().get(indexOdHighestFreeFrame).isActive())
                                        {
                                            highestFreeFrame = FRAME_TABLE.getFrameTable().get(indexOdHighestFreeFrame);
                                            break;
                                        }
                                    }

                                    if (highestFreeFrame == null)
                                    {
                                        System.err.println("Error: highest free frame could not be found");
                                        System.exit(1);
                                    }
                                    // Replaces the free frame with the new one
                                    FRAME_TABLE.getFrameTable().set(
                                            indexOdHighestFreeFrame, new FrameTableElement(
                                                    i, PROCESS_CONTAINER.get(i).getCurrentPage(PAGE_SIZE), false, false,
                                                    CURRENT_TIME, indexOdHighestFreeFrame, true));

                                    if (IS_VERBOSE)
                                        System.out.printf("using free frame %d\n", indexOdHighestFreeFrame);
                                }
                            } // End of dealing with page miss: page table was not full, free frame was used
                        } // End of dealing with page miss
                        int randomNumber = getRandomNumber(lineScanner);

                        PROCESS_CONTAINER.get(i).setNextReferencedWord(randomNumber,
                                PROCESS_SIZE, NUMBER_OF_REFERENCES_PER_PROCESS);

                        // Outputs the random number if flag is given
                        if (IS_RANDOM)
                            System.out.printf("%d uses random number: %d\n", i + 1, randomNumber);

                        if ((IS_RANDOM) && (REPLACEMENT_ALGORITHM.equals("random")) && (wasEvictedRandom))
                        {
                            wasEvictedRandom = false;
                            System.out.printf("%d uses random number: %d\n", i + 1, randomReplacementNumber);
                        }
                        ++CURRENT_TIME;
                    } // End of the current round of references for that process
                } // End of iterating through all processes
            } // End of looping through all processes and references
            System.out.println();
        }
        catch (FileNotFoundException e)
        {
            System.err.println("Error: Unable to locate the random number file, please make sure it is in the current" +
                    "directory, and is named 'random-numbers'");
            System.exit(1);
        }
        catch (StringIndexOutOfBoundsException e)
        {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        finally
        {
            if (lineScanner != null)
                lineScanner.close();
        }
    } // End of the begin simulation method

    private static FrameTableElement evict(Scanner lineScanner)
    {
        FrameTableElement evictedFrame = null;
        switch (REPLACEMENT_ALGORITHM)
        {
            case "lru":
            {
                evictedFrame = evictLRU();
                ++GLOBAL_EVICTIONS;
                break;
            }
            case "random":
            {
                evictedFrame = evictRandom(lineScanner);
                ++GLOBAL_EVICTIONS;
                break;
            }
            case "lifo":
            {
                evictedFrame = evictLIFO();
                ++GLOBAL_EVICTIONS;
                break;
            }
            default:
            {
                System.err.println("Error: invalid replacement algorithm was input, please check again");
                System.exit(1);
            }
        }
        return evictedFrame;
    } // End of the evict method

    /***** Eviction Policies *****/
    private static FrameTableElement evictLRU()
    {
        if (!FRAME_TABLE.isFull(TOTAL_NUMBER_OF_PAGES))
        {
            System.err.println("Error: attempted to evict a table that was not filled");
            System.exit(1);
        }

        // Divides all frames into four classes (pools A, B, C, & D)
        ArrayList<FrameTableElement> poolA = new ArrayList<>();     // Not referenced, not modified.
        ArrayList<FrameTableElement> poolB = new ArrayList<>();     // Not referenced, modified.
        ArrayList<FrameTableElement> poolC = new ArrayList<>();     // Referenced, not modified.
        ArrayList<FrameTableElement> poolD = new ArrayList<>();     // Referenced, modified.

        for (FrameTableElement currentElement : FRAME_TABLE.getFrameTable())
        {
            // Checks if it was recently referenced
            if (currentElement.isLoaded())
            {
                // Frame table element was recently referenced
                if (currentElement.isModified())
                {
                    // [Pool D] Frame table element was recently modified
                    poolD.add(currentElement);
                }
                else
                {
                    // [Pool C] Frame table element was not recently modified
                    poolC.add(currentElement);
                }
            } // End of dealing with recently referenced elements
            else
            {
                // Frame table element was not recently referenced
                if (currentElement.isModified())
                {
                    // [Pool B] Frame table element was recently modified
                    poolB.add(currentElement);
                }
                else
                {
                    // [Pool A] Frame table element was not recently modified
                    poolA.add(currentElement);
                }
            } // End of dealing with not recently referenced elements
        } // End of filling up each individual pools

        testAllPoolsLRU(poolA, poolB, poolC, poolD); //TODO remove after

        // Marks a victim frame from the lowest non-empty class
        // Deals with pool A
        int lowestIndex = 100; // arbitrarily set
        int lowestStartTime = 100; // arbitrarily set
        FrameTableElement returnValue = null;
        for (FrameTableElement currentElement : poolA)
        {
            if (currentElement.getTimeAdded() < lowestStartTime)
            {
                lowestStartTime = currentElement.getTimeAdded();
                lowestIndex = currentElement.getFrameTableIndex();
                returnValue = currentElement;
            }
        }

        if (returnValue != null)
            return returnValue;

        // Deals with pool B
        lowestIndex = 100; // arbitrarily set
        lowestStartTime = 100; // arbitrarily set
        for (FrameTableElement currentElement : poolB)
        {
            if (currentElement.getTimeAdded() < lowestStartTime)
            {
                lowestStartTime = currentElement.getTimeAdded();
                lowestIndex = currentElement.getFrameTableIndex();
                returnValue = currentElement;
            }
        }

        if (returnValue != null)
            return returnValue;

        // Deals with pool C
        lowestIndex = 100; // arbitrarily set
        lowestStartTime = 100; // arbitrarily set
        for (FrameTableElement currentElement : poolC)
        {
            if (currentElement.getTimeAdded() < lowestStartTime)
            {
                lowestStartTime = currentElement.getTimeAdded();
                lowestIndex = currentElement.getFrameTableIndex();
                returnValue = currentElement;
            }
        }

        if (returnValue != null)
            return returnValue;

        // Deals with pool D
        lowestIndex = 100; // arbitrarily set
        lowestStartTime = 100; // arbitrarily set
        for (FrameTableElement currentElement : poolD)
        {
            if (currentElement.getTimeAdded() < lowestStartTime)
            {
                lowestStartTime = currentElement.getTimeAdded();
                lowestIndex = currentElement.getFrameTableIndex();
                returnValue = currentElement;
            }
        }

        if (returnValue != null)
            return returnValue;

        System.err.println("Error: could not find a page to evict in LRU");
        System.exit(1);
        return null;
    } // End of the evictLRU method

    private static void testAllPoolsLRU(ArrayList<FrameTableElement> poolA, ArrayList<FrameTableElement> poolB,
                                        ArrayList<FrameTableElement> poolC, ArrayList<FrameTableElement> poolD)
    {
        System.out.println("Pool A has size: " + poolA.size());
        System.out.println("Pool B has size: " + poolB.size());
        System.out.println("Pool C has size: " + poolC.size());
        System.out.println("Pool D has size: " + poolD.size());
    } // End of the test all pools LRU method

    private static FrameTableElement evictRandom(Scanner lineScanner)
    {
        int randomNumber = getRandomNumber(lineScanner);
        randomReplacementNumber = randomNumber;
        wasEvictedRandom = true;
        return FRAME_TABLE.getFrameTable().get(randomNumber % TOTAL_NUMBER_OF_PAGES);
    } // End of the evictRandom method

    private static FrameTableElement evictLIFO()
    {
        int latestIndex = 0;
        int latestTime = 0;

        for (int i = 0; i <FRAME_TABLE.getFrameTable().size(); ++i)
        {
            if ((FRAME_TABLE.getFrameTable().get(i).getTimeAdded() > latestTime) && (FRAME_TABLE.getFrameTable().get(i).isActive()))
            {
                latestTime = FRAME_TABLE.getFrameTable().get(i).getTimeAdded();
                latestIndex = i;
            }
        }
        return FRAME_TABLE.getFrameTable().get(latestIndex);
    } // End of the evictLIFO method

    /***** Helper Methods *****/

    private static boolean areAllProcessesDone()
    {
        for (Process currentProcess : PROCESS_CONTAINER)
        {
            if (!currentProcess.isFinished())
                return false;
        }
        return true;
    } // End of the are all processes done method


    /**
     * [DONE] [Helper Method] Returns the next random integer from the random number file
     * @param lineScanner The scanner at the current start of line
     * @return The next random integer in the random number file
     * @throws StringIndexOutOfBoundsException Throws an exception when the end of line has been reached
     */
    private static int getRandomNumber(Scanner lineScanner) throws StringIndexOutOfBoundsException
    {
        if (lineScanner.hasNextLine())
            return Integer.parseInt(lineScanner.nextLine());
        else
            throw new StringIndexOutOfBoundsException("Error: Reached the end of the random-number file");
    } // End of the get random number method

    /**
     * [DONE] [Helper Method] Initialises all global flags to the values passed in through the commandline
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
        NUMBER_OF_REFERENCES_PER_PROCESS = Integer.parseInt(args[4 + offset]);
        REPLACEMENT_ALGORITHM = args[5 + offset];
        IS_VERBOSE = !args[6 + offset].equals("0");
        TOTAL_NUMBER_OF_PAGES = (int) Math.ceil((double)MACHINE_SIZE/PAGE_SIZE);
        FRAME_TABLE = new FrameTable(new ArrayList<>());

        // Initialises the frame table with all frames
        for (int i = 0; i < TOTAL_NUMBER_OF_PAGES; ++i)
            FRAME_TABLE.getFrameTable().add(new FrameTableElement(-1, -1, false, false, -1, -1, false));
    } // End of the setflags method

    /**
     * [DONE] [Helper Method] Validates all the input of the commandline
     * @param args The commandline arguments
     */
    private static void validateInput(String[] args)
    {
        try
        {
            // Checks for input size
            if ((args.length != 7) && (args.length != 8))
                throw new InvalidInputException();

            // Checks for input types
            int offset = 0;
            if (args.length == 8)
                offset = 1;
            for (int i = offset; i < 5 + offset; ++i)
                Integer.parseInt(args[i]);
        }
        catch (InvalidInputException | NumberFormatException e)
        {
            System.err.println("Error: Invalid input detected, please check your inputs");
            System.exit(1);
        }
    } // End of the validateInput method

    /**
     * [DONE] [Application Method] Gets the set of process in the jobmix for a given J-value
     * @return An arraylist of processes based on the job mix number
     */
    private static ArrayList<Process> getJobMix()
    {
        ArrayList<Process> jobMix = new ArrayList<>();
        switch (JOB_MIX_NUMBER)
        {
            case 1:
            {
                // Case 1: 1 process with A=1 and B=C=0, the simplest (fully sequential) case
                jobMix.add(new Process(1, 1, 0, 0, 0, 111 % PROCESS_SIZE, 1, 0, false, 0));
                break;
            }
            case 2:
            {
                // Case 2: Four processes, each with A=1 and B=C=0
                for (int i = 1; i <= 4; ++i)
                    jobMix.add(new Process(i, 1, 0, 0, 0, 111 % PROCESS_SIZE, 1, 0, false, 0));
                break;
            }
            case 3:
            {
                // Case 3: Four processes, each with A=B=C=0 (fully random references)
                for (int i = 1; i <= 4; ++i)
                    jobMix.add(new Process(i, 0, 0, 0, 0, 111 % PROCESS_SIZE, 1, 0, false, 0));
                break;
            }
            case 4:
            {
                // Case 4: One process with A=.75, B=.25 and C=0
                // one process with A=.75, B=0, and C=.25
                // one process with A=.75, B=.125 and C=.125
                // one process with A=.5, B=.125 and C=.125
                jobMix.add(new Process(1, 0.750, 0.250, 0, 0, 111 % PROCESS_SIZE, 1, 0, false, 0));
                jobMix.add(new Process(2, 0.750, 0, 0.250, 0, 111 % PROCESS_SIZE, 1, 0, false, 0));
                jobMix.add(new Process(3, 0.750, 0.125, 0.125, 0, 111 % PROCESS_SIZE, 1, 0, false, 0));
                jobMix.add(new Process(4, 0.500, 0.125, 0.125, 0, 111 % PROCESS_SIZE, 1, 0, false, 0));
                break;
            }
            default:
            {
                System.err.println("Error: Invalid job mix was given");
                System.exit(1);
            }
        }
        return jobMix;
    } // End of the get job mix method

    /**
     * [Print Method] Prints the summary output for the simulation TODO check if this works correctly
     */
    private static void printOutput()
    {
        int totalNumberOfFaults = 0;
        int totalResidencySum = 0;

        // Outputs each process's summary data
        for (Process currentProcess : PROCESS_CONTAINER)
        {
            totalNumberOfFaults += currentProcess.getNumberOfFaults();
            System.out.printf("Process %d had %d faults", currentProcess.getProcessID(), currentProcess.getNumberOfFaults());
            // Checks
            if (GLOBAL_EVICTIONS > 0)
            {
                // There was a nonzero number of evictions
//                System.out.printf("total residency time is %d, number of faults = %d\n",//TODO remove after
//                        currentProcess.getTotalResidencyTime(), currentProcess.getNumberOfFaults());

                System.out.printf(" and %.1f average residency\n", (double) currentProcess.getTotalResidencyTime()/
                        currentProcess.getNumberOfEvictions());
                totalResidencySum += currentProcess.getTotalResidencyTime();
            }
            else
            {
                // There were no evictions
                System.out.println(".\n\tWith no evictions, the average residence is undefined.");
            }
        }

        // Outputs the global summary data
        System.out.println();
        System.out.print("The total number of faults is " + totalNumberOfFaults);

        if (GLOBAL_EVICTIONS != 0)
            System.out.printf(" and the overall average residency is %.1f.\n\n", (double) totalResidencySum/GLOBAL_EVICTIONS);
        else
            System.out.println(".\n\tWith no evictions, the overall average residence is undefined.\n");
    } // End of the print output method

    /**
     * [DONE] [Print Method] Prints the initial state of all the global flags
     */
    private static void printInitialState()
    {testFlags();} // End of the print initial state method

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
        System.out.printf("The number of preferences per process is: %d.\n", NUMBER_OF_REFERENCES_PER_PROCESS);
        System.out.printf("The replacement algorithm is: %s.\n", REPLACEMENT_ALGORITHM);
        System.out.printf("Show verbose is: %b.\n",IS_VERBOSE);
        System.out.println();
    } // End of the test flags method
} // End of the lab 4
