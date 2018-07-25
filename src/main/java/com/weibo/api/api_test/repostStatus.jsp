<%@ page contentType="text/json; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>

<%@ page import="cn.sina.api.SinaUser"%>
<%@ page import="cn.sina.api.Status"%>
<%@ page import="cn.sina.api.commons.util.ApiLogger"%>
<%@ page import="cn.sina.api.commons.util.ArrayUtil" %>
<%@ page import="cn.sina.api.data.dao.impl2.RepostDualDaoImpl" %>
<%@ page import="cn.sina.api.data.service.SinaUserService" %>
<%@ page import="com.google.common.collect.Lists" %>
<%@ page import="com.google.common.collect.Maps" %>
<%@ page import="com.weibo.api.core.ApplicationContextHolder" %>
<%@ page import="com.weibo.api.data.status.service.StatusContentService" %>
<%@ page import="net.sf.json.JSONArray" %>
<%@ page import="net.sf.json.JSONObject" %>
<%@ page import="org.apache.commons.collections.MapUtils" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>

<%!
    // init
    RepostDualDaoImpl repostDao = (RepostDualDaoImpl) ApplicationContextHolder.getBean("repostDualDao");
    StatusContentService statusContentService = (StatusContentService) ApplicationContextHolder.getBean("statusContentService");
    SinaUserService sinaUserService = (SinaUserService) ApplicationContextHolder.getBean("sinaUserService");

    // user batchGet Limit
    private static final int BATTH_GET_LIMIT = 50;
%>

<%!
    // getStatus
    void getStatus(long[] ids, Map<Long, Status> statusMap) {
        List<Long> repostIds = Lists.newArrayList();
    	for (long repostId : ids) {
            repostIds.add(repostId);
            if (repostIds.size() < BATTH_GET_LIMIT) {
            	continue;
            } else {
                statusMap.putAll(statusContentService.gets(ArrayUtil.toLongArr(repostIds)));
                repostIds.clear();
            }
        }
        statusMap.putAll(statusContentService.gets(ArrayUtil.toLongArr(repostIds)));

        List<Long> statusAuthorUids = Lists.newArrayList();
        for (Status status : statusMap.values()) {
        	if (status != null) { statusAuthorUids.add(status.getAuthorId()); }
        }

        Map<Long, SinaUser> userMap = Maps.newHashMap();
        List<Long> repostUids = Lists.newArrayList();
        for (long repostUid : statusAuthorUids) {
            repostUids.add(repostUid);
            if (repostUids.size() < BATTH_GET_LIMIT) {
            	continue;
            } else {
                userMap.putAll(sinaUserService.getBareSinaUsers(ArrayUtil.toLongArr(repostUids)));
                repostUids.clear();
            }
        }
        userMap.putAll(sinaUserService.getBareSinaUsers(ArrayUtil.toLongArr(repostUids)));

        if (userMap == null) { userMap = Maps.newHashMap(); }
        for (Status status : statusMap.values()) { status.author = userMap.get(status.getAuthorId()); }
    }
%>

<%!
    // getJson
    String getJson(long id, long[] ids, Map<Long, Status> statusMap) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", id);
        JSONArray jsonArray = new JSONArray();
        SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");

        if (MapUtils.isNotEmpty(statusMap)) {
            for (long repostId : ids) {
                Status status = statusMap.get(repostId);
            	if (status == null) { continue; }
                JSONObject repostObject = new JSONObject();
                repostObject.put("repostId", repostId);
                repostObject.put("repostUid", status.getAuthorId());
                repostObject.put("validUser", status.author != null);
                repostObject.put("apiState", status.apiState);
                repostObject.put("mflag", status.mflag);
                repostObject.put("created_at", dateFormater.format(status.created_at));
                repostObject.put("test", status.text);
                jsonArray.add(repostObject.toString());
            }
        } else {
        	for (long repostId : ids) {
                JSONObject repostObject = new JSONObject();
                repostObject.put("repostId", repostId);
                jsonArray.add(repostObject.toString());
            }
        }

        jsonObject.element("repost", jsonArray.toString());
        return jsonObject.toString();
    }
%>

<%
    long id = 0L;
    int count = 20;
    int pageNum = 1;
    boolean isGetContent = false;

    if (request.getParameter("id") != null) { id = Long.parseLong(request.getParameter("id")); }
    if (request.getParameter("page") != null) { pageNum = Integer.parseInt(request.getParameter("page")); }
    if (request.getParameter("count") != null) { count = Integer.parseInt(request.getParameter("count")); }
    if (request.getParameter("get_content") != null) { isGetContent = (Integer.parseInt(request.getParameter("get_content")) == 1); }

    try {
        long[] ids = repostDao.getRepostTimeLineIds(id, 0L, 0L, count, pageNum);
        Map<Long, Status> statusMap = Maps.newHashMap();
        if (isGetContent) { getStatus(ids, statusMap); }
        out.print(getJson(id, ids, statusMap));
    } catch (Exception e) {
        ApiLogger.error("repostStatus failed", e);
    }
%>
