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

public interface ArchiveManager
{
   /**
    * Copy source file into configuredf destination.
    * 
    * @param destination
    * @param source
    * @param name
    * @return the output file pointer.
    */
   public File copy(File source, File destination, String name) throws Exception;

   /**
    * Returns the expected extension attached to this kind of archive.
    * 
    * @return
    */
   public String getFileExtension();
}
