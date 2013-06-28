/**
 * ESA SIP Builder
 * Copyright (C) 2012, 2013 European Space Agency (ESA)
 * Copyright (C) 2012, 2013 GAEL Systems
 * GNU Lesser General Public License (LGPL)
 * 
 * This file is part of ESA SIP Builder software suite.
 * 
 * ESA SIP Builder is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Data Request Broker is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.gael.ccsds.sip.archive;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.apache.commons.compress.utils.IOUtils;

public class DirectoryArchiveManager implements ArchiveManager
{
   @Override
   public File copy(final File source, final File destination, final String name)
         throws Exception
   {
      // Concatenate destination directory to the expected directory path name.
      final File dst = new File(destination, name);
      // Create dirs if not exists
      if (!dst.getParentFile().exists())
      {
         dst.getParentFile().mkdirs();
      }

      if (!source.isDirectory())
      {
         final FileInputStream fis = new FileInputStream(source);
         final FileOutputStream fos = new FileOutputStream(dst);
         IOUtils.copy(fis, fos);

         fis.close();
         fos.close();
      }
      else
      {
         dst.mkdirs();
      }
      return dst;
   }

   @Override
   public String getFileExtension()
   {
      return "";
   }
}
