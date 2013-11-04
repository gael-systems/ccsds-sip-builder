/**
 * SIP Builder
 * Copyright (C) 2012, 2013 GAEL Systems
 * Copyright (C) 2012, 2013 European Space Agency (ESA)
 * GNU Lesser General Public License (LGPL)
 * 
 * This file is part of SIP Builder software.
 * 
 * SIP Builder is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * SIP Builder is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.gael.ccsds.sip;

import java.io.BufferedReader;
import java.io.File;
import java.io.StringReader;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;
import org.ccsds.pais.xml.OccurrenceType;
import org.ccsds.pais.xml.TransferObjectTypeDescriptor;
import org.ccsds.pais.xml.TransferObjectTypeDescriptor.Description;
import org.ccsds.pais.xml.TransferObjectTypeDescriptor.Description.TransferObjectTypeSize;
import org.ccsds.pais.xml.TransferObjectTypeDescriptor.Identification;

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
      logger.info("  --project <project-file>\n\n");
      logger.info(
         "     Builds all SIPs according the given XML <project-file>\n\n");
      logger.info("  --packaging <none|dir|zip|tar|tgz>\n\n");
      logger.info(
         "     Forces the output SIP packaging to one of 'none' (left\n" +
         "     as a directory), 'dir' (same as 'none'), 'zip', 'tgz',\n" +
         "     or 'tar'.\n\n");
      logger.info("  --delete <id1> [<id2> [...] ]\n\n");
      logger.info(
         "     Generates a SIP which requests the deletion of transfer\n" +
         "     objects of <id1>, <id2>, ... The identifier of the SIP\n" +
         "     will...\n\n");
      logger.info("  --help\n\n");
      logger.info("     Prints this message.\n\n");
      logger.info("  --version\n\n");
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
      List<String> objects_to_delete = new Vector<String>();

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
         // Match --delete argument
         else if (args[iarg].equals("--delete"))
         {
            // Check that at least a file path follows the option
            if (iarg >= (args.length - 1))
            {
               logger.fatal("The \"--delete\" option shall be "
                     + "followed by an identifier.");
               display_usage = true;
               argument_error = true;
               continue;
            }

            // Get argument
            iarg += 1;
            objects_to_delete.add(args[iarg]);

         }
         // Match --packaging argument
         else if (args[iarg].equals("--packaging"))
         {
            if (packaging != null)
            {
               logger.fatal("--packaging argument provided multiple times!");
               argument_error = true;
            }

            // Check that at least a file path follows the option
            if (iarg >= (args.length - 1))
            {
               logger.fatal("The \"--packaging\" option shall be " +
                     "followed by one of 'none', 'dir', 'zip', 'tar'," +
                     " 'tgz'.");
               display_usage = true;
               argument_error = true;
               continue;
            }

            // Get argument
            iarg += 1;
            packaging = args[iarg];

         }
         // Case of unsupported option
         else
         {
            logger.warn("Unsupported option \"" + args[iarg] +
                  "\" (skipped).");
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
         
         // Create output directory if required
         if (!target_directory.exists())
         {
            if (!target_directory.mkdirs())
            {
               logger.fatal("Cannot create output directory \"" +
                  target_directory + "\"!");
               System.exit(1);
            }
            else
            {
               logger.info("Output directory \"" +
                     target_directory + "\" created!");
            }
         }
         else
         {
            logger.info("Output directory \"" +
                  target_directory + "\" exists!");
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

         // Get main SIPs (standard sips from descriptors and collectors)
         List<Sip> main_sips = builder.getProject().getSips();

         // Prepare a list for Transfer Objects hashed according to
         // their descriptor identifiers. In the current architecture, the
         // Transfer Objects are the first Content Unit children of a Sip
         HashMap<String, List<ContentUnit>> all_transfer_objects =
            new HashMap<String, List<ContentUnit>>();

         // Loops among Transfer Objects and fill the hash map
         for (Sip sip : main_sips)
         {
            for (ContentUnit transfer_object: sip)
            {
               List<ContentUnit> transfer_objects = all_transfer_objects.get(transfer_object.getId());

               if (transfer_objects == null)
               {
                  transfer_objects = new Vector<ContentUnit>();
                  all_transfer_objects.put(transfer_object.getId(),
                        transfer_objects);
               }

               transfer_objects.add(transfer_object);
            }
         }

         // Check transfer object occurrences against descriptors constraint
         // and flag last Transfer Object of a type for the overall project
         for (TransferObjectTypeDescriptor type_descriptor :
                 builder.getProject().getTypeDescriptors())
         {
            // Get current type descriptor identification
            Identification identification =
               type_descriptor.getIdentification();

            // Check identification
            if (identification == null)
            {
               logger.warn("One Transfer Object Type Descriptor has no " +
                  "identification!");
               continue;
            }
            
            // Get current descriptor ID
            String descriptor_id = identification.getDescriptorID();
            
            // Check descriptor ID
            if ((descriptor_id == null) ||
                (descriptor_id.length() <= 0))
            {
               logger.warn("One Transfer Object Type Descriptor has a " +
                  "null or zero length ID!");
               continue;
            }
            
            // Prevent leading or trailing spaces of descriptor ID
            descriptor_id = descriptor_id.trim();

            // Get the list of Transfer Objects for the descriptor ID
            List<ContentUnit> transfer_objects =
               all_transfer_objects.get(descriptor_id);

            // Flag the last Transfer Object of the current type
            if ((transfer_objects != null) &&
                (transfer_objects.size() > 0))
            {
               // Get last Transfer Object
               ContentUnit last_transfer_object = transfer_objects.get(
                  transfer_objects.size()-1);

               // Check the retrieved Transfer Object is not null and flag
               // it as the last one in the overall project
               if (last_transfer_object != null)
               {
                  last_transfer_object.setLastTransferObject(true);
               }
               // Otherwise log this unexpected situation
               else
               {
                  logger.error("The last Transfer Object of type \"" +
                     descriptor_id + "\" is null!");
               }
            } // Flag the last Transfer Object of the current type

            // Get description of the type descriptor
            Description description = type_descriptor.getDescription();
            
            // Check description reference (mandatory in PAIS)
            if (description == null)
            {
               logger.warn("The Transfer Object Type descriptor \"" +
                  descriptor_id + "\" has no description!");
               continue;
            }
            
            // Get Transfer Object Type allowed occurrences
            OccurrenceType type_occurrences =
                  description.getTransferObjectTypeOccurrence();

            // Check that type occurrences have been provided
            if (type_occurrences == null)
            {
               logger.warn("The Transfer Object Type descriptor \"" +
                     descriptor_id + "\" has no occurrence constraints!");
            }
            // Otherwise, test the occurrences
            else
            {
               // Get minimum occurrence
               BigInteger min_occurrence =
                  type_occurrences.getMinOccurrence();

               if ((min_occurrence == null) ||
                   (min_occurrence.longValue() <= 0))
               {
                  logger.warn("The Transfer Object Type descriptor \""
                        + descriptor_id + "\" shall not declare a null or "
                        + "negative minimum occurrence!");
               }

               // Check actual number of Transfer Object against the minimum
               if ((min_occurrence != null) &&
                   (transfer_objects != null) &&
                   (transfer_objects.size() < min_occurrence.intValue()))
               {
                  logger.error("The " + transfer_objects.size() + " \""
                        + descriptor_id
                        + "\" Transfer Object(s) does not reach the "
                        + "required minimum of " + min_occurrence + "!");
               }

               // Get maximum occurrence
               BigInteger max_occurrence =
                  type_occurrences.getMaxOccurrence();

               if ((max_occurrence != null) &&
                   (max_occurrence.longValue() <= 0))
               {
                  logger.warn("The Transfer Object Type descriptor \""
                        + descriptor_id + "\" shall not declare a negative "
                        + "maximum occurrence!");
               }

               // Check actual number of Transfer Object against the minimum
               if ((max_occurrence != null) &&
                   (transfer_objects != null) &&
                   (transfer_objects.size() > max_occurrence.intValue()))
               {
                  logger.error("The " + transfer_objects.size() + " \""
                        + descriptor_id + "\" Transfer Object(s) exceed "
                        + "the " + max_occurrence + " maximum allowed!");
               }
            } // Test the occurrences

            // Get optional Transfer Object size constraints
            TransferObjectTypeSize type_size =
               description.getTransferObjectTypeSize();

            // Check the sizes of the Transfer Objects against descriptor
            // constraints
            if (type_size != null)
            {
               // Get size Units Type (required field)
               String units_type = type_size.getUnitsType();

               if ((units_type == null) ||
                   (units_type.length() <= 0))
               {
                  logger.warn("The \"" + descriptor_id +
                     "\" provides size constraints without units!");
                  continue;
               }

               // Parse units type (lower cased) to decode a scaling factor
               // from bytes
               units_type = units_type.toLowerCase();

               long unit_to_bytes = -1;

               if ("kb".equals(units_type))
               {
                  unit_to_bytes = 1024;
               }
               else if ("mb".equals(units_type))
               {
                  unit_to_bytes = 1048576;
               }
               else if ("gb".equals(units_type))
               {
                  unit_to_bytes = 1073741824;
               }
               else if ("tb".equals(units_type))
               {
                  unit_to_bytes = 1099511627776L;
               }
               else if ("pb".equals(units_type))
               {
                  unit_to_bytes = 1125899906842620L;
               }
               else
               {
                  logger.warn("The \"" + descriptor_id +
                     "\" declares an invalid unit: \"" + units_type +
                     "\"!");
                  continue;
               }

               // Get minimum size (if provided)
               Float min_size = type_size.getMinSize();

               if (min_size != null)
               {
                  if (min_size.longValue() < 0)
                  {
                     logger.warn("The \"" + descriptor_id +
                        "\" declares a negative minimum size: " +
                        min_size.longValue() + "!");
                     min_size = null;
                  }
               }

               // Get maximum size (if provided)
               Float max_size = type_size.getMinSize();

               if (max_size != null)
               {
                  if (max_size.longValue() < 0)
                  {
                     logger.warn("The \"" + descriptor_id +
                           "\" declares a negative maximum size: " +
                           max_size.longValue() + "!");
                     max_size = null;
                  }
               }

               // Check size of all Transfer Objects of the current type
               for (ContentUnit transfer_object : transfer_objects)
               {
                  // Get current Transfer Object size
                  double current_size = (double)transfer_object.getSize() /
                     unit_to_bytes;

                  // Check against minimum size (if any)
                  if ((min_size != null) &&
                      (current_size < min_size.doubleValue()))
                  {
                     logger.error("A Transfer Object has a " +
                        current_size + " " + units_type + " size, lower " +
                        "than the minimum " + min_size.floatValue() + " " +
                        units_type + " required by the \"" + descriptor_id +
                        "\" type descriptor.");
                  }

                  // Check against maximum size (if any)
                  if ((max_size != null) &&
                      (current_size > max_size.doubleValue()))
                  {
                     logger.error("A Transfer Object has a " +
                           current_size + " " + units_type + " size, " +
                           "greater than the maximum " +
                           max_size.floatValue() + " " + units_type +
                           " allowed by the \"" + descriptor_id +
                           "\" type descriptor.");
                  }

               }
            }

         } // Loop among Transfer Object Type descriptors

         // Loop among SIPs
         int sip_counter = 0;
         for (Sip sip : main_sips)
         {
            logger.info("Writing a SIP of type \"" +
               sip.getContentTypeId() + "\"...");

            // Writing SIP package
            sip.packageAsXfdu(target_directory, index_manager, packaging);

            sip_counter += 1;
         }

         // Produce requests of transfer object to delete
// TODO: To be continued
//         if (objects_to_delete.size() > 0)
//         {
//            // Create SIP
//            Sip sip = new Sip();
//            sip.
//         }

         // Log the end of the processing
         logger.info("" + sip_counter + " SIP(s) produced!");

      }
      // Case of exception during SIP builder processing
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
