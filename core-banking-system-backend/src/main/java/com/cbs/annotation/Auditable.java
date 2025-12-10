// src/main/java/com/cbs/annotation/Auditable.java
package com.cbs.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import com.cbs.model.enums.AuditAction;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Auditable {
    String entityType() default "";

    AuditAction action() default AuditAction.READ;
}