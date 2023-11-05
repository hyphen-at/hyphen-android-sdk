package at.hyphen.android.sdk.core.asn1;

public class Asn1Utils {
    private static final char[] HEX_CHARS = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
    };

    public static int hexCharToInt(char c) {
        if (c >= '0' && c <= '9') return (c - '0');
        if (c >= 'A' && c <= 'F') return (c - 'A' + 10);
        if (c >= 'a' && c <= 'f') return (c - 'a' + 10);
        throw new RuntimeException("invalid hex char '" + c + "'");
    }

    /**
     * Converts a hex String to a byte array.
     *
     * @param s A string of hexadecimal characters, must be an even number of
     *          chars long
     * @return byte array representation
     * @throws RuntimeException on invalid format
     */
    public static byte[] hexStringToBytes(String s) {
        byte[] ret;
        if (s == null) return null;
        int sz = s.length();
        ret = new byte[sz / 2];
        for (int i = 0; i < sz; i += 2) {
            ret[i / 2] = (byte) ((hexCharToInt(s.charAt(i)) << 4)
                    | hexCharToInt(s.charAt(i + 1)));
        }
        return ret;
    }

    /**
     * Converts a byte array into a String of hexadecimal characters.
     *
     * @param bytes an array of bytes
     * @return hex string representation of bytes array
     */
    public static String bytesToHexString(byte[] bytes) {
        if (bytes == null) return null;
        StringBuilder ret = new StringBuilder(2 * bytes.length);
        for (byte aByte : bytes) {
            int b;
            b = 0x0f & (aByte >> 4);
            ret.append(HEX_CHARS[b]);
            b = 0x0f & aByte;
            ret.append(HEX_CHARS[b]);
        }
        return ret.toString();
    }

    /**
     * Converts a series of bytes to an integer. This method currently only supports positive 32-bit
     * integers.
     *
     * @param src    The source bytes.
     * @param offset The position of the first byte of the data to be converted. The data is base
     *               256 with the most significant digit first.
     * @param length The length of the data to be converted. It must be <= 4.
     * @throws IllegalArgumentException  If {@code length} is bigger than 4 or {@code src} cannot be
     *                                   parsed as a positive integer.
     * @throws IndexOutOfBoundsException If the range defined by {@code offset} and {@code length}
     *                                   exceeds the bounds of {@code src}.
     */
    public static int bytesToInt(byte[] src, int offset, int length) {
        if (length > 4) {
            throw new IllegalArgumentException(
                    "length must be <= 4 (only 32-bit integer supported): " + length);
        }
        if (offset < 0 || length < 0 || offset + length > src.length) {
            throw new IndexOutOfBoundsException(
                    "Out of the bounds: src=["
                            + src.length
                            + "], offset="
                            + offset
                            + ", length="
                            + length);
        }
        int result = 0;
        for (int i = 0; i < length; i++) {
            result = (result << 8) | (src[offset + i] & 0xFF);
        }
        if (result < 0) {
            throw new IllegalArgumentException(
                    "src cannot be parsed as a positive integer: " + result);
        }
        return result;
    }

    /**
     * Converts a series of bytes to a raw long variable which can be both positive and negative.
     * This method currently only supports 64-bit long variable.
     *
     * @param src    The source bytes.
     * @param offset The position of the first byte of the data to be converted. The data is base
     *               256 with the most significant digit first.
     * @param length The length of the data to be converted. It must be <= 8.
     * @throws IllegalArgumentException  If {@code length} is bigger than 8.
     * @throws IndexOutOfBoundsException If the range defined by {@code offset} and {@code length}
     *                                   exceeds the bounds of {@code src}.
     */
    public static long bytesToRawLong(byte[] src, int offset, int length) {
        if (length > 8) {
            throw new IllegalArgumentException(
                    "length must be <= 8 (only 64-bit long supported): " + length);
        }
        if (offset < 0 || length < 0 || offset + length > src.length) {
            throw new IndexOutOfBoundsException(
                    "Out of the bounds: src=["
                            + src.length
                            + "], offset="
                            + offset
                            + ", length="
                            + length);
        }
        long result = 0;
        for (int i = 0; i < length; i++) {
            result = (result << 8) | (src[offset + i] & 0xFF);
        }
        return result;
    }

    /**
     * Converts an integer to a new byte array with base 256 and the most significant digit first.
     *
     * @throws IllegalArgumentException If {@code value} is negative.
     */
    public static byte[] unsignedIntToBytes(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("value must be 0 or positive: " + value);
        }
        byte[] bytes = new byte[byteNumForUnsignedInt(value)];
        unsignedIntToBytes(value, bytes, 0);
        return bytes;
    }

    /**
     * Converts an integer to a new byte array with base 256 and the most significant digit first.
     * The first byte's highest bit is used for sign. If the most significant digit is larger than
     * 127, an extra byte (0) will be prepended before it. This method currently doesn't support
     * negative values.
     *
     * @throws IllegalArgumentException If {@code value} is negative.
     */
    public static byte[] signedIntToBytes(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("value must be 0 or positive: " + value);
        }
        byte[] bytes = new byte[byteNumForSignedInt(value)];
        signedIntToBytes(value, bytes, 0);
        return bytes;
    }

    /**
     * Converts an integer to a series of bytes with base 256 and the most significant digit first.
     *
     * @param value  The integer to be converted.
     * @param dest   The destination byte array.
     * @param offset The start offset of the byte array.
     * @return The number of byte needeed.
     * @throws IllegalArgumentException  If {@code value} is negative.
     * @throws IndexOutOfBoundsException If {@code offset} exceeds the bounds of {@code dest}.
     */
    public static int unsignedIntToBytes(int value, byte[] dest, int offset) {
        return intToBytes(value, dest, offset, false);
    }

    /**
     * Converts an integer to a series of bytes with base 256 and the most significant digit first.
     * The first byte's highest bit is used for sign. If the most significant digit is larger than
     * 127, an extra byte (0) will be prepended before it. This method currently doesn't support
     * negative values.
     *
     * @throws IllegalArgumentException  If {@code value} is negative.
     * @throws IndexOutOfBoundsException If {@code offset} exceeds the bounds of {@code dest}.
     */
    public static int signedIntToBytes(int value, byte[] dest, int offset) {
        return intToBytes(value, dest, offset, true);
    }

    /**
     * Calculates the number of required bytes to represent {@code value}. The bytes will be base
     * 256 with the most significant digit first.
     *
     * @throws IllegalArgumentException If {@code value} is negative.
     */
    public static int byteNumForUnsignedInt(int value) {
        return byteNumForInt(value, false);
    }

    /**
     * Calculates the number of required bytes to represent {@code value}. The bytes will be base
     * 256 with the most significant digit first. If the most significant digit is larger than 127,
     * an extra byte (0) will be prepended before it. This method currently only supports positive
     * integers.
     *
     * @throws IllegalArgumentException If {@code value} is negative.
     */
    public static int byteNumForSignedInt(int value) {
        return byteNumForInt(value, true);
    }

    private static int intToBytes(int value, byte[] dest, int offset, boolean signed) {
        int l = byteNumForInt(value, signed);
        if (offset < 0 || offset + l > dest.length) {
            throw new IndexOutOfBoundsException("Not enough space to write. Required bytes: " + l);
        }
        for (int i = l - 1, v = value; i >= 0; i--, v >>>= 8) {
            byte b = (byte) (v & 0xFF);
            dest[offset + i] = b;
        }
        return l;
    }

    private static int byteNumForInt(int value, boolean signed) {
        if (value < 0) {
            throw new IllegalArgumentException("value must be 0 or positive: " + value);
        }
        if (signed) {
            if (value <= 0x7F) {
                return 1;
            }
            if (value <= 0x7FFF) {
                return 2;
            }
            if (value <= 0x7FFFFF) {
                return 3;
            }
        } else {
            if (value <= 0xFF) {
                return 1;
            }
            if (value <= 0xFFFF) {
                return 2;
            }
            if (value <= 0xFFFFFF) {
                return 3;
            }
        }
        return 4;
    }

    /**
     * Counts the number of trailing zero bits of a byte.
     */
    public static byte countTrailingZeros(byte b) {
        if (b == 0) {
            return 8;
        }
        int v = b & 0xFF;
        byte c = 7;
        if ((v & 0x0F) != 0) {
            c -= 4;
        }
        if ((v & 0x33) != 0) {
            c -= 2;
        }
        if ((v & 0x55) != 0) {
            c -= 1;
        }
        return c;
    }

    /**
     * Converts a byte to a hex string.
     */
    public static String byteToHex(byte b) {
        return new String(new char[]{HEX_CHARS[(b & 0xFF) >>> 4], HEX_CHARS[b & 0xF]});
    }
}
