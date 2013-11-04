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

/**
 * @author pidancier
 */
public class NoneArchiveManager implements ArchiveManager
{

   /*
    * (non-Javadoc)
    * @see esa.pais.builder.archive.ArchiveManager#copy(java.io.File,
    * java.io.File, java.lang.String)
    */
   @Override
   public File copy(final File source, final File destination, final String name)
         throws Exception
   {
      return new File(destination, name);
   }

   /*
    * (non-Javadoc)
    * @see esa.pais.builder.archive.ArchiveManager#getFileExtension()
    */
   @Override
   public String getFileExtension()
   {
      return "";
   }

}
