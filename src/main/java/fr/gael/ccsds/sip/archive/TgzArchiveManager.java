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

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.compress.utils.IOUtils;

public class TgzArchiveManager implements ArchiveManager
{
   /*
    * TGZ archive manager
    */
   @Override
   public File copy(final File src, final File tar_file, final String dst)
         throws Exception
   {
      final ArchiveStreamFactory asf = new ArchiveStreamFactory();
      final CompressorStreamFactory csf = new CompressorStreamFactory();

      // Case of tar already exist: all the entries must be copied..
      if (tar_file.exists())
      {
         final FileInputStream fis = new FileInputStream(tar_file);
         final CompressorInputStream cis =
            csf.createCompressorInputStream(CompressorStreamFactory.GZIP, fis);
         final ArchiveInputStream ais =
            asf.createArchiveInputStream(ArchiveStreamFactory.TAR, cis);

         final File tempFile = File.createTempFile("updateTar", "tar");
         final FileOutputStream fos = new FileOutputStream(tempFile);
         final CompressorOutputStream cos =
            csf.createCompressorOutputStream(CompressorStreamFactory.GZIP, fos);
         final ArchiveOutputStream aos =
            asf.createArchiveOutputStream(ArchiveStreamFactory.TAR, cos);

         // copy the existing entries
         ArchiveEntry nextEntry;
         while ((nextEntry = ais.getNextEntry()) != null)
         {
            aos.putArchiveEntry(nextEntry);
            IOUtils.copy(ais, aos);
            aos.closeArchiveEntry();
         }

         // create the new entry
         final TarArchiveEntry entry = new TarArchiveEntry(src, dst);
         entry.setSize(src.length());
         aos.putArchiveEntry(entry);
         final FileInputStream sfis = new FileInputStream(src);
         IOUtils.copy(sfis, aos);
         sfis.close();
         aos.closeArchiveEntry();

         aos.finish();
         ais.close();
         aos.close();
         fis.close();

         // copies the new file over the old
         tar_file.delete();
         tempFile.renameTo(tar_file);
         return tar_file;
      }
      else
      {
         final FileOutputStream fos = new FileOutputStream(tar_file);
         final CompressorOutputStream cos =
            csf.createCompressorOutputStream(CompressorStreamFactory.GZIP, fos);
         final ArchiveOutputStream aos =
            asf.createArchiveOutputStream(ArchiveStreamFactory.TAR, cos);

         // create the new entry
         final TarArchiveEntry entry = new TarArchiveEntry(src, dst);
         entry.setSize(src.length());
         aos.putArchiveEntry(entry);
         final FileInputStream sfis = new FileInputStream(src);
         IOUtils.copy(sfis, aos);
         sfis.close();
         aos.closeArchiveEntry();

         aos.finish();
         aos.close();
         fos.close();
      }
      return tar_file;
   }

   @Override
   public String getFileExtension()
   {
      return ".tar.gz";
   }

}
