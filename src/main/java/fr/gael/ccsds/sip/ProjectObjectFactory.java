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
