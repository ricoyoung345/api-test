<%@ page language="java" contentType="text/json; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>

<%@ page import="cn.sina.api.commons.util.ApiLogger"%>
<%@ page import="com.weibo.api.core.ApplicationContextHolder"%>
<%@ page import="cn.sina.api.commons.util.JsonWrapper" %>
<%@ page import="cn.sina.api.mcq.handle.CommentTreeMsgHandler" %>

<%!
    //get beans
    CommentTreeMsgHandler commentTreeMsgHandler = (CommentTreeMsgHandler) ApplicationContextHolder.getBean("commentTreeMsgHandler");
    String msg = "{\"type\":\"2\",\"apiId\":4244675429013392,\"info\":{\"cmtid\":\"4244675429013392\",\"uid\":1658876261,\"floor_number\":1,\"time\":1527486780,\"start_push_time\":1527486780000,\"status\":0,\"openapiStatus\":3,\"cmtinfo\":\"看完图。。默默拿起了手机。。\",\"appid\":2453752,\"ip\":\"180.149.153.176\",\"port\":0,\"localIp\":\"10.22.6.60\",\"srcObjectType\":0,\"rootid\":4244675429013392,\"markable\":true,\"minfo\":{\"uid\":2434744995,\"source\":2453764,\"mblogid\":\"4244666641744835\"},\"no_tips\":0,\"hot\":false,\"hotflow\":true,\"custom_source\":\"\",\"mode\":0,\"withRepost\":false,\"fromDeliver\":0,\"liveBizCode\":-1,\"object_uuids\":[],\"cmtReminds\":[],\"push_search\":false,\"kwdlevel\":0,\"zonelevel\":0,\"sass_status\":0,\"userlevel\":1,\"usertype\":1,\"dept\":0,\"auxtype\":0,\"effectscale\":100,\"leaderSassCode\":1,\"errorno\":1,\"adTags\":{\"adv\":[\"0\"]},\"requestId\":793511142275853846},\"mrpcId\":\"010A16063C00000164C6D978DB097E0E\"}";
%>

<%
    try {
        commentTreeMsgHandler.handleMsq("read_key_never_used", msg, new JsonWrapper(msg));

        ApiLogger.info("repairComment success, msg: " + msg);
    } catch (Exception e) {
        ApiLogger.error("repairComment failed: ", e);
    }
%>
