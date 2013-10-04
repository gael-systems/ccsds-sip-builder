package fr.gael.ccsds.sip;

import java.io.BufferedReader;
import java.io.File;
import java.io.StringReader;
import java.util.Calendar;

import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;

public class SipBuilder
{
   /**
    * A logger for this class.
    */
   private static Logger logger = Logger.getLogger(SipBuilder.class);

   /**
    * Project configuration
    */
   private Project project = null;

   /**
    * Build an instance of this class.
    * 
    * @throws JAXBException if the give file could not be parsed.
    */
   SipBuilder(File project_file) throws JAXBException
   {
      this.setProject(Project.readProject(project_file));
   }

   /**
    * Log usage message
    */
   private static void logUsage(Logger logger)
   {
      logger.info("Usage: java -jar sip-builder-<version>.jar [Options]");
      logger.info("Options:");
      logger.info("  --project <project-file>");
      logger.info("     Builds all SIPs according the given XML <project-file>");
      logger.info("  --help");
      logger.info("     Prints this message");
      logger.info("  --version");
      logger.info("     Prints the release version of this application");
   } // End logUsage()

   /**
    * The default command line application.
    * 
    * @param args are the command line arguments.
    */
   public static void main(String[] args)
   {
      // Print current application information
      String version = SipBuilder.class.getPackage().getImplementationVersion();

      logger.info("SIP Builder " + (version == null ? "" : version + " ")
            + "- (c) 2012 -" + Calendar.getInstance().get(Calendar.YEAR)
            + " - ESA / GAEL Systems");

      // Check arguments
      if (args.length <= 0)
      {
         logger.error("This application requires at least a parameter");
         logUsage(logger);
         System.exit(1);
      }

      // A first loop on arguments to identify help parameter that
      // overrides any others.
      for (int iarg = 0; iarg < args.length; iarg++)
      {
         // Manage help request
         if (args[iarg].equals("--help") || args[iarg].equals("-h")
               || args[iarg].equals("-help"))
         {
            logUsage(logger);
            System.exit(0);
         }

         // Manage version request (exit immediately - already printed)
         if (args[iarg].equals("--version") || args[iarg].equals("-v")
               || args[iarg].equals("-version"))
         {
            System.exit(0);
         }
      }

      // Initialize parameters
      File project_file = null;
      File target_directory = null;
      String packaging = null;

      boolean argument_error = false;
      boolean display_usage = false;

      // Loop on remaining arguments.
      for (int iarg = 0; iarg < args.length; iarg++)
      {
         // Match --project argument
         if (args[iarg].equals("--project"))
         {
            if (project_file != null)
            {
               logger.fatal("--project argument provided multiple times!");
               argument_error = true;
            }

            // Check that at least a file path follows the option
            if (iarg >= (args.length - 1))
            {
               logger.fatal("The \"--project\" option shall be "
                     + "followed by a path to a file.");
               display_usage = true;
               argument_error = true;
               continue;
            }

            // Get argument
            iarg += 1;
            project_file = new File(args[iarg]);

            // Check that file exist
            if (!project_file.exists())
            {
               logger.fatal("Input project file \"" + project_file
                     + "\" does not exist!");
               argument_error = true;
               continue;
            }
         }
         // Case of unsupported option
         else
         {
            logger.warn("Unsupported option \"" + args[iarg] + "\" (skipped).");
         }

      } // Loop among arguments

      // Check that mandatory project file has been resolved
      if (project_file == null)
      {
         logger.fatal("No project file specified!");
         argument_error = true;
         display_usage = true;
      }

      // Exit on argument error
      if (argument_error)
      {
         if (display_usage)
         {
            logUsage(logger);
         }

         System.exit(1);
      }

      // Create a SIP builder
      SipBuilder builder = null;

      try
      {
         logger.info("Parsing project file...");
         builder = new SipBuilder(project_file);

         // Get output directory (if not provided in parameter)
         if (target_directory == null)
         {
            if (builder.getProject().getOutputDirectory() != null)
            {
               target_directory =
                     new File(builder.getProject().getOutputDirectory());
            }
            else
            {
               target_directory = new File(".");
            }
         }

         // Get output packaging (if not provided in parameter)
         if (packaging == null)
         {
            if (builder.getProject().getOutputPackaging() != null)
            {
               packaging =
                     builder.getProject().getOutputPackaging();
            }
            else
            {
               packaging = "zip";
            }
         }
         
         // Log descriptor tree
         logger.info("Descriptor tree:");

         BufferedReader reader = new BufferedReader(new StringReader(
               builder.getProject().toString()));

         String line = null;
         while ((line = reader.readLine()) != null)
         {
            logger.info("   " + line);
         }

         // Create a dedicated index manager
         IndexManager index_manager = new IndexManager();

         // Loop among SIPs
         int sip_counter = 0;
         for (Sip sip : builder.getProject().getSips())
         {
            logger.info("Writing a SIP of type \"" + sip.getContentTypeId() + "\"...");

            // Writing SIP package
            sip.packageAsXfdu(target_directory, index_manager, packaging);
            
            sip_counter += 1;
         }

         logger.info("" + sip_counter + " SIP(s) produced!");
      }
      catch (Exception exception)
      {
         if (exception.getMessage() != null)
         {
            logger.fatal(exception.getMessage(), exception);
         }
         else
         {
            logger.fatal("Error while processing SIP(s)!", exception);
         }
         System.exit(1);
      }
   } // End main(String [])

   /**
    * @return the current project configuration.
    */
   public Project getProject()
   {
      return project;
   } // End getProject()

   /**
    * Assign a given project configuration.
    * 
    * @param project configuration to be assigned.
    */
   private void setProject(Project project)
   {
      this.project = project;
   } // End setProject()

} // End SipBuilder class
