package com.LandSurveyor;

import net.runelite.client.RuneLite;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class csvExport {
    private final static String PATH = RuneLite.RUNELITE_DIR + "//landSurveyor//";
    private static final DateFormat TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");

    private static File csvFile;
    private static BufferedWriter writer;
    private static boolean isWriting = false;

    /// Cache to prevent duplicates: key = "x,y", value = elevation
    private static final Map<String, Integer> tileCache = new HashMap<>();

    private static final Logger logger = Logger.getLogger(csvExport.class.getName());

    public static void updateWritingState(boolean writeToFile)
    {
        if (writeToFile && !isWriting)
        {
            startNewCsv();
        }
        else if (!writeToFile && isWriting)
        {
            stopCsv();
        }
    }


     /// Creates a new CSV file
    private static void startNewCsv()
    {
        try
        {
            File folder = new File(PATH);
            if (!folder.exists())
            {
                folder.mkdirs();
            }

            String timestamp = TIME_FORMAT.format(new Date());
            csvFile = new File(folder, timestamp + ".csv");

            writer = new BufferedWriter(new FileWriter(csvFile, true));
            writer.write("X,Y,Elevation");
            writer.newLine();

            tileCache.clear();
            isWriting = true;
        }
        catch (IOException e)
        {
            logger.log(Level.SEVERE, "Failed to start new CSV file", e);
            stopCsv();
        }
    }

     /// Stops CSV logging and closes the writer
    private static void stopCsv()
    {
        try
        {
            if (writer != null)
            {
                writer.flush();
                writer.close();
            }
        }
        catch (IOException e)
        {
            logger.log(Level.SEVERE, "Error while closing CSV writer", e);
        }
        finally
        {
            writer = null;
            csvFile = null;
            isWriting = false;
            tileCache.clear();
        }
    }

    /// Writes a tile's data if not already written, or updates it if elevation changed
    public static void writeTile(int x, int y, int elevation)
    {
        if (!isWriting || writer == null)
        {
            return;
        }

        String key = x + "," + y;

        // If tile is new, write it
        if (!tileCache.containsKey(key))
        {
            appendTile(x, y, elevation);
            tileCache.put(key, elevation);
            return;
        }

        // Tile exists, check elevation change
        int oldElevation = tileCache.get(key);
        if (oldElevation != elevation)
        {
            tileCache.put(key, elevation);
            rewriteCsv();
        }
    }

     /// Appends a single tile to the CSV without reprocessing the entire file
    private static void appendTile(int x, int y, int elevation)
    {
        try
        {
            writer.write(x + "," + y + "," + elevation);
            writer.newLine();
            writer.flush();
        }
        catch (IOException e)
        {
            logger.log(Level.SEVERE, "Error writing tile to CSV", e);
        }
    }

    private static void rewriteCsv()
    {
        try
        {
            writer.close();
            writer = new BufferedWriter(new FileWriter(csvFile, false));

            // Write header again
            writer.write("X,Y,Elevation");
            writer.newLine();

            // Re-dump cache contents
            for (Map.Entry<String, Integer> entry : tileCache.entrySet())
            {
                writer.write(entry.getKey() + "," + entry.getValue());
                writer.newLine();
            }

            writer.flush();
        }
        catch (IOException e)
        {
            logger.log(Level.SEVERE, "Error rewriting CSV file", e);
        }
    }
}
