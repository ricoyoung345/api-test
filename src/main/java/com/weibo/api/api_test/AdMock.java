package com.weibo.api.api_test;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class AdMock extends HttpServlet {
	/**
	 * The doGet method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to get.
	 *
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/json");
		PrintWriter out = response.getWriter();
		out.println("{\"errno\":0,\"data\":[{\"recommend\":\"广告\",\"mediainfo\":[{\"is_filter_duplicated\":1,\"autoplay\":0}],\"open_adaptive\":{\"is_hide_repost\":0,\"is_new_style\":1,\"is_hide_comment\":0,\"is_hide_like\":0,\"trend_status_source_type\":1},\"id\":\"4240385154547220\",\"product\":\"Sfst\",\"monitor_url\":\"\",\"structs\":[{\"scheme\":\"\",\"name\":\"\"}],\"__tmeta\":{\"idx_type\":\"277830\",\"idx_product\":\"Brand\",\"id\":\"277830\",\"type\":\"ad\",\"resp_type\":\"ad\",\"idx_channel\":\"agent\",\"idx_value\":630},\"ad_replace\":{\"source\":\"<a href=\\\"https:\\/\\/weibo.com\\/5189707911\\/G9lGDtMec?type=comment#_rnd1524482002759\\\" rel=\\\"nofollow\\\">微博创作者广告共享计划<\\/a>\"},\"adtype\":\"1\",\"adstyle\":7,\"promotion_objective\":88020003,\"tag\":\"软件\",\"mark\":\"5_reallog_mark_ad:8|5_1529895523121104094005000002139476\"}],\"error\":\"\"}");
		out.flush();
		out.close();
	}

	/**
	 * The doPost method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to post.
	 *
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/json");
		PrintWriter out = response.getWriter();
		out.println("{\"errno\":0,\"data\":[{\"recommend\":\"广告\",\"mediainfo\":[{\"is_filter_duplicated\":1,\"autoplay\":0}],\"open_adaptive\":{\"is_hide_repost\":0,\"is_new_style\":1,\"is_hide_comment\":0,\"is_hide_like\":0,\"trend_status_source_type\":1},\"id\":\"4240385154547220\",\"product\":\"Sfst\",\"monitor_url\":\"\",\"structs\":[{\"scheme\":\"\",\"name\":\"\"}],\"__tmeta\":{\"idx_type\":\"277830\",\"idx_product\":\"Brand\",\"id\":\"277830\",\"type\":\"ad\",\"resp_type\":\"ad\",\"idx_channel\":\"agent\",\"idx_value\":630},\"ad_replace\":{\"source\":\"<a href=\\\"https:\\/\\/weibo.com\\/5189707911\\/G9lGDtMec?type=comment#_rnd1524482002759\\\" rel=\\\"nofollow\\\">微博创作者广告共享计划<\\/a>\"},\"adtype\":\"1\",\"adstyle\":7,\"promotion_objective\":88020003,\"tag\":\"软件\",\"mark\":\"5_reallog_mark_ad:8|5_1529895523121104094005000002139476\"}],\"error\":\"\"}");
		out.flush();
		out.close();
	}
}