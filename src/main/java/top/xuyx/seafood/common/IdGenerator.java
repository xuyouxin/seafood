package top.xuyx.seafood.common;

import java.util.UUID;

public class IdGenerator {

    public static String generateId() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}
