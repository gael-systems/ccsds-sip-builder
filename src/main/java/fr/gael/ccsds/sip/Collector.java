package fr.gael.ccsds.sip;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.codehaus.plexus.util.DirectoryScanner;

public class Collector
{
   /**
    * The collector definition from the project file.
    */
   private final fr.gael.ccsds.sip.xml.Collector xmlCollector;
   
   /**
    * A scanner for this collector.
    */
   private final DirectoryScanner scanner;

   /**
    * Scanner result cache. The results are hashed according to the base
    * directory.
    */
   private final HashMap<String, String []> resultCache;
   
   private File baseDirectory;

   /**
    * Build a data collector based on a given definition.
    * @param xml_collector definition from the project file.
    */
   Collector(fr.gael.ccsds.sip.xml.Collector xml_collector)
   {
      // Assign the collector definition
      this.xmlCollector = xml_collector;

      // Create the directory scanner
      this.scanner = new DirectoryScanner();

      // Configure include patterns of the scanner from the definition
      if ((xml_collector.getInclude() != null) &&
          (xml_collector.getInclude().size() > 0))
      {
         List<String> includes = xml_collector.getInclude();
         this.scanner.setIncludes(
               includes.toArray(new String[includes.size()]));
      }

      // Configure exclude patterns of the scanner from the definition
      if ((xml_collector.getExclude() != null) &&
          (xml_collector.getExclude().size() > 0))
      {
         List<String> excludes = xml_collector.getExclude();
         this.scanner.setExcludes(
               excludes.toArray(new String[excludes.size()]));
      }

      // Configure case sensitivity of the scanner
      this.scanner.setCaseSensitive(xml_collector.isCaseSensitive());

      // Initialize result cache
      this.resultCache = new HashMap<String, String []>();

      // Set local base directory
      if (xml_collector.getBaseDirectory() != null)
      {
         this.baseDirectory = new File(xml_collector.getBaseDirectory());
      }
      else
      {
         this.baseDirectory = null;
      }

   } // End Collector(fr.gael.ccsds.sip.xml.Collector)

   /**
    * @return the resolve base directory.
    */
   public File resolveBaseDirectory(File base_directory)
   {
      if (this.baseDirectory != null)
      {
         if (this.baseDirectory.isAbsolute())
         {
            return base_directory;
         }
         else if (base_directory != null)
         {
            return new File(base_directory, this.baseDirectory.getPath());
         }
      }

      return base_directory;

   } // End resolveBaseDirectory()

   /**
    * 
    * @param base_directory
    * @return
    */
   public String [] getIncludedFiles(File base_directory)
   {
      // Attempt to get previously computed results
      String [] results = this.resultCache.get(base_directory.getAbsolutePath());
      
      // Return immediately if a result was already processed
      if (results != null)
      {
         return results;
      }

      // Configure scanner base directory
      this.scanner.setBasedir(this.resolveBaseDirectory(base_directory));

      // Scan the files
      this.scanner.scan();

      // Gather results (either files and directories)
      List<String> files = new ArrayList<String>();
      files.addAll(Arrays.asList(this.scanner.getIncludedFiles()));
      files.addAll(Arrays.asList(this.scanner.getIncludedDirectories()));
      results = files.toArray(new String[files.size()]);

      // Cache the resulting files
      if (results != null)
      {
         this.resultCache.put(base_directory.getAbsolutePath(), results);
      }

      // Return result
      return results;

   } // End getIncludedFiles(File)

} // End Collector class
