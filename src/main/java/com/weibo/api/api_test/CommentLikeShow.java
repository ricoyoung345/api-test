package com.weibo.api.api_test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import cn.sina.api.commons.util.ApacheHttpClient;
import org.apache.commons.collections.MapUtils;

import com.weibo.api.motan.util.CollectionUtil;

import cn.sina.api.PostSource;
import cn.sina.api.SinaUser;
import cn.sina.api.commons.util.ApiLogger;
import cn.sina.api.commons.util.ArrayUtil;
import cn.sina.api.commons.util.JsonBuilder;
import cn.sina.api.commons.util.JsonWrapper;
import cn.sina.api.commons.util.Util;
import cn.sina.api.data.dao.AttitudeShardDao;
import cn.sina.api.data.model.Attitude;
import cn.sina.api.data.model.AttitudePBUtil;
import cn.sina.api.data.model.IdFlag;
import cn.sina.api.data.service.SinaUserService;
import cn.sina.api.data.storage.StorageAble;
import cn.sina.api.data.storage.StorageProxyFactory;
import net.sf.json.JSONArray;

public class CommentLikeShow {
	@Resource
	AttitudeShardDao attitudeShardDao;
	@Resource
	SinaUserService sinaUserService;
	@Resource
	StorageProxyFactory storageProxy;

	ApacheHttpClient httpclient=new ApacheHttpClient();

	public List<Attitude> getAttitudeList(List<Long> attitudeIds) {
		long[] ids = ArrayUtil.toLongArr(attitudeIds);
		// 从缓存和数据库获取表态内容数据
		int len = ids.length;
		String[] keys = new String[len];
		for (int i = 0; i < len; i++) {
			keys[i] = StorageAble.getCacheKey(ids[i], StorageAble.Type.content_id_attitude_pb);
			if(ApiLogger.isDebugEnabled()){
				ApiLogger.debug("attitude getAttitudes key:"+keys[i]);
			}
		}
		Map<String, byte[]> values = storageProxy.getAttitudeContentStorageProxy().getMulti(keys);

		// 反序列化
		List<Attitude> attitudees = new ArrayList<Attitude>(len==0?10:len);
		for (int i = 0; i < len; i++) {
			byte[] v = values.get(keys[i]);
			Attitude attitude = AttitudePBUtil.parseFromPB(v);
			if (attitude != null) {
				attitude = attitudeShardDao.getRawAttitude(ids[i]);
			}

			long attitudeAuthorId = attitude.getAuthorId();
			Map<Long, SinaUser> sinaUserMap = sinaUserService.getBareSinaUsers(new long[]{attitudeAuthorId});
			if (MapUtils.isNotEmpty(sinaUserMap) && sinaUserMap.get(attitudeAuthorId) != null) { attitude.author = sinaUserMap.get(attitudeAuthorId);}
			if (attitude != null && attitude.author != null) { attitudees.add(attitude); }
		}

		return attitudees;
	}

	String getJson(List<Attitude> attitudes) {
		JsonBuilder jsonResult = new JsonBuilder();
		JSONArray jsonArray = new JSONArray();
		if (attitudes != null && !CollectionUtil.isEmpty(attitudes)) {
			for (Attitude attitude : attitudes) {
				JsonBuilder attitudeJson = new JsonBuilder();
				StringBuilder stringBuilder = new StringBuilder();
				stringBuilder.append(attitude.getAuthorId());
				attitudeJson.append("attitude", stringBuilder.toString());
				jsonArray.add(attitudeJson.flip().toString());
			}
		}

		jsonResult.appendJsonValue("attitudes", jsonArray.toString() == null ? "[]" : jsonArray.toString());
		return jsonResult.flip().toString();
	}

	public PostSource getPostSource(int sourceid) {
		try {
			String rt = httpclient.getURL("http://i.open.t.sina.com.cn/openapi/getappinfobyappid.php?appid="+sourceid);
			JsonWrapper msg = new JsonWrapper(rt);
			if(msg.get("result.app_id")!=null){
				return new PostSource(Util.convertInt(msg.get("result.app_id")),msg.get("result.app_name"),msg.get("result.source_url"));
			}
		} catch (Exception e) {
			ApiLogger.warn("Error: getPostSource, app_key:" + sourceid, e);
		}
		return PostSource.defaultPostSource;
	}


	public void main() {
		long id = 0;
		int count = 1000;
		boolean isAsc = false;
		List<IdFlag> idflags = attitudeShardDao.getStatusAttitudeUid(id, 0, count);
		List<Long> ids = new ArrayList<Long>(idflags.size());
		if (idflags != null && idflags.size() > 0) {
			for (IdFlag idFlag : idflags) {
				ids.add(idFlag.getId());
			}
		}
		List<Attitude> attitudeList = getAttitudeList(ids);
		String json = getJson(attitudeList);
	}
}