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
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Pattern;

/**
 * <p>
 * This is a utility class used by selectors and DirectoryScanner. The
 * functionality more properly belongs just to selectors, but unfortunately
 * DirectoryScanner exposed these as protected methods. Thus we have to support
 * any subclasses of DirectoryScanner that may access these methods.
 * </p>
 * <p>
 * This is a Singleton.
 * </p>
 * 
 * @author Arnout J. Kuiper <a href="mailto:ajkuiper@wxs.nl">ajkuiper@wxs.nl</a>
 * @author Magesh Umasankar
 * @author <a href="mailto:bruce@callenish.com">Bruce Atherton</a>
 * @since 1.5
 * @version $Id$
 */
public final class SelectorUtils
{

   public static final String PATTERN_HANDLER_PREFIX = "[";

   public static final String PATTERN_HANDLER_SUFFIX = "]";

   public static final String REGEX_HANDLER_PREFIX = "%regex"
         + PATTERN_HANDLER_PREFIX;

   public static final String ANT_HANDLER_PREFIX = "%ant"
         + PATTERN_HANDLER_PREFIX;

   private static SelectorUtils instance = new SelectorUtils();

   /**
    * Private Constructor
    */
   private SelectorUtils()
   {
   }

   /**
    * Retrieves the manager of the Singleton.
    */
   public static SelectorUtils getInstance()
   {
      return instance;
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
   public static boolean matchPatternStart(final String pattern, final String str)
   {
      return matchPatternStart(pattern, str, true);
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
   public static boolean matchPatternStart(String pattern, final String str,
         final boolean isCaseSensitive)
   {
      if ((pattern.length() > (REGEX_HANDLER_PREFIX.length()
            + PATTERN_HANDLER_SUFFIX.length() + 1))
            && pattern.startsWith(REGEX_HANDLER_PREFIX)
            && pattern.endsWith(PATTERN_HANDLER_SUFFIX))
      {
         // FIXME: ICK! But we can't do partial matches for regex, so we have to
         // reserve judgement until we have
         // a file to deal with, or we can definitely say this is an
         // exclusion...
         return true;
      }
      else
      {
         if ((pattern.length() > (ANT_HANDLER_PREFIX.length()
               + PATTERN_HANDLER_SUFFIX.length() + 1))
               && pattern.startsWith(ANT_HANDLER_PREFIX)
               && pattern.endsWith(PATTERN_HANDLER_SUFFIX))
         {
            pattern =
               pattern.substring(ANT_HANDLER_PREFIX.length(), pattern.length()
                     - PATTERN_HANDLER_SUFFIX.length());
         }

         final String altStr = str.replace('\\', '/');

         return matchAntPathPatternStart(pattern, str, "/",
               isCaseSensitive)
               || matchAntPathPatternStart(pattern, altStr, "/", isCaseSensitive);
      }
   }

   private static boolean matchAntPathPatternStart(final String pattern,
         final String str, final String separator, final boolean isCaseSensitive)
   {
      // When str starts with a File.separator, pattern has to start with a
      // File.separator.
      // When pattern starts with a File.separator, str has to start with a
      // File.separator.
      if (str.startsWith(separator) != pattern.startsWith(separator))
      {
         return false;
      }

      final Vector patDirs = tokenizePath(pattern, separator);
      final Vector strDirs = tokenizePath(str, separator);

      int patIdxStart = 0;
      final int patIdxEnd = patDirs.size() - 1;
      int strIdxStart = 0;
      final int strIdxEnd = strDirs.size() - 1;

      // up to first '**'
      while ((patIdxStart <= patIdxEnd) && (strIdxStart <= strIdxEnd))
      {
         final String patDir = (String) patDirs.elementAt(patIdxStart);
         if (patDir.equals("**"))
         {
            break;
         }
         if (!match(patDir, (String) strDirs.elementAt(strIdxStart),
               isCaseSensitive))
         {
            return false;
         }
         patIdxStart++;
         strIdxStart++;
      }

      if (strIdxStart > strIdxEnd)
      {
         // String is exhausted
         return true;
      }
      else if (patIdxStart > patIdxEnd)
      {
         // String not exhausted, but pattern is. Failure.
         return false;
      }
      else
      {
         // pattern now holds ** while string is not exhausted
         // this will generate false positives but we can live with that.
         return true;
      }
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
   public static boolean matchPath(final String pattern, final String str)
   {
      return matchPath(pattern, str, true);
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
   public static boolean matchPath(String pattern, final String str,
         final boolean isCaseSensitive)
   {
      if ((pattern.length() > (REGEX_HANDLER_PREFIX.length()
            + PATTERN_HANDLER_SUFFIX.length() + 1))
            && pattern.startsWith(REGEX_HANDLER_PREFIX)
            && pattern.endsWith(PATTERN_HANDLER_SUFFIX))
      {
         pattern =
            pattern.substring(REGEX_HANDLER_PREFIX.length(), pattern.length()
                  - PATTERN_HANDLER_SUFFIX.length());

         return str.matches(pattern);
      }
      else
      {
         if ((pattern.length() > (ANT_HANDLER_PREFIX.length()
               + PATTERN_HANDLER_SUFFIX.length() + 1))
               && pattern.startsWith(ANT_HANDLER_PREFIX)
               && pattern.endsWith(PATTERN_HANDLER_SUFFIX))
         {
            pattern =
               pattern.substring(ANT_HANDLER_PREFIX.length(), pattern.length()
                     - PATTERN_HANDLER_SUFFIX.length());
         }

         return matchAntPathPattern(pattern, str, isCaseSensitive);
      }
   }

   private static boolean matchAntPathPattern(final String pattern,
         final String str, final boolean isCaseSensitive)
   {
      // When str starts with a File.separator, pattern has to start with a
      // File.separator.
      // When pattern starts with a File.separator, str has to start with a
      // File.separator.
      if (str.startsWith(File.separator) != pattern.startsWith(File.separator))
      {
         return false;
      }

      Vector patDirs = tokenizePath(pattern, File.separator);
      Vector strDirs = tokenizePath(str, File.separator);

      int patIdxStart = 0;
      int patIdxEnd = patDirs.size() - 1;
      int strIdxStart = 0;
      int strIdxEnd = strDirs.size() - 1;

      // up to first '**'
      while ((patIdxStart <= patIdxEnd) && (strIdxStart <= strIdxEnd))
      {
         final String patDir = (String) patDirs.elementAt(patIdxStart);
         if (patDir.equals("**"))
         {
            break;
         }
         if (!match(patDir, (String) strDirs.elementAt(strIdxStart),
               isCaseSensitive))
         {
            patDirs = null;
            strDirs = null;
            return false;
         }
         patIdxStart++;
         strIdxStart++;
      }
      if (strIdxStart > strIdxEnd)
      {
         // String is exhausted
         for (int i = patIdxStart; i <= patIdxEnd; i++)
         {
            if (!patDirs.elementAt(i).equals("**"))
            {
               patDirs = null;
               strDirs = null;
               return false;
            }
         }
         return true;
      }
      else
      {
         if (patIdxStart > patIdxEnd)
         {
            // String not exhausted, but pattern is. Failure.
            patDirs = null;
            strDirs = null;
            return false;
         }
      }

      // up to last '**'
      while ((patIdxStart <= patIdxEnd) && (strIdxStart <= strIdxEnd))
      {
         final String patDir = (String) patDirs.elementAt(patIdxEnd);
         if (patDir.equals("**"))
         {
            break;
         }
         if (!match(patDir, (String) strDirs.elementAt(strIdxEnd),
               isCaseSensitive))
         {
            patDirs = null;
            strDirs = null;
            return false;
         }
         patIdxEnd--;
         strIdxEnd--;
      }
      if (strIdxStart > strIdxEnd)
      {
         // String is exhausted
         for (int i = patIdxStart; i <= patIdxEnd; i++)
         {
            if (!patDirs.elementAt(i).equals("**"))
            {
               patDirs = null;
               strDirs = null;
               return false;
            }
         }
         return true;
      }

      while ((patIdxStart != patIdxEnd) && (strIdxStart <= strIdxEnd))
      {
         int patIdxTmp = -1;
         for (int i = patIdxStart + 1; i <= patIdxEnd; i++)
         {
            if (patDirs.elementAt(i).equals("**"))
            {
               patIdxTmp = i;
               break;
            }
         }
         if (patIdxTmp == (patIdxStart + 1))
         {
            // '**/**' situation, so skip one
            patIdxStart++;
            continue;
         }
         // Find the pattern between padIdxStart & padIdxTmp in str between
         // strIdxStart & strIdxEnd
         final int patLength = (patIdxTmp - patIdxStart - 1);
         final int strLength = ((strIdxEnd - strIdxStart) + 1);
         int foundIdx = -1;
         strLoop: for (int i = 0; i <= (strLength - patLength); i++)
         {
            for (int j = 0; j < patLength; j++)
            {
               final String subPat =
                  (String) patDirs.elementAt(patIdxStart + j + 1);
               final String subStr =
                  (String) strDirs.elementAt(strIdxStart + i + j);
               if (!match(subPat, subStr, isCaseSensitive))
               {
                  continue strLoop;
               }
            }

            foundIdx = strIdxStart + i;
            break;
         }

         if (foundIdx == -1)
         {
            patDirs = null;
            strDirs = null;
            return false;
         }

         patIdxStart = patIdxTmp;
         strIdxStart = foundIdx + patLength;
      }

      for (int i = patIdxStart; i <= patIdxEnd; i++)
      {
         if (!patDirs.elementAt(i).equals("**"))
         {
            patDirs = null;
            strDirs = null;
            return false;
         }
      }

      return true;
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
      return match(pattern, str, true);
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
   public static boolean old_match(final String pattern, final String str,
         final boolean isCaseSensitive)
   {
      final char[] patArr = pattern.toCharArray();
      final char[] strArr = str.toCharArray();
      int patIdxStart = 0;
      int patIdxEnd = patArr.length - 1;
      int strIdxStart = 0;
      int strIdxEnd = strArr.length - 1;
      char ch;

      boolean containsStar = false;
      for (int i = 0; i < patArr.length; i++)
      {
         if (patArr[i] == '*')
         {
            containsStar = true;
            break;
         }
      }

      if (!containsStar)
      {
         // No '*'s, so we make a shortcut
         if (patIdxEnd != strIdxEnd)
         {
            return false; // Pattern and string do not have the same size
         }
         for (int i = 0; i <= patIdxEnd; i++)
         {
            ch = patArr[i];
            if ((ch != '?') && !equals(ch, strArr[i], isCaseSensitive))
            {
               return false; // Character mismatch
            }
         }
         return true; // String matches against pattern
      }

      if (patIdxEnd == 0)
      {
         return true; // Pattern contains only '*', which matches anything
      }

      // Process characters before first star
      while (((ch = patArr[patIdxStart]) != '*') && (strIdxStart <= strIdxEnd))
      {
         if ((ch != '?') && !equals(ch, strArr[strIdxStart], isCaseSensitive))
         {
            return false; // Character mismatch
         }
         patIdxStart++;
         strIdxStart++;
      }
      if (strIdxStart > strIdxEnd)
      {
         // All characters in the string are used. Check if only '*'s are
         // left in the pattern. If so, we succeeded. Otherwise failure.
         for (int i = patIdxStart; i <= patIdxEnd; i++)
         {
            if (patArr[i] != '*')
            {
               return false;
            }
         }
         return true;
      }

      // Process characters after last star
      while (((ch = patArr[patIdxEnd]) != '*') && (strIdxStart <= strIdxEnd))
      {
         if ((ch != '?') && !equals(ch, strArr[strIdxEnd], isCaseSensitive))
         {
            return false; // Character mismatch
         }
         patIdxEnd--;
         strIdxEnd--;
      }
      if (strIdxStart > strIdxEnd)
      {
         // All characters in the string are used. Check if only '*'s are
         // left in the pattern. If so, we succeeded. Otherwise failure.
         for (int i = patIdxStart; i <= patIdxEnd; i++)
         {
            if (patArr[i] != '*')
            {
               return false;
            }
         }
         return true;
      }

      // process pattern between stars. padIdxStart and patIdxEnd point
      // always to a '*'.
      while ((patIdxStart != patIdxEnd) && (strIdxStart <= strIdxEnd))
      {
         int patIdxTmp = -1;
         for (int i = patIdxStart + 1; i <= patIdxEnd; i++)
         {
            if (patArr[i] == '*')
            {
               patIdxTmp = i;
               break;
            }
         }
         if (patIdxTmp == (patIdxStart + 1))
         {
            // Two stars next to each other, skip the first one.
            patIdxStart++;
            continue;
         }
         // Find the pattern between padIdxStart & padIdxTmp in str between
         // strIdxStart & strIdxEnd
         final int patLength = (patIdxTmp - patIdxStart - 1);
         final int strLength = ((strIdxEnd - strIdxStart) + 1);
         int foundIdx = -1;
         strLoop: for (int i = 0; i <= (strLength - patLength); i++)
         {
            for (int j = 0; j < patLength; j++)
            {
               ch = patArr[patIdxStart + j + 1];
               if ((ch != '?')
                     && !equals(ch, strArr[strIdxStart + i + j], isCaseSensitive))
               {
                  continue strLoop;
               }
            }

            foundIdx = strIdxStart + i;
            break;
         }

         if (foundIdx == -1)
         {
            return false;
         }

         patIdxStart = patIdxTmp;
         strIdxStart = foundIdx + patLength;
      }

      // All characters in the string are used. Check if only '*'s are left
      // in the pattern. If so, we succeeded. Otherwise failure.
      for (int i = patIdxStart; i <= patIdxEnd; i++)
      {
         if (patArr[i] != '*')
         {
            return false;
         }
      }
      return true;
   }

   /**
    * Uses regular expression to perform match...
    * 
    * @param pattern
    * @param str
    * @param isCaseSensitive
    * @return
    */
   public static boolean match(final String pattern, final String str,
         final boolean isCaseSensitive)
   {
      String fixed_pattern = pattern.replace("**", ".*");
//System.err.print("PATTERN = \"" + fixed_pattern + "\", STRING = \"" + str + "\"");
      Pattern compiled_pattern;
      if (isCaseSensitive)
      {
         compiled_pattern = Pattern.compile(fixed_pattern);
      }
      else
      {
         compiled_pattern = Pattern.compile(fixed_pattern, Pattern.CASE_INSENSITIVE);
      }
      boolean result =  compiled_pattern.matcher(str).matches();

//System.err.println(" -> " + result);
      return result;

   }

   /**
    * Tests whether two characters are equal.
    */
   private static boolean equals(final char c1, final char c2,
         final boolean isCaseSensitive)
   {
      if (c1 == c2)
      {
         return true;
      }
      if (!isCaseSensitive)
      {
         // NOTE: Try both upper case and lower case as done by
         // String.equalsIgnoreCase()
         if ((Character.toUpperCase(c1) == Character.toUpperCase(c2))
               || (Character.toLowerCase(c1) == Character.toLowerCase(c2)))
         {
            return true;
         }
      }
      return false;
   }

   /**
    * Breaks a path up into a Vector of path elements, tokenizing on
    * <code>File.separator</code>.
    * 
    * @param path Path to tokenize. Must not be <code>null</code>.
    * @return a Vector of path elements from the tokenized path
    */
   public static Vector tokenizePath(final String path)
   {
      return tokenizePath(path, File.separator);
   }

   public static Vector tokenizePath(final String path, final String separator)
   {
      final Vector ret = new Vector();
      final StringTokenizer st = new StringTokenizer(path, separator);
      while (st.hasMoreTokens())
      {
         ret.addElement(st.nextToken());
      }
      return ret;
   }

   /**
    * Returns dependency information on these two files. If src has been
    * modified later than target, it returns true. If target doesn't exist, it
    * likewise returns true. Otherwise, target is newer than src and is not out
    * of date, thus the method returns false. It also returns false if the src
    * file doesn't even exist, since how could the target then be out of date.
    * 
    * @param src the original file
    * @param target the file being compared against
    * @param granularity the amount in seconds of slack we will give in
    *           determining out of dateness
    * @return whether the target is out of date
    */
   public static boolean isOutOfDate(final File src, final File target,
         final int granularity)
   {
      if (!src.exists())
      {
         return false;
      }
      if (!target.exists())
      {
         return true;
      }
      if ((src.lastModified() - granularity) > target.lastModified())
      {
         return true;
      }
      return false;
   }

   /**
    * "Flattens" a string by removing all whitespace (space, tab, linefeed,
    * carriage return, and formfeed). This uses StringTokenizer and the default
    * set of tokens as documented in the single arguement constructor.
    * 
    * @param input a String to remove all whitespace.
    * @return a String that has had all whitespace removed.
    */
   public static String removeWhitespace(final String input)
   {
      final StringBuffer result = new StringBuffer();
      if (input != null)
      {
         final StringTokenizer st = new StringTokenizer(input);
         while (st.hasMoreTokens())
         {
            result.append(st.nextToken());
         }
      }
      return result.toString();
   }
}
