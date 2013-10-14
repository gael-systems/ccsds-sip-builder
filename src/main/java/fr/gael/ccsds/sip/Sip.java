package fr.gael.ccsds.sip;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.ccsds.pais.xml.TransferObjectTypeDescriptor;
import org.ccsds.pais.xml.TransferObjectTypeDescriptor.Identification;
import org.codehaus.plexus.util.DirectoryScanner;

import fr.gael.ccsds.sip.archive.ArchiveFactory;
import fr.gael.ccsds.sip.archive.ArchiveManager;
import esa.xfdu.Xfdu;
import esa.xfdu.header.EnvironmentInfo;
import esa.xfdu.header.Extension;
import esa.xfdu.header.PackageHeader;
import esa.xfdu.header.VolumeInfo;
import esa.xfdu.object.data.DataObject;
import fr.gael.drb.xsd.Namespace;

public class Sip extends Vector<ContentUnit>
{
   /**
    * A logger for this class.
    */
   private static Logger logger = Logger.getLogger(Project.class);

   /**
    * Project governing this SIP definition.
    */
   private Project project;
// TODO: should not be a direct link to the project but rather extracted data

   /**
    * Identifier of this SIP
    */
   private String id;

   private String contentTypeId;

   /**
    * @return the project
    */
   public Project getProject()
   {
      return project;
   }

   /**
    * @param project the project to set
    */
   public void setProject(Project project)
   {
      this.project = project;
   }

   /**
    * @return the id
    */
   public String getId()
   {
      return id;
   }

   /**
    * @param id the id to set
    */
   public void setId(String id)
   {
      this.id = id;
   }

   /**
    * @return the contentType
    */
   public String getContentTypeId()
   {
      return contentTypeId;
   }

   /**
    * @param contentTypeId the content type identifier to set
    */
   public void setContentTypeId(String contentType_id)
   {
      this.contentTypeId = contentType_id;
   }

   /**
    * @param directory
    */
   public void packageAsXfdu(final File directory, final IndexManager
         index_manager, final String packaging) throws SipBuilderException
   {
      // Check output directory
      if ((directory == null) || (!directory.isDirectory()))
      {
         throw new SipBuilderException("Invalid output directory \"" + 
            directory + "\" (does not exists or not a directory)");
      }

      // Get current sequence number
      int sequence_number = index_manager.getNextIndex("" + this.getId() +
            "-SIP");

      // Build SIP unique identifier
      String sip_identifier = "" + this.getId() +"-SIP-" +
         String.format("%04d", sequence_number);

      // Create staging directory for the output package
      File stage_package =
         new File(directory.getAbsolutePath() + File.separator +
               sip_identifier);

      // Remove directory if already existing
      if (stage_package.exists())
      {
         logger.warn("Directory \"" + stage_package.getName()
               + "\" already exists: removing...");
         recursiveDeleteFile(stage_package);
      }

      // Create directory
      if (stage_package.mkdirs() == true)
      {
         logger.info("Directory \"" + stage_package.getName() + "\" created");
      }
      else
      {
         logger.error("Cannot create directory \"" + stage_package.getPath()
               + "\"");
         throw new SipBuilderException("Cannot create directory \""
               + stage_package.getPath() + "\"");
      }

      final String xfdumanifest = new File(stage_package, "xfdumanifest.xml").getPath();

      logger.info("Creating xfdu manifest...");
      final Xfdu xfdu = new Xfdu(xfdumanifest, null, null, null, null);

      // Update XFDU namespace
      final Namespace ns = xfdu.getNamespace();
      final Namespace sipns = new Namespace();
      sipns.bind("sip", "urn:ccsds:schema:pais:1");
      ns.addNamespaces(sipns);

      final VolumeInfo vi = new VolumeInfo("1.0", null);

      // Insertion of package header entry
      final PackageHeader ph =
         new PackageHeader(sip_identifier, vi, new ArrayList<EnvironmentInfo>());

      // Get producer source ID
      String source_id = null;
      
      for (TransferObjectTypeDescriptor type_descriptor :
              this.project.getTypeDescriptors())
      {
         Identification identification =
            type_descriptor.getIdentification();
         
         if ((identification != null) &&
             (identification.getProducerSourceID() != null))
         {
            source_id = identification.getProducerSourceID().trim();
            break;
         }
         else
         {
            logger.warn("One descriptor has no Identification or no " +
               "Producer Source ID!");
         }
      }
      
      if (source_id == null)
      {
         logger.warn("No descriptor contains a valid Producer Source ID!");
         source_id = "UNSPECIFIED_SOURCE";
      }

      // Build environment info
      final EnvironmentInfo ei = new EnvironmentInfo(null, new Extension(
            "<sipGlobalInformation "
                  + " xmlns=\"urn:ccsds:schema:pais:1\">\n"
                  + "   <sipID>" + sip_identifier + "</sipID>\n"
                  + "   <producerSourceID>"
                  + source_id + "</producerSourceID>\n"
                  + "   <producerArchiveProjectID>"
                  + this.project.getConstraints().getProducerArchiveProjectID()
                  + "</producerArchiveProjectID>\n"
                  + "   <sipContentTypeID>"
                  + this.getContentTypeId() + "</sipContentTypeID>\n"
                  + "   <sipSequenceNumber>"
                  + sequence_number + "</sipSequenceNumber>\n"
                  + "</sipGlobalInformation>\n"));

      ph.getEnvironmentInfo().add(ei);
      xfdu.setPackageHeader(ph);

      // Add content units and collect data objects
      ArrayList<DataObject> data_objects = new ArrayList<DataObject>();

      for (ContentUnit child_unit : this)
      {
         xfdu.add(child_unit.toXfduContentUnit(index_manager, data_objects,
               stage_package.getAbsolutePath()));
      }

      // Add collected data objects
      for (DataObject data_object : data_objects)
      {
         xfdu.addDataObject(data_object);
      }

      // Save manifest
      try
      {
         xfdu.save();
      }
      catch (IOException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      
      // File packaging
      // Case of nothing to do
      if ((packaging == null) ||
          ArchiveFactory.PACKAGING_NONE.equals(packaging) ||
          ArchiveFactory.PACKAGING_DIRECTORY.equals(packaging))
      {
         return;
      }

      final ArchiveManager am = ArchiveFactory.open(packaging);
      final File destination = new File(stage_package.getParentFile(),
            stage_package.getName() + am.getFileExtension());

      // Delete destination file if exists
      if (destination.exists())
      {
         throw new SipBuilderException("Archive file \"" + destination.getName() + "\" "
               + "already exists: please remove it!");
      }

      final DirectoryScanner scanner = new DirectoryScanner();
      // Search for every things
      final String[] include = new String[] { "**" };
      scanner.setIncludes(include);
      scanner.setBasedir(stage_package.getAbsolutePath());
      scanner.setCaseSensitive(true);
      scanner.scan();

      logger.info("Archiving SIP as \"" + destination.getName() + "\"...");

      for (final String file : scanner.getIncludedFiles())
      {
         try
         {
            am.copy(new File(stage_package.getAbsoluteFile(), file),
                  destination, file);
         }
         catch (final Exception e)
         {
            logger.error("Cannot copy file \"" + file + "\" into archive.", e);
         }
      }

      // Delete staging directory
      logger.info("Deleting staging directory...");
      recursiveDeleteFile(stage_package);

      return;

   } // End packageAsXfdu(File, IndexManager, String)

   @Override
   public String toString()
   {
      String message = "SIP: \"" + this.getId() + "\"";

      for (ContentUnit content_unit : this)
      {
         message += "\n" + content_unit.toString();
      }

      return message;
   }

   private static boolean recursiveDeleteFile(final File path)
   {
      if (path.exists())
      {
         if (path.isDirectory())
         {
            final File[] files = path.listFiles();
            for (int i = 0; i < files.length; i++)
            {
               if (files[i].isDirectory())
               {
                  if (!recursiveDeleteFile(files[i]))
                  {
                     logger.error("Directory \"" + files[i].getPath()
                           + "\" wont be deleted.");
                  }
               }
               else
               {
                  if (!files[i].delete())
                  {
                     logger.error("File \"" + files[i].getPath()
                           + "\" wont be deleted.");
                  }
               }
            }
         }
      }
      return (path.delete());
   }

} // End Sip class
