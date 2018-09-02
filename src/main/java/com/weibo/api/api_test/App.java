package com.weibo.api.api_test;

import cn.sina.api.SinaUser;
import cn.sina.api.Status;
import cn.sina.api.commons.redis.balancer.JedisMSServer;
import cn.sina.api.commons.util.ApacheHttpClient;
import cn.sina.api.commons.util.ApiLogger;
import cn.sina.api.commons.util.ApiUtil;
import cn.sina.api.commons.util.ArrayUtil;
import cn.sina.api.commons.util.Base62Parse;
import cn.sina.api.commons.util.Util;
import cn.sina.api.commons.util.UuidHelper;
import cn.sina.api.data.dao.impl2.strategy.TableChannel;
import cn.sina.api.data.dao.impl2.strategy.TableContainer;
import cn.sina.api.data.dao.util.JdbcTemplate;
import cn.sina.api.data.model.BaseStatus;
import cn.sina.api.data.model.CmtTreeBean;
import cn.sina.api.data.model.Comment;
import cn.sina.api.data.model.CommentHotFlowMeta;
import cn.sina.api.data.model.CommentPBUtil;
import cn.sina.api.data.model.IdFlag;
import cn.sina.api.data.model.StatusFlag;
import cn.sina.api.data.model.StatusHelper;
import cn.sina.api.data.model.StatusPBUtil;
import cn.sina.api.data.service.SinaUserService;
import cn.sina.api.data.storage.StorageProxy;
import cn.sina.api.data.util.StatusHotCommentUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.weibo.api.commons.util.HashUtil;
import com.weibo.api.data.status.service.StatusCountService;
import com.weibo.api.engine.comment.service.CommentHotFlowRedisService;
import com.weibo.api.engine.comment.util.ApprovalCommentUtil;
import com.weibo.api.engine.comment.util.CommentsUtil;
import com.weibo.api.engine.core.model.CommentHotFlowContext;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import reactor.function.support.UriUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Hello world!
 *
 */
public class App 
{
	public static void main(String[] args) {
		ProcessHttp.testGet();
	}

	private static void MCTest(int keys, int listLength) {
		int keyNum = keys;
		int size_const = 300;
		int score_const = 1;
		long statusId = 3683372550715671L;
		long commentId = 4183372550715671L;
		try {
			while (keyNum-- > 0) { // 添加keyNum个key
				int listSize = listLength;
				String key = String.valueOf(statusId - keyNum) + ".hotf";
				CommentHotFlowVector commentHotFlowVector = new CommentHotFlowVector();
				commentHotFlowVector.metaList = Lists.newArrayList();
				while (listSize-- > 0) {
					long cid = (commentId - keyNum * size_const - listSize);
					double score = StatusHotCommentUtil.getStoreScoreWithCid(score_const, cid);
					CommentHotFlowMeta commentHotFlowMeta = new CommentHotFlowMeta(cid, score);
					commentHotFlowVector.metaList.add(commentHotFlowMeta);
				}
				MCClient.setValue(key, commentHotFlowVector.toPb());
			}

			System.out.println(keys + "keys, " + listLength + " item perkey");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void getIndex(long id) {
		getFI("CommentTree 486*：", 4861, id, 32, 1);
		getFI("CommentTimeline 492*：", 4921, id, 32, 1);
		getSI("CommentNewApproval 529*：", 5291, id, 32, 8);
		getRedisI("CommentHotFlow 20573 ~ 20604：", 20573, id, 1024, 32);
	}

	private static void getFI(String desc, int startPort, long id, int dbCount, int tableCount) {
		System.out.println(desc + (" port:" + (startPort + ((ApiUtil.getHash4split(id, dbCount * Math.max(1, tableCount)) / tableCount) / 4))) + " database:" + (ApiUtil.getHash4split(id, dbCount * Math.max(1, tableCount)) / tableCount));
	}

	private static void getSI(String desc, int startPort, long id, int dbCount, int tableCount) {
		System.out.println(desc + (" port:" + (startPort + ((ApiUtil.getHash4split(id, dbCount * Math.max(1, tableCount)) / tableCount) / 4))) + " database:" + (ApiUtil.getHash4split(id, dbCount * Math.max(1, tableCount)) / tableCount) + " table:" +  (ApiUtil.getHash4split(id, dbCount * Math.max(1, tableCount)) % tableCount));
	}

	private static void getRedisI(String desc, int startPort, long id, int hashGene, int tablePerDb) {
		System.out.println(desc + (" port:" + (startPort + (HashUtil.getHash(id, hashGene, "crc32", "new") / tablePerDb))));
	}

	public static final String statusId2Url(long uid, long mid) {
		if (uid <= 0 || !UuidHelper.isValidId(mid)) {
			return null;
		}

		StringBuilder urlBuilder = new StringBuilder();
		urlBuilder.append("http://weibo.com/").append(uid).append("/").append(Base62Parse.encode(mid));
		return urlBuilder.toString();
	}
}

class ProcessHttp {
	public static List<String> SHOW_URLS = Lists.newArrayList();
	public static String path = "/Users/erming/platform/idea/api-test/src/main/resources/showparam.txt";
	public static ApacheHttpClient httpclient = new ApacheHttpClient(100, 1000, 1000, 1024 * 1024);
	public static ThreadPoolExecutor POOL = new ThreadPoolExecutor(2, 2, 10L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(100), new ThreadPoolExecutor.CallerRunsPolicy());

	public static void testPost() {
		String POST_URL = "http://10.77.96.56:9699/uve/service/comments_list?";
		Map<String, String> paramMap = Maps.newHashMap();
		paramMap.put("c", "android");
		paramMap.put("ua", "HUAWEI-FRD-AL10__weibo__8.4.3__android__android7.0");
		paramMap.put("wm", "3333_1001");
		paramMap.put("from", "1084395010");
		paramMap.put("lang", "zh_CN");
		paramMap.put("appid", "6");
		paramMap.put("attachment", "");
		paramMap.put("feedtype", "0");
		paramMap.put("increment", "");
		paramMap.put("ip", "10.222.68.61");
		paramMap.put("list_id", "");
		paramMap.put("posid", "pos50753fa08810c");
		paramMap.put("proxy_source", "3439264077");
		paramMap.put("source", "3439264077");
		paramMap.put("uid", "3002231187");
		paramMap.put("blog_author_id", "1889377232");
		paramMap.put("blue_v", "0");
		paramMap.put("mid", "4232784643625730");
		paramMap.put("content", "万人迷手撕腹黑女现场直击，谁说漂亮女生没智慧 http://t.cn/RutdgyM ​​​");
		paramMap.put("org_content", "");
		paramMap.put("unread_status", "20");
		paramMap.put("refresh_times", "1");
		paramMap.put("is_ad", "false");
		paramMap.put("refreshId", "null");
		try {
			long totalTime = 0;
			for (int i = 0; i <= 99; i++) {
				long timeStart = System.currentTimeMillis();
				String rt = httpclient.postAsync(UriUtils.encodeHttpUrl(POST_URL, "utf-8"), paramMap);
				long timeEnd = System.currentTimeMillis();

				System.out.println(rt);
				totalTime += (timeEnd - timeStart);
			}
			System.out.println("post avg: " + totalTime/100);
		} catch (Exception e) {
			System.out.println("Whatever!");
		}
	}

	public static void testGet() {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(path)));
			String line = null;
			while ((line = reader.readLine()) != null)
				SHOW_URLS.add(line);

			Random random = new Random(System.currentTimeMillis());
			Map<String, String> headers = Maps.newHashMap();
			headers.put("Authorization", "Basic amluZ2ppbmdfdGVzdDMxMTE0QHNpbmEuY246MTIzMjIz");
			String URL = "http://10.39.59.72:8080/2/attitudes/show.json?";

			int count = 0;
			long startTime = System.currentTimeMillis();
			while (true) {
				StringBuilder GET_URL = new StringBuilder();
				GET_URL.append(URL);
				GET_URL.append(SHOW_URLS.get(random.nextInt(100)));

				POOL.execute(new Runnable() {
					@Override
					public void run() {
						try {
							String rt = httpclient.get(GET_URL.toString(), headers, "utf-8");
							if ((System.currentTimeMillis()) % 60 == 0)
								System.out.println(rt);
						} catch (Exception e) {
							System.out.println("httpclient error " + GET_URL);
							e.printStackTrace();
						}
					}
				});
				count++;

				long endTime = System.currentTimeMillis();
				double timecousume = ((double) endTime - (double) startTime) / 1000;
				if (timecousume < 1) timecousume = 1;
				if ((System.currentTimeMillis() / 1000) % 10 == 0)
					System.out.println("round:" + count + ", QPS:" + count / timecousume);
			}


		} catch (Exception e) {
			System.out.println("testGet error!");
			e.printStackTrace();
		}
	}
}

class ProcessFile {
	private static void process() {
		List<String> logList = Lists.newArrayListWithCapacity(1800000);
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File("")));
			String line = null;
			while ((line = reader.readLine()) != null) {
				if (StringUtils.isBlank(line)) {
					continue;
				}

				String[] logArray = line.split("\t");
				if (ArrayUtils.isEmpty(logArray) || logArray.length < 3) {
					continue;
				}

				long time = Long.valueOf(logArray[0]);
				StringBuilder stringBuilder = new StringBuilder();
				for (int i = 0; i < logArray.length; i++) {
					if (StringUtils.isNotEmpty(logArray[i]) && i >= 2 && i <= logArray.length - 4) {
						String[] objectsIds = logArray[i].split(",");
						stringBuilder.append(objectsIds[2]).append(",");
					}
				}

				if (stringBuilder.length() > 0) {
					stringBuilder.deleteCharAt(stringBuilder.length() - 1);
				} else {
					continue;
				}

				logList.add(time + " " + stringBuilder.toString());
			}
			System.out.println(logList.size());
			Scanner input = new Scanner(System.in);
			String val = null;
			System.out.println("input timerange:");
			while((val = input.next()) != null) {
				if ("q".equals(val)) {
					break;
				}

				int timeRange = Integer.valueOf(val);
				highestHitRate(logList, timeRange);
				System.out.println("input timerange:");
			}
			System.out.println("done");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
	}

	private static void highestHitRate(List<String> logList, int timeRange) {
		Queue<Map<String, Integer>> analysisQueue = Queues.newArrayDeque();
		long QueueFirstTime = 1529848800L;
		long lastComputeTime = 1529848800L;
		long maxRepeatRateBlock = 1529848800L;
		float maxRepeatRate = 0F;
		long maxBigThan2 = 0;
		long queryTimes = 0L;
		long uniqueObjects = 0L;
		DecimalFormat df = new DecimalFormat("0.00");
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");

		Map<String, Integer> idTotalCountMap = Maps.newHashMap();
		Map<String, Integer> idCountMap = Maps.newHashMap();
		for (String log : logList) {
			String[] logArray = log.split(" ");
			long time = Long.valueOf(logArray[0]);
			String ids = logArray[1];

			if (time > (QueueFirstTime + timeRange - 1)) {
				// step 1 -> 上一个1秒区间的数据放进队列，并新建一个存放当前1s区间的数据
				analysisQueue.add(idCountMap);
				idCountMap = Maps.newHashMap();

				// step 2 -> 统计timeRange区间内uid重复度
				long blockUniqueIds = idTotalCountMap.size();
				long blockTotalIds = 0L;
				long bigThan2 = 0L;
				for (int uidCount : idTotalCountMap.values()) {
					if (uidCount >= 2) {
						bigThan2++;
					}
					blockTotalIds += uidCount;
				}

				// step 3 -> 更新重复度最大的区间
				float blockRepeatRate = (float) (blockTotalIds - blockUniqueIds) / blockTotalIds;
				if (blockRepeatRate > maxRepeatRate) {
					maxRepeatRate = blockRepeatRate;
					maxRepeatRateBlock = QueueFirstTime;
					maxBigThan2 = bigThan2;
					queryTimes = blockTotalIds;
					uniqueObjects = blockUniqueIds;
				}

				// step 4 -> 区间总数map需要减去第一个区间的数据
				QueueFirstTime++;
				Map<String, Integer> firstBlockMap = analysisQueue.poll();
				for (Map.Entry<String, Integer> entry : firstBlockMap.entrySet()) {
					String id = entry.getKey();
					int firstBlockCount = entry.getValue();
					if (idTotalCountMap.containsKey(id)) {
						int blockTotalCount = idTotalCountMap.get(id);
						int residualCount = blockTotalCount - firstBlockCount;
						if (residualCount <= 0) {
							idTotalCountMap.remove(id);
						} else {
							idTotalCountMap.put(id, residualCount);
						}
					}
				}

				// step 5 -> 统计当前行的信息，放到新的uidCountMap里面去
				countIds(idCountMap, idTotalCountMap, ids);
			} else if (time > lastComputeTime) {
				// 计数数据
				analysisQueue.add(idCountMap);
				idCountMap = Maps.newHashMap();
				countIds(idCountMap, idTotalCountMap, ids);
			} else {
				countIds(idCountMap, idTotalCountMap, ids);
			}

			lastComputeTime = time;
		}

		System.out.println("maxHitRate: " + dateFormater.format(maxRepeatRateBlock * 1000L) + " ~ " + dateFormater.format((maxRepeatRateBlock + timeRange - 1) * 1000L) + "    " + df.format(maxRepeatRate) + "    uniqueObjects:" + uniqueObjects + "    bigThan2:" + maxBigThan2 + "    queryTimes:" + queryTimes);
	}

	private static void countIds(Map<String, Integer> uidCountMap, Map<String, Integer> uidTotalCountMap, String ids) {
		if (StringUtils.isBlank(ids) || uidCountMap == null || uidTotalCountMap == null) {
			return;
		}

		String[] idsArray = ids.split(",");
		if (ArrayUtils.isEmpty(idsArray)) {
			return;
		}

		for (String idStr : idsArray) {
			// 更新当前map
			Integer oldIntegerCount = uidCountMap.putIfAbsent(idStr, 1);
			if (oldIntegerCount != null) {
				int oldValue = uidCountMap.get(idStr);
				uidCountMap.replace(idStr, oldValue, ++oldValue);
			}

			// 更新区间map
			Integer oldTotalIntegerCount = uidTotalCountMap.putIfAbsent(idStr, 1);
			if (oldTotalIntegerCount != null) {
				int oldValue = uidTotalCountMap.get(idStr);
				uidTotalCountMap.replace(idStr, oldValue, ++oldValue);
			}
		}
	}

	public static List<File> getFileList(String strPath, List<File> fileList) {
		File dir = new File(strPath);
		File[] files = dir.listFiles();
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					getFileList(files[i].getAbsolutePath(), fileList);
				} else {
					String strFileName = files[i].getAbsolutePath();
					fileList.add(files[i]);
				}
			}

		}
		return fileList;
	}

	public static void analyseFile(List<File> fileList) {
		if (CollectionUtils.isEmpty(fileList)) {
			System.out.println("empty");
		}
		try {
			Set<Long> set50 = Sets.newHashSet();
			Set<Long> set100 = Sets.newHashSet();
			Set<Long> set200 = Sets.newHashSet();
			Set<Long> set300 = Sets.newHashSet();
			for (File file : fileList) {
				String filePath = file.getAbsolutePath();
				if (filePath == null || filePath.endsWith("statistic.txt") || !filePath.endsWith(".txt")) {
					continue;
				}
				System.out.println(filePath);
				String writeFilePath = (filePath.split("\\."))[0] + "_statistic.txt";
				BufferedReader reader = new BufferedReader(new FileReader(filePath));
				BufferedWriter writer = new BufferedWriter(new FileWriter(writeFilePath));
				String line = reader.readLine();
				if (StringUtils.isBlank(line)) {
					continue;
				}
				setSet(set50, set100, set200, set300, line);
				while((line = reader.readLine()) != null) {
					writeFile(set50, set100, set200, set300, line, writer);
				}
				writer.flush();
			}
		} catch (IOException e) {

		} finally {

		}
	}

	public static void setSet(Set<Long> set50, Set<Long> set100, Set<Long> set200, Set<Long> set300, String line) {
		set50.clear();
		set100.clear();
		set200.clear();
		set300.clear();
		String[] array = line.split(" ");
		if (array.length < 4) {
			return;
		}
		String[] cidArray = array[3].split(",");
		for (String cidStr : cidArray) {
			try {
				Long.parseLong(cidStr);
			} catch (Exception e) {
				continue;
			}
			if (set50.size() < 20) {
				set50.add(Long.parseLong(cidStr));
			}
			if (set100.size() < 100) {
				set100.add(Long.parseLong(cidStr));
			}
			if (set200.size() < 200) {
				set200.add(Long.parseLong(cidStr));
			}
			if (set300.size() < 300) {
				set300.add(Long.parseLong(cidStr));
			}
		}
	}

	public static void writeFile(Set<Long> set50, Set<Long> set100, Set<Long> set200, Set<Long> set300, String line, BufferedWriter writer) throws IOException {
		String[] array = line.split(" ");
		if (array.length < 4) {
			return;
		}
		String[] cidArray = array[3].split(",");
		int num50 = 0;
		int num100 = 0;
		int num200 = 0;
		int num300 = 0;
		for (int i = 0; i < cidArray.length; i++) {
			try {
				Long.parseLong(cidArray[i]);
			} catch (Exception e) {
				continue;
			}
			if (i < 20 && !set50.contains(Long.parseLong(cidArray[i]))) {
				num50++;
			}
			if (i < 100 && !set100.contains(Long.parseLong(cidArray[i]))) {
				num100++;
			}
			if (i < 200 && !set200.contains(Long.parseLong(cidArray[i]))) {
				num200++;
			}
			if (i < 300 && !set300.contains(Long.parseLong(cidArray[i]))) {
				num300++;
			}
		}
		StringBuilder sb = new StringBuilder();
		sb.append(array[0]).append("\t").append(array[1]).append("\t").append(array[2]).append("\t").append(num50).append("\t").append(num100).append("\t").append(num200).append("\t").append(num300);
		System.out.println(sb.toString());
		writer.write(sb.toString() + "\r\n");
		setSet(set50, set100, set200, set300, line);
	}

	public static void hotflowAnalysis() {
		String strPath = "/Users/erming/Desktop/midCids/";
		List<File> fileList = Lists.newArrayList();
		fileList = getFileList(strPath, fileList);
		analyseFile(fileList);
	}
}

class ExportCommentList {
	public static ThreadLocal<Map<Long, Status>> statusMapLocal = new ThreadLocal<>();
	public static ThreadLocal<TableContainer> tableContainerLocal = new ThreadLocal<>();
	public static ThreadLocal<SinaUserService> sinaUserServiceLocal = new ThreadLocal<>();

	void commentListExport() {
		List<String> xmlList = Lists.newArrayList();
		xmlList.add("file:/Users/erming/platform/idea/weibo-api-core/src/main/resources/spring/configloader.xml");
		xmlList.add("file:/Users/erming/platform/idea/api-engine/src/spring/comment-hot-flow.xml");
		xmlList.add("file:/Users/erming/platform/idea/api-comment/src/spring/service/status-count.xml");
		xmlList.add("file:/Users/erming/platform/idea/api-comment/src/spring/redis/comment-tree-floor-num.xml");
		xmlList.add("file:/Users/erming/platform/idea/web_v4/src/spring/cache-service.xml");
		// xmlList.add("file:/Users/erming/platform/idea/api-comment/src/spring/service/status-repost.xml");
		// xmlList.add("file:/Users/erming/platform/idea/web_v4/src/spring/graph_client.xml");
		// xmlList.add("file:/Users/erming/platform/idea/api-comment/src/spring/service/status-count.xml");

		xmlList.add("classpath:rpc.xml");
		xmlList.add("classpath:mysql.xml");
		xmlList.add("classpath:proxy.xml");
		xmlList.add("classpath:mc.xml");
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(ArrayUtil.toStringArr(xmlList));
		context.start();
		StorageProxy<byte[]> content2StorageProxy = (StorageProxy<byte[]>) context.getBean("content2StorageProxy");
		SinaUserService sinaUserService = (SinaUserService) context.getBean("sinaUserService");
		TableContainer tableContainer = (TableContainer) context.getBean("tableContainer");
		CommentHotFlowRedisService commentHotFlowRedisService = (CommentHotFlowRedisService) context.getBean("commentHotFlowRedisService");
		JedisMSServer commentTreeFloorNumStorage = (JedisMSServer) context.getBean("commentTreeFloorNumStorage");
		StatusCountService statusCountService = (StatusCountService) context.getBean("statusCountService");

		Date date = new Date(1533052800000L);
		List<IdFlag> statusIdList = getTimelineAllIncludeNotShowWithFlag(tableContainer, 1223540840L, date);

		Map<Long, Status> statusMap = Maps.newHashMap();
		List<Status> statusList = Lists.newArrayList();
		for (IdFlag idFlag : statusIdList) {
			Status status = ExportContent.getStatus(tableContainer, sinaUserService, idFlag.getId(), true);
			statusList.add(status);
			statusMap.put(idFlag.getId(), status);
		}

		statusMapLocal.set(statusMap);
		tableContainerLocal.set(tableContainer);
		sinaUserServiceLocal.set(sinaUserService);

		Map<Long, List<Comment>> statusHotFlowMap = Maps.newHashMap();
		Map<Long, List<Comment>> rootHotChildMap = Maps.newHashMap();
		Map<Long, List<Comment>> statusTimeFlowMap = Maps.newHashMap();
		Map<Long, List<Comment>> rootTimeChildMap = Maps.newHashMap();
		for (Status status : statusList) {
			statusHotFlowMap.put(status.id, getCommentHotflow(tableContainer, sinaUserService, status.id, status, commentTreeFloorNumStorage, statusCountService, commentHotFlowRedisService, content2StorageProxy));
			statusTimeFlowMap.put(status.id, getCommentTreeList(tableContainer, sinaUserService, true, false, status.id, 2000, date, content2StorageProxy));
		}

		for (List<Comment> commentList : statusHotFlowMap.values())
			for (Comment comment : commentList)
				if (comment != null)
					rootHotChildMap.put(comment.id, getCommentHotflow(tableContainer, sinaUserService, comment.id, comment.srcStatus, commentTreeFloorNumStorage, statusCountService, commentHotFlowRedisService, content2StorageProxy));

		for (List<Comment> commentList : statusTimeFlowMap.values())
			for (Comment comment : commentList)
				if (comment != null)
					rootTimeChildMap.put(comment.id, getCommentTreeList(tableContainer, sinaUserService, false, false, comment.id, 2000, date, content2StorageProxy));

		ExportExcel.exportExcel(statusList, statusHotFlowMap, rootHotChildMap, statusTimeFlowMap, rootTimeChildMap);

		System.out.println("\ndone!");
	}

	List<Comment> getCommentHotflow(TableContainer tableContainer, SinaUserService sinaUserService, long id, Status status, JedisMSServer commentTreeFloorNumStorage, StatusCountService statusCountService, CommentHotFlowRedisService commentHotFlowRedisService, StorageProxy<byte[]> storageProxy) {
		Map<Long, Integer> statusFloorMap = Maps.newHashMap();
		Map<Long, Integer> childTotalNumberMap = statusCountService.gets(new long[] {id}, StatusCountService.Type.COMMENT);
		CommentHotFlowContext commentHotFlowContext = new CommentHotFlowContext();

		StringBuffer sb = new StringBuffer();
		sb.append(String.valueOf(id)).append(".count");
		String floorNum = commentTreeFloorNumStorage.get(sb.toString());
		statusFloorMap.put(id, (int)(floorNum == null ? 0 : Long.parseLong(floorNum)));

		commentHotFlowContext.setSrcStatusId(status.id);
		commentHotFlowContext.setStatusFloorMap(statusFloorMap);
		commentHotFlowContext.setChildTotalNumberMap(childTotalNumberMap);
		commentHotFlowContext.setNeedApprovalComment(ApprovalCommentUtil.needApprovalComment(status));
		commentHotFlowContext.setNeedNewApprovalComment(ApprovalCommentUtil.needNewApprovalComment(status));
		List<CommentHotFlowMeta> commentHotFlowMetas = commentHotFlowRedisService.getWithRevRange(id, 0, 300, commentHotFlowContext);

		List<Comment> commentList = Lists.newArrayList();
		for (CommentHotFlowMeta commentHotFlowMeta : commentHotFlowMetas)
			commentList.add(ExportContent.getComment(storageProxy, commentHotFlowMeta.getCid()));

		return commentList;
	}

	List<Comment> getCommentTreeList(TableContainer tableContainer, SinaUserService sinaUserService, boolean isMid, boolean isAsc, long id, int count, Date createdDate, StorageProxy<byte[]> storageProxy) {
		String tableName = isMid ? "cmt_tree_mid_rootid" : "cmt_tree_rootid_childid";
		String sqlName = isMid ?
				isAsc ? "GET_ROOT_COMMENT_ASC" : "GET_ROOT_COMMENT_DESC":
				isAsc ? "GET_CHILD_COMMENT_ASC" : "GET_CHILD_COMMENT_DESC";
		TableChannel tableChannel = tableContainer.getTableChannel(tableName, sqlName, id, createdDate);
		String sql = tableChannel.getSql();
		System.out.println(sql);

		List<CmtTreeBean> list = Lists.newArrayList();
		if (isMid) {
			tableChannel.getJdbcTemplate().query(sql, new Object[] { id, count }, new RowMapper() {
				@Override
				public Object mapRow(ResultSet rs, int i) throws SQLException {
					CmtTreeBean result = new CmtTreeBean();
					result.setRoot_id(rs.getLong("mid"));
					result.setChild_id(rs.getLong("cmt_root_id"));
					result.setVflag(rs.getInt("vflag"));
					result.setMflag(rs.getInt("mflag"));
					list.add(result);
					return null;
				}
			});
		} else {
			tableChannel.getJdbcTemplate().query(sql, new Object[] { id, count }, new RowMapper() {
				@Override
				public Object mapRow(ResultSet rs, int i) throws SQLException {
					CmtTreeBean result = new CmtTreeBean();
					result.setRoot_id(rs.getLong("cmt_root_id"));
					result.setChild_id(rs.getLong("cmt_child_id"));
					result.setVflag(rs.getInt("vflag"));
					result.setMflag(rs.getInt("mflag"));
					list.add(result);
					return null;
				}
			});
		}

		List<Comment> commentList = Lists.newArrayList();
		for (CmtTreeBean cmtTreeBean : list)
			commentList.add(ExportContent.getComment(storageProxy, cmtTreeBean.getChild_id()));

		return commentList;
	}

	List<IdFlag> getTimelineAllIncludeNotShowWithFlag(final TableContainer tableContainer, final long uid, Date time) {
		final List<IdFlag> idFlags = Lists.newArrayList();

		TableChannel channel =
				tableContainer.getTableChannel("timeline", "GET_TIMELINE_ALL_IN_MONTH_WITH_FLAG_ALL", uid,
						time);
		JdbcTemplate template = channel.getJdbcTemplate();
		String sql = channel.getSql();

		template.query(sql, new Object[] {uid}, new RowMapper() {
			public Object mapRow(ResultSet rs, int id) throws SQLException {
				long statusId = rs.getLong("status_id");

				long flag = getOldFlagFromTimeline(uid, rs);

				idFlags.add(new IdFlag(statusId, flag));

				return null;
			}
		});

		return idFlags;
	}

	long getOldFlagFromTimeline(long uid, ResultSet rs) throws SQLException {
		byte vflag = rs.getByte("vflag");
		int fflag = rs.getInt("fflag");
		int source = getSourceValue(uid, rs.getLong("source"));

		return new StatusFlag(vflag, fflag, source).parserToOldFlag();
	}

	int getSourceValue(long uid, long source) {
		if (source > Integer.MAX_VALUE) {
			ApiLogger.warn(new StringBuilder(128).append("invalid source, uid=").append(uid)
					.append(" source=").append(source));

			return 1;
		}

		return (int) source;
	}
}

class ExportExcel {
	public static void exportExcel(List<Status> statusList, Map<Long, List<Comment>> statusHotFlowMap, Map<Long, List<Comment>> rootHotChildMap, Map<Long, List<Comment>> statusTimeFlowMap, Map<Long, List<Comment>> rootTimeChildMap) {
		try {
			System.out.println("start excel");

			SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
			for(Status status : statusList) {
				if (status == null)
					continue;

				HSSFWorkbook workbook = new HSSFWorkbook();
				HSSFCellStyle sheetStyle = getHssfSheetCellStyle(workbook);
				HSSFFont headfont = getHssfHeartFont(workbook);
				HSSFCellStyle headstyle = getHssfHeadCellStyle(workbook, headfont);
				HSSFFont font = getHssfFont(workbook);
				HSSFCellStyle style = getHssfCellStyle(workbook, font);
				HSSFCellStyle centerstyle = getHssfCenterCellStyle(workbook, font);

				System.out.println("[Excel export]: " + status.id);
				exportFlow("热门", statusHotFlowMap, rootHotChildMap, dateFormater, status, workbook, sheetStyle, headstyle, style, centerstyle);
				exportFlow("时间", statusTimeFlowMap, rootTimeChildMap, dateFormater, status, workbook, sheetStyle, headstyle, style, centerstyle);

				String filename = "/Users/erming/Desktop/ids/" + status.id + ".xls";//设置下载时客户端Excel的名称
				workbook.write(new FileOutputStream(new File(filename)));
			}
		} catch (Exception ex) {

		}
	}

	private static HSSFCellStyle getHssfSheetCellStyle(HSSFWorkbook workbook) {
		HSSFCellStyle sheetStyle = workbook.createCellStyle();
		sheetStyle.setFillBackgroundColor(HSSFColor.GREY_25_PERCENT.index);
		sheetStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
		return sheetStyle;
	}

	private static HSSFFont getHssfHeartFont(HSSFWorkbook workbook) {
		HSSFFont headfont = workbook.createFont();
		headfont.setFontName("黑体");
		headfont.setFontHeightInPoints((short) 22);// 字体大小
		headfont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);// 加粗
		return headfont;
	}

	private static HSSFCellStyle getHssfHeadCellStyle(HSSFWorkbook workbook, HSSFFont headfont) {
		HSSFCellStyle headstyle = workbook.createCellStyle();
		headstyle.setFont(headfont);
		headstyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 左右居中
		headstyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 上下居中
		headstyle.setLocked(true);
		return headstyle;
	}

	private static HSSFCellStyle getHssfCenterCellStyle(HSSFWorkbook workbook, HSSFFont font) {
		HSSFCellStyle centerstyle = workbook.createCellStyle();
		centerstyle.setFont(font);
		centerstyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 左右居中
		centerstyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 上下居中
		centerstyle.setWrapText(true);
		centerstyle.setLeftBorderColor(HSSFColor.BLACK.index);
		centerstyle.setBorderLeft((short) 1);
		centerstyle.setRightBorderColor(HSSFColor.BLACK.index);
		centerstyle.setBorderRight((short) 1);
		centerstyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); // 设置单元格的边框为粗体
		centerstyle.setBottomBorderColor(HSSFColor.BLACK.index); // 设置单元格的边框颜色．
		centerstyle.setFillForegroundColor(HSSFColor.WHITE.index);// 设置单元格的背景颜色．
		return centerstyle;
	}

	private static HSSFFont getHssfFont(HSSFWorkbook workbook) {
		HSSFFont font = workbook.createFont();
		font.setFontName("宋体");
		font.setFontHeightInPoints((short) 12);
		return font;
	}

	private static HSSFCellStyle getHssfCellStyle(HSSFWorkbook workbook, HSSFFont font) {
		HSSFCellStyle style = workbook.createCellStyle();
		style.setFont(font);
		style.setAlignment(HSSFCellStyle.ALIGN_LEFT);// 左右居中
		style.setVerticalAlignment(HSSFCellStyle.VERTICAL_TOP);// 上下居中
		style.setWrapText(true);
		style.setLeftBorderColor(HSSFColor.BLACK.index);
		style.setBorderLeft((short) 1);
		style.setRightBorderColor(HSSFColor.BLACK.index);
		style.setBorderRight((short) 1);
		style.setBorderBottom(HSSFCellStyle.BORDER_THIN); // 设置单元格的边框为粗体
		style.setBottomBorderColor(HSSFColor.BLACK.index); // 设置单元格的边框颜色．
		style.setFillForegroundColor(HSSFColor.WHITE.index);// 设置单元格的背景颜色
		return style;
	}

	private static void exportFlow(String type, Map<Long, List<Comment>> statusHotFlowMap, Map<Long, List<Comment>> rootHotChildMap, SimpleDateFormat dateFormater, Status status, HSSFWorkbook workbook, HSSFCellStyle sheetStyle, HSSFCellStyle headstyle, HSSFCellStyle style, HSSFCellStyle centerstyle) {
		int rowNum = 0;
		String link = App.statusId2Url(status.getAuthorId(), status.id);

		HSSFSheet sheet = workbook.createSheet(String.valueOf(type));
		for (int i = 0; i <= 4; i++) { sheet.setDefaultColumnStyle((short) i, sheetStyle); }
		sheet.setColumnWidth(0, 6000);
		sheet.setColumnWidth(1, 6000);
		sheet.setColumnWidth(2, 6000);
		sheet.setColumnWidth(3, 6000);
		sheet.setColumnWidth(4, 6000);
		sheet.setColumnWidth(5, 6000);

		HSSFRow row = sheet.createRow(rowNum);
		row.setHeight((short) 800);
		HSSFCell cell = row.createCell(0);
		cell.setCellType(HSSFCell.CELL_TYPE_FORMULA);
		cell.setCellFormula("HYPERLINK(\"" + link + "\",\""+ link +"\")");
		cell.setCellStyle(headstyle);
		sheet.addMergedRegion(new CellRangeAddress(rowNum,rowNum,0,4));

		row = sheet.createRow(++rowNum);
		cell = row.createCell(0);
		cell.setCellValue("微博作者uid");
		cell.setCellStyle(style);
		cell = row.createCell(1);
		cell.setCellValue("微博作者昵称");
		cell.setCellStyle(style);
		cell = row.createCell(2);
		cell.setCellValue("微博内容");
		cell.setCellStyle(style);
		cell = row.createCell(3);
		cell.setCellValue("发表时间");
		cell.setCellStyle(style);
		cell = row.createCell(4);
		cell.setCellValue("微博状态");
		cell.setCellStyle(style);

		setRowText(sheet, ++rowNum, style, status, dateFormater);

		row = sheet.createRow(++rowNum);
		row = sheet.createRow(++rowNum);
		cell = row.createCell(0);
		cell.setCellValue("评论列表");
		cell.setCellStyle(centerstyle);
		sheet.addMergedRegion(new CellRangeAddress(rowNum,rowNum,0,5));

		row = sheet.createRow(++rowNum);
		cell = row.createCell(0);
		cell.setCellValue("评论作者uid");
		cell.setCellStyle(style);
		cell = row.createCell(1);
		cell.setCellValue("评论作者昵称");
		cell.setCellStyle(style);
		cell = row.createCell(2);
		cell.setCellValue("评论内容");
		cell.setCellStyle(style);
		cell = row.createCell(3);
		cell.setCellValue("评论时间");
		cell.setCellStyle(style);
		cell = row.createCell(4);
		cell.setCellValue("一二级");
		cell.setCellStyle(style);
		cell = row.createCell(5);
		cell.setCellValue("评论状态");
		cell.setCellStyle(style);

		if (MapUtils.isNotEmpty(statusHotFlowMap)) {
			List<Comment> rootComments = statusHotFlowMap.get(status.id);
			if (CollectionUtils.isNotEmpty(rootComments))
				for (Comment rootComment : rootComments) {
					setRowText(sheet, ++rowNum, style, rootComment, dateFormater);
					if (MapUtils.isNotEmpty(rootHotChildMap)) {
						List<Comment> childComments = rootHotChildMap.get(rootComment.id);
						if (CollectionUtils.isNotEmpty(childComments))
							for (Comment childComment : childComments)
								setRowText(sheet, ++rowNum, style, childComment, dateFormater);
					}
					sheet.createRow(++rowNum);
				}
		}
	}

	static void setRowText(HSSFSheet sheet, int rowNum, HSSFCellStyle style, Status status, SimpleDateFormat dateFormater) {
		HSSFRow row = sheet.createRow(rowNum);
		row.setHeight((short) 800);
		HSSFCell cell = row.createCell(0);
		cell.setCellValue(status.getAuthorId());
		cell.setCellStyle(style);
		cell = row.createCell(1);
		cell.setCellValue(status.author != null ? status.author.screen_name : String.valueOf(status.getAuthorId()));
		cell.setCellStyle(style);
		cell = row.createCell(2);
		cell.setCellValue(status.text);
		cell.setCellStyle(style);
		cell = row.createCell(3);
		cell.setCellValue(dateFormater.format(status.created_at));
		cell.setCellStyle(style);
		cell = row.createCell(4);
		cell.setCellValue(State.mflagMeanMap.get((int) status.state) != null ? State.mflagMeanMap.get((int) status.state) : "默认");
		cell.setCellStyle(style);
	}

	static void setRowText(HSSFSheet sheet, int rowNum, HSSFCellStyle style, Comment comment, SimpleDateFormat dateFormater) {
		HSSFRow row = sheet.createRow(rowNum);
		HSSFCell cell = row.createCell(0);
		cell.setCellValue(comment.uid_db);
		cell.setCellStyle(style);
		cell = row.createCell(1);
		cell.setCellValue(comment.author != null ? comment.author.screen_name : "author");
		cell.setCellStyle(style);
		cell = row.createCell(2);
		cell.setCellValue(comment.text);
		cell.setCellStyle(style);
		cell = row.createCell(3);
		cell.setCellValue(dateFormater.format(comment.created_at));
		cell.setCellStyle(style);
		cell = row.createCell(4);
		cell.setCellValue(CommentsUtil.isOriginRootComment(comment) ? "一" : "二");
		cell.setCellStyle(style);
		cell = row.createCell(5);
		cell.setCellValue(State.mflagMeanMap.get((int) comment.state) != null ? State.mflagMeanMap.get((int) comment.state) : "默认");
		cell.setCellStyle(style);
	}

}

class ExportContent {
	private static void testStatus() {
		List<String> xmlList = Lists.newArrayList();
		xmlList.add("file:/Users/erming/platform/idea/weibo-api-core/src/main/resources/spring/configloader.xml");

		xmlList.add("classpath:mysql.xml");
		xmlList.add("classpath:rpc.xml");
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(ArrayUtil.toStringArr(xmlList));
		context.start();
		SinaUserService sinaUserService = (SinaUserService) context.getBean("sinaUserService");
		TableContainer tableContainer = (TableContainer) context.getBean("tableContainer");

		Date date = new Date(1533052800000L);
		Status status = getStatus(tableContainer, sinaUserService, 4278668047843758L, true);
		System.out.println("wait");
	}

	private static void testComment() {
		List<String> xmlList = Lists.newArrayList();
		xmlList.add("file:/Users/erming/platform/idea/weibo-api-core/src/main/resources/spring/configloader.xml");
		xmlList.add("file:/Users/erming/platform/idea/web_v4/src/spring/cache-service.xml");

		xmlList.add("classpath:rpc.xml");
		xmlList.add("classpath:mysql.xml");
		xmlList.add("classpath:proxy.xml");
		xmlList.add("classpath:mc.xml");
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(ArrayUtil.toStringArr(xmlList));
		context.start();
		StorageProxy<byte[]> content2StorageProxy = (StorageProxy<byte[]>) context.getBean("content2StorageProxy");
		SinaUserService sinaUserService = (SinaUserService) context.getBean("sinaUserService");
		TableContainer tableContainer = (TableContainer) context.getBean("tableContainer");
		ExportCommentList.tableContainerLocal.set(tableContainer);
		ExportCommentList.sinaUserServiceLocal.set(sinaUserService);

		Date date = new Date(1533052800000L);
		List<Long> commentIdLst = Lists.newArrayList(4276207128112203L, 4276197217450986L, 4276195338325653L, 4276203366062579L, 4276200664596722L, 4276198403692661L, 4276214070199746L, 4276207044091594L, 4276208277622102L, 4276206976867580L, 4276213176305535L, 4276203806167711L, 4276196588191718L, 4276196415809532L, 4276195912992864L, 4276197515342206L, 4276197297424017L, 4276197154335980L, 4276195983591400L, 4276194935166359L);
	}

	private static void updateStatusList(TableContainer tableContainer) {
		Date date = new Date(1530374400000L);
		TableChannel tableChannel = tableContainer.getTableChannel("status_cmt", "UPDATE_STATUS_CMT_STATE", 4264946021779761L, date);
		String sql = tableChannel.getSql();
		try {
			boolean isUpdated = tableChannel.getJdbcTemplate().update(sql, new Object[]{0, 0, 4264946021779761L, 4265759767614279L, 0, 0}) > 0;
			System.out.println("\nupdateStatusList");
			System.out.println(isUpdated);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void updateBymeList(TableContainer tableContainer) {
		TableChannel tableChannel = tableContainer.getTableChannel("cmt_timeline", "UPDATE_TIMELINE_STATE", 6012794304L, 4265759767614279L);
		String sql = tableChannel.getSql();
		try {
			boolean isUpdated = tableChannel.getJdbcTemplate().update(sql, new Object[]{0, 0, 6012794304L, 4265759767614279L, 0, 0}) > 0;
			System.out.println("\nupdateBymeList");
			System.out.println(isUpdated);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean updateContentState(TableContainer tableContainer, Comment comment) {
		if (comment == null) { return false; }

		TableChannel tableChannel = tableContainer.getTableChannel("comment", "UPDATE_CONTENT", comment.id, comment.id);
		String sql = tableChannel.getSql();
		try {
			System.out.println(CommentPBUtil.toDbPB(comment).length);
			boolean isUpdated = tableChannel.getJdbcTemplate().update(sql, new Object[]{CommentPBUtil.toDbPB(comment), comment.id, CommentPBUtil.toDbPB(comment)}) > 0;
			System.out.println("\nupdateContentState");
			System.out.println(isUpdated);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	private static void getBymeList(TableContainer tableContainer) {
		try {
			Date date = new Date(1530374400000L);
			final List<BymeMeta> bymeMetas = Lists.newArrayList();
			TableChannel bymeTableChannel = tableContainer.getTableChannel("cmt_timeline", "GET_TIMELINE", 6012794304L, date);
			String sql = bymeTableChannel.getSql();

			System.out.println("\ngetBymeList");
			bymeTableChannel.getJdbcTemplate().query(sql, new Object[]{6012794304L, Comment.VFLAG_SHOW, 0 ,20}, new RowMapper(){
				public Object mapRow(ResultSet rs, int id) throws SQLException {
					BymeMeta bymeMeta = new BymeMeta();
					bymeMeta.uid = rs.getLong("uid");
					bymeMeta.cmt_id = rs.getLong("cmt_id");
					bymeMeta.type = rs.getInt("type");
					bymeMeta.mflag = rs.getInt("mflag");
					bymeMeta.vflag = rs.getInt("vflag");
					bymeMetas.add(bymeMeta);
					return null;
				}
			});
			for (BymeMeta bymeMeta : bymeMetas) {
				System.out.println("uid:" + bymeMeta.uid + "\tcmt_id:" + bymeMeta.cmt_id + "\ttype:" + bymeMeta.type + "\tmflag:" + bymeMeta.mflag + "\tvflag:" + bymeMeta.vflag);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void getStatusShowList(TableContainer tableContainer, SinaUserService sinaUserService) {
		try {
			Date date = new Date(1533052800000L);
			TableChannel statusTableChannel = tableContainer.getTableChannel("status_cmt", "GET_STATUS_COMMENTMETA_ALL_IN_MONTH", 4268594756270940L, date);
			String statusSql = statusTableChannel.getSql();
			Object[] paramsObject = new Object[]{ 4268594756270940L, Comment.VFLAG_SHOW};
			final List<StatusMeta> statusMetas = Lists.newArrayList();

			statusTableChannel.getJdbcTemplate().query(statusSql, paramsObject, new RowMapper() {
				@Override
				public Object mapRow(ResultSet rs, int i) throws SQLException {
					StatusMeta statusMeta = new StatusMeta();
					statusMeta.status_id = rs.getLong("status_id");
					statusMeta.cmt_id = rs.getLong("cmt_id");
					statusMeta.mflag = rs.getInt("mflag");
					statusMeta.uid = rs.getLong("uid");
					statusMeta.vflag = rs.getInt("vflag");
					statusMetas.add(statusMeta);
					return null;
				}
			});
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String getState(Comment comment) {
		if (comment == null) {
			return "";
		}

		int apiState = StatusHelper.getApiStateByState(comment.state);
		if (apiState == BaseStatus.STATE_SHOW) {
			return "公开可见";
		} else if (apiState == BaseStatus.STATE_SHOW_SELF) {
			return "仅评论人可见";
		} else {
			return "已删除";
		}
	}

	static Comment getComment(StorageProxy<byte[]> content2StorageProxy, long id) {
		byte[] value = content2StorageProxy.get(id + ".ccp");
		if (value != null)
			return CommentPBUtil.parseFromPB(value);

		return null;
	}

	static Status getStatus(TableContainer tableContainer, SinaUserService sinaUserService, final long id, final boolean loadDeleted) {
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

	private static void fileChannel() {
		RandomAccessFile aFile = null;
		try {
			aFile = new RandomAccessFile("data/nio-data.txt", "rw");
			FileChannel inChannel = aFile.getChannel();

			ByteBuffer buf = ByteBuffer.allocate(48);

			int bytesRead = inChannel.read(buf);
			while (bytesRead != -1) {

				System.out.println("Read " + bytesRead);
				buf.flip();

				while(buf.hasRemaining()){
					System.out.print((char) buf.get());
				}

				buf.clear();
				bytesRead = inChannel.read(buf);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				aFile.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static byte[][] stringKeysToByteArrayKeys(Collection stringKeys) {
		if (org.apache.commons.collections.CollectionUtils.isEmpty(stringKeys)) {
			return null;
		}

		String[] stringKeysArray = ArrayUtil.toStringArr(stringKeys);
		byte[][] byteArrayKeys = new byte[stringKeys.size()][];
		for (int i = 0; i < stringKeys.size(); i++) {
			byteArrayKeys[i] = Util.toBytes(stringKeysArray[i]);
		}

		return byteArrayKeys;
	}
}

class BymeMeta {
	public long uid;
	public int vflag;
	public int type;
	public long cmt_id;
	public int mflag;
}

class StatusMeta {
	public long status_id;
	public int vflag;
	public long cmt_id;
	public int mflag;
	public long uid;
}

class State {
	public static Map<Integer, String> mflagMeanMap = Maps.newHashMap();
	static {
		mflagMeanMap.put(-1, "禁止发表");
		mflagMeanMap.put(98, "公开可见");
		mflagMeanMap.put(0, "未审核");
		mflagMeanMap.put(1, "已通过");
		mflagMeanMap.put(2, "已隐藏搜索");
		mflagMeanMap.put(3, "已设私密");
		mflagMeanMap.put(4, "先审后发");
		mflagMeanMap.put(5, "已删除");
		mflagMeanMap.put(6, "已删转发");
		mflagMeanMap.put(7, "自己删除");
		mflagMeanMap.put(8, "封杀删除");
		mflagMeanMap.put(9, "删除根");
		mflagMeanMap.put(10, "删除单条");
		mflagMeanMap.put(11, "自动设私");
		mflagMeanMap.put(12, "自动禁止搜索");
		mflagMeanMap.put(13, "禁止feed");
		mflagMeanMap.put(14, "自动禁止feed");
		mflagMeanMap.put(15, "自动广告私");
		mflagMeanMap.put(16, "海外微博自动私");
		mflagMeanMap.put(17, "海外微博私");
		mflagMeanMap.put(18, "海外微博自动隐");
		mflagMeanMap.put(19, "海外微博隐");
		mflagMeanMap.put(20, "微博发布限制，仅自己可见");
		mflagMeanMap.put(21, "广告行为先审后发");
		mflagMeanMap.put(22, "反垃圾止(禁止feed)");
		mflagMeanMap.put(23, "反垃圾自动止(禁自动止feed)");
		mflagMeanMap.put(24, "微博、评论不上榜");
		mflagMeanMap.put(25, "用户好友圈定向");
		mflagMeanMap.put(26, "审核好友圈定向");
		mflagMeanMap.put(27, "审核自动好友圈定向");
		mflagMeanMap.put(28, "评论人身攻击识别设私");
		mflagMeanMap.put(29, "被政府删除");
		mflagMeanMap.put(30, "被政府设私");
		mflagMeanMap.put(31, "被政府设好友圈");
		mflagMeanMap.put(32, "被投诉删除");
		mflagMeanMap.put(33, "被投诉设私");
		mflagMeanMap.put(34, "被投诉设好友圈");
		mflagMeanMap.put(35, "被垂直业务设私");
		mflagMeanMap.put(36, "被平台自动设私");
		mflagMeanMap.put(37, "评论审核通过");
		mflagMeanMap.put(38, "评论审核不通过");
		mflagMeanMap.put(39, "博主自己发的评论--仅供蓝V媒体用户审核评论时使用");
		mflagMeanMap.put(40, "反垃圾评论点赞有概率禁止评论上热榜");
		mflagMeanMap.put(41, "恶意导流人工私");
		mflagMeanMap.put(42, "恶意导流自动私");
		mflagMeanMap.put(43, "反垃圾自动隐");
		mflagMeanMap.put(62, "免费问答的锁定态");
		mflagMeanMap.put(63, "问答私");
	}
}
