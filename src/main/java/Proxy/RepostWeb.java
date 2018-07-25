package Proxy;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class RepostWeb {


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
		long id = Long.valueOf(request.getParameter("id"));
		int page = Integer.valueOf(request.getParameter("page"));
		int count = Integer.valueOf(request.getParameter("count"));

		PrintWriter out = response.getWriter();
		out.println("{\"errno\":0,\"error\":\"\",\"data\":[{\"recommend\":\"广告\",\"adtype\":\"1\",\"id\":\"4223210045571265\",\"product\":\"Sfst\",\"monitor_url\":[{\"third_party_click\":\"https://e.cn.miaozhen.com/r/k=2070777&p=7CmBF&dx=__IPDX__&rt=2&ns=__IP__&ni=__IESID__&v=__LOC__&xa=__ADPLATFORM__&tr=__REQUESTID__&mo=__OS__&m0=__OPENUDID__&m0a=__DUID__&m1=__ANDROIDID1__&m1a=__ANDROIDID__&m2=__IMEI__&m4=__AAID__&m5=__IDFA__&m6=__MAC1__&m6a=__MAC__&o=\",\"exp_video\":\"\",\"exp_display\":\"https://g.cn.miaozhen.com/x/k=2070777&p=7CmBF&dx=__IPDX__&rt=2&ns=__IP__&ni=__IESID__&v=__LOC__&xa=__ADPLATFORM__&tr=__REQUESTID__&mo=__OS__&m0=__OPENUDID__&m0a=__DUID__&m1=__ANDROIDID1__&m1a=__ANDROIDID__&m2=__IMEI__&m4=__AAID__&m5=__IDFA__&m6=__MAC1__&m6a=__MAC__&vx=__VIEWSEQ__&ve=__DISVID__&vg=__IFAUTO__&vd=__DETAIL__&vf=__GROUP__&va=1&o=\",\"third_party_show\":\"\"}],\"promotion_objective\":\"88020003\",\"mark\":\"3_0B0847E506FFFC1A97ABB2387BEA88E2B77796D1E2E44FFE8392D49DFBBC859597EE78C0D7EBD8BE79A9D6C7241295911C13C24086C7F43B0E8F559F33CF93C902B46FF6B64935509574F6E08F0CDB5149D36251FCD7A5F8A7A687272512BC90\",\"product\":\"Sfst\",\"ad_replace\":{\"source\":\"<a href=\\\"https://weibo.com/5189707911/G9lGDtMec?type=comment#_rnd1524482002759\\\" rel=\\\"nofollow\\\">微博创作者广告共享计划</a>\"},\"open_adaptive\":{\"is_hide_repost\":2,\"is_hide_comment\":1,\"trend_status_source_type\":1,\"is_hide_like\":0,\"is_new_style\":1},\"adstyle\":100,\"structs\":\"\"}],\"error\":\"\"}");
		out.flush();
		out.close();
	}
}
