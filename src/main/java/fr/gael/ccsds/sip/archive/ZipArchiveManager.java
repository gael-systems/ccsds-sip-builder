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
package fr.gael.ccsds.sip.archive;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;

public class ZipArchiveManager implements ArchiveManager
{

   /**
    * Produces Zip compressed archive.
    */
   @Override
   public File copy(final File src, final File zip_file, final String dst)
         throws Exception
   {
      if (zip_file.exists())
      {
         final FileInputStream fis = new FileInputStream(zip_file);
         final ZipArchiveInputStream zis = new ZipArchiveInputStream(fis);

         final File tempFile = File.createTempFile("updateZip", "zip");
         final FileOutputStream fos = new FileOutputStream(tempFile);
         final ZipArchiveOutputStream zos = new ZipArchiveOutputStream(fos);

         // copy the existing entries
         ZipArchiveEntry nextEntry;
         while ((nextEntry = zis.getNextZipEntry()) != null)
         {
            zos.putArchiveEntry(nextEntry);
            IOUtils.copy(zis, zos);
            zos.closeArchiveEntry();
         }

         // create the new entry
         final ZipArchiveEntry entry = new ZipArchiveEntry(src, dst);
         entry.setSize(src.length());
         zos.putArchiveEntry(entry);
         final FileInputStream sfis = new FileInputStream(src);
         IOUtils.copy(sfis, zos);
         sfis.close();
         zos.closeArchiveEntry();

         zos.finish();
         zis.close();
         fis.close();
         zos.close();

         // Rename the new file over the old
         boolean status = zip_file.delete();
         File saved_tempFile = tempFile;
         status = tempFile.renameTo(zip_file);

        // Copy the new file over the old if the renaming failed
        if (!status)
        {
           final FileInputStream tfis = new FileInputStream(saved_tempFile);
           final FileOutputStream tfos = new FileOutputStream(zip_file);

           final byte[] buf = new byte[1024];
           int i = 0;

           while ((i = tfis.read(buf)) != -1)
           {
              tfos.write(buf, 0, i);
           }

           tfis.close();
           tfos.close();
           
           saved_tempFile.delete();
        }

         return zip_file;

      }
      else
      {
         final FileOutputStream fos = new FileOutputStream(zip_file);
         final ZipArchiveOutputStream zos = new ZipArchiveOutputStream(fos);

         final ZipArchiveEntry entry = new ZipArchiveEntry(src, dst);
         entry.setSize(src.length());
         zos.putArchiveEntry(entry);

         final FileInputStream sfis = new FileInputStream(src);
         IOUtils.copy(sfis, zos);
         sfis.close();

         zos.closeArchiveEntry();

         zos.finish();
         zos.close();
         fos.close();
      }
      return zip_file;
   }

   @Override
   public String getFileExtension()
   {
      return ".zip";
   }

}
