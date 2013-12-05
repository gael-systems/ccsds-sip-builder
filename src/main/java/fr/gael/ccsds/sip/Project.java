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

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.HashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.ccsds.pais.xml.CollectionDescriptor;
import org.ccsds.pais.xml.DataObjectType;
import org.ccsds.pais.xml.OccurrenceType;
import org.ccsds.pais.xml.SipConstraints;
import org.ccsds.pais.xml.SipConstraints.SipContentType;
import org.ccsds.pais.xml.SipConstraints.SipContentType.AuthorizedDescriptor;
import org.ccsds.pais.xml.TransferObjectGroupType;
import org.ccsds.pais.xml.TransferObjectTypeDescriptor;
import org.ccsds.pais.xml.TransferObjectTypeDescriptor.Identification;

import fr.gael.ccsds.sip.xml.Descriptor;

/**
 * A customization of the JAXB binding of Project type.
 */
public class Project extends fr.gael.ccsds.sip.xml.Project
{
   /**
    * A logger for this class.
    */
   private static Logger logger = Logger.getLogger(Project.class);

   /**
    * The input project file (if any)
    */
   private File configurationFile = null;

   /**
    * SIP constraints
    */
   private SipConstraints constraints = null;
   
   /**
    * Transfer Object Type descriptors hashed according to their type
    * identifiers.
    */
   private HashMap<String, TransferObjectTypeDescriptor> typeDescriptors =
         null;

   /**
    * Data Object collectors hashed according to their type identifiers.
    */
   private HashMap<String, Collector> fileCollectors = null;

   /**
    * File collectors base directory.
    */
   private File collectorsBaseDirectory;

   /**
    * Reads a project file and build (bind) the corresponding instance.
    * 
    * @param project_file the abstract path to the project file (XML).
    * @return the {@link Project} instance representing the input file.
    * @throws JAXBException if the input file could not be parsed.
    */
   public static Project readProject(File project_file) throws JAXBException
   {
      // Read project File
      try
      {
         // Build a JAXB context
         JAXBContext context =
            JAXBContext.newInstance("fr.gael.ccsds.sip.xml");

         // Build a parser
         Unmarshaller unmarshaller = context.createUnmarshaller();

         // Configure the parser with a customized object factory
         unmarshaller.setProperty("com.sun.xml.bind.ObjectFactory",
               new ProjectObjectFactory());

         // De-scramble the input file
         logger.info("Reading project file \"" +
            project_file.getName() + "\"...");

         JAXBElement<fr.gael.ccsds.sip.xml.Project> project_element =
               unmarshaller.unmarshal(new StreamSource(project_file),
               fr.gael.ccsds.sip.xml.Project.class);

         // Get the parsed project
         Project project = (Project)project_element.getValue();

         // Save the input file
         project.setConfigurationFile(project_file);

         // Get an absolute abstract file to avoid any parent issue
         File project_absolute_file = project_file.getAbsoluteFile();

         // Read descriptors
         if (project.getDescriptors() != null)
         {
            // Get descriptor base directory
            File descriptors_base_directory =
                    project_absolute_file.getParentFile();

            if ((project.getDescriptors() != null) &&
                (project.getDescriptors().getBaseDirectory() != null))
            {
               descriptors_base_directory = new File(
                     project.getDescriptors().getBaseDirectory());

               if (!descriptors_base_directory.isAbsolute())
               {
                  descriptors_base_directory =
                     new File("" +
                        project_absolute_file.getParentFile().
                        getAbsolutePath() + File.separator +
                        descriptors_base_directory.getPath());
               }
            }

            // Check base directory
            if (!descriptors_base_directory.exists())
            {
               logger.error("Descriptor base directory could not be " +
                  "resolved: \"" +
                  descriptors_base_directory.getAbsolutePath() + "\"");
            }

            // Build a JAXB context
            context = JAXBContext.newInstance("org.ccsds.pais.xml");

            // Build a parser
            unmarshaller = context.createUnmarshaller();

            // Loop among descriptors
            for (Descriptor descriptor :
                    project.getDescriptors().getDescriptor())
            {
               // Compute current descriptor file path
               File descriptor_file = new File(descriptor.getFile());
               
               if (!descriptor_file.isAbsolute())
               {
                  descriptor_file = new File(
                     descriptors_base_directory.getAbsolutePath() +
                     File.separator + descriptor_file.getPath());
               }

               logger.info("Reading descriptor file \"" +
                     descriptor_file.getName() + "\"...");

               Object object = unmarshaller.unmarshal(new StreamSource(
                     descriptor_file));
               
               if (object instanceof TransferObjectTypeDescriptor)
               {
                  project.setTypeDescriptor((TransferObjectTypeDescriptor)
                        object);
               }
               else if (object instanceof SipConstraints)
               {
                  project.setConstraints((SipConstraints)object);
               }
               else if (object instanceof CollectionDescriptor)
               {
                  logger.warn("Collection Descriptors are not " +
                     "supported (skipped)");
               }
               else
               {
                  logger.error("Unsupported descriptor type (class \"" +
                     object.getClass().getName() + "\" (skipped)");
               }

            } // Loop among descriptor file(s)
         } // Case of descriptor declarations present

         // Set collectors base directory
         if ((project.getCollectors() != null) &&
             (project.getCollectors().getBaseDirectory() != null))
         {
            // Get base directory from the project file
            File base_directory = new File(
                  project.getCollectors().getBaseDirectory());

            // Complete base directory with current directory if 
            if (!base_directory.isAbsolute())
            {
               base_directory = new File("" +
                  project_absolute_file.getParentFile().
                     getAbsolutePath() + File.separator +
                     base_directory.getPath());
            }

            // Set directory
            project.setCollectorsBaseDirectory(base_directory);
         }
         // Otherwise use the current working directory
         else
         {
            project.setCollectorsBaseDirectory(new File("."));
         }

         // Check collectors base directory
         if (project.getCollectorsBaseDirectory().exists())
         {
            logger.info("Collectors base directory is: \"" +
               project.getCollectorsBaseDirectory().getAbsolutePath() +
               "\"");
         }
         else
         {
            logger.error("Collectors base directory does no exist: \"" +
                  project.getCollectorsBaseDirectory().getAbsolutePath() +
                  "\"");
         }

         // Initialize collectors
         project.initializeFileCollectors();

         // Returned the built project
         return project;
      }
      catch (final JAXBException exception)
      {
         logger.error("Cannot read project from \"" + project_file.getPath()
               + "\".", exception);
         throw exception;
      }

   } // / End readProject(File)

   /**
    * Build a default instance of this class.
    */
   public Project()
   {
      // Does nothing
   }

   /**
    * @return the configurationFile
    */
   public File getConfigurationFile()
   {
      return configurationFile;
   }

   /**
    * @param configurationFile the configurationFile to set
    */
   public void setConfigurationFile(File configurationFile)
   {
      this.configurationFile = configurationFile;
   }

   /**
    * Initialize or re-initialize the map of Data Object collectors from the
    * XML collectors. This operation replaces any existing instance.
    */
   private void initializeFileCollectors()
   {
      // Initialize a new hash map
      this.fileCollectors = new HashMap<String, Collector>();
      
      // Return immediately if no input XML collector found
      if ((this.getCollectors() == null) ||
          (this.getCollectors().getCollector() == null))
      {
         return;
      }
   
      // Loop among input XML collectors
      for (fr.gael.ccsds.sip.xml.Collector xml_collector :
              this.getCollectors().getCollector())
      {
         this.fileCollectors.put(xml_collector.getTypeId(),
               new Collector(xml_collector));

         logger.info("Collector added for type \"" +
               xml_collector.getTypeId() + "\"");
      }
   
   } // End intializeDataObjectCollectors()

   private static long computeRequiredParentNumber(OccurrenceType
      occurrence, List<?> children)
   {
      // Get maximum occurrence
      long maximum_occurrence = 0;

      if ((occurrence != null) && (occurrence.getMaxOccurrence() != null))
      {
         // Get occurrence
         maximum_occurrence = occurrence.getMaxOccurrence().longValue();

         // Check occurrence
         if (maximum_occurrence <= 0)
         {
            logger.error("Invalid maximum occurrence of " + maximum_occurrence
                  + " (considering unlimited).");
            maximum_occurrence = 0;
         }
      }

      // Get number of children
      int children_count = 0;

      if (children != null)
      {
         children_count = children.size();
      }

      // Case of no children: no Content Unit is required
      if (children_count <= 0)
      {
         return 0;
      }

      // Case of unlimited occurrences: only one Content Unit is required
      if (maximum_occurrence <= 0)
      {
         return 1;
      }

      // Case of children count lower than maximum: one unit required
      if (children_count <= maximum_occurrence)
      {
         return 1;
      }

      // Case of limited occurrences: return the minimum number of units
      return (long)Math.ceil(children_count / (double)maximum_occurrence);

   } // End computeRequiredContentUnitNumber(OccurrenceType,

   /**
    * Dispatch a series of Content Unit as children of another series
    * according to a maximum of occurrences per parent. It is expected that
    * the caller has provided a sufficient number of parent Content Units.
    *
    * @param parents the list of parent Content Units to be filled.
    * @param occurrence from which the maximum number of children per parent
    *    shall be extracted (unlimited if null).
    * @param children the children to be dispatched.
    */
   private static <T> void dispatchChildren(List<? extends List<T>>
      parents, OccurrenceType occurrence, List<T> children)
   {
      // Get maximum occurrence
      long maximum_occurrence = 0;
   
      if ((occurrence != null) &&
          (occurrence.getMaxOccurrence() != null))
      {
         // Get occurrence
         maximum_occurrence = occurrence.getMaxOccurrence().longValue();
   
         // Check occurrence
         if (maximum_occurrence <= 0)
         {
            logger.error("Invalid maximum occurrence of " +
               maximum_occurrence + " (considering unlimited).");
            maximum_occurrence = 0;
         }
      }

      // Prepare a counter among children units
      long content_unit_counter = 0;

      // Loop among children units
      for (T child : children)
      {
         // Compute the index of the target Transfer Object Unit
         int parent_unit_index = 0;

         if (maximum_occurrence > 0)
         {
            parent_unit_index = (int)(content_unit_counter /
                  maximum_occurrence);
         }

         // Append current unit
         parents.get(parent_unit_index).add(child);

         // Update current child counter
         content_unit_counter += 1;

      } // Loop among content units

   } // End dispatchObjects(List<ContentUnit>, OccurrenceType,

   /**
    * @return the list of SIP content types
    */
   public List<SipContentType> getSipContentTypes()
   {
      // Return an empty list if no constraint has been initialized
      if (this.constraints == null)
      {
         return new ArrayList<SipContentType>();
      }

      // Return the content types
      return this.constraints.getSipContentType();

   } // End getSipContentTypes()

   /**
    * @return the list of transfer object type descriptors
    */
   public Collection<TransferObjectTypeDescriptor> getTypeDescriptors()
   {
      return this.typeDescriptors.values();

   } // End getTypeDescriptors()

   /**
    * @return the transfer object type descriptor of a given ID
    */
   public TransferObjectTypeDescriptor getTypeDescriptor(String descriptor_id)
   {
      // Return immediately if no type descriptor is defined
      if (this.typeDescriptors == null)
      {
         return null;
      }

      // Return the descriptor
      return this.typeDescriptors.get(descriptor_id);

   } // End getTypeDescriptor(String)
   
   /**
    * Add or replace a type descriptor of this project.
    * 
    * @param descriptor the descriptor to be added or replaced
    */
   public void setTypeDescriptor(TransferObjectTypeDescriptor descriptor)
   {
      // Check input parameter
      if (descriptor == null)
      {
         throw new NullPointerException("Cannot hash null type descriptor.");
      }

      // Get Identification element
      Identification identification = descriptor.getIdentification();

      if (identification == null)
      {
         throw new NullPointerException("Cannot hash a type descriptor " +
            "with no identification.");
      }

      // Get descriptor ID
      String identifier = identification.getDescriptorID();
      
      if ((identifier == null) ||
          (identifier.length() <= 0))
      {
         throw new NullPointerException("Cannot hass a descriptor with a " +
            "null or zero-length identifier.");
      }

      // Trim identifier to avoid leading or trailing white-spaces
      identifier = identifier.trim();

      // Create hash map if not already done
      if (this.typeDescriptors == null)
      {
         this.typeDescriptors = new HashMap<String,
               TransferObjectTypeDescriptor>();
      }

      // Hash descriptor in the map according to the descriptor ID
      this.typeDescriptors.put(identifier, descriptor);

   } // End setTypeDescriptor(TransferObjectTypeDescriptor)

   /**
    * @return the constraints
    */
   public SipConstraints getConstraints()
   {
      return constraints;
   }

   /**
    * @param constraints the constraints to set
    */
   public void setConstraints(SipConstraints constraints)
   {
      this.constraints = constraints;
   }

   /**
    * @return the Data Object Collector corresponding a given type ID.
    */
   public Collector getCollector(String type_id)
   {
      // Build the map of collector if not already done
      if (this.fileCollectors == null)
      {
         this.initializeFileCollectors();
      }

      // Return the collector (if any)
      return this.fileCollectors.get(type_id);

   } // End getCollector(String)

   /**
    * @return the collectorsBaseDirectory
    */
   public File getCollectorsBaseDirectory()
   {
      return collectorsBaseDirectory;
   }

   /**
    * @param collectorsBaseDirectory the collectorsBaseDirectory to set
    */
   public void setCollectorsBaseDirectory(File collectorsBaseDirectory)
   {
      this.collectorsBaseDirectory = collectorsBaseDirectory;
   }

   /**
    * @return the SIPs corresponding to the present project definition.
    */
   public List<Sip> getSips()
   {
      // Log call
      logger.info("Processing SIPs...");

      // Get SIP content types
      Collection<SipContentType> content_types =
            this.getSipContentTypes();

      // Return immediately if of no content type definition was retrieved
      if (content_types.size() <= 0)
      {
         logger.info("No SIP content type defined.");
         return new ArrayList<Sip>();
      }

      // Sort content types according if constrained
      ArrayList<SipContentType> sorted_types =
            new ArrayList<SipContentType>(content_types);

      Collections.sort(sorted_types, new Comparator<SipContentType>()
      {
         public int compare(SipContentType type1, SipContentType type2)
         {
            BigInteger type1_seq = getSipContentTypeSequenceNumber(
                  type1.getSipContentTypeID());

            if (type1_seq == null)
            {
               return 1;
            }

            BigInteger type2_seq = getSipContentTypeSequenceNumber(
                  type2.getSipContentTypeID());

            if (type2_seq == null)
            {
               return -1;
            }

            return type1_seq.compareTo(type2_seq);
         }
      });

      // Return the built list of SIPs
      return this.getSips(sorted_types);

   } // End getSips()

   public BigInteger getSipContentTypeSequenceNumber(String content_type_id)
   {
      if (this.getConstraints() == null)
      {
         return null;
      }
      
      List<SipConstraints.SipSequencingConstraintGroup> groups =
            this.getConstraints().getSipSequencingConstraintGroup();
      
      if ((groups == null) || (groups.size() <= 0))
      {
         return null;
      }

      for (SipConstraints.SipSequencingConstraintGroup group : groups)
      {
         List<SipConstraints.SipSequencingConstraintGroup.ConstraintItem>
            constraints = group.getConstraintItem();
         
         if (constraints == null)
         {
            continue;
         }
         
         for (SipConstraints.SipSequencingConstraintGroup.ConstraintItem
               constraint : constraints)
         {
            if (content_type_id.equals(constraint.getSipContentTypeID()))
            {
               return constraint.getConstraintSerialNumber();
            }
         }
      }
      
      return null;
   } // End getSipContentTypeSequenceNumber(String)

   /**
    * @return the SIPs corresponding to a given list of SIP content types.
    */
   public List<Sip> getSips(Collection<SipContentType> content_types)
   {
      // Build list of output SIPs
      ArrayList<Sip> sips = new ArrayList<Sip>();

      // Loop among content types
      for (SipContentType content_type : content_types)
      {
         // Log loop
         logger.info("Processing SIP content type \"" +
            content_type.getSipContentTypeID() + "\"...");

         // Derive SIP from the current content type
         List<Sip> current_sips = this.getSips(content_type);

         // Check returned SIP
         if ((current_sips == null) ||
             (current_sips.size() <= 0))
         {
            logger.error("Could not derive SIPs from content type \"" +
               content_type.getSipContentTypeID() + "\" (skipped)");
            continue;
         }

         // Add derived SIPs to output list
         sips.addAll(current_sips);

      } // Loop among content types

      // Return the built list of SIPs
      return sips;

   } // End getSips()

   /**
    * @return SIPs corresponding to a given SIP content type.
    */
   public List<Sip> getSips(SipContentType content_type)
   {
      // Prepare output list of SIPs
      ArrayList<Sip> sips = new ArrayList<Sip>();

      // Loop among authorized descriptors
      for (AuthorizedDescriptor authorized_descriptor :
              content_type.getAuthorizedDescriptor())
      {
         // Log Loop
         logger.info("Processing Transfer Object Descriptor \"" +
            authorized_descriptor.getDescriptorID() + "\"...");

         // Get descriptor identifier
         String descriptor_id = authorized_descriptor.getDescriptorID();

         // Check descriptor identifier
         if ((descriptor_id == null) ||
             (descriptor_id.length() <= 0))
         {
            logger.error("Invalid authorized descriptor ID (null or " +
               "zero length) for SIP content type \"" +
                  content_type.getSipContentTypeID() + "\" (skipped)");
            continue;
         }

         // Get descriptor from ID
         TransferObjectTypeDescriptor descriptor =
               this.getTypeDescriptor(descriptor_id.trim());

         // Check descriptor
         if (descriptor == null)
         {
            logger.error("Cannot retieve a descriptor identified by \"" +
               descriptor_id + "\" for SIP content type \"" +
               content_type.getSipContentTypeID() + "\"");
            continue;
         }

         // Derive Content Units from current descriptor
         List<ContentUnit> content_units =
               this.getContentUnits(descriptor);

         // Check the number transfer objects against type constraints
         if ((content_units != null) &&
             (descriptor.getDescription() != null) &&
             (descriptor.getDescription().getTransferObjectTypeOccurrence()
                   != null))
         {
            OccurrenceType occurrence = descriptor.getDescription().
               getTransferObjectTypeOccurrence();

            boolean occurrence_error = false;

            // Check minimum occurrence
            if ((occurrence.getMinOccurrence() != null) &&
                (occurrence.getMinOccurrence().intValue() >
                   content_units.size()))
            {
               logger.warn("The " + content_units.size() +
                  " object(s) collected for \"" + descriptor_id +
                  "\" type do not meet the minimum of " + 
                  occurrence.getMinOccurrence().intValue() +
                  " object(s) required.");
               occurrence_error = true;
            }

            // Check maximum occurrence
            if ((occurrence.getMaxOccurrence() != null) &&
                (occurrence.getMaxOccurrence().intValue() <
                   content_units.size()))
            {
               logger.warn("The " + content_units.size() +
                  " object(s) collected for \"" + descriptor_id +
                  "\" type exceed the maximum of " + 
                  occurrence.getMaxOccurrence().intValue() +
                  " object(s) allowed.");
               occurrence_error = true;
            }

            // No-error report
            if (!occurrence_error)
            {
               logger.info("The " + content_units.size() +
                  " object(s) collected for \"" + descriptor_id +
                  "\" type is included in the authorized range " +
                  "of occurrences.");
            }
         }

         // Compute number of required SIPs to hold the units of this type
         long required_sip_number = computeRequiredParentNumber(
               authorized_descriptor.getOccurrence(), content_units);

         // Complete list of SIPs if not sufficient
         while (required_sip_number > sips.size())
         {
            Sip sip = new Sip();
            sip.setId(this.getConstraints().getProducerArchiveProjectID());
            sip.setProject(this);
            sip.setContentTypeId(content_type.getSipContentTypeID());
            sips.add(sip);
         }

         // Dispatch content units in the appropriate SIPs
         dispatchChildren(sips, authorized_descriptor.getOccurrence(),
               content_units);

      } // Loop among authorized descriptors

      // Return the built SIP
      return sips;

   } // End getSips(SipContentType)

   /**
    * @return the Content Units corresponding to a given descriptor.
    */
   public List<ContentUnit> getContentUnits(TransferObjectTypeDescriptor
         descriptor)
   {
      // Prepare output list of Content Units (Transfer Object level)
      ArrayList<ContentUnit> content_units = new ArrayList<ContentUnit>();

      // Loop among top level group type
      for (TransferObjectGroupType group_type : descriptor.getGroupType())
      {
         // Log loop
         logger.info("Processing Group Type \"" +
            group_type.getGroupTypeID() + "\"...");

         // Get Content Units of the current Transfer Object group type
         List<ContentUnit> current_content_units =
               this.getContentUnits(group_type,
                     this.getCollectorsBaseDirectory());

         // Get number of returned Content Units
         long content_unit_number = 0;

         if (current_content_units != null)
         {
            content_unit_number = current_content_units.size();
         }

         // Stop here if no Content Units is to process
         if (content_unit_number <= 0)
         {
            continue;
         }

         // Check number of content unit minimum occurrence (if specified)
         OccurrenceType group_type_occurrence =
               group_type.getGroupTypeOccurrence();

         if ((group_type_occurrence != null) &&
             (group_type_occurrence.getMinOccurrence() != null))
         {
            if (content_unit_number <
                  group_type_occurrence.getMinOccurrence().longValue())
            {
               logger.error("Too few Content Units collected from " +
                  "group type \"" + group_type.getGroupTypeID() +
                  "\": a minimum of " +
                  group_type_occurrence.getMinOccurrence().longValue() +
                  " required (proceeding anyway)");
            }
         }

         // Compute the number of Transfer Object Content Units required to
         // hold the group type content units according the maximum allowed
         // occurrences
         long required_tranfer_object_number =
               computeRequiredParentNumber(group_type_occurrence,
               current_content_units);

         // Create the missing Transfer Object Content Units
         while (required_tranfer_object_number > content_units.size())
         {
            ContentUnit content_unit = new ContentUnit();
            content_unit.setId(
                  descriptor.getIdentification().getDescriptorID());
            content_unit.setTextInfo(descriptor.getDescription().getTransferObjectTypeTitle());
            content_units.add(content_unit);
         }

         // Dispatch the resulting units in their corresponding Transfer
         // Object Content Units
         dispatchChildren(content_units, group_type_occurrence,
               current_content_units);

      } // Loop among top level group type

      // Return SIPs
      return content_units;

   } // getSips(TransferObjectTypeDescriptor)

   /**
    * @return the Content Units corresponding to a given group type.
    */
   public List<ContentUnit> getContentUnits(
         final TransferObjectGroupType group_type,
         final File base_directory)
   {
      // Prepare output list of Content Units (Transfer Object level)
      ArrayList<ContentUnit> content_units = new ArrayList<ContentUnit>();

      // Get collector associated to the current group type
      logger.info("Collecting files from \"" +
         base_directory.getName() + "\"");

      Collector collector = this.getCollector(group_type.getGroupTypeID());
      
      String [] collected_files = null;
      
      if (collector != null)
      {
         collected_files = collector.getIncludedFiles(base_directory);
      }

      if ((collected_files == null) || (collected_files.length <= 0))
      {
         collected_files = new String[1];
         collected_files[0] = "";
      }

      // Loop among collected files
      for (String subdirectory_path : collected_files)
      {
         // Log loop
         logger.debug("Processing collected file: " + subdirectory_path);

         // Compute contextual base directory
         File context_base_directory = null;

         if (collector != null)
         {
            context_base_directory = new File(
                  collector.resolveBaseDirectory(base_directory) +
                  File.separator + subdirectory_path);
         }
         else
         {
            context_base_directory = new File(base_directory +
                  File.separator + subdirectory_path);
         }

         // Prepare a contextual list of content units (cannot be merged
         // with other collected directories
         ArrayList<ContentUnit> context_content_units =
            new ArrayList<ContentUnit>();

         // Loop among sub-group types
         if (group_type.getGroupType() != null)
         {
            for (TransferObjectGroupType subgroup_type : group_type
                  .getGroupType())
            {
               // Log loop
               logger.info("Processing Group Type \""
                     + subgroup_type.getGroupTypeID() + "\"...");

               // Get Content Units of the current Transfer Object group type
               List<ContentUnit> current_content_units =
                  this.getContentUnits(subgroup_type, context_base_directory);

               // Get number of returned Content Units
               long content_unit_number = 0;

               if (current_content_units != null)
               {
                  content_unit_number = current_content_units.size();
               }

               // Stop here if no Content Units is to process
               if (content_unit_number <= 0)
               {
                  continue;
               }

               // Check number of content unit minimum occurrence (if specified)
               OccurrenceType group_type_occurrence =
                  subgroup_type.getGroupTypeOccurrence();

               if ((group_type_occurrence != null)
                     && (group_type_occurrence.getMinOccurrence() != null))
               {
                  if (content_unit_number < group_type_occurrence
                        .getMinOccurrence().longValue())
                  {
                     logger.error("Too few Content Units collected from "
                           + "group type \""
                           + subgroup_type.getGroupTypeID()
                           + "\": a minimum of "
                           + group_type_occurrence.getMinOccurrence()
                                 .longValue() + " required (proceeding anyway)");
                  }
               }

               // Compute the number of Transfer Object Content Units required
               // to
               // hold the group type content units according the maximum
               // allowed
               // occurrences
               long required_tranfer_object_number =
                  computeRequiredParentNumber(group_type_occurrence,
                        current_content_units);

               // Create the missing Transfer Object Content Units
               while (required_tranfer_object_number > context_content_units
                     .size())
               {
                  ContentUnit content_unit = new ContentUnit();
                  content_unit.setId(group_type.getGroupTypeID());
                  content_unit.setTextInfo(group_type.getGroupTypeDescription());
                  content_unit.setInstancePath(subdirectory_path.replace(File.separatorChar, '/'));
                  context_content_units.add(content_unit);
               }

               // Dispatch the resulting units in their corresponding Transfer
               // Object Content Units
               dispatchChildren(context_content_units, group_type_occurrence,
                     current_content_units);
            } // Loop among sub-group types

         } // Case of sub-group types

         // Loop among Data Object types
         if (group_type.getDataObjectType() != null)
         {
            for (DataObjectType data_type : group_type.getDataObjectType())
            {
               // Log loop
               logger.info("Processing Data Object Type \""
                     + data_type.getDataObjectTypeID() + "\"...");

               // Get Content Units of the current Transfer Object group type
               List<ContentUnit> current_content_units =
                  this.getContentUnits(data_type, context_base_directory);

               // Get number of returned Content Units
               long content_unit_number = 0;

               if (current_content_units != null)
               {
                  content_unit_number = current_content_units.size();
               }

               // Stop here if no Content Units is to process
               if (content_unit_number <= 0)
               {
                  continue;
               }

               // Check number of content unit minimum occurrence (if specified)
               OccurrenceType data_type_occurrence =
                  data_type.getDataObjectTypeOccurrence();

               if ((data_type_occurrence != null)
                     && (data_type_occurrence.getMinOccurrence() != null))
               {
                  if (content_unit_number < data_type_occurrence
                        .getMinOccurrence().longValue())
                  {
                     logger.error("Too few Content Units collected from "
                           + "data object type \""
                           + data_type.getDataObjectTypeID()
                           + "\": a minimum of "
                           + data_type_occurrence.getMinOccurrence().longValue()
                           + " required (proceeding anyway)");
                  }
               }

               // Compute the number of Transfer Object Content Units required
               // to
               // hold the group type content units according the maximum
               // allowed
               // occurrences
               long required_tranfer_object_number =
                  computeRequiredParentNumber(data_type_occurrence,
                        current_content_units);

               // Create the missing Transfer Object Content Units
               while (required_tranfer_object_number > context_content_units.size())
               {
                  ContentUnit content_unit = new ContentUnit();
                  content_unit.setId(group_type.getGroupTypeID());
                  content_unit.setTextInfo(group_type.getGroupTypeDescription());
                  content_unit.setInstancePath(subdirectory_path.replace(File.separatorChar, '/'));
                  context_content_units.add(content_unit);
               }

               // Dispatch the resulting units in their corresponding Transfer
               // Object Content Units
               dispatchChildren(context_content_units, data_type_occurrence,
                     current_content_units);

            } // Loop among data object types
         } // Case of data object types

         // Append contextual content units to the global ones
         content_units.addAll(context_content_units);

      } // Loop among collected files

      // Return built Content Units
      return content_units;

   } // End getContentUnits(TransferObjectGroupType)

   /**
    * @return the Content Units corresponding to a given data object type.
    */
   public List<ContentUnit> getContentUnits(final DataObjectType data_type,
         final File base_directory)
   {
      // Prepare output list of Content Units (Transfer Object level)
      ArrayList<ContentUnit> content_units = new ArrayList<ContentUnit>();

      // Get collector associated to the current group type
      Collector collector = this.getCollector(data_type.getDataObjectTypeID());

      String [] collected_files = null;
      
      if (collector != null)
      {
         logger.info("Collecting files from \"" +
            collector.resolveBaseDirectory(
                  base_directory.getAbsoluteFile()).getName() + "\"");
         collected_files = collector.getIncludedFiles(base_directory);
      }

      if ((collected_files == null) || (collected_files.length <= 0))
      {
         collected_files = new String[1];
         collected_files[0] = "";
      }

      // Get maximum number of occurrences
      long max_occurrence = 1;

      OccurrenceType file_occurrence =
            data_type.getDataObjectTypeFileOccurrence();

      if (file_occurrence != null)
      {
         if (file_occurrence.getMaxOccurrence() != null)
         {
            max_occurrence = file_occurrence.getMaxOccurrence().longValue();
         }
         else if (file_occurrence.getMaxUnknown() != null)
         {
            max_occurrence = -1;
         }
      }

      ContentUnit content_unit = null;

      // Loop among collected files
      for (String file_path : collected_files)
      {
         // Log loop
         logger.debug("Processing collected file: \"" + file_path +
               "\"...");

         // Get file abstract absolute path
         File file = null;

         if (collector != null)
         {
            file = new File(
                  collector.resolveBaseDirectory(base_directory) +
                  File.separator + file_path);
         }
         else
         {
            file = new File(base_directory.getAbsolutePath() +
                  File.separator + file_path);
         }

         // Creates a CU if none exists or if one is necessary for each DO
         if ((content_unit == null) ||
             ((max_occurrence > 0) &&
              (content_unit.getDataObjectFiles() != null) &&
              (content_unit.getDataObjectFiles().size() >= max_occurrence)))
         {
            content_unit = new ContentUnit();

            content_unit.setId(data_type.getDataObjectTypeID());
            content_unit.setTextInfo(data_type.getDataObjectTypeDescription());

            content_units.add(content_unit);
         }
         
         content_unit.addDataObjectFile(new DataObjectFile(file,
               file_path.replace(File.separatorChar, '/')));
      }

      return content_units;

   } // End getContentUnits(DataObjectType)
   
   
   @Override
   public String toString()
   {
      String message = "";
      
      for (TransferObjectTypeDescriptor descriptor :
              this.getTypeDescriptors())
      {
         message += "Transfer Object Type: \"" +
            descriptor.getIdentification().getDescriptorID() + "\"\n";

         message += toStringGroup(descriptor.getGroupType(), "   ");
      }
      
      return message;
   }

   private static String toStringGroup(final List<TransferObjectGroupType> group_types,
         final String indent)
   {
      String message = "";
      for (TransferObjectGroupType group_type : group_types)
      {
         message += indent + "Group Type: \"" + group_type.getGroupTypeID()
               + "\"\n";
         
         if (group_type.getDataObjectType() != null)
         {
            message += toStringDataObject(group_type.getDataObjectType(),
                  indent + "   ");
         }

         message += toStringGroup(group_type.getGroupType(), indent + "   ");
      }
      return message;
   }

   private static String toStringDataObject(
         final List<DataObjectType> data_object_types, final String indent)
   {
      String message = "";
      for (DataObjectType data_object_type : data_object_types)
      {
         message +=
            indent + "Data Object Type: \""
                  + data_object_type.getDataObjectTypeID() + "\"\n";
      }
      return message;
   }

} // End Project class
