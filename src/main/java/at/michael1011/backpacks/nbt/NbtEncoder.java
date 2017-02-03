package at.michael1011.backpacks.nbt;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

// todo: improve array and list handling
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

        encoded.append("[");

        for (int i = 0; i < size; i++) {
            Object nbtBase = getEntry.invoke(nbtTagList, i);

            if (encoded.length() > 1) {
                encoded.append("/");
            }

            encoded.append(encodeNbtBase(nbtBase));
        }

        encoded.append("]");

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
                type = "byte";
                value = defaultValue(nbtBase);

                break;

            case 2:
                type = "short";
                value = defaultValue(nbtBase);

                break;

            case 3:
                type = "int";
                value = defaultValue(nbtBase);

                break;

            case 4:
                type = "long";
                value = defaultValue(nbtBase);

                break;

            case 5:
                type = "float";
                value = defaultValue(nbtBase);

                break;

            case 6:
                type = "double";
                value = defaultValue(nbtBase);

                break;

            case 8:
                type = "string";
                value = "(" + nbtBase.toString() + ")";

                break;

            case 7:
                type = "byteArray";
                value = arrayValue(nbtBase);

                break;

            case 11:
                type = "intArray";
                value = arrayValue(nbtBase);

                break;

            case 9:
                type = "list";
                value = encodeNbtList(nbtBase);

                break;

            case 10:
                type = "compound";
                value = "{" + encodeNbt(nbtBase) + "}";

                break;
        }

        return type + value;
    }

    private static String defaultValue(Object nbtBase) {
        String value = nbtBase.toString();

        return "(" + value.substring(0, value.length()-1) + ")";
    }

    private static String arrayValue(Object nbtBase) {
        String value = nbtBase.toString();

        if (!value.startsWith("[")) {
            value = "[" + value + "]";
        }

        return value;
    }

}
