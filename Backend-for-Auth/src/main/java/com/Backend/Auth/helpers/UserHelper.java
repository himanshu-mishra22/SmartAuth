package com.Backend.Auth.helpers;

import java.util.UUID;

public class UserHelper {
    public static UUID parseUUId(String userId) {
        return UUID.fromString(userId);
    }
}
