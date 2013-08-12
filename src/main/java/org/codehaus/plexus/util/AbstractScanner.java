/**
 * Copyright The Codehaus Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codehaus.plexus.util;

import java.io.File;

public abstract class AbstractScanner implements Scanner
{
   /**
    * Patterns which should be excluded by default, like SCM files
    * <ul>
    * <li>Misc: &#42;&#42;/&#42;~, &#42;&#42;/#&#42;#, &#42;&#42;/.#&#42;,
    * &#42;&#42;/%&#42;%, &#42;&#42;/._&#42;</li>
    * <li>CVS: &#42;&#42;/CVS, &#42;&#42;/CVS/&#42;&#42;, &#42;&#42;/.cvsignore</li>
    * <li>RCS: &#42;&#42;/RCS, &#42;&#42;/RCS/&#42;&#42;</li>
    * <li>SCCS: &#42;&#42;/SCCS, &#42;&#42;/SCCS/&#42;&#42;</li>
    * <li>VSSercer: &#42;&#42;/vssver.scc</li>
    * <li>SVN: &#42;&#42;/.svn, &#42;&#42;/.svn/&#42;&#42;</li>
    * <li>GNU: &#42;&#42;/.arch-ids, &#42;&#42;/.arch-ids/&#42;&#42;</li>
    * <li>Bazaar: &#42;&#42;/.bzr, &#42;&#42;/.bzr/&#42;&#42;</li>
    * <li>SurroundSCM: &#42;&#42;/.MySCMServerInfo</li>
    * <li>Mac: &#42;&#42;/.DS_Store</li>
    * <li>Serena Dimension: &#42;&#42;/.metadata,
    * &#42;&#42;/.metadata/&#42;&#42;</li>
    * <li>Mercurial: &#42;&#42;/.hg, &#42;&#42;/.hg/&#42;&#42;</li>
    * <li>GIT: &#42;&#42;/.git, &#42;&#42;/.git/&#42;&#42;</li>
    * <li>Bitkeeper: &#42;&#42;/BitKeeper, &#42;&#42;/BitKeeper/&#42;&#42;,
    * &#42;&#42;/ChangeSet, &#42;&#42;/ChangeSet/&#42;&#42;</li>
    * <li>Darcs: &#42;&#42;/_darcs, &#42;&#42;/_darcs/&#42;&#42;,
    * &#42;&#42;/.darcsrepo,
    * &#42;&#42;/.darcsrepo/&#42;&#42;&#42;&#42;/-darcs-backup&#42;,
    * &#42;&#42;/.darcs-temp-mail
    * </ul>
    * 
    * @see #addDefaultExcludes()
    */
   public static final String[] DEFAULTEXCLUDES = {
         // Miscellaneous typical temporary files
         "**/*~", "**/#*#", "**/.#*", "**/%*%", "**/._*",

         // CVS
         "**/CVS", "**/CVS/**", "**/.cvsignore",

         // RCS
         "**/RCS", "**/RCS/**",

         // SCCS
         "**/SCCS", "**/SCCS/**",

         // Visual SourceSafe
         "**/vssver.scc",

         // Subversion
         "**/.svn", "**/.svn/**",

         // Arch
         "**/.arch-ids", "**/.arch-ids/**",

         // Bazaar
         "**/.bzr", "**/.bzr/**",

         // SurroundSCM
         "**/.MySCMServerInfo",

         // Mac
         "**/.DS_Store",

         // Serena Dimensions Version 10
         "**/.metadata", "**/.metadata/**",

         // Mercurial
         "**/.hg", "**/.hg/**",

         // git
         "**/.git", "**/.git/**",

         // BitKeeper
         "**/BitKeeper", "**/BitKeeper/**", "**/ChangeSet", "**/ChangeSet/**",

         // darcs
         "**/_darcs", "**/_darcs/**", "**/.darcsrepo", "**/.darcsrepo/**",
         "**/-darcs-backup*", "**/.darcs-temp-mail" };

   /** The patterns for the files to be included. */
   protected String[] includes;

   /** The patterns for the files to be excluded. */
   protected String[] excludes;

   /**
    * Whether or not the file system should be treated as a case sensitive one.
    */
   protected boolean isCaseSensitive = true;

   /**
    * Sets whether or not the file system should be regarded as case sensitive.
    * 
    * @param isCaseSensitive whether or not the file system should be regarded
    *           as a case sensitive one
    */
   public void setCaseSensitive(final boolean isCaseSensitive)
   {
      this.isCaseSensitive = isCaseSensitive;
   }

   /**
    * Tests whether or not a given path matches the start of a given pattern up
    * to the first "**".
    * <p>
    * This is not a general purpose test and should only be used if you can live
    * with false positives. For example, <code>pattern=**\a</code> and
    * <code>str=b</code> will yield <code>true</code>.
    * 
    * @param pattern The pattern to match against. Must not be <code>null</code>
    *           .
    * @param str The path to match, as a String. Must not be <code>null</code>.
    * @return whether or not a given path matches the start of a given pattern up
    *         to the first "**".
    */
   protected static boolean matchPatternStart(final String pattern,
         final String str)
   {
      return SelectorUtils.matchPatternStart(pattern, str);
   }

   /**
    * Tests whether or not a given path matches the start of a given pattern up
    * to the first "**".
    * <p>
    * This is not a general purpose test and should only be used if you can live
    * with false positives. For example, <code>pattern=**\a</code> and
    * <code>str=b</code> will yield <code>true</code>.
    * 
    * @param pattern The pattern to match against. Must not be <code>null</code>
    *           .
    * @param str The path to match, as a String. Must not be <code>null</code>.
    * @param isCaseSensitive Whether or not matching should be performed case
    *           sensitively.
    * @return whether or not a given path matches the start of a given pattern up
    *         to the first "**".
    */
   protected static boolean matchPatternStart(final String pattern,
         final String str, final boolean isCaseSensitive)
   {
      return SelectorUtils.matchPatternStart(pattern, str, isCaseSensitive);
   }

   /**
    * Tests whether or not a given path matches a given pattern.
    * 
    * @param pattern The pattern to match against. Must not be <code>null</code>
    *           .
    * @param str The path to match, as a String. Must not be <code>null</code>.
    * @return <code>true</code> if the pattern matches against the string, or
    *         <code>false</code> otherwise.
    */
   protected static boolean matchPath(final String pattern, final String str)
   {
      return SelectorUtils.matchPath(pattern, str);
   }

   /**
    * Tests whether or not a given path matches a given pattern.
    * 
    * @param pattern The pattern to match against. Must not be <code>null</code>
    *           .
    * @param str The path to match, as a String. Must not be <code>null</code>.
    * @param isCaseSensitive Whether or not matching should be performed case
    *           sensitively.
    * @return <code>true</code> if the pattern matches against the string, or
    *         <code>false</code> otherwise.
    */
   protected static boolean matchPath(final String pattern, final String str,
         final boolean isCaseSensitive)
   {
      return SelectorUtils.matchPath(pattern, str, isCaseSensitive);
   }

   /**
    * Tests whether or not a string matches against a pattern. The pattern may
    * contain two special characters:<br>
    * '*' means zero or more characters<br>
    * '?' means one and only one character
    * 
    * @param pattern The pattern to match against. Must not be <code>null</code>
    *           .
    * @param str The string which must be matched against the pattern. Must not
    *           be <code>null</code>.
    * @return <code>true</code> if the string matches against the pattern, or
    *         <code>false</code> otherwise.
    */
   public static boolean match(final String pattern, final String str)
   {
      return SelectorUtils.match(pattern, str);
   }

   /**
    * Tests whether or not a string matches against a pattern. The pattern may
    * contain two special characters:<br>
    * '*' means zero or more characters<br>
    * '?' means one and only one character
    * 
    * @param pattern The pattern to match against. Must not be <code>null</code>
    *           .
    * @param str The string which must be matched against the pattern. Must not
    *           be <code>null</code>.
    * @param isCaseSensitive Whether or not matching should be performed case
    *           sensitively.
    * @return <code>true</code> if the string matches against the pattern, or
    *         <code>false</code> otherwise.
    */
   protected static boolean match(final String pattern, final String str,
         final boolean isCaseSensitive)
   {
      return SelectorUtils.match(pattern, str, isCaseSensitive);
   }

   /**
    * Sets the list of include patterns to use. All '/' and '\' characters are
    * replaced by <code>File.separatorChar</code>, so the separator used need
    * not match <code>File.separatorChar</code>.
    * <p>
    * When a pattern ends with a '/' or '\', "**" is appended.
    * 
    * @param includes A list of include patterns. May be <code>null</code>,
    *           indicating that all files should be included. If a non-
    *           <code>null</code> list is given, all elements must be non-
    *           <code>null</code>.
    */
   public void setIncludes(final String[] includes)
   {
      if (includes == null)
      {
         this.includes = null;
      }
      else
      {
         this.includes = new String[includes.length];
         for (int i = 0; i < includes.length; i++)
         {
            this.includes[i] = this.normalizePattern(includes[i]);
         }
      }
   }

   /**
    * Sets the list of exclude patterns to use. All '/' and '\' characters are
    * replaced by <code>File.separatorChar</code>, so the separator used need
    * not match <code>File.separatorChar</code>.
    * <p>
    * When a pattern ends with a '/' or '\', "**" is appended.
    * 
    * @param excludes A list of exclude patterns. May be <code>null</code>,
    *           indicating that no files should be excluded. If a non-
    *           <code>null</code> list is given, all elements must be non-
    *           <code>null</code>.
    */
   public void setExcludes(final String[] excludes)
   {
      if (excludes == null)
      {
         this.excludes = null;
      }
      else
      {
         this.excludes = new String[excludes.length];
         for (int i = 0; i < excludes.length; i++)
         {
            this.excludes[i] = this.normalizePattern(excludes[i]);
         }
      }
   }

   /**
    * Normalizes the pattern, e.g. converts forward and backward slashes to the
    * platform-specific file separator.
    * 
    * @param pattern The pattern to normalize, must not be <code>null</code>.
    * @return The normalized pattern, never <code>null</code>.
    */
   private String normalizePattern(String pattern)
   {
      pattern = pattern.trim();

      if (pattern.startsWith(SelectorUtils.REGEX_HANDLER_PREFIX))
      {
         pattern = StringUtils.replace(pattern, "\\\\", "/");
      }
      else
      {
         pattern = pattern.replace('\\', '/');

         if (pattern.endsWith("/"))
         {
            pattern += "**";
         }
      }

      return pattern;
   }

   /**
    * Tests whether or not a name matches against at least one include pattern.
    * 
    * @param name The name to match. Must not be <code>null</code>.
    * @return <code>true</code> when the name matches against at least one
    *         include pattern, or <code>false</code> otherwise.
    */
   protected boolean isIncluded(final String name)
   {
      for (int i = 0; i < this.includes.length; i++)
      {
         if (matchPath(this.includes[i], name, this.isCaseSensitive))
         {
            return true;
         }
      }
      return false;
   }

   /**
    * Tests whether or not a name matches the start of at least one include
    * pattern.
    * 
    * @param name The name to match. Must not be <code>null</code>.
    * @return <code>true</code> when the name matches against the start of at
    *         least one include pattern, or <code>false</code> otherwise.
    */
   protected boolean couldHoldIncluded(final String name)
   {
      for (int i = 0; i < this.includes.length; i++)
      {
         if (matchPatternStart(this.includes[i], name, this.isCaseSensitive))
         {
            return true;
         }
      }
      return false;
   }

   /**
    * Tests whether or not a name matches against at least one exclude pattern.
    * 
    * @param name The name to match. Must not be <code>null</code>.
    * @return <code>true</code> when the name matches against at least one
    *         exclude pattern, or <code>false</code> otherwise.
    */
   protected boolean isExcluded(final String name)
   {
      for (int i = 0; i < this.excludes.length; i++)
      {
         if (matchPath(this.excludes[i], name, this.isCaseSensitive))
         {
            return true;
         }
      }
      return false;
   }

   /**
    * Adds default exclusions to the current exclusions set.
    */
   public void addDefaultExcludes()
   {
      final int excludesLength =
         this.excludes == null ? 0 : this.excludes.length;
      String[] newExcludes;
      newExcludes = new String[excludesLength + DEFAULTEXCLUDES.length];
      if (excludesLength > 0)
      {
         System.arraycopy(this.excludes, 0, newExcludes, 0, excludesLength);
      }
      for (int i = 0; i < DEFAULTEXCLUDES.length; i++)
      {
         newExcludes[i + excludesLength] = DEFAULTEXCLUDES[i];
      }
      this.excludes = newExcludes;
   }

   protected void setupDefaultFilters()
   {
      if (this.includes == null)
      {
         // No includes supplied, so set it to 'matches all'
         this.includes = new String[1];
         this.includes[0] = "**";
      }
      if (this.excludes == null)
      {
         this.excludes = new String[0];
      }
   }
}