package at.michael1011.backpacks.nbt;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

@SuppressWarnings("unchecked")
public class NbtEncoder {

    public static String encodeNbt(Object nbtTag) throws NoSuchMethodException,
            InvocationTargetException, IllegalAccessException {

        StringBuilder encoded = new StringBuilder();

        Method getNbtKeys = nbtTag.getClass().getMethod("c");
        Method getNbtBase = nbtTag.getClass().getMethod("get", String.class);

        Set<String> nbtKeys = (Set<String>) getNbtKeys.invoke(nbtTag);

        for (String key : nbtKeys) {
            Object nbtBase = getNbtBase.invoke(nbtTag, key);

            if (encoded.length() != 0) {
                encoded.append("/");
            }

            encoded.append(key).append(":").append(encodeNbtBase(nbtBase));
        }

        return encoded.toString();
    }

    private static String encodeNbtList(Object nbtTagList) throws NoSuchMethodException,
            InvocationTargetException, IllegalAccessException {

        Method getSize = nbtTagList.getClass().getMethod("size");
        Method getEntry = nbtTagList.getClass().getMethod("h", int.class);

        int size = (int) getSize.invoke(nbtTagList);

        StringBuilder encoded = new StringBuilder();

        encoded.append("{");

        for (int i = 0; i < size; i++) {
            Object nbtBase = getEntry.invoke(nbtTagList, i);

            if (encoded.length() > 1) {
                encoded.append("/");
            }

            encoded.append(encodeNbtBase(nbtBase));
        }

        encoded.append("}");

        return encoded.toString();
    }

    private static String encodeNbtBase(Object nbtBase) throws InvocationTargetException,
            IllegalAccessException, NoSuchMethodException {

        String type = "";
        String value = "";

        Method getNbtBaseId = nbtBase.getClass().getMethod("getTypeId");

        Byte id = (Byte) getNbtBaseId.invoke(nbtBase);

        switch (id) {
            case 1:
                // Byte
                type = "1";
                value = defaultValue(nbtBase);

                break;

            case 2:
                // Short
                type = "2";
                value = defaultValue(nbtBase);

                break;

            case 3:
                // Int
                type = "3";
                value = "(" + nbtBase.toString() + ")";

                break;

            case 4:
                // Long
                type = "4";
                value = defaultValue(nbtBase);

                break;

            case 5:
                // Float
                type = "5";
                value = defaultValue(nbtBase);

                break;

            case 6:
                // Double
                type = "6";
                value = defaultValue(nbtBase);

                break;

            case 8:
                // String
                type = "8";

                value = nbtBase.toString();
                value = "(" + value.substring(1, value.length()-1) + ")";

                break;

            case 7:
                // Byte-Array
                type = "7";

                StringBuilder byteBuilder = new StringBuilder();

                byteBuilder.append("[");

                byte[] byteData = (byte[]) nbtBase.getClass().getMethod("c").invoke(nbtBase);

                for (byte b : byteData) {
                    if (byteBuilder.length() > 1) {
                        byteBuilder.append(",");
                    }

                    byteBuilder.append(b);
                }

                byteBuilder.append("]");

                value = byteBuilder.toString();

                break;

            case 11:
                // Int-Array
                type = "11";

                StringBuilder intBuilder = new StringBuilder();

                intBuilder.append("[");

                int[] intData = (int[]) nbtBase.getClass().getMethod("d").invoke(nbtBase);

                for (int b : intData) {
                    if (intBuilder.length() > 1) {
                        intBuilder.append(",");
                    }

                    intBuilder.append(b);
                }

                intBuilder.append("]");

                value = intBuilder.toString();

                break;

            case 9:
                // List
                type = "9";
                value = encodeNbtList(nbtBase);

                break;

            case 10:
                // Compound
                type = "10";
                value = "{" + encodeNbt(nbtBase) + "}";

                break;
        }

        return type + value;
    }

    private static String defaultValue(Object nbtBase) {
        String value = nbtBase.toString();

        return "(" + value.substring(0, value.length()-1) + ")";
    }

}
