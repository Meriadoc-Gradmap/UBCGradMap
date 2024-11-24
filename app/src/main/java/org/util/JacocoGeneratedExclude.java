package org.util;

import java.lang.annotation.*;

/**
 * Annotation for exclusion from Jacoco, to be used on main methods and stuff
 * We can exclude files in the gradle.build but this is helpful for methods
 * <a href="https://www.baeldung.com/jacoco-report-exclude">[Source]</a>
 *
 * @author William Banquier
 * @author baeldung.com
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR})
public @interface JacocoGeneratedExclude { }