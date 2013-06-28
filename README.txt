ESA SIP Builder
---------------

The Data Request Broker - DRB API(r) is an application programming interface for
reading, writing and processing heterogeneous data. DRB API(r) is a software
abstract layer that helps developers programming applications as independently
as possible from the way data are encoded within files. Indeed, DRB API(r) is 
based on a uniform data model that makes the handling of supported data formats 
much easier. 

Programmed in Java and embedding W3C standards, DRB API(r) provides a portable 
solution that remains powerful even on large data collections. Initially developed 
and well-tried for accessing satellite imagery for European Space Agency, DRB API(r)
is the result of more than 15 years of expertise and know-how in data access 
programming. Thus, DRB API(r) provides a complete set of tools to solve simple and
complex data access issues with minimum engineering efforts.

DRB API(r) comes up with an XQuery engine that allows selecting and computing data
across targeted files. It also provides an XML Schema validation facility that runs 
over supported data. DRB API(r) embeds implementations for accessing local file
systems, HTTP and FTP remote servers and Jar, Tar or ZIP files. It also
includes implementations that accesses XML documents and binary files when
these last are described through XML Schema documents. Extra implementations
are available in DRB API(r) extensions or can be programmed by any developers.


DISTRIBUTION STRUCTURE
----------------------

    |-> docs                    <-- (self installer distribution only)
    |   |-> java                <-- Java API documentation
    |      |-> index.html       <-- Starting HTML page
    |
    |-> lib                     <-- (binary or self installer distribution only)
    |   |-> java                <-- DRB and dependent Java Jar archives
    |
    |-> src                     <-- (source distribution only)
    |   |-> main
    |       |-> java            <-- Java sources
    |       |-> javacc          <-- Java Compiler Compiler sources
    |       |-> javadoc         <-- Java Documentation Package Headers
    |       |-> resources       <-- Resources to be included in the output Jar
    |       |-> checkstyle      <-- Definitions for verifying coding standard
    |
    |-> README.txt              <-- Hereby document
    |-> COPYING*                <-- License contents


DISTRIBUTION REPOSITORY
-----------------------

DRB API software distributions are made available from http://www.gael.fr/drb
Web site.


SOFTWARE REQUIREMENTS FOR API 
-----------------------------

FOR API USERS 

   - Java 1.5.0 SDK (http://java.sun.com) or higher installed on target machine 

FOR API CONTRIBUTORS

   - Java 1.5.0 SDK (http://java.sun.com)
   - Maven 2.x (http://www.maven.apache.org)
   - Internet connection if building plugins are not available in Maven local
     repository. 



BINARY INSTALLATION
-------------------

1. Download API binaries:

   drb-[M]-[m]-[t]-bin.tar.gz  or  drb-[M]-[m]-[t]-bin.zip

2. Extract archive content to the installation directory e.g. /usr/local/ for
   UNIX systems or c:\Program Files\ for Windows, etc.

3. Test installation. 


SOURCES INSTALLATION AND BUILDING
---------------------------------

1. Download API sources:

   drb-[M]-[m]-[t]-src.tar.gz  or  drb-[M]-[m]-[t]-src.zip

2. Extract archive content to the installation directory e.g. your home directory.

3. Move to the installation home directory in a command prompt environment e.g.
   DOS, or any UNIX terminal.

4. Build API Jar archive with the following command:

   mvn package

The previous command should have built DRB as drb-SNAPSHOT.jar file in a
"target" sub-directory. For further information on Maven building commands see
Maven (http://maven.apache.org)


TEST API INSTALLATION
---------------------

1. Test the installation by running the following command:

   java -jar [DRB_INSTALLATION_HOME]/lib/java/drb-[M]-[m]-[t].jar --version

2. For using DRB, configure your CLASSPATH or your IDE to reference the following
   Java Jar archive:

   [DRB_INSTALLATION_HOME]/lib/java/drb-[M]-[m]-[t].jar

3. It is not necessary to reference the dependencies accompanying the DRB Jar
   since it references them automatically.


USE AND COPYRIGHT
-----------------
Data Request Broker - DRB API
Copyright (C) 2001,2002,2003,2004,2005,2006,2007,2008,2009 GAEL Consultant

Data Request Broker - DRB API software is licensed under the terms and
conditions laid down from GNU Lesser General Public License (LGPL) v3 license
which are reminded in COPYING.LESSER file included in hereby distribution.
Permission to use, copy, modify, distribute, and sell this software and its
documentation for any purpose is hereby granted without fee, provided that
(i) the above copyright notices and the GNU LGPL permission notice appear in
all copies of the software and related documentation, and (ii) the names of
GAEL Consultant may not be used in any advertising or publicity relating to the
software without the specific, prior written permission of GAEL Consultant. 

THE SOFTWARE IS PROVIDED "AS-IS" AND WITHOUT WARRANTY OF ANY KIND, EXPRESS,
IMPLIED OR OTHERWISE, INCLUDING WITHOUT LIMITATION, ANY WARRANTY OF
MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT SHALL GAEL
CONSULTANT BE LIABLE FOR ANY SPECIAL, INCIDENTAL, INDIRECT OR CONSEQUENTIAL
DAMAGES OF ANY KIND, OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA
OR PROFITS, WHETHER OR NOT ADVISED OF THE POSSIBILITY OF DAMAGE, AND ON ANY
THEORY OF LIABILITY, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
PERFORMANCE OF THIS SOFTWARE. 

Present distribution includes software developed at The Apache Software
Foundation (http://www.apache.org/):

- The Codehaus Foundation (org.codehaus.plexus.util)


CONTACT
------- 

GAEL Systems
25 rue Alfred Nobel,
Parc Descartes Nobel, 77420 Champs-sur-Marne, France
(tel) +33 1 64 73 99 55, (fax) +33 1 64 73 51 60
Contact: info@gael.fr
http://www.gael.fr
