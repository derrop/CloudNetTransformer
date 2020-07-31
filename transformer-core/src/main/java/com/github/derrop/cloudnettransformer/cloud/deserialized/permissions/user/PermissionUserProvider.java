package com.github.derrop.cloudnettransformer.cloud.deserialized.permissions.user;

import java.util.UUID;
import java.util.function.Consumer;

public interface PermissionUserProvider {

    long getRegisteredCount();

    void loadUsers(Consumer<PermissionUser> consumer);

    void insertUser(PermissionUser permissionUser);

    boolean containsUser(UUID uniqueId);

    PermissionUser getUser(UUID uniqueId);

}
