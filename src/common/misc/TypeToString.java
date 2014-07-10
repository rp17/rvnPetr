/*
 * 
 *  Desc:   an interface for a class that has a static
 *          method for converting an int into a string. (useful when debugging
 *          to convert enumerations)
 * 
 *  @author Petr (http://www.sallyx.org/)
 */
package common.misc;


abstract public class TypeToString
{
  abstract public String Convert(int enumeration);
}
