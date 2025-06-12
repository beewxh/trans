package com.hsbc.trans.controller.web;

import org.springframework.stereotype.Controller;

import java.lang.annotation.*;

/**
 * Annotation to mark Web Controllers that should be handled by WebExceptionHandler
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Controller
public @interface WebController {
} 