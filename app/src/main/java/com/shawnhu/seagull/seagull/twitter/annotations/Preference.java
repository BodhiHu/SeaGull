package com.shawnhu.seagull.seagull.twitter.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Preference {

    boolean defaultBoolean() default false;

    float defaultFloat() default 0;

    int defaultInt() default 0;

    long defaultLong() default 0;

    int defaultResource() default 0;

    String defaultString() default "";

    boolean exportable() default true;

    boolean hasDefault() default false;

    Type type() default Type.NULL;

    public static enum Type {
        BOOLEAN(1), INT(2), LONG(3), FLOAT(4), STRING(5), NULL(0), INVALID(-1);
        private int type;

        Type(final int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }
    }
}
