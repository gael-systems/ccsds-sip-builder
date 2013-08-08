package fr.gael.ccsds.sip;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.commons.compress.utils.IOUtils;

import esa.xfdu.header.Extension;
import esa.xfdu.object.data.ByteStream;
import esa.xfdu.object.data.DataObject;
import esa.xfdu.object.data.DefaultDataObject;
import esa.xfdu.object.data.FileContent;
import esa.xfdu.object.data.FileLocation;
import esa.xfdu.object.metadata.Reference;

/**
 * A class denoting a content unit of a SIP information package map section.
 * Content Unit may have children Content Units.
 *
 * @see {@link Sip} class
 */
public class ContentUnit extends Vector<ContentUnit>
{
   /**
    * Identifier of this Content Unit
    */
   private String id;
   
   private String textInfo;
   
   private String typeId;

   private List<DataObjectFile> dataObjectFiles = null;
   
   private String instancePath;
   
   private ContentUnit parent = null;

   @Override
   public boolean add(ContentUnit unit)
   {
      unit.setParent(this);
      return super.add(unit);
   }

   /**
    * @return the parent
    */
   public ContentUnit getParent()
   {
      return parent;
   }

   /**
    * @param parent the parent to set
    */
   public void setParent(ContentUnit parent)
   {
      this.parent = parent;
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
    * @return the textInfo
    */
   public String getTextInfo()
   {
      return textInfo;
   }

   /**
    * @param textInfo the textInfo to set
    */
   public void setTextInfo(String textInfo)
   {
      this.textInfo = textInfo;
   }

   /**
    * @return the typeId
    */
   public String getTypeId()
   {
      return typeId;
   }

   /**
    * @param typeId the typeId to set
    */
   public void setTypeId(String typeId)
   {
      this.typeId = typeId;
   }

   /**
    * @return the dataObjectFile
    */
   public List<DataObjectFile> getDataObjectFiles()
   {
      return dataObjectFiles;
   }

   /**
    * @param dataObjectFile the dataObjectFile to set
    */
   public void addDataObjectFile(DataObjectFile dataObjectFile)
   {
      if (dataObjectFile == null)
      {
         return;
      }

      if (this.dataObjectFiles == null)
      {
         this.dataObjectFiles = new Vector<DataObjectFile>();
      }

      this.dataObjectFiles.add(dataObjectFile);
   }

   /**
    * @return the instancePath
    */
   public String getInstancePath()
   {
      return instancePath;
   }

   public String getPackageRelativePath()
   {
      String path = null;

      if (this.getParent() != null)
      {
         String parent_path = this.getParent().getPackageRelativePath();
         
         if (parent_path != null)
         {
            path = parent_path;
         }
      }

      if ((this.getInstancePath() != null) &&
          (this.getInstancePath().length() > 0))
      {
         if ((path != null) && (path.length() > 0))
         {
            path += "/" + this.getInstancePath();
         }
         else
         {
            path = this.getInstancePath();
         }
      }

      return path;
   }

   /**
    * @param instancePath the instancePath to set
    */
   public void setInstancePath(String parentRelativePath)
   {
      this.instancePath = parentRelativePath;
   }

   public ContentUnit getRoot()
   {
      if (this.getParent() != null)
      {
         return this.getParent().getRoot();
      }
      
      return this;
   }

   public esa.xfdu.map.ContentUnit toXfduContentUnit(IndexManager index_manager,
         List<DataObject> data_objects, String package_path)
   {
      //String current_id = index_manager.getNextId("CU-" + this.getId());

      // TODO: ContentUnit ID and this.getTextInfo() should be optional
      // set from a parameter
      esa.xfdu.map.ContentUnit xfdu_unit =
         new esa.xfdu.map.ContentUnit(null, null, null,
               null, null, null, null, null);

      Extension extension = null;

      if ((this.dataObjectFiles == null) || (this.dataObjectFiles.size() <= 0))
      {
         if (this.getParent() == null)
         {
            extension =
               new Extension(
                     "<sipTransferObject xmlns=\"urn:ccsds:schema:pais:1\">\n"
                           + "   <descriptorID xmlns=\"\">"
                           + this.getId()
                           + "</descriptorID>\n"
                           + "   <transferObjectID xmlns=\"\">"
                           + index_manager.getNextId(this.getId())
                           + "</transferObjectID>\n" + "</sipTransferObject>\n");
            // + (stot.getLastTransferObjectFlag() != null ?
            // ("   <lastTransferObjectFlag xmlns=\"\">"
            // + stot.getLastTransferObjectFlag() +
            // "</lastTransferObjectFlag>\n")
            // : "")
            // + (stot.getReplacementTransferObjectID() != null ?
            // ("   <replacementTransferObjectID xmlns=\"\">"
            // + stot.getReplacementTransferObjectID() +
            // "</replacementTransferObjectID>\n")
            // : "") + "</sipTransferObject>\n");
         }
         else
         {
            extension =
               new Extension(
                     "<sipTransferObjectGroup xmlns=\"urn:ccsds:schema:pais:1\">\n"
                           + (this.getId() != null ? "   <associatedDescriptorGroupTypeID xmlns=\"\">"
                                 + this.getId()
                                 + "</associatedDescriptorGroupTypeID>\n"
                                 : "")
                           + (this.getInstancePath() != null ? "   <transferObjectGroupInstanceName xmlns=\"\">"
                                 + this.getInstancePath()
                                 + "</transferObjectGroupInstanceName>\n"
                                 : "") + "</sipTransferObjectGroup>\n");
         }
      }
      else
      {
         extension =
            new Extension(
                  "<sipDataObject xmlns=\"urn:ccsds:schema:pais:1\">\n"
                        + (this.getId() != null ? "   <associatedDescriptorDataID xmlns=\"\">"
                              + this.getId() + "</associatedDescriptorDataID>\n"
                              : "") + "</sipDataObject>\n");
         // + (sdot.getDataObjectPreservationName() != null ?
         // "   <dataObjectPreservationName xmlns=\"\">"
         // + sdot.getDataObjectPreservationName()
         // + "</dataObjectPreservationName>\n"
         // : "")
         // + "</sipDataObject>\n");
      }

      if (extension != null)
      {
         xfdu_unit.setExtension(extension);
      }

      for (ContentUnit child_unit : this)
      {
         xfdu_unit.addChild(child_unit.toXfduContentUnit(index_manager,
               data_objects, package_path));
      }

      if ((this.dataObjectFiles != null) && (this.dataObjectFiles.size() > 0))
      {
         for (DataObjectFile current_file : this.dataObjectFiles)
         {

            // Get size
            long total_length = current_file.getFile().length();

            // Create the Data Object
            DataObject data_object =
               new DefaultDataObject(index_manager.getNextId("DO-"
                     + this.getId()), null, null, total_length, null, "MD5");

            String output_file_path = "" + current_file.getInstancePath();

            String package_relpath = this.getPackageRelativePath();

            if ((package_relpath != null) && (package_relpath.length() > 0))
            {
               output_file_path = package_relpath + "/" + output_file_path;
            }

            output_file_path = output_file_path.replace(File.separatorChar,
                  '/');

            final Reference reference =
               new Reference(null, "URL", null, null, null, package_path);

            reference.setHref("file:" + output_file_path);

            final FileLocation floc = new FileLocation(reference);
            final List<FileLocation> flocs = new ArrayList<FileLocation>();
            flocs.add(floc);

            final ByteStream bs = new ByteStream((FileContent) null, // fcontent
                  null, // identifier
                  null, // mime_type
                  total_length, // size
                  null, // checksum
                  "MD5"); // checksum_type);

            bs.setFileLocation2(flocs);
            data_object.getByteStreams().add(bs);

            // COpying file
            // TODO: is this the right place ???
            File output_file = new File(package_path, output_file_path);
            File output_dir = output_file.getParentFile();
            output_dir.mkdirs();

            try
            {
               FileInputStream input_stream = new FileInputStream(
                  current_file.getFile());
               FileOutputStream output_stream =
                     new FileOutputStream(output_file);

               IOUtils.copy(input_stream, output_stream);

               input_stream.close();
               output_stream.close();
            }
            catch (FileNotFoundException e)
            {
               // TODO Auto-generated catch block
               e.printStackTrace();
            }
            catch (IOException e)
            {
               // TODO Auto-generated catch block
               e.printStackTrace();
            }

            // Reference Data Object from the Content Unit
            xfdu_unit.addDataObject(data_object);

            // Add the Data Object to the output collector
            data_objects.add(data_object);
         }
      }

      // Return built XFDU content unit
      return xfdu_unit;
   }

   @Override
   public String toString()
   {
      String message = "ContentUnit: \"" + this.getId() + "\"";

      int child_count = 0;
      for (ContentUnit content_unit : this)
      {
         message += "\n" + (child_count++) + content_unit.toString();
      }
if (this.size() <= 0) message += " (no child)";
message += "\n (End \"" + this.getId() + "\")";
      return message;
   }

} // End ContentUInit class
