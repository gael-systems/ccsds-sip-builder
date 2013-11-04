SIP Builder v${project.version}
------------------

The goal of the SIP Builder is to generate XFDU/SIP packages from input data
files and in conformance to a set of descriptors and constraints files
following the Producer–Archive Interface Specification (PAIS) standard under
development of the Consultative Committee for Space Data Systems (CCSDS).

The SIP Builder is an Open Source software aiming at verifying parts of the
PAIS standard, at serving as PAIS implementation alternative required for
the CCSDS recommendation process and, as far as possible, providing the PAIS
users with a handy tool for approaching the standard. The SIP Builder is a
prototype that should not be considered as an operational software. It may
change drastically from a version to another without special notification.


SOURCE DISTRIBUTION STRUCTURE
-----------------------------

    |-> src                     <-- (source distribution only)
    |   |-> main
    |       |-> assembly        <-- Maven assembly descriptors
    |       |-> java            <-- Java sources
    |       |-> resources       <-- Resources to be included in the output Jar
    |
    |-> README.txt              <-- Hereby document
    |-> COPYING*                <-- GNU LGPL License


SOFTWARE REQUIREMENTS FOR API 
-----------------------------

   - Java JDK 6 or higher
     (http://www.oracle.com/technetwork/java/javase/downloads)
   - Maven 3.0 or higher
     (http://www.maven.apache.org)
   - Internet connection if third party dependencies are not available in
     Maven local repository.


SOURCES INSTALLATION AND BUILDING
---------------------------------

1. Download API sources:

   sip-builder-[M].[m].[b]-src.zip

2. Extract archive content to the installation directory.

3. Move to the extracted directory in a command prompt environment e.g.
   DOS, or any UNIX terminal.

4. Build the software binaries and assemblies with the following command:

   mvn package

The previous command are built in a "target" sub-directory. For further
information on Maven building commands see Maven (http://maven.apache.org)


USE AND COPYRIGHT
-----------------
SIP Builder
Copyright (C) 2012,2013 GAEL Systems
Copyright (C) 2012,2013 ESA

SIP Builder software is licensed under the terms and conditions laid down
from GNU Lesser General Public License (LGPL) v3 license which are reminded
in COPYING.LESSER file included in hereby distribution. Permission to use,
copy, modify, distribute, and sell this software and its documentation for
any purpose is hereby granted without fee, provided that (i) the above
copyright notices and the GNU LGPL permission notice appear in all copies of
the software and related documentation, and (ii) the names of GAEL Systems
and ESA may not be used in any advertising or publicity relating to the
software without the specific, prior written permission of GAEL Systems or
ESA. 

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
