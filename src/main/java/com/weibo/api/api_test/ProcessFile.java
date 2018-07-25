package com.weibo.api.api_test;

import cn.sina.api.commons.util.ApiLogger;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class ProcessFile {
	static final String FILE_READ_PATH = "/Users/erming/Desktop/cardExposure.log";
	private static void processFile() {
		List<String> logList = Lists.newArrayListWithCapacity(1800000);
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(FILE_READ_PATH)));
			String line = null;
			while ((line = reader.readLine()) != null) {
				/*if (StringUtils.isBlank(line)) {
					continue;
				}

				if (line.split(" ").length < 2) {
					continue;
				}

				String[] logArray = line.split(" ");
				long time = Long.valueOf(logArray[0]);

				String rootComments = null;
				String childComments = null;
				if (logArray.length == 2) {
					rootComments = logArray[1];
				} else if (logArray.length == 3) {
					rootComments = logArray[1];
					childComments = logArray[2];
				}

				StringBuilder stringBuilder = new StringBuilder();
				if (StringUtils.isNotBlank(rootComments) && rootComments.length() > 10) {
					String[] commentIdUids = rootComments.split(",");
					for (String commentIdUid : commentIdUids) {
						stringBuilder.append(commentIdUid.split(":")[1]).append(",");
					}
				}
				if (StringUtils.isNotBlank(childComments) && childComments.length() > 10) {
					String[] commentIdUids = childComments.split(",");
					for (String commentIdUid : commentIdUids) {
						stringBuilder.append(commentIdUid.split(":")[1]).append(",");
					}
				}
				if (stringBuilder.length() > 0) {
					stringBuilder.deleteCharAt(stringBuilder.length() - 1);
				} else {
					continue;
				}*/

				if (StringUtils.isBlank(line)) {
					continue;
				}

				if (line.split(" ").length < 3) {
					continue;
				}

				String[] logArray = line.split(" ");
				long time = Long.valueOf(logArray[0]);

				StringBuilder stringBuilder = new StringBuilder();
				for (int i = 0; i < logArray.length; i++) {
					if (i >= 2 && i <= logArray.length - 4) {
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
			highestHitRate(logList, 3);
			System.out.println("done");
		} catch (Exception e) {
			ApiLogger.error("processFile error", e);
		} finally {
		}
	}

	private static void highestHitRate(List<String> logList, int timeRange) {
		Queue<Map<String, Integer>> analysisQueue = Queues.newArrayDeque();
		long QueueFirstTime = 1529848800L;
		long lastComputeTime = 1529848800L;
		long maxRepeatRateBlock = 1529848800L;
		float maxRepeatRate = 0F;
		DecimalFormat df = new DecimalFormat("0.00");
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");

		Map<String, Integer> uidTotalCountMap = Maps.newHashMap();
		Map<String, Integer> uidCountMap = Maps.newHashMap();
		for (String log : logList) {
			String[] logArray = log.split(" ");
			long time = Long.valueOf(logArray[0]);
			String uids = logArray[1];

			if (time > (QueueFirstTime + timeRange - 1)) {
				// step 1 -> 上一个1秒区间的数据放进队列，并新建一个存放当前1s区间的数据
				analysisQueue.add(uidCountMap);
				uidCountMap = Maps.newHashMap();

				// step 2 -> 统计timeRange区间内uid重复度
				long blockUniqueUids = uidTotalCountMap.size();
				long blockTotalUids = 0L;
				for (int uidCount : uidTotalCountMap.values()) {
					blockTotalUids += uidCount;
				}

				// step 3 -> 更新重复读最大的区间
				float blockRepeatRate = (float) (blockTotalUids - blockUniqueUids) / blockTotalUids;
				if (blockRepeatRate > maxRepeatRate) {
					maxRepeatRate = blockRepeatRate;
					maxRepeatRateBlock = QueueFirstTime;
				}
				System.out.println(dateFormater.format(QueueFirstTime * 1000L) + " ~ " + dateFormater.format(lastComputeTime * 1000L) + " " + df.format(blockRepeatRate));

				// step 4 -> 区间总数map需要减去第一个区间的数据
				QueueFirstTime++;
				Map<String, Integer> firstBlockMap = analysisQueue.poll();
				for (Map.Entry<String, Integer> entry : firstBlockMap.entrySet()) {
					String uid = entry.getKey();
					int firstBlockCount = entry.getValue();
					if (uidTotalCountMap.containsKey(uid)) {
						int blockTotalCount = uidTotalCountMap.get(uid);
						int residualCount = blockTotalCount - firstBlockCount;
						if (residualCount <= 0) {
							uidTotalCountMap.remove(uid);
						} else {
							uidTotalCountMap.put(uid, residualCount);
						}
					}
				}

				// step 5 -> 统计当前行的信息，放到新的uidCountMap里面去
				countUids(uidCountMap, uidTotalCountMap, uids);
			} else if (time > lastComputeTime) {
				// 计数数据
				analysisQueue.add(uidCountMap);
				uidCountMap = Maps.newHashMap();
				countUids(uidCountMap, uidTotalCountMap, uids);
			} else {
				countUids(uidCountMap, uidTotalCountMap, uids);
			}

			lastComputeTime = time;
		}

		System.out.println("maxHitRate: " + dateFormater.format(maxRepeatRateBlock * 1000L) + " ~ " + dateFormater.format((maxRepeatRateBlock + timeRange - 1) * 1000L) + "    " + df.format(maxRepeatRate));
	}

	private static void countUids(Map<String, Integer> uidCountMap, Map<String, Integer> uidTotalCountMap, String uids) {
		if (StringUtils.isBlank(uids) || uidCountMap == null || uidTotalCountMap == null) {
			return;
		}

		String[] uidsArray = uids.split(",");
		if (ArrayUtils.isEmpty(uidsArray)) {
			return;
		}

		for (String uidStr : uidsArray) {
			// 更新当前map
			Integer oldIntegerCount = uidCountMap.putIfAbsent(uidStr, 1);
			if (oldIntegerCount != null) {
				int oldValue = uidCountMap.get(uidStr);
				uidCountMap.replace(uidStr, oldValue, ++oldValue);
			}

			// 更新区间map
			Integer oldTotalIntegerCount = uidTotalCountMap.putIfAbsent(uidStr, 1);
			if (oldTotalIntegerCount != null) {
				int oldValue = uidTotalCountMap.get(uidStr);
				uidTotalCountMap.replace(uidStr, oldValue, ++oldValue);
			}
		}
	}
}
