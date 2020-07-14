package com.example.demo;

import java.lang.reflect.Field;
import java.util.Random;
import java.util.UUID;

public class TestUtils {
    private static Random randomNumberGenerator = new Random();
    public static void injectObjects(Object target, String fieldName, Object toInject){
        boolean wasPrivate = false;

        try {
            Field declaredField = target.getClass().getDeclaredField(fieldName);

            if (!declaredField.isAccessible()){
                declaredField.setAccessible(true);
                wasPrivate = true;
            }
            declaredField.set(target, toInject);
            if (wasPrivate){
                declaredField.setAccessible(false);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }


   public static String getRandomString(){
        return UUID.randomUUID().toString();
    }

    public static Long getRandomLong(){
        return randomNumberGenerator.nextLong();
    }

    public static Integer getRandomInt(){
        return randomNumberGenerator.nextInt();
    }

    public static Double getRandomDouble(){
        return randomNumberGenerator.nextDouble();
    }
}
