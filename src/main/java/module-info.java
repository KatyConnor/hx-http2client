
module hx.http2client {
    requires jdk.incubator.httpclient;
    requires org.apache.httpcomponents.httpclient;
    requires spring.boot.starter;
    requires spring.beans;
    requires spring.core;
    requires spring.context;
    requires spring.web;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires slf4j.api;
    requires org.apache.httpcomponents.httpcore;
    requires fastjson;
    requires spring.webmvc;
    requires org.apache.commons.codec;
    requires hamcrest.core;

    exports hx.http2.client.config;
    exports hx.http2.client.excutor;
    exports hx.http2.client.thread;
    exports hx.http2.client.handler;
    exports hx.http2.client.enums;
    exports hx.http2.client.exception;
    exports hx.http2.client.response;
    exports hx.http2.client.utils;
}