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

import fr.gael.ccsds.sip.xml.ObjectFactory;

/**
 * A customization of the generated JAXB {@link ObjectFactory}.
 */
public class ProjectObjectFactory extends ObjectFactory
{
   public ProjectObjectFactory()
   {
      // TODO Auto-generated constructor stub
   }

   /**
    * Create an instance of {@link fr.gael.ccsds.sip.xml.Project }
    * which is actually an instance of {@link Project} class.
    */
   @Override
   public fr.gael.ccsds.sip.xml.Project createProject()
   {
      return new Project();
   }

}
