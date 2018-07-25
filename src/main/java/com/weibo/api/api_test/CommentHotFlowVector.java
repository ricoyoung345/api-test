package com.weibo.api.api_test;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;

import com.google.common.collect.Lists;
import com.google.protobuf.InvalidProtocolBufferException;

import cn.sina.api.data.model.CommentHotFlowMeta;

public class CommentHotFlowVector {

	List<CommentHotFlowMeta> metaList;

	public byte[] toPb () {
		CommentHotFlowVectorWrap.CommentHotFlowVector.Builder  vectorBuilder = CommentHotFlowVectorWrap.CommentHotFlowVector.newBuilder();
		if (CollectionUtils.isNotEmpty(metaList)) {
			List<CommentHotFlowVectorWrap.CommentHotFlowVector.CommentHotflowMeta> feedVectorWrapper = new ArrayList<>(metaList.size());
			for (CommentHotFlowMeta meta : metaList) {
				CommentHotFlowVectorWrap.CommentHotFlowVector.CommentHotflowMeta.Builder metaItemBuilder = CommentHotFlowVectorWrap.CommentHotFlowVector.CommentHotflowMeta.newBuilder();
				metaItemBuilder.setCid(meta.getCid());
				metaItemBuilder.setScore(meta.getScore());
				feedVectorWrapper.add(metaItemBuilder.build());
			}
			vectorBuilder.addAllVector(feedVectorWrapper);
		}
		return vectorBuilder.build().toByteArray();
	}

	public CommentHotFlowVector paseFromPb (byte[] data) {
		if (ArrayUtils.isEmpty(data)) {
			return null;
		}

		CommentHotFlowVectorWrap.CommentHotFlowVector vectorWrapper = null;
		try {
			vectorWrapper = CommentHotFlowVectorWrap.CommentHotFlowVector.parseFrom(data);
		} catch (InvalidProtocolBufferException e) {
			return null;
		}
		if (vectorWrapper == null || CollectionUtils.isEmpty(vectorWrapper.getVectorList())) {
			return null;
		}

		CommentHotFlowVector hotFlowVector = new CommentHotFlowVector();
		hotFlowVector.metaList = Lists.newArrayList();
		List<CommentHotFlowVectorWrap.CommentHotFlowVector.CommentHotflowMeta> feedWrappers = vectorWrapper.getVectorList();
		for (CommentHotFlowVectorWrap.CommentHotFlowVector.CommentHotflowMeta wrapper : feedWrappers) {
			long cid = wrapper.getCid();
			double score = wrapper.getScore();
			CommentHotFlowMeta commentHotFlowMeta = new CommentHotFlowMeta(cid, score);
			hotFlowVector.metaList.add(commentHotFlowMeta);
		}

		return hotFlowVector;
	}
}
