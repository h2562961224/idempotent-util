package com.fuxiaofei.common.idempotent.util;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

/**
 * @author huxiaohui
 * @date 2018/8/2 09:03
 */
@Slf4j
public class BeanUtil {
    public static <T> T defaultValueForClass(Class<T> beanClass) {
        Object o = null;

        try {
            if (beanClass.isPrimitive()) {
                if (beanClass.isAssignableFrom(Integer.TYPE)) {
                    o = 1;
                } else if (beanClass.isAssignableFrom(Boolean.TYPE)) {
                    o = Boolean.FALSE;
                } else if (beanClass.isAssignableFrom(Short.TYPE)) {
                    o = 1;
                } else if (beanClass.isAssignableFrom(Long.TYPE)) {
                    o = 1;
                } else if (beanClass.isAssignableFrom(Float.TYPE)) {
                    o = 1.0F;
                } else if (beanClass.isAssignableFrom(Double.TYPE)) {
                    o = 1.0D;
                } else if (beanClass.isAssignableFrom(Byte.TYPE)) {
                    o = Byte.valueOf("1");
                }
            } else if (beanClass.isEnum()) {
                o = firstEnumName(beanClass);
            } else if (beanClass.isInterface()) {
                Object result = null;
                if (List.class.isAssignableFrom(beanClass)) {
                    result = new ArrayList();
                } else if (Set.class.isAssignableFrom(beanClass)) {
                    result = new HashSet();
                } else if (Map.class.isAssignableFrom(beanClass)) {
                    result = new HashMap();
                }

                o = result;
            } else if (beanClass.isArray()) {
                Class<?> componentType = beanClass.getComponentType();
                Object array = Array.newInstance(componentType, 1);
                Object element = defaultValueForClass(componentType);
                Array.set(array, 0, element);
                o = array;
            } else if (beanClass.isAssignableFrom(String.class)) {
                o = "string";
            } else if (beanClass.isAssignableFrom(Boolean.class)) {
                o = Boolean.FALSE;
            } else if (beanClass.isAssignableFrom(Byte.class)) {
                o = Byte.valueOf("1");
            } else if (beanClass.isAssignableFrom(Short.class)) {
                o = Short.valueOf("1");
            } else if (beanClass.isAssignableFrom(Integer.class)) {
                o = 1;
            } else if (beanClass.isAssignableFrom(Long.class)) {
                o = 1L;
            } else if (beanClass.isAssignableFrom(Float.class)) {
                o = 1.0F;
            } else if (beanClass.isAssignableFrom(Double.class)) {
                o = 1.0D;
            } else if (beanClass.isAssignableFrom(BigDecimal.class)) {
                o = BigDecimal.ONE;
            } else if (beanClass.isAssignableFrom(BigInteger.class)) {
                o = BigInteger.ONE;
            } else if (beanClass.isAssignableFrom(Date.class)) {
                o = new Date();
            } else if (beanClass.isAssignableFrom(LocalDate.class)) {
                o = LocalDate.now();
            } else if (beanClass.isAssignableFrom(LocalTime.class)) {
                o = LocalTime.now();
            } else if (beanClass.isAssignableFrom(LocalDateTime.class)) {
                o = LocalDateTime.now();
            } else if (!Modifier.isAbstract(beanClass.getModifiers())) {
                o = beanClass.newInstance();
                Field[] declaredFields = o.getClass().getDeclaredFields();
                o = defaultValueForFields(o, declaredFields);

                for (Class superclass = o.getClass().getSuperclass(); Objects.nonNull(superclass) && !superclass.equals(Object.class); superclass = superclass.getSuperclass()) {
                    declaredFields = superclass.getDeclaredFields();
                    o = defaultValueForFields(o, declaredFields);
                }
            }
        } catch (Throwable var5) {
            log.warn("[Error] Object:" + o + ", beanClass:" + beanClass, var5);
        }

        return (T)o;
    }

    public static <T> T defaultValueForFields(T o, Field[] declaredFields) {
        Arrays.stream(declaredFields).forEach((field) -> {
            defaultValueForOneField(o, field);
        });
        return o;
    }

    public static <T> void defaultValueForOneField(T o, Field field) {
        try {
            field.setAccessible(true);
            Object value = field.get(o);
            Class<?> fieldType = field.getType();
            if (!o.getClass().equals(fieldType)) {
                if (fieldType.isInterface()) {
                    if (List.class.isAssignableFrom(fieldType)) {
                        if (Objects.isNull(value)) {
                            collection(o, field, new ArrayList());
                        }
                    } else if (Set.class.isAssignableFrom(fieldType)) {
                        if (Objects.isNull(value)) {
                            collection(o, field, new HashSet());
                        }
                    } else if (Map.class.isAssignableFrom(fieldType) && Objects.isNull(value)) {
                        field.set(o, new HashMap());
                    }
                } else if (!fieldType.isPrimitive()) {
                    if (fieldType.isEnum()) {
                        if (Objects.isNull(value)) {
                            field.set(o, firstEnumName(fieldType));
                        }
                    } else if (fieldType.isArray()) {
                        if (Objects.isNull(value)) {
                            Class<?> componentType = fieldType.getComponentType();
                            Object array = Array.newInstance(componentType, 1);
                            Object element = defaultValueForClass(componentType);
                            Array.set(array, 0, element);
                            field.set(o, array);
                        }
                    } else if (fieldType.isAssignableFrom(String.class)) {
                        if (Objects.isNull(value)) {
                            field.set(o, field.getName());
                        }
                    } else if (fieldType.isAssignableFrom(Boolean.class)) {
                        if (Objects.isNull(value)) {
                            field.set(o, Boolean.FALSE);
                        }
                    } else if (fieldType.isAssignableFrom(Byte.class)) {
                        if (Objects.isNull(value)) {
                            field.set(o, Byte.valueOf("1"));
                        }
                    } else if (fieldType.isAssignableFrom(Short.class)) {
                        if (Objects.isNull(value)) {
                            field.set(o, Short.valueOf("1"));
                        }
                    } else if (fieldType.isAssignableFrom(Integer.class)) {
                        if (Objects.isNull(value)) {
                            field.set(o, 1);
                        }
                    } else if (fieldType.isAssignableFrom(Long.class)) {
                        if (Objects.isNull(value)) {
                            field.set(o, 1L);
                        }
                    } else if (fieldType.isAssignableFrom(Float.class)) {
                        if (Objects.isNull(value)) {
                            field.set(o, 1.0F);
                        }
                    } else if (fieldType.isAssignableFrom(Double.class)) {
                        if (Objects.isNull(value)) {
                            field.set(o, 1.0D);
                        }
                    } else if (fieldType.isAssignableFrom(BigDecimal.class)) {
                        if (Objects.isNull(value)) {
                            field.set(o, BigDecimal.ONE);
                        }
                    } else if (fieldType.isAssignableFrom(BigInteger.class)) {
                        if (Objects.isNull(value)) {
                            field.set(o, BigInteger.ONE);
                        }
                    } else if (fieldType.isAssignableFrom(Date.class)) {
                        if (Objects.isNull(value)) {
                            field.set(o, new Date());
                        }
                    } else if (fieldType.isAssignableFrom(LocalDate.class)) {
                        if (Objects.isNull(value)) {
                            field.set(o, LocalDate.now());
                        }
                    } else if (fieldType.isAssignableFrom(LocalTime.class)) {
                        if (Objects.isNull(value)) {
                            field.set(o, LocalTime.now());
                        }
                    } else if (fieldType.isAssignableFrom(LocalDateTime.class)) {
                        if (Objects.isNull(value)) {
                            field.set(o, LocalDateTime.now());
                        }
                    } else {
                        field.set(o, defaultValueForClass(fieldType));
                    }
                }
            }
        } catch (Throwable var7) {
            log.warn("[Error] Object:" + o + ", Field:" + field, var7);
        }


    }

    private static <T> void collection(T o, Field field, Collection collection) throws IllegalAccessException {
        Class<?> actualType = actualType(field);
        if (actualType.isEnum()) {
            Collections.addAll(collection, allEnumNames(actualType));
        } else if (!o.getClass().equals(actualType) && !actualType.isInterface()) {
            Object element = actualObject(field);
            collection.add(element);
        }

        field.set(o, collection);
    }

    public static Object actualObject(Field field) {
        Class<?> actualType = actualType(field);
        return defaultValueForClass(actualType);
    }

    public static Class<?> actualType(Field genericField) {
        if (genericField.getGenericType() instanceof ParameterizedType) {
            ParameterizedType genericType = (ParameterizedType)genericField.getGenericType();
            return (Class)genericType.getActualTypeArguments()[0];
        } else {
            return genericField.getType();
        }
    }

    public static Object firstEnumName(Class<?> enumType) {
        return allEnumNames(enumType)[0];
    }

    public static Object[] allEnumNames(Class<?> enumType) {
        return enumType.getEnumConstants();
    }
}
