<%@ page language="java" contentType="text/json; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>

<%@ page import="cn.sina.api.commons.util.ApiLogger"%>
<%@ page import="com.weibo.api.core.ApplicationContextHolder"%>
<%@ page import="com.weibo.api.data.status.service.StatusCountService"%>

<%@ page import="com.weibo.api.data.status.service.StatusCountService.Type"%>

<%!
    //get beans
    StatusCountService statusCountService = (StatusCountService) ApplicationContextHolder.getBean("statusCountService");
%>

<%
    try {
        // get parameters
        long id = Long.parseLong(request.getParameter("id"));
        int count = Integer.parseInt(request.getParameter("count"));

        boolean isDesc = (count <= 0);
        count = Math.abs(count);

        for (int i = 0; i < count; i++) {
            if (isDesc) {
                statusCountService.decr(id, Type.COMMENT);
            } else {
                statusCountService.incr(id, Type.COMMENT);
            }
        }

        ApiLogger.info("repairCommentCount success, id: " + id + ", count: " + count);
    } catch (Exception e) {
        ApiLogger.error("repairCommentCount failed: ", e);
    }
%>
