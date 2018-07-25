package com.weibo.api.api_test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;

import cn.sina.api.data.dao.impl2.RepostDualDaoImpl;
import cn.sina.api.data.dao.impl2.strategy.TableChannel;
import cn.sina.api.data.dao.impl2.strategy.TableContainer;
import cn.sina.api.data.dao.util.DaoUtil;
import cn.sina.api.data.model.Comment;
import cn.sina.api.data.model.CounterType;
import cn.sina.api.data.storage.StorageAble;
import cn.sina.api.data.util.ProfileLogUtils;
import cn.sina.api.mcq.DataAccessType;
import cn.sina.api.mcq.McqDataAccessException;
import cn.sina.api.user.model.UserAttr;
import com.weibo.api.engine.comment.context.CommentHotFlowRedisType;
import com.weibo.api.engine.comment.service.CommentHotFlowRedisService;
import com.weibo.api.feed.framework.model.ResourceMonitorUtil;
import net.sf.json.JSONArray;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.weibo.api.commons.util.HashUtil;

import cn.sina.api.commons.util.ApacheHttpClient;
import cn.sina.api.commons.util.ApiUtil;
import cn.sina.api.commons.util.ArrayUtil;
import cn.sina.api.commons.util.Base62Parse;
import cn.sina.api.commons.util.Util;
import cn.sina.api.commons.util.UuidHelper;
import cn.sina.api.data.model.CommentHotFlowMeta;
import cn.sina.api.data.util.StatusHotCommentUtil;
import cn.sina.api.user.model.VerifiedTypeExt;
import cn.sina.api.user.service.UserVerifiedService;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.ReflectionUtils;
import reactor.function.support.UriUtils;

import static com.weibo.api.feed.framework.model.ResourceMonitorUtil.BymeShow_4921_4928;

/**
 * Hello world!
 *
 */
public class App 
{
	public static void main(String[] args) {
		beanReader();
	}

	private static int getBitOperate(int fromBit, int toBit) {
		int result = 0;
		int init = 1;
		for (int i = fromBit; i <= toBit; i++) {
			result |= (init << i - 1);
		}

		return result;
	}

	public static final String statusId2Url(long uid, long mid) {
		if (uid <= 0 || !UuidHelper.isValidId(mid)) {
			return null;
		}

		StringBuilder urlBuilder = new StringBuilder();
		urlBuilder.append("http://weibo.com/").append(uid).append("/").append(Base62Parse.encode(mid));
		return urlBuilder.toString();
	}

	static final String FILE_READ_PATH = "/Users/erming/Desktop/exposure_object.20180624_22_24.log";

	private static void beanReader() {
		List<String> xmlList = Lists.newArrayList();
		xmlList.add("file:/Users/erming/platform/idea/weibo-api-core/src/main/resources/spring/configloader.xml");
		// xmlList.add("file:/Users/erming/platform/idea/api-engine/src/spring/comment-hot-flow.xml");
		// xmlList.add("file:/Users/erming/platform/idea/api-comment/src/spring/service/status-repost.xml");
		// xmlList.add("file:/Users/erming/platform/idea/web_v4/src/spring/graph_client.xml");
		// xmlList.add("file:/Users/erming/platform/idea/api-comment/src/spring/service/status-count.xml");

		// xmlList.add("classpath:rpc.xml");
		xmlList.add("classpath:mysql.xml");
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(ArrayUtil.toStringArr(xmlList));
		context.start();
		// UserVerifiedService userVerifiedService = (UserVerifiedService) context.getBean("userVerifiedService");
//		FriendService friendService = (FriendService) context.getBean("friendService");
//		SinaUserService sinaUserService = (SinaUserService) context.getBean("sinaUserService");
//		VClubService vClubService = (VClubService) context.getBean("vClubService");
//		UserActiveTagService userActiveTagService = (UserActiveTagService) context.getBean("userActiveTagService");
//		WbobjectRpcService wbObjectRpcService = (WbobjectRpcService) context.getBean("wbObjectRpcService");
//		ABTestClient abTestClient = (ABTestClient) context.getBean("abTestClient");
//		StatusCountService statusCountService = (StatusCountService) context.getBean("statusCountService");
//		System.out.println(wbObjectRpcService.getObjectById(String.valueOf(4238287297655623L)));
		/*CommentHotFlowRedisService commentHotFlowRedisService = (CommentHotFlowRedisService) context.getBean("commentHotFlowRedisService");
		List<CommentHotFlowMeta> commentHotFlowMetaList = commentHotFlowRedisService.getAll(4263122598926741L, 300, CommentHotFlowRedisType.defaultType);
		System.out.println(CommentHotFlowRedisType.defaultType);
		for (CommentHotFlowMeta commentHotFlowMeta : commentHotFlowMetaList) {
			System.out.println("cid: " + commentHotFlowMeta.getCid() + ", score: " + StatusHotCommentUtil.getRealScoreWithoutCid(commentHotFlowMeta.getScore()));
		}*/

		/*RepostDualDaoImpl repostDao = (RepostDualDaoImpl) context.getBean("repostDao");
		long[] ids = repostDao.getRepostTimeLineIds(4249801081494707L, 0L, 0L, 200, 1);
		for (int i = 0; i < ids.length; i++) {
			System.out.println(ids[i]);
		}
		String str = "jdbc:mysql://m4921i.mars.grid.sina.com.cn:4921/?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true";*/

		/**
		 * 检查好友关系 (检查 fromUid 是否关注了 toUid)
		 *
		 * @param fromUid
		 * @param toUid
		 * @return
		 */

		// System.out.println(friendService.isFriend(6045832505L, 5995175834L));
		// System.out.println(friendService.isFriend(6120827117L, 5995175834L));
		//System.out.println(abTestClient.getStringSwitch(1L, "comment_funny_pictures_default_search_keyword"));
		/*SinaUser sinaUser = sinaUserService.getBareSinaUser(5995175834L);
		System.out.println(sinaUser.vclub_member == Constants.VCLUB_MEMBER_TYPE);

		Map<Long, Boolean> subscribersMap = vClubService.checkSubscribers(new long[] {1761568773L}, 5350509769L);
		System.out.println(MapUtils.isNotEmpty(subscribersMap) && subscribersMap.get(1761568773L));


		UserActiveTag userActiveTag = userActiveTagService.getUserActiveTag(String.valueOf(5113652006L));
		userActiveTag = userActiveTagService.getUserActiveTag(String.valueOf(2717539745L));
		UserFrequentType userFrequentType = UserFrequentType.getUserFrequentType(userActiveTag);
		System.out.println(String.valueOf(userFrequentType.toString()));

		System.out.println(wbObjectRpcService.getObjectUuidByObjectId("1034:3ce417e3005977a8b5ed347d96e1c26d", 200L));
		System.out.println(wbObjectRpcService.get("1034:3ce417e3005977a8b5ed347d96e1c26d"));

		System.out.println(abTestClient.getBooleanSwitch(5626498571L, "comment_extend_recommend_visitor_enable", false));*/

		/*List<Long> uidList = Lists.newArrayList(2171757642L);
		List<VerifiedTypeExt> userVerifiedTypeList = userVerifiedService.getVerifiedTypeExt(ArrayUtil.toLongArr(uidList));
		for (int i = 0; i < uidList.size(); i++) {
			VerifiedTypeExt userVerifiedTypeExt = userVerifiedTypeList.get(i);
			if (userVerifiedTypeExt != null) {
				System.out.println(uidList.get(i) + ":" + userVerifiedTypeExt.getUserRead());
			} else {
				System.out.println(uidList.get(i) + ":none");
			}
		}*/

		TableContainer tableContainer = (TableContainer) context.getBean("tableContainer");
		final List<Long> ids = new ArrayList<Long>();
		Date date = new Date(1530374400000L);
		TableChannel tableChannel = tableContainer.getTableChannel("cmt_timeline", "GET_TIMELINE", 2368909530L, date);

		String sql = tableChannel.getSql();

		tableChannel.getJdbcTemplate().query(sql, new Object[]{2368909530L, Comment.VFLAG_SHOW, 0 ,20}, new RowMapper(){
			public Object mapRow(ResultSet rs, int id) throws SQLException {
				ids.add(rs.getLong("cmt_id"));
				return null;
			}
		});
		for (long id : ids) {
			System.out.println(id);
		}
		System.out.println("done");
	}

	public static void testSwap () {
		Integer a = 1;
		Integer b = 2;
		System.out.println("before swap: a=" + a + ", b=" + b);
		swap(a, b);
		System.out.println("before swap: a=" + a + ", b=" + b);

		Integer c = 1;
		Integer d = 1;
		System.out.println(c==d);
		System.out.println(c.equals(d));

		Integer e = 128;
		Integer f = 128;
		System.out.println(e==f);
		System.out.println(e.equals(f));
	}

	private static void swap (Integer num1, Integer num2) {
		try {
			Field field = Integer.class.getDeclaredField("value");
			field.setAccessible(true);
			int tmp = num1.intValue();
			field.set(num1, num2);
			field.set(num2, new Integer(tmp));
			field.setInt(num2, tmp);
		} catch (Exception e) {
			e.printStackTrace();
		}
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

	private static void processFile() {
		List<String> logList = Lists.newArrayListWithCapacity(1800000);
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(FILE_READ_PATH)));
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

	public static ApacheHttpClient httpclient = new ApacheHttpClient(100, 1000, 1000, 1024 * 1024);

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
		String GET_URL = "http://10.77.96.56:9699/uve/service/comments_list?max_id=0&c=android&ua=HUAWEI-FRD-AL10__weibo__8.4.3__android__android7.0&wm=3333_1001&from=1084395010&lang=zh_CN&appid=6&attachment=&feedtype=0&increment=&ip=10.222.68.61&list_id=&posid=pos50753fa08810c&proxy_source=3439264077&source=3439264077&uid=3002231187&blog_author_id=1889377232&blue_v=0&mid=4232784643625730&content=%E4%B8%87%E4%BA%BA%E8%BF%B7%E6%89%8B%E6%92%95%E8%85%B9%E9%BB%91%E5%A5%B3%E7%8E%B0%E5%9C%BA%E7%9B%B4%E5%87%BB%EF%BC%8C%E8%B0%81%E8%AF%B4%E6%BC%82%E4%BA%AE%E5%A5%B3%E7%94%9F%E6%B2%A1%E6%99%BA%E6%85%A7%20http://t.cn/RutdgyM%20%E2%80%8B%E2%80%8B%E2%80%8B&org_content=&unread_status=20&refresh_times=1&is_ad=false&refreshId=null";
		try {
			long totalTime = 0;
			for (int i = 0; i <= 99; i++) {
				long timeStart = System.currentTimeMillis();
				String rt = httpclient.getAsync(UriUtils.encodeHttpUrl(GET_URL, "utf-8"), 1000L);
				long timeEnd = System.currentTimeMillis();

				System.out.println(rt);
				totalTime += (timeEnd - timeStart);
			}
			System.out.println("get avg: " + totalTime/100);
		} catch (Exception e) {
			System.out.println("Whatever!");
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
		//getFI("CommentTimeline 492*：", 4921, id, 32, 1);
		//getSI("CommentNewApproval 529*：", 5291, id, 32, 8);
		//getRedisI("CommentHotFlow 20573 ~ 20604：", 20573, id, 1024, 32);
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
}
