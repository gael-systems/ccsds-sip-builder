<?xml version="1.0" encoding="ISO-8859-1"?>

<!--
 ! ESA SIP Builder
 ! Copyright (C) 2012, 2013 European Space Agency (ESA)
 ! Copyright (C) 2012, 2013 GAEL Systems
 ! GNU Lesser General Public License (LGPL)
 !
 ! This file is part of ESA SIP Builder software suite.
 !
 ! ESA SIP Builder is free software: you can redistribute it and/or modify
 ! it under the terms of the GNU Lesser General Public License as published by
 ! the Free Software Foundation, either version 3 of the License, or
 ! (at your option) any later version.
 !
 ! Data Request Broker is distributed in the hope that it will be useful,
 ! but WITHOUT ANY WARRANTY; without even the implied warranty of
 ! MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 ! GNU Lesser General Public License for more details.
 !
 ! You should have received a copy of the GNU Lesser General Public License
 ! along with this program.  If not, see <http://www.gnu.org/licenses/>.
 -->

<!-- This stylesheet pre-processes TOD XML file in order to add some missing 
   informations. As a first implementation, the TOD xml are enrichied with XML-Schemas 
   that are usually missing in our sample cases. As schemas are stored into 
   the application software has been altered to retrieve these schemas into 
   the classpath if "classpath:" protocol is defined in the URL. -->
<xsl:stylesheet version="1.0"
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

   <xsl:output method="xml" version="1.0" encoding="ISO-8859-1"
      standalone="yes" indent="yes" />

   <!-- Check out these directories is case of schemas moved. -->
   <xsl:variable name="tod_path">
      <xsl:text>classpath:org/ccsds/tod/</xsl:text>
   </xsl:variable>

   <xsl:variable name="sip_path">
      <xsl:text>classpath:org/ccsds/sip/</xsl:text>
   </xsl:variable>

   <!-- Setup the schema names -->
   <xsl:variable name="tod_descriptor_path">
      <xsl:value-of
         select="concat($tod_path, 'descriptor_transfer_object.xsd')" />
   </xsl:variable>
   <xsl:variable name="tod_collection_path">
      <xsl:value-of select="concat($tod_path, 'descriptor_collection.xsd')" />
   </xsl:variable>

   <xsl:variable name="sip_contraints_path">
      <xsl:value-of select="concat($sip_path, 'sip_constraints.xsd')" />
   </xsl:variable>


   <xsl:template match="transferObjectTypeDescriptor">
      <transferObjectTypeDescriptor
         xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
         <xsl:attribute name="xsi:noNamespaceSchemaLocation">
        <xsl:value-of select="$tod_descriptor_path" />
     </xsl:attribute>
         <xsl:copy-of select="*" />
      </transferObjectTypeDescriptor>
   </xsl:template>

   <xsl:template match="collectionDescriptor">
      <collectionDescriptor xmlns:xs="http://www.w3.org/2001/XMLSchema"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
         <xsl:attribute name="xsi:noNamespaceSchemaLocation">
        <xsl:value-of select="$tod_collection_path" />
     </xsl:attribute>
         <xsl:copy-of select="*" />
      </collectionDescriptor>
   </xsl:template>

   <xsl:template match="sipConstraints">
      <sipConstraints xmlns:xs="http://www.w3.org/2001/XMLSchema"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
         <xsl:attribute name="xsi:noNamespaceSchemaLocation">
        <xsl:value-of select="$sip_contraints_path" />
     </xsl:attribute>
         <xsl:copy-of select="*" />
      </sipConstraints>
   </xsl:template>

</xsl:stylesheet>