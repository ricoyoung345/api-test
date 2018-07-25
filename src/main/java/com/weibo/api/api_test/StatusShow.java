package com.weibo.api.api_test;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.MapUtils;

import com.google.common.collect.Lists;
import com.weibo.api.motan.util.CollectionUtil;

import cn.sina.api.SinaUser;
import cn.sina.api.commons.util.JsonBuilder;
import cn.sina.api.data.dao.CommentShardDao;
import cn.sina.api.data.model.BaseStatus;
import cn.sina.api.data.model.Comment;
import cn.sina.api.data.model.CommentPBUtil;
import cn.sina.api.data.model.MonthTableSI;
import cn.sina.api.data.service.CounterService;
import cn.sina.api.data.service.SinaUserService;
import cn.sina.api.data.storage.StorageAble;
import cn.sina.api.data.storage.StorageProxyFactory;
import net.sf.json.JSONArray;

public class StatusShow {
	@Resource CommentShardDao commentShardDao;
	@Resource CounterService counterService;
	@Resource SinaUserService sinaUserService;
	@Resource StorageProxyFactory storageProxy;

	public List<Long> getStatusIds(long statusId, int offset, int count, boolean isAsc) {
		// 从新库读取数据，老库中无该数据，所以不需要从老库中加载
		/////////////////////////////////////////////////////////////
		List<Long> ids = new LinkedList<Long>();
		List<MonthTableSI> monthStats = commentShardDao.getStatusCmtSI(statusId, -1);
		if(isAsc){
			Collections.reverse(monthStats);
		}
		int readThrough = 0;
		for (MonthTableSI ms : monthStats) {
			int countInMonth = ms.getCount();
			if (countInMonth <= 0) {
				continue;
			}

			readThrough += countInMonth;
			int offsetInMonth = 0;
			if(readThrough < offset){
				continue;
			}else if((readThrough - countInMonth) < offset){
				offsetInMonth = offset - (readThrough - countInMonth);
			}

			//FIXME 判断条件不准, 会多加载第一个月的数据, 最后一个月的数据少一些
			if(offsetInMonth == 0 && count - ids.size() >= ms.getCount()){
				ids.addAll(0, commentShardDao.getStatusCmtByMonth(statusId, ms.getStatDate()));
			}else{
				//最后取limit offsetInMonth,count - ids.size()
				if(isAsc){
					ids.addAll(0, commentShardDao.getStatusCmtByMonthAsc(statusId, ms.getStatDate(), offsetInMonth, count - ids.size()));
				} else {
					ids.addAll(0, commentShardDao.getStatusCmtByMonth(statusId, ms.getStatDate(),offsetInMonth, count - ids.size()));
				}
			}
			if(ids.size() >= count){
				break;
			}
		}

		//TODO：这个地方存在性能与准确性的平衡，暂时压力看看效果 fishermen 2011.1
		/////////////////////////////////////////////////////////////
		Collections.sort(ids);
		if(isAsc){
			Collections.reverse(ids);
		}

		return ids;
	}

	public List<Comment> getCommentList(List<Long> cids) {
		List<Comment> commentList = Lists.newArrayList();
		long[] commentIds = new long[cids.size()];
		for (int i=0; i < cids.size(); i++) {
			commentIds[i] = cids.get(i);
		}
		String[] keys = new String[commentIds.length];
		long key = 0;
		for (int i = 0; i < commentIds.length; i++) {
			key = commentIds[i];
			keys[i] = StorageAble.getCacheKey(key, StorageAble.Type.content_id_comment_pb);
		}
		Map<String, byte[]> values = storageProxy.getContent2StorageProxy().getMulti(keys);
		for (int i = 0; i < commentIds.length; i++) {
			byte[] v = values.get(keys[i]);
			Comment comment = null;
			if (v != null) {
				comment = CommentPBUtil.parseFromPB(v);
				if (comment == null) {
					comment = commentShardDao.getRawComment(commentIds[i], true);
				}
			} else {
				comment = commentShardDao.getRawComment(commentIds[i], true);
			}

			if (comment != null && comment.apiState == BaseStatus.STATE_SHOW) {
				long commentId = comment.id;
				long commentAuthorId = comment.getAuthorId();
				Map<Long, SinaUser> sinaUserMap = sinaUserService.getBareSinaUsers(new long[]{commentAuthorId});
				if (MapUtils.isNotEmpty(sinaUserMap) && sinaUserMap.get(commentAuthorId) != null) { comment.author = sinaUserMap.get(commentAuthorId);}

				Map<Long, Integer> likeCountMap = counterService.getStatusAttitudeCounters(new long[] {commentId});
				if (MapUtils.isNotEmpty(likeCountMap) && likeCountMap.get(commentId) != null) { comment.likeCount = likeCountMap.get(commentId);}
			}

			if (comment != null && comment.apiState == BaseStatus.STATE_SHOW && comment.author != null) {
				commentList.add(comment);
			}
		}

		return commentList;
	}

	String getJson(List<Comment> comments) {
		JsonBuilder jsonResult = new JsonBuilder();
		JSONArray jsonArray = new JSONArray();
		if (comments != null && !CollectionUtil.isEmpty(comments)) {
			for (Comment comment : comments) {
				if (comment.author == null) { continue; }
				JsonBuilder commentJson = new JsonBuilder();
				SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
				StringBuilder stringBuilder = new StringBuilder();
				stringBuilder.append(comment.author.screen_name).append("\t").append(dateFormater.format(comment.created_at)).append("\t").append(comment.text).append("\t").append(comment.likeCount != null ? comment.likeCount : 0);
				commentJson.append("comment", stringBuilder.toString());
				jsonArray.add(commentJson.flip().toString());
			}
		}
		jsonResult.appendJsonValue("comments", jsonArray.toString() == null ? "[]" : jsonArray.toString());
		return jsonResult.flip().toString();
	}

	public void main() {
		long id = 0;
		int count = 1000;
		boolean isAsc = false;
		List<Long> comemntIds = getStatusIds(id, 0, count, isAsc);
		List<Comment> commentList = getCommentList(comemntIds);
		String json = getJson(commentList);
	}
}
