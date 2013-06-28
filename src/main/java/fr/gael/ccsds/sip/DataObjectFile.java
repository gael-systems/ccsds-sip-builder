package fr.gael.ccsds.sip;

import java.io.File;

public class DataObjectFile
{
   final File file;
   final String instancePath;

   public DataObjectFile(File input_file, String instance_path)
   {
      this.file = input_file;
      this.instancePath = instance_path;
   }

   /**
    * @return the file
    */
   public File getFile()
   {
      return file;
   }

   /**
    * @return the instancePath
    */
   public String getInstancePath()
   {
      return instancePath;
   }
}
