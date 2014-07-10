/**
 *
 * Desc: various useful functions that operate on or with streams
 *
 * @author Petr (http://www.sallyx.org/)
 */
package common.misc;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

public class Stream_Utility_function {

    /**
     * convert a type to a string
     */
    public static String ttos(final int t) {
        return "" + t;
    }

    public static <T extends Number> String ttos(final T t) {
        return ttos(t, 2);
    }

    public static <T extends Number> String ttos(final T t, int precision) {
        if (precision == 1) {
            return "" + t;
        }
        double multipicationFactor = Math.pow(10, precision);
        double interestedInZeroDPs = t.doubleValue() * multipicationFactor;
        return "" + (Math.round(interestedInZeroDPs) / multipicationFactor);
    }

    /**
     * convert a bool to a string
     */
    public static String btos(boolean b) {
        if (b) {
            return "true";
        }
        return "false";
    }

    /**
     * grabs a value of the specified type from an input stream
     */
    public static int GetValueFromStream_Int(InputStream stream) {
        return GetValueFromStream_Int(new Scanner(stream));
    }

    /**
     * grabs a value of the specified type from an input stream
     */
    public static int GetValueFromStream_Int(Scanner dis) {
        Integer val;
        try {
            val = dis.nextInt();
            //make sure it was the correct type
        } catch (Exception e) {
            throw new RuntimeException("Attempting to retrieve wrong type from stream", e);
        }
        return val;
    }

    /**
     * /* writes the value as a binary string of bits
     */
    public static void WriteBitsToStream(PrintStream stream, Integer val) {
        int iNumBits = Integer.SIZE;
        while (--iNumBits >= 0) {
            if ((iNumBits + 1) % 8 == 0) {
                stream.print(" ");
            }
            long mask = 1 << iNumBits;
            if ((val & mask) != 0) {
                stream.print("1");
            } else {
                stream.print("0");
            }
        }
    }
}
