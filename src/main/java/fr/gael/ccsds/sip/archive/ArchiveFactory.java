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

import fr.gael.ccsds.sip.SipBuilderException;

public class ArchiveFactory
{
   /**
    * Definition of packages.
    */
   public final static String PACKAGING_NONE = "none";
   public final static String PACKAGING_DIRECTORY = "dir";
   public final static String PACKAGING_TAR = "tar";
   public final static String PACKAGING_TGZ = "tgz";
   public final static String PACKAGING_ZIP = "zip";

   public static ArchiveManager open(final String type)
   {
      if (PACKAGING_DIRECTORY.equals(type))
      {
         return new DirectoryArchiveManager();
      }
      if (PACKAGING_TAR.equals(type))
      {
         return new TarArchiveManager();
      }
      if (PACKAGING_TGZ.equals(type))
      {
         return new TgzArchiveManager();
      }
      if (PACKAGING_ZIP.equals(type))
      {
         return new ZipArchiveManager();
      }
      if (PACKAGING_NONE.equals(type))
      {
         return new NoneArchiveManager();
      }

      throw new SipBuilderException("Unknown type \"" + type + "\".");

   }
}
