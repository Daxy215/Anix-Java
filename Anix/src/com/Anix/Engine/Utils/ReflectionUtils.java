package com.Anix.Engine.Utils;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Stream;

import static java.lang.String.format;

public final class ReflectionUtils {

    private ReflectionUtils() { }

    private static final String GETTER_PREFIX = "get";
    private static final String SETTER_PREFIX = "set";

    /**
     * Get name of getter
     *
     * @param fieldName fieldName
     * @return getter name
     */
    public static String getterByFieldName(String fieldName) {
        if (isStringNullOrEmpty(fieldName))
            return null;

        return convertFieldByAddingPrefix(fieldName, GETTER_PREFIX);
    }

    /**
     * Get name of setter
     *
     * @param fieldName fieldName
     * @return setter name
     */
    public static String setterByFieldName(String fieldName) {
        if (isStringNullOrEmpty(fieldName))
            return null;

        return convertFieldByAddingPrefix(fieldName, SETTER_PREFIX);
    }

    /**
     * Get the contents of the field with any access modifier
     *
     * @param obj obj
     * @param fieldName fieldName
     * @return content of field
     */
    public static Object getFieldContent(Object obj, String fieldName) {
        if (!isValidParams(obj, fieldName))
            return null;

        try {
            Field declaredField = getFieldAccessible(obj, fieldName);
            return declaredField.get(obj);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Cannot get field content for field name: " + fieldName, e);
        }
    }

    /**
     * @param clazz clazz
     * @param fieldName fieldName
     * @return content static field
     */
    public static Object getStaticFieldContent(final Class<?> clazz, final String fieldName) {
        try {
            Field field = getFieldWithCheck(clazz, fieldName);
            field.setAccessible(true);
            return field.get(clazz);
        } catch (Exception e) {
            String exceptionMsg = format("Cannot find or get static field: '%s' from class: '%s'", fieldName, clazz);
            throw new RuntimeException(exceptionMsg, e);
        }
    }

    /**
     * Set the contents to the field with any access modifier
     *
     * @param obj obj
     * @param fieldName fieldName
     * @param value value
     */
    public static void setFieldContent(Object obj, String fieldName, Object value) {
        if (!isValidParams(obj, fieldName))
            return;

        try {
            Field declaredField = getFieldAccessible(obj, fieldName);
            declaredField.set(obj, value);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Cannot set field content for field name: " + fieldName, e);
        }
    }

    /**
     * Call a method with any access modifier
     *
     * @param obj obj
     * @param methodName methodName
     * @return result of method
     */
    public static Object callMethod(Object obj, String methodName) {
        if (!isValidParams(obj, methodName))
            return null;

        try {
            Method method = obj.getClass().getMethod(methodName);
            method.setAccessible(true);
            return method.invoke(obj);
        } catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new IllegalArgumentException("Cannot invoke method name: " + methodName, e);
        }
    }

    /**
     * Get all fields even from parent
     *
     * @param clazz clazz
     * @return array of fields
     */
    public static Field[] getAllFields(Class<?> clazz) {
        if (clazz == null) return null;

        List<Field> fields = new ArrayList<>(Arrays.asList(clazz.getDeclaredFields()));
        if (clazz.getSuperclass() != null) {
            // danger! Recursion
            fields.addAll(Arrays.asList(getAllFields(clazz.getSuperclass())));
        }
        return fields.toArray(new Field[] {});
    }

    /**
     * Get the Field from Object even from parent
     *
     * @param obj obj
     * @param fieldName fieldName
     * @return {@code Optional}
     */
    public static Optional<Field> getField(Object obj, String fieldName) {
        if (!isValidParams(obj, fieldName))
            return Optional.empty();

        Class<?> clazz = obj.getClass();
        return getField(clazz, fieldName);
    }

    /**
     * Get the Field from Class even from parent
     *
     * @param clazz clazz
     * @param fieldName fieldName
     * @return {@code Optional}
     */
    public static Optional<Field> getField(Class<?> clazz, String fieldName) {
        if (!isValidParams(clazz, fieldName))
            return Optional.empty();

        Field[] fields = getAllFields(clazz);
        return Stream.of(fields)
                .filter(x -> x.getName().equals(fieldName))
                .findFirst();
    }

    /**
     * @param clazz clazz
     * @param fieldName fieldName
     * @return Class
     */
    public static Class<?> getFieldType(Class<?> clazz, String fieldName) {
        return getFieldWithCheck(clazz, fieldName).getType();
    }

    /**
     * @param clazz clazz
     * @param fieldName fieldName
     * @return Field
     */
    public static Field getFieldWithCheck(Class<?> clazz, String fieldName) {
        return ReflectionUtils.getField(clazz, fieldName)
                .orElseThrow(() -> {
                    String msg = String.format("Cannot find field name: '%s' from class: '%s'", fieldName, clazz);
                    return new IllegalArgumentException(msg);
                });
    }

    /**
     * Get the field values with the types already listed according to the field type
     *
     * @param clazz clazz
     * @param fieldName fieldName
     * @param fieldValue fieldValue
     * @return value cast to specific field type
     */
    public static Object castFieldValueByClass(Class<?> clazz, String fieldName, Object fieldValue) {
        Field field = getField(clazz, fieldName)
                .orElseThrow(() -> new IllegalArgumentException(String.format("Cannot find field by name: '%s'", fieldName)));

        Class<?> fieldType = field.getType();

        return castFieldValueByType(fieldType, fieldValue);
    }

    /**
     * @param fieldType fieldType
     * @param fieldValue fieldValue
     * @return casted value
     */
    public static Object castFieldValueByType(Class<?> fieldType, Object fieldValue) {
        if (fieldType.isAssignableFrom(Boolean.class)) {
            if (fieldValue instanceof String) {
                return convertStringToBoolean((String) fieldValue);
            }
            if (fieldValue instanceof Number) {
                return !(fieldValue).equals(0);
            }
            return fieldValue;
        }

        else if (fieldType.isAssignableFrom(Double.class)) {
            if (fieldValue instanceof String) {
                return Double.valueOf((String)fieldValue);
            }
            return ((Number) fieldValue).doubleValue();
        }

        else if (fieldType.isAssignableFrom(Long.class)) {
            if (fieldValue instanceof String) {
                return Long.valueOf((String)fieldValue);
            }
            return ((Number) fieldValue).longValue();
        }

        else if (fieldType.isAssignableFrom(Float.class)) {
            if (fieldValue instanceof String) {
                return Float.valueOf((String)fieldValue);
            }
            return ((Number) fieldValue).floatValue();
        }

        else if (fieldType.isAssignableFrom(Integer.class)) {
            if (fieldValue instanceof String) {
                return Integer.valueOf((String)fieldValue);
            }
            return ((Number) fieldValue).intValue();
        }

        else if (fieldType.isAssignableFrom(Short.class)) {
            if (fieldValue instanceof String) {
                return Short.valueOf((String)fieldValue);
            }
            return ((Number) fieldValue).shortValue();
        }

        return fieldValue;
    }

    private static boolean convertStringToBoolean(String s) {
        String trim = s.trim();
        return !trim.equals("") && !trim.equals("0") && !trim.toLowerCase().equals("false");
    }

    private static boolean isValidParams(Object obj, String param) {
        return (obj != null && !isStringNullOrEmpty(param));
    }

    private static boolean isStringNullOrEmpty(String fieldName) {
        return fieldName == null || fieldName.trim().length() == 0;
    }

    private static Field getFieldAccessible(Object obj, String fieldName) {
        Optional<Field> optionalField = getField(obj, fieldName);
        return optionalField
                .map(el -> {
                    el.setAccessible(true);
                    return el;
                })
                .orElseThrow(() -> new IllegalArgumentException("Cannot find field name: " + fieldName));
    }

    private static String convertFieldByAddingPrefix(String fieldName, String prefix) {
        return prefix + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }
}