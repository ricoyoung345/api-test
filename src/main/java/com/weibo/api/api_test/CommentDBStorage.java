package com.weibo.api.api_test;

import cn.sina.api.SinaUser;
import cn.sina.api.Status;
import cn.sina.api.commons.util.ApiLogger;
import cn.sina.api.data.dao.impl2.strategy.TableChannel;
import cn.sina.api.data.dao.impl2.strategy.TableContainer;
import cn.sina.api.data.dao.util.JdbcTemplate;
import cn.sina.api.data.model.Comment;
import cn.sina.api.data.model.CommentPBUtil;
import cn.sina.api.data.model.StatusPBUtil;
import cn.sina.api.data.service.SinaUserService;
import cn.sina.api.data.storage.StorageAble;
import cn.vika.memcached.CasValue;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;

public class CommentDBStorage extends StorageAble<byte[]> {
	private TableContainer tableContainer;
	SinaUserService sinaUserService;

	@Override
	public byte[] get(String key) {
		if(ApiLogger.isDebugEnabled()){
			ApiLogger.debug(new StringBuilder(32).append("Load Content from db, key=").append(key));
		}

		String sqlKey = getSqlKey(key);
		if("0".equals(sqlKey)){
			ApiLogger.warn(new StringBuilder(64).append("Warn: content sql key is zero:").append(key));
			return null;
		}
		if(key.endsWith(CacheSuffix.CONTENT_CACHE_COMMENT_PB)){
			Comment comment = getComment(Long.valueOf(sqlKey));
			if (comment != null)
				return CommentPBUtil.toPB(comment, false);
		}else{
			throw new IllegalArgumentException("Error: unsupport type for get content, key=" + key);
		}
		return new byte[0];
	}

	Comment getComment(long id) {
		Map<Long, Status> statusMap = ExportCommentList.statusMapLocal.get();
		tableContainer = ExportCommentList.tableContainerLocal.get();
		sinaUserService = ExportCommentList.sinaUserServiceLocal.get();

		TableChannel channel = tableContainer.getTableChannel("comment", "GET_CONTENT", id, id);
		String sql = channel.getSql();

		try {
			Comment comment = (Comment)channel.getJdbcTemplate().query(sql, new Long[]{id}, new ResultSetExtractor(){
				public Comment extractData(ResultSet rs) throws SQLException, DataAccessException {
					if(rs.next()){
						Comment comment = CommentPBUtil.parseFromPB(rs.getBytes("content"), true);
						if(comment != null){
							comment.id = rs.getLong("id");
							comment.author = sinaUserService.getSinaUser(comment.getAuthorId());
							if (statusMap.get(comment.src_status_id_db) != null)
								comment.srcStatus = statusMap.get(comment.src_status_id_db);
							else
								comment.srcStatus = getStatus(comment.src_status_id_db, true);
//							if (comment.replyComment != null)
//								comment.replyComment.author = sinaUserService.getSinaUser(comment.replyComment.getAuthorId());
							if (comment.author != null && comment.srcStatus != null)
								return comment;
						}
					}
					return null;
				}
			});

			return comment;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	Status getStatus(final long id, final boolean loadDeleted) {
		TableChannel channel = tableContainer.getTableChannel("status", "GET_CONTENT", id, id);
		JdbcTemplate template = channel.getJdbcTemplate();
		String sql = channel.getSql();

		Status status = (Status) template.query(sql, new Long[] {id}, new ResultSetExtractor() {
			@Override
			public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
				if (rs.next()) {
					Status status = StatusPBUtil.parseFromPB(rs.getBytes("content"), loadDeleted);
					if (status != null) {
						status.id = id;
						SinaUser sinaUser = sinaUserService.getSinaUser(status.getAuthorId());
						status.author = sinaUser;
						return status;
					}
				}
				return null;
			}
		});

		return status;
	}

	@Override
	public CasValue<byte[]> getCas(String s) {
		return null;
	}

	@Override
	public Map<String, byte[]> getMulti(String[] strings) {
		return null;
	}

	@Override
	public boolean set(String s, byte[] bytes) {
		return false;
	}

	@Override
	public boolean setCas(String s, CasValue<byte[]> casValue) {
		return false;
	}

	@Override
	public boolean set(String s, byte[] bytes, Date date) {
		return false;
	}

	@Override
	public boolean setCas(String s, CasValue<byte[]> casValue, Date date) {
		return false;
	}

	@Override
	public boolean delete(String s) {
		return false;
	}
}
