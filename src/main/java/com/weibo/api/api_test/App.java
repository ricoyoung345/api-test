package com.weibo.api.api_test;

import cn.sina.api.SinaUser;
import cn.sina.api.commons.util.ApacheHttpClient;
import cn.sina.api.commons.util.ApiUtil;
import cn.sina.api.commons.util.ArrayUtil;
import cn.sina.api.commons.util.Base62Parse;
import cn.sina.api.commons.util.Util;
import cn.sina.api.data.dao.impl2.strategy.TableChannel;
import cn.sina.api.data.dao.impl2.strategy.TableContainer;
import cn.sina.api.data.model.BaseStatus;
import cn.sina.api.data.model.CmtTreeBean;
import cn.sina.api.data.model.Comment;
import cn.sina.api.data.model.CommentHotFlowMeta;
import cn.sina.api.data.model.CommentPBUtil;
import cn.sina.api.data.model.StatusHelper;
import cn.sina.api.data.service.SinaUserService;
import cn.sina.api.data.util.StatusHotCommentUtil;
import cn.sina.api.user.model.UserAttr;
import cn.sina.api.user.service.UserService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.weibo.api.commons.util.HashUtil;
import net.sf.json.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;

/**
 * Hello world!
 *
 */
public class App 
{
	public static void main(String[] args) {
		System.out.println(Base62Parse.encode(4266525668142902L));;
		System.out.println("wait");
	}

	private static int getBitOperate(int fromBit, int toBit) {
		int result = 0;
		int init = 1;
		for (int i = fromBit; i <= toBit; i++) {
			result |= (init << i - 1);
		}

		return result;
	}

	static final String FILE_READ_PATH = "/Users/erming/Desktop/exposure_object.20180624_22_24.log";

	public static void getXlsxx() {
		List<String> xmlList = Lists.newArrayList();
		xmlList.add("classpath:rpc.xml");
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(ArrayUtil.toStringArr(xmlList));
		context.start();
		UserService userService = (UserService) context.getBean("userService");

		BufferedReader reader = null;
		try {
			Map<Long, Map<String, List<String>>> midInteraction = Maps.newHashMap();

			List<File> fileList = Lists.newArrayList();
			fileList = getFileList("/Users/erming/Desktop/interaction/", fileList);
			for (File file : fileList) {
				String filePath = file.getAbsolutePath();
				if (filePath == null || !filePath.endsWith(".txt")) { continue; }
				String interactionName = file.getName().split("\\.")[0];

				BufferedReader innerReader = new BufferedReader(new FileReader(new File(filePath)));
				String innerLine = null;
				while ((innerLine = innerReader.readLine()) != null) {
					String[] interactionArray = innerLine.split("\t");
					long mid = Long.valueOf(interactionArray[2]);

					Map<String, List<String>> interactionMap = midInteraction.get(mid);
					if (interactionMap == null) { interactionMap = Maps.newHashMap(); midInteraction.put(mid, interactionMap); }

					List<String> interactionList = interactionMap.get(interactionName);
					if (interactionList == null) { interactionList = Lists.newArrayList(); interactionMap.put(interactionName, interactionList); }

					interactionList.add(innerLine);
				}
			}
			System.out.println();

			System.out.println("start excel");
			HSSFWorkbook workbook = new HSSFWorkbook();
			// Sheet样式
			HSSFCellStyle sheetStyle = workbook.createCellStyle();
			sheetStyle.setFillBackgroundColor(HSSFColor.GREY_25_PERCENT.index);
			sheetStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);

			HSSFFont headfont = workbook.createFont();
			headfont.setFontName("黑体");
			headfont.setFontHeightInPoints((short) 22);// 字体大小
			headfont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);// 加粗
			// 另一个样式
			HSSFCellStyle headstyle = workbook.createCellStyle();
			headstyle.setFont(headfont);
			headstyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 左右居中
			headstyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 上下居中
			headstyle.setLocked(true);

			HSSFFont font = workbook.createFont();
			font.setFontName("宋体");
			font.setFontHeightInPoints((short) 12);

			// 普通单元格样式
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

			// 另一个样式
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

			int lineNum = 1;
			String line;
			reader = new BufferedReader(new FileReader(new File("/Users/erming/Desktop/mids.txt")));
			while ((line = reader.readLine()) != null) {
				System.out.println("[Excel export]" + lineNum++);
				int rowNum = 0;
				String[] linkArray = line.split("\t");
				String link = linkArray[0];
				String hexMid = link.substring(link.lastIndexOf("/"));
				long mid = Base62Parse.decode(hexMid);

				Map<String, List<String>> interactionMap = midInteraction.get(mid);
				if (MapUtils.isEmpty(interactionMap)) { continue; }

				HSSFSheet sheet = workbook.createSheet(String.valueOf(mid));
				for (int i = 0; i <= 4; i++) { sheet.setDefaultColumnStyle((short) i, sheetStyle); }
				sheet.setColumnWidth(0, 6000);
				sheet.setColumnWidth(1, 4000);
				sheet.setColumnWidth(2, 5000);
				sheet.setColumnWidth(3, 4000);
				sheet.setColumnWidth(4, 4000);

				HSSFRow row = sheet.createRow(rowNum);
				row.setHeight((short) 800);
				HSSFCell cell = row.createCell(0);
				cell.setCellType(HSSFCell.CELL_TYPE_FORMULA);//CELL_TYPE_FORMULA
				cell.setCellFormula("HYPERLINK(\"" + link + "\",\""+ link +"\")");
				cell.setCellStyle(headstyle);
				sheet.addMergedRegion(new CellRangeAddress(rowNum,rowNum,0,3));

				row = sheet.createRow(++rowNum);
				cell = row.createCell(0);
				cell.setCellValue("转发数");
				cell.setCellStyle(style);
				cell = row.createCell(1);
				cell.setCellValue("评论数");
				cell.setCellStyle(style);
				cell = row.createCell(2);
				cell.setCellValue("赞数");
				cell.setCellStyle(style);
				cell = row.createCell(3);
				cell.setCellValue("总计数");
				cell.setCellStyle(style);

				row = sheet.createRow(++rowNum);
				cell = row.createCell(0);
				cell.setCellValue(linkArray[1]);
				cell.setCellStyle(style);
				cell = row.createCell(1);
				cell.setCellValue(linkArray[2]);
				cell.setCellStyle(style);
				cell = row.createCell(2);
				cell.setCellValue(linkArray[3]);
				cell.setCellStyle(style);
				cell = row.createCell(3);
				cell.setCellValue(linkArray[4]);
				cell.setCellStyle(style);

				row = sheet.createRow(++rowNum);
				row = sheet.createRow(++rowNum);
				cell = row.createCell(0);
				cell.setCellValue("转发");
				cell.setCellStyle(centerstyle);
				sheet.addMergedRegion(new CellRangeAddress(rowNum,rowNum,0,3));

				row = sheet.createRow(++rowNum);
				cell = row.createCell(0);
				cell.setCellValue("转发时间");
				cell.setCellStyle(style);
				cell = row.createCell(1);
				cell.setCellValue("转发用户");
				cell.setCellStyle(style);
				cell = row.createCell(2);
				cell.setCellValue("转发用户质量等级");
				cell.setCellStyle(style);
				cell = row.createCell(3);
				cell.setCellValue("IP");
				cell.setCellStyle(style);
//				cell = row.createCell(4);
//				cell.setCellValue("IP所属区域");
//				cell.setCellStyle(style);

				if (MapUtils.isNotEmpty(interactionMap) && CollectionUtils.isNotEmpty(interactionMap.get("转发"))) {
					for (InnerLine innerLine : InnerLine.getSortList(interactionMap.get("转发"))) {
						row = sheet.createRow(++rowNum);
						setRowText(userService, row, style, innerLine.line);
					}
				}

				row = sheet.createRow(++rowNum);
				row = sheet.createRow(++rowNum);
				cell = row.createCell(0);
				cell.setCellValue("评论");
				cell.setCellStyle(centerstyle);
				sheet.addMergedRegion(new CellRangeAddress(rowNum,rowNum,0,3));

				row = sheet.createRow(++rowNum);
				cell = row.createCell(0);
				cell.setCellValue("评论时间");
				cell.setCellStyle(style);
				cell = row.createCell(1);
				cell.setCellValue("评论用户");
				cell.setCellStyle(style);
				cell = row.createCell(2);
				cell.setCellValue("评论用户质量等级");
				cell.setCellStyle(style);
				cell = row.createCell(3);
				cell.setCellValue("IP");
				cell.setCellStyle(style);
//				cell = row.createCell(4);
//				cell.setCellValue("IP所属区域");
//				cell.setCellStyle(style);

				if (MapUtils.isNotEmpty(interactionMap) && CollectionUtils.isNotEmpty(interactionMap.get("评论"))) {
					for (InnerLine innerLine : InnerLine.getSortList(interactionMap.get("评论"))) {
						row = sheet.createRow(++rowNum);
						setRowText(userService, row, style, innerLine.line);
					}
				}

				row = sheet.createRow(++rowNum);
				row = sheet.createRow(++rowNum);
				cell = row.createCell(0);
				cell.setCellValue("点赞");
				cell.setCellStyle(centerstyle);
				sheet.addMergedRegion(new CellRangeAddress(rowNum,rowNum,0,3));

				row = sheet.createRow(++rowNum);
				cell = row.createCell(0);
				cell.setCellValue("点赞时间");
				cell.setCellStyle(style);
				cell = row.createCell(1);
				cell.setCellValue("点赞用户");
				cell.setCellStyle(style);
				cell = row.createCell(2);
				cell.setCellValue("点赞用户质量等级");
				cell.setCellStyle(style);
				cell = row.createCell(3);
				cell.setCellValue("IP");
				cell.setCellStyle(style);
//				cell = row.createCell(4);
//				cell.setCellValue("IP所属区域");
//				cell.setCellStyle(style);

				if (MapUtils.isNotEmpty(interactionMap) && CollectionUtils.isNotEmpty(interactionMap.get("赞"))) {
					for (InnerLine innerLine : InnerLine.getSortList(interactionMap.get("赞"))) {
						row = sheet.createRow(++rowNum);
						setRowText(userService, row, style, innerLine.line);
					}
				}
			}

			String filename = "/Users/erming/Desktop/interaction.xls";//设置下载时客户端Excel的名称
			workbook.write(new FileOutputStream(new File(filename)));
		} catch (Exception ex) {

		}
	}

	public static void getXlsx() {
		List<String> xmlList = Lists.newArrayList();
		xmlList.add("classpath:rpc.xml");
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(ArrayUtil.toStringArr(xmlList));
		context.start();
		UserService userService = (UserService) context.getBean("userService");

		BufferedReader reader = null;
		try {
			Map<Long, Map<String, List<String>>> midInteraction = Maps.newHashMap();

			List<File> fileList = Lists.newArrayList();
			fileList = getFileList("/Users/erming/Desktop/interaction/", fileList);
			/*for (File file : fileList) {
				String filePath = file.getAbsolutePath();
				if (filePath == null || !filePath.endsWith(".txt")) { continue; }
				String interactionName = file.getName().split("\\.")[0];

				BufferedReader innerReader = new BufferedReader(new FileReader(new File(filePath)));
				String innerLine = null;
				while ((innerLine = innerReader.readLine()) != null) {
					String[] interactionArray = innerLine.split("\t");
					long mid = Long.valueOf(interactionArray[2]);

					Map<String, List<String>> interactionMap = midInteraction.get(mid);
					if (interactionMap == null) { interactionMap = Maps.newHashMap(); midInteraction.put(mid, interactionMap); }

					List<String> interactionList = interactionMap.get(interactionName);
					if (interactionList == null) { interactionList = Lists.newArrayList(); interactionMap.put(interactionName, interactionList); }

					interactionList.add(innerLine);
				}
			}*/

			Map<String, Integer> uidRepost = Maps.newHashMap();
			Map<String, Integer> uidComment = Maps.newHashMap();
			Map<String, Integer> uidAttitude = Maps.newHashMap();
			Map<String, Integer> ipRepost = Maps.newHashMap();
			Map<String, Integer> ipComment = Maps.newHashMap();
			Map<String, Integer> ipAttitude = Maps.newHashMap();

			for (File file : fileList) {
				String filePath = file.getAbsolutePath();
				if (filePath == null || !filePath.endsWith(".txt")) { continue; }
				String interactionName = file.getName().split("\\.")[0];

				BufferedReader innerReader = new BufferedReader(new FileReader(new File(filePath)));
				String innerLine = null;
				while ((innerLine = innerReader.readLine()) != null) {
					String[] interactionArray = innerLine.split("\t");
					String uidStr = interactionArray[1];
					String ipStr = interactionArray[3];

					if (interactionName.equals("转发")) {
						if (uidRepost.get(uidStr) != null) {
							int count = uidRepost.get(uidStr);
							count++;
							uidRepost.put(uidStr, count);
						} else {
							uidRepost.put(uidStr, 1);
						}

						if (ipRepost.get(ipStr) != null) {
							int count = ipRepost.get(ipStr);
							count++;
							ipRepost.put(ipStr, count);
						} else {
							ipRepost.put(ipStr, 1);
						}
					} else if (interactionName.equals("评论")) {
						if (uidComment.get(uidStr) != null) {
							int count = uidComment.get(uidStr);
							count++;
							uidComment.put(uidStr, count);
						} else {
							uidComment.put(uidStr, 1);
						}

						if (ipComment.get(ipStr) != null) {
							int count = ipComment.get(ipStr);
							count++;
							ipComment.put(ipStr, count);
						} else {
							ipComment.put(ipStr, 1);
						}
					} else if (interactionName.equals("赞")) {
						if (uidAttitude.get(uidStr) != null) {
							int count = uidAttitude.get(uidStr);
							count++;
							uidAttitude.put(uidStr, count);
						} else {
							uidAttitude.put(uidStr, 1);
						}

						if (ipAttitude.get(ipStr) != null) {
							int count = ipAttitude.get(ipStr);
							count++;
							ipAttitude.put(ipStr, count);
						} else {
							ipAttitude.put(ipStr, 1);
						}
					}
				}
			}

			List<InnerCount> sortUidRepost = InnerCount.getSortList(ipAttitude);
			for (InnerCount innerCount : sortUidRepost) {
				StringBuilder sb = new StringBuilder();
				sb.append(innerCount.uid).append("\t").append(innerCount.count);
				System.out.println(sb.toString());
			}

			/*HSSFWorkbook workbook = new HSSFWorkbook();
			// Sheet样式
			HSSFCellStyle sheetStyle = workbook.createCellStyle();
			sheetStyle.setFillBackgroundColor(HSSFColor.GREY_25_PERCENT.index);
			sheetStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);

			HSSFFont headfont = workbook.createFont();
			headfont.setFontName("黑体");
			headfont.setFontHeightInPoints((short) 22);// 字体大小
			headfont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);// 加粗
			// 另一个样式
			HSSFCellStyle headstyle = workbook.createCellStyle();
			headstyle.setFont(headfont);
			headstyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 左右居中
			headstyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 上下居中
			headstyle.setLocked(true);

			HSSFFont font = workbook.createFont();
			font.setFontName("宋体");
			font.setFontHeightInPoints((short) 12);

			// 普通单元格样式
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

			// 另一个样式
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
			centerstyle.setFillForegroundColor(HSSFColor.WHITE.index);// 设置单元格的背景颜色．*/

			/*int lineNum = 1;
			String line;
			reader = new BufferedReader(new FileReader(new File("/Users/erming/Desktop/mids.txt")));
			while ((line = reader.readLine()) != null) {
				System.out.println(lineNum++);
				getddd(line, midInteraction, userService);
				*//*System.out.println("[Excel export]" + lineNum++);
				int rowNum = 0;
				String[] linkArray = line.split("\t");
				String link = linkArray[0];
				String hexMid = link.substring(link.lastIndexOf("/"));
				long mid = Base62Parse.decode(hexMid);

				Map<String, List<String>> interactionMap = midInteraction.get(mid);
				if (MapUtils.isEmpty(interactionMap)) { continue; }

				HSSFSheet sheet = workbook.createSheet(String.valueOf(mid));
				for (int i = 0; i <= 4; i++) { sheet.setDefaultColumnStyle((short) i, sheetStyle); }
				sheet.setColumnWidth(0, 6000);
				sheet.setColumnWidth(1, 4000);
				sheet.setColumnWidth(2, 5000);
				sheet.setColumnWidth(3, 4000);
				sheet.setColumnWidth(4, 4000);

				HSSFRow row = sheet.createRow(rowNum);
				row.setHeight((short) 800);
				HSSFCell cell = row.createCell(0);
				cell.setCellType(HSSFCell.CELL_TYPE_FORMULA);//CELL_TYPE_FORMULA
				cell.setCellFormula("HYPERLINK(\"" + link + "\",\""+ link +"\")");
				cell.setCellStyle(headstyle);
				sheet.addMergedRegion(new CellRangeAddress(rowNum,rowNum,0,3));

				row = sheet.createRow(++rowNum);
				cell = row.createCell(0);
				cell.setCellValue("转发数");
				cell.setCellStyle(style);
				cell = row.createCell(1);
				cell.setCellValue("评论数");
				cell.setCellStyle(style);
				cell = row.createCell(2);
				cell.setCellValue("赞数");
				cell.setCellStyle(style);
				cell = row.createCell(3);
				cell.setCellValue("总计数");
				cell.setCellStyle(style);

				row = sheet.createRow(++rowNum);
				cell = row.createCell(0);
				cell.setCellValue(linkArray[1]);
				cell.setCellStyle(style);
				cell = row.createCell(1);
				cell.setCellValue(linkArray[2]);
				cell.setCellStyle(style);
				cell = row.createCell(2);
				cell.setCellValue(linkArray[3]);
				cell.setCellStyle(style);
				cell = row.createCell(3);
				cell.setCellValue(linkArray[4]);
				cell.setCellStyle(style);

				row = sheet.createRow(++rowNum);
				row = sheet.createRow(++rowNum);
				cell = row.createCell(0);
				cell.setCellValue("转发");
				cell.setCellStyle(centerstyle);
				sheet.addMergedRegion(new CellRangeAddress(rowNum,rowNum,0,3));

				row = sheet.createRow(++rowNum);
				cell = row.createCell(0);
				cell.setCellValue("转发时间");
				cell.setCellStyle(style);
				cell = row.createCell(1);
				cell.setCellValue("转发用户");
				cell.setCellStyle(style);
				cell = row.createCell(2);
				cell.setCellValue("转发用户质量等级");
				cell.setCellStyle(style);
				cell = row.createCell(3);
				cell.setCellValue("IP");
				cell.setCellStyle(style);
//				cell = row.createCell(4);
//				cell.setCellValue("IP所属区域");
//				cell.setCellStyle(style);

				if (MapUtils.isNotEmpty(interactionMap) && CollectionUtils.isNotEmpty(interactionMap.get("转发"))) {
					for (InnerLine innerLine : InnerLine.getSortList(interactionMap.get("转发"))) {
						row = sheet.createRow(++rowNum);
						setRowText(userService, row, style, innerLine.line);
					}
				}

				row = sheet.createRow(++rowNum);
				row = sheet.createRow(++rowNum);
				cell = row.createCell(0);
				cell.setCellValue("评论");
				cell.setCellStyle(centerstyle);
				sheet.addMergedRegion(new CellRangeAddress(rowNum,rowNum,0,3));

				row = sheet.createRow(++rowNum);
				cell = row.createCell(0);
				cell.setCellValue("评论时间");
				cell.setCellStyle(style);
				cell = row.createCell(1);
				cell.setCellValue("评论用户");
				cell.setCellStyle(style);
				cell = row.createCell(2);
				cell.setCellValue("评论用户质量等级");
				cell.setCellStyle(style);
				cell = row.createCell(3);
				cell.setCellValue("IP");
				cell.setCellStyle(style);
//				cell = row.createCell(4);
//				cell.setCellValue("IP所属区域");
//				cell.setCellStyle(style);

				if (MapUtils.isNotEmpty(interactionMap) && CollectionUtils.isNotEmpty(interactionMap.get("评论"))) {
					for (InnerLine innerLine : InnerLine.getSortList(interactionMap.get("评论"))) {
						row = sheet.createRow(++rowNum);
						setRowText(userService, row, style, innerLine.line);
					}
				}

				row = sheet.createRow(++rowNum);
				row = sheet.createRow(++rowNum);
				cell = row.createCell(0);
				cell.setCellValue("点赞");
				cell.setCellStyle(centerstyle);
				sheet.addMergedRegion(new CellRangeAddress(rowNum,rowNum,0,3));

				row = sheet.createRow(++rowNum);
				cell = row.createCell(0);
				cell.setCellValue("点赞时间");
				cell.setCellStyle(style);
				cell = row.createCell(1);
				cell.setCellValue("点赞用户");
				cell.setCellStyle(style);
				cell = row.createCell(2);
				cell.setCellValue("点赞用户质量等级");
				cell.setCellStyle(style);
				cell = row.createCell(3);
				cell.setCellValue("IP");
				cell.setCellStyle(style);
//				cell = row.createCell(4);
//				cell.setCellValue("IP所属区域");
//				cell.setCellStyle(style);

				if (MapUtils.isNotEmpty(interactionMap) && CollectionUtils.isNotEmpty(interactionMap.get("赞"))) {
					for (InnerLine innerLine : InnerLine.getSortList(interactionMap.get("赞"))) {
						row = sheet.createRow(++rowNum);
						setRowText(userService, row, style, innerLine.line);
					}
				}*//*
			}*/

			/*String filename = "/Users/erming/Desktop/interaction.xls";//设置下载时客户端Excel的名称
			workbook.write(new FileOutputStream(new File(filename)));*/
		} catch (Exception ex) {

		}
	}

	public static void getddd(String line, Map<Long, Map<String, List<String>>> midInteraction, UserService userService) throws FileNotFoundException {
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFCellStyle sheetStyle = workbook.createCellStyle();
		sheetStyle.setFillBackgroundColor(HSSFColor.GREY_25_PERCENT.index);
		sheetStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);

		HSSFFont headfont = workbook.createFont();
		headfont.setFontName("黑体");
		headfont.setFontHeightInPoints((short) 22);// 字体大小
		headfont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);// 加粗

		HSSFCellStyle headstyle = workbook.createCellStyle();
		headstyle.setFont(headfont);
		headstyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 左右居中
		headstyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 上下居中
		headstyle.setLocked(true);

		HSSFFont font = workbook.createFont();
		font.setFontName("宋体");
		font.setFontHeightInPoints((short) 12);

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

		int rowNum = 0;
		String[] linkArray = line.split("\t");
		String link = linkArray[0];
		String hexMid = link.substring(link.lastIndexOf("/"));
		long mid = Base62Parse.decode(hexMid);

		Map<String, List<String>> interactionMap = midInteraction.get(mid);
		if (MapUtils.isEmpty(interactionMap)) { return; }

		HSSFSheet sheet = workbook.createSheet(String.valueOf(mid));
		for (int i = 0; i <= 4; i++) { sheet.setDefaultColumnStyle((short) i, sheetStyle); }
		sheet.setColumnWidth(0, 6000);
		sheet.setColumnWidth(1, 4000);
		sheet.setColumnWidth(2, 5000);
		sheet.setColumnWidth(3, 5000);

		HSSFRow row = sheet.createRow(rowNum);
		row.setHeight((short) 800);
		HSSFCell cell = row.createCell(0);
		cell.setCellType(HSSFCell.CELL_TYPE_FORMULA);//CELL_TYPE_FORMULA
		cell.setCellFormula("HYPERLINK(\"" + link + "\",\""+ link +"\")");
		cell.setCellStyle(headstyle);
		sheet.addMergedRegion(new CellRangeAddress(rowNum,rowNum,0,3));

		row = sheet.createRow(++rowNum);
		cell = row.createCell(0);
		cell.setCellValue("转发数");
		cell.setCellStyle(style);
		cell = row.createCell(1);
		cell.setCellValue("评论数");
		cell.setCellStyle(style);
		cell = row.createCell(2);
		cell.setCellValue("赞数");
		cell.setCellStyle(style);
		cell = row.createCell(3);
		cell.setCellValue("总计数");
		cell.setCellStyle(style);

		row = sheet.createRow(++rowNum);
		cell = row.createCell(0);
		cell.setCellValue(linkArray[1]);
		cell.setCellStyle(style);
		cell = row.createCell(1);
		cell.setCellValue(linkArray[2]);
		cell.setCellStyle(style);
		cell = row.createCell(2);
		cell.setCellValue(linkArray[3]);
		cell.setCellStyle(style);
		cell = row.createCell(3);
		cell.setCellValue(linkArray[4]);
		cell.setCellStyle(style);

		row = sheet.createRow(++rowNum);
		row = sheet.createRow(++rowNum);
		cell = row.createCell(0);
		cell.setCellValue("转发");
		cell.setCellStyle(centerstyle);
		cell = row.createCell(1);
		cell.setCellStyle(centerstyle);
		cell = row.createCell(2);
		cell.setCellStyle(centerstyle);
		cell = row.createCell(3);
		cell.setCellStyle(centerstyle);
		sheet.addMergedRegion(new CellRangeAddress(rowNum,rowNum,0,3));

		row = sheet.createRow(++rowNum);
		cell = row.createCell(0);
		cell.setCellValue("转发时间");
		cell.setCellStyle(style);
		cell = row.createCell(1);
		cell.setCellValue("转发用户");
		cell.setCellStyle(style);
		cell = row.createCell(2);
		cell.setCellValue("转发用户质量等级");
		cell.setCellStyle(style);
		cell = row.createCell(3);
		cell.setCellValue("IP");
		cell.setCellStyle(style);

		if (MapUtils.isNotEmpty(interactionMap) && CollectionUtils.isNotEmpty(interactionMap.get("转发"))) {
			for (InnerLine innerLine : InnerLine.getSortList(interactionMap.get("转发"))) {
				row = sheet.createRow(++rowNum);
				setRowText(userService, row, style, innerLine.line);
			}
		}

		row = sheet.createRow(++rowNum);
		row = sheet.createRow(++rowNum);
		cell = row.createCell(0);
		cell.setCellValue("评论");
		cell.setCellStyle(centerstyle);
		cell = row.createCell(1);
		cell.setCellStyle(centerstyle);
		cell = row.createCell(2);
		cell.setCellStyle(centerstyle);
		cell = row.createCell(3);
		cell.setCellStyle(centerstyle);
		sheet.addMergedRegion(new CellRangeAddress(rowNum,rowNum,0,3));

		row = sheet.createRow(++rowNum);
		cell = row.createCell(0);
		cell.setCellValue("评论时间");
		cell.setCellStyle(style);
		cell = row.createCell(1);
		cell.setCellValue("评论用户");
		cell.setCellStyle(style);
		cell = row.createCell(2);
		cell.setCellValue("评论用户质量等级");
		cell.setCellStyle(style);
		cell = row.createCell(3);
		cell.setCellValue("IP");
		cell.setCellStyle(style);

		if (MapUtils.isNotEmpty(interactionMap) && CollectionUtils.isNotEmpty(interactionMap.get("评论"))) {
			for (InnerLine innerLine : InnerLine.getSortList(interactionMap.get("评论"))) {
				row = sheet.createRow(++rowNum);
				setRowText(userService, row, style, innerLine.line);
			}
		}

		row = sheet.createRow(++rowNum);
		row = sheet.createRow(++rowNum);
		cell = row.createCell(0);
		cell.setCellValue("点赞");
		cell.setCellStyle(centerstyle);
		cell = row.createCell(1);
		cell.setCellStyle(centerstyle);
		cell = row.createCell(2);
		cell.setCellStyle(centerstyle);
		cell = row.createCell(3);
		cell.setCellStyle(centerstyle);
		sheet.addMergedRegion(new CellRangeAddress(rowNum,rowNum,0,3));

		row = sheet.createRow(++rowNum);
		cell = row.createCell(0);
		cell.setCellValue("点赞时间");
		cell.setCellStyle(style);
		cell = row.createCell(1);
		cell.setCellValue("点赞用户");
		cell.setCellStyle(style);
		cell = row.createCell(2);
		cell.setCellValue("点赞用户质量等级");
		cell.setCellStyle(style);
		cell = row.createCell(3);
		cell.setCellValue("IP");
		cell.setCellStyle(style);

		if (MapUtils.isNotEmpty(interactionMap) && CollectionUtils.isNotEmpty(interactionMap.get("赞"))) {
			for (InnerLine innerLine : InnerLine.getSortList(interactionMap.get("赞"))) {
				row = sheet.createRow(++rowNum);
				setRowText(userService, row, style, innerLine.line);
			}
		}

		String filename = "/Users/erming/Desktop/status/" + String.valueOf(mid) + ".xls";//设置下载时客户端Excel的名称
		try {
			workbook.write(new FileOutputStream(new File(filename)));
		} catch (IOException e) {
			System.out.println("error," + mid);
		}
	}

	static class InnerLine {
		long timeMillis;
		String line;

		InnerLine(long timeMillis, String line) {
			this.timeMillis = timeMillis;
			this.line = line;
		}

		public static List<InnerLine> getSortList(List<String> stringList) {
			List<InnerLine> sorInnerLine = new ArrayList<>();
			for (String line : stringList) {
				String[] oplogArray = line.split("\t");
				String dateStr = oplogArray[0];
				try {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					java.util.Date date = (java.util.Date) sdf.parse(dateStr);
					sorInnerLine.add(new InnerLine(date.getTime(), line));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}

			Collections.sort(sorInnerLine, new Comparator<InnerLine>() {
				@Override
				public int compare(InnerLine o1, InnerLine o2) {
					return (o1.timeMillis < o2.timeMillis) ? -1 : (o1.timeMillis == o2.timeMillis) ? 0 : 1;
				}
			});

			return sorInnerLine;
		}
	}

	static class InnerCount {
		String uid;
		int count;

		InnerCount(String uid, int count) {
			this.uid = uid;
			this.count = count;
		}

		public static List<InnerCount> getSortList(Map<String ,Integer> map) {
			List<InnerCount> sorInnerCount = new ArrayList<>();
			for (Map.Entry<String, Integer> uidRepostEntry : map.entrySet()) {
				String ip = uidRepostEntry.getKey();
				int count = uidRepostEntry.getValue();
				sorInnerCount.add(new InnerCount(ip, count));
			}

			Collections.sort(sorInnerCount, new Comparator<InnerCount>() {
				@Override
				public int compare(InnerCount o1, InnerCount o2) {
					return (o1.count < o2.count) ? 1 : (o1.count == o2.count) ? 0 : -1;
				}
			});

			return sorInnerCount;
		}
	}

	private static void setRowText(UserService userService, HSSFRow row, HSSFCellStyle style, String oplog) {
		String[] oplogArray = oplog.split("\t");
		long uid = Long.valueOf(oplogArray[1]);

		HSSFCell cell = row.createCell(0);
		cell.setCellValue(oplogArray[0]);
		cell.setCellStyle(style);
		cell = row.createCell(1);
		cell.setCellValue(oplogArray[1]);
		cell.setCellStyle(style);
		cell = row.createCell(2);
		Map<Long, UserAttr> userAttrMap = userService.getAllUserAttr(new long[]{uid});
		if (userAttrMap != null && userAttrMap.get(uid) != null) {
			cell.setCellValue(userAttrMap.get(uid).getLevel());
		} else {
			cell.setCellValue(userAttrMap.get(uid).getLevel());
		}
		cell.setCellStyle(style);
		cell = row.createCell(3);
		cell.setCellValue(oplogArray[3]);
		cell.setCellStyle(style);
//		cell = row.createCell(4);
//		cell.setCellValue(GetAddressByIp(oplogArray[3]));
//		cell.setCellStyle(style);
	}

	public void excelPrint() {
		HSSFWorkbook workbook = new HSSFWorkbook();// 创建一个Excel文件
		HSSFSheet sheet = workbook.createSheet();// 创建一个Excel的Sheet
		sheet.createFreezePane(1, 3);// 冻结
		// 设置列宽
		sheet.setColumnWidth(0, 1000);
		sheet.setColumnWidth(1, 3500);
		sheet.setColumnWidth(2, 3500);
		sheet.setColumnWidth(3, 6500);
		sheet.setColumnWidth(4, 6500);
		sheet.setColumnWidth(5, 6500);
		sheet.setColumnWidth(6, 6500);
		sheet.setColumnWidth(7, 2500);

		// Sheet样式
		HSSFCellStyle sheetStyle = workbook.createCellStyle();
		// 背景色的设定
		sheetStyle.setFillBackgroundColor(HSSFColor.GREY_25_PERCENT.index);
		// 前景色的设定
		sheetStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
		// 填充模式
		sheetStyle.setFillPattern(HSSFCellStyle.FINE_DOTS);
		// 设置列的样式
		for (int i = 0; i <= 14; i++) {
			sheet.setDefaultColumnStyle((short) i, sheetStyle);
		}
		// 设置字体
		HSSFFont headfont = workbook.createFont();
		headfont.setFontName("黑体");
		headfont.setFontHeightInPoints((short) 20);// 字体大小
		headfont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);// 加粗
		// 另一个样式
		HSSFCellStyle headstyle = workbook.createCellStyle();
		headstyle.setFont(headfont);
		headstyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 左右居中
		headstyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 上下居中
		headstyle.setLocked(true);
		headstyle.setWrapText(true);// 自动换行
		// 另一个字体样式
		HSSFFont columnHeadFont = workbook.createFont();
		columnHeadFont.setFontName("宋体");
		columnHeadFont.setFontHeightInPoints((short) 10);
		columnHeadFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		// 列头的样式
		HSSFCellStyle columnHeadStyle = workbook.createCellStyle();
		columnHeadStyle.setFont(columnHeadFont);
		columnHeadStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 左右居中
		columnHeadStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 上下居中
		columnHeadStyle.setLocked(true);
		columnHeadStyle.setWrapText(true);
		columnHeadStyle.setLeftBorderColor(HSSFColor.BLACK.index);// 左边框的颜色
		columnHeadStyle.setBorderLeft((short) 1);// 边框的大小
		columnHeadStyle.setRightBorderColor(HSSFColor.BLACK.index);// 右边框的颜色
		columnHeadStyle.setBorderRight((short) 1);// 边框的大小
		columnHeadStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); // 设置单元格的边框为粗体
		columnHeadStyle.setBottomBorderColor(HSSFColor.BLACK.index); // 设置单元格的边框颜色
		// 设置单元格的背景颜色（单元格的样式会覆盖列或行的样式）
		columnHeadStyle.setFillForegroundColor(HSSFColor.WHITE.index);

		HSSFFont font = workbook.createFont();
		font.setFontName("宋体");
		font.setFontHeightInPoints((short) 10);
		// 普通单元格样式
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
		style.setFillForegroundColor(HSSFColor.WHITE.index);// 设置单元格的背景颜色．
		// 另一个样式
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

		try {
			// 创建第一行
			HSSFRow row0 = sheet.createRow(0);
			// 设置行高
			row0.setHeight((short) 900);
			// 创建第一列
			HSSFCell cell0 = row0.createCell(0);
			cell0.setCellValue(new HSSFRichTextString("中非发展基金投资项目调度会工作落实情况对照表"));
			cell0.setCellStyle(headstyle);
			/**
			 * 合并单元格
			 *    第一个参数：第一个单元格的行数（从0开始）
			 *    第二个参数：第二个单元格的行数（从0开始）
			 *    第三个参数：第一个单元格的列数（从0开始）
			 *    第四个参数：第二个单元格的列数（从0开始）
			 */
			CellRangeAddress range = new CellRangeAddress(0, 0, 0, 7);
			sheet.addMergedRegion(range);
			// 创建第二行
			HSSFRow row1 = sheet.createRow(1);
			HSSFCell cell1 = row1.createCell(0);
			cell1.setCellValue(new HSSFRichTextString("本次会议时间：2009年8月31日       前次会议时间：2009年8月24日"));
			cell1.setCellStyle(centerstyle);
			// 合并单元格
			range = new CellRangeAddress(1, 2, 0, 7);
			sheet.addMergedRegion(range);
			// 第三行
			HSSFRow row2 = sheet.createRow(3);
			row2.setHeight((short) 750);
			HSSFCell cell = row2.createCell(0);
			cell.setCellValue(new HSSFRichTextString("责任者"));
			cell.setCellStyle(columnHeadStyle);
			cell = row2.createCell(1);
			cell.setCellValue(new HSSFRichTextString("成熟度排序"));
			cell.setCellStyle(columnHeadStyle);
			cell = row2.createCell(2);
			cell.setCellValue(new HSSFRichTextString("事项"));
			cell.setCellStyle(columnHeadStyle);
			cell = row2.createCell(3);
			cell.setCellValue(new HSSFRichTextString("前次会议要求/n/新项目的项目概要"));
			cell.setCellStyle(columnHeadStyle);
			cell = row2.createCell(4);
			cell.setCellValue(new HSSFRichTextString("上周工作进展"));
			cell.setCellStyle(columnHeadStyle);
			cell = row2.createCell(5);
			cell.setCellValue(new HSSFRichTextString("本周工作计划"));
			cell.setCellStyle(columnHeadStyle);
			cell = row2.createCell(6);
			cell.setCellValue(new HSSFRichTextString("问题和建议"));
			cell.setCellStyle(columnHeadStyle);
			cell = row2.createCell(7);
			cell.setCellValue(new HSSFRichTextString("备 注"));
			cell.setCellStyle(columnHeadStyle);
			// 访问数据库，得到数据集
			int m = 4;
			int k = 4;
			for (int i = 0; i < 1; i++) {
				HSSFRow row = sheet.createRow(m);
				cell = row.createCell(0);
				cell.setCellValue(new HSSFRichTextString("dname"));
				cell.setCellStyle(centerstyle);
				// 合并单元格
				range = new CellRangeAddress(m, m + 0, 0, 0);
				sheet.addMergedRegion(range);

				for (int j = 0; j < 1; j++) {
					// 遍历数据集创建Excel的行
					row = sheet.getRow(k + j);
					if (null == row) {
						row = sheet.createRow(k + j);
					}
					cell = row.createCell(1);
					cell.setCellValue("getWnumber");
					cell.setCellStyle(centerstyle);
					cell = row.createCell(2);
					cell.setCellValue(new HSSFRichTextString("getWitem"));
					cell.setCellStyle(style);
					cell = row.createCell(3);
					cell.setCellValue(new HSSFRichTextString("getWmeting"));
					cell.setCellStyle(style);
					cell = row.createCell(4);
					cell.setCellValue(new HSSFRichTextString("getWbweek"));
					cell.setCellStyle(style);
					cell = row.createCell(5);
					cell.setCellValue(new HSSFRichTextString("getWtweek"));
					cell.setCellStyle(style);
					cell = row.createCell(6);
					cell.setCellValue(new HSSFRichTextString("getWproblem"));
					cell.setCellStyle(style);
					cell = row.createCell(7);
					cell.setCellValue(new HSSFRichTextString("getWremark"));
					cell.setCellStyle(style);
				}
			}
			// 列尾
			int footRownumber = sheet.getLastRowNum();
			HSSFRow footRow = sheet.createRow(footRownumber + 1);
			HSSFCell footRowcell = footRow.createCell(0);
			footRowcell.setCellValue(new HSSFRichTextString("                    审  定：XXX      审  核：XXX     汇  总：XX"));
			footRowcell.setCellStyle(centerstyle);
			range = new CellRangeAddress(footRownumber + 1, footRownumber + 1, 0, 7);
			sheet.addMergedRegion(range);

//			HttpServletResponse response = getResponse();
//			HttpServletRequest request = getRequest();
//			String filename = "未命名.xls";//设置下载时客户端Excel的名称
//			// 请见：http://zmx.javaeye.com/blog/622529
//			filename = Util.encodeFilename(filename, request);
//			response.setContentType("application/vnd.ms-excel");
//			response.setHeader("Content-disposition", "attachment;filename=" + filename);
//			OutputStream ouputStream = response.getOutputStream();
//			workbook.write(ouputStream);
//			ouputStream.flush();
//			ouputStream.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void excelPrint(String line, Map<String, List<String>> interactionMap) {
		try {
			//创建HSSFWorkbook对象(excel的文档对象)
			HSSFWorkbook workbook = new HSSFWorkbook();
			//建立新的sheet对象（excel的表单）
			HSSFSheet sheet = workbook.createSheet("成绩表");
			//在sheet里创建第一行，参数为行索引(excel的行)，可以是0～65535之间的任何一个
			HSSFRow row1=sheet.createRow(0);
			//创建单元格（excel的单元格，参数为列索引，可以是0～255之间的任何一个
			HSSFCell cell=row1.createCell(0);

			//设置单元格内容
			cell.setCellValue("学员考试成绩一览表");
			//合并单元格CellRangeAddress构造参数依次表示起始行，截至行，起始列， 截至列
			sheet.addMergedRegion(new CellRangeAddress(0,0,0,3));
			//在sheet里创建第二行
			HSSFRow row2=sheet.createRow(1);
			//创建单元格并设置单元格内容
			row2.createCell(0).setCellValue("姓名");
			row2.createCell(1).setCellValue("班级");
			row2.createCell(2).setCellValue("笔试成绩");
			row2.createCell(3).setCellValue("机试成绩");
			//在sheet里创建第三行
			HSSFRow row3=sheet.createRow(2);
			row3.createCell(0).setCellValue("李明");
			row3.createCell(1).setCellValue("As178");
			row3.createCell(2).setCellValue(87);
			row3.createCell(3).setCellValue(78);

			String filename = "/Users/erming/Desktop/未命名.xls";//设置下载时客户端Excel的名称
			workbook.write(new FileOutputStream(new File(filename)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void beanReader() {
		List<String> xmlList = Lists.newArrayList();
		xmlList.add("file:/Users/erming/platform/idea/weibo-api-core/src/main/resources/spring/configloader.xml");
		// xmlList.add("file:/Users/erming/platform/idea/api-engine/src/spring/comment-hot-flow.xml");
		// xmlList.add("file:/Users/erming/platform/idea/api-comment/src/spring/service/status-repost.xml");
		// xmlList.add("file:/Users/erming/platform/idea/web_v4/src/spring/graph_client.xml");
		// xmlList.add("file:/Users/erming/platform/idea/api-comment/src/spring/service/status-count.xml");

		xmlList.add("classpath:rpc.xml");
		xmlList.add("classpath:mysql.xml");
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(ArrayUtil.toStringArr(xmlList));
		context.start();
		//UserVerifiedService userVerifiedService = (UserVerifiedService) context.getBean("userVerifiedService");
//		FriendService friendService = (FriendService) context.getBean("friendService");
		SinaUserService sinaUserService = (SinaUserService) context.getBean("sinaUserService");
//		VClubService vClubService = (VClubService) context.getBean("vClubService");
//		UserActiveTagService userActiveTagService = (UserActiveTagService) context.getBean("userActiveTagService");
		//WbobjectRpcService wbObjectRpcService = (WbobjectRpcService) context.getBean("wbObjectRpcService");
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

		//System.out.println(wbObjectRpcService.getObjectUuidByObjectId("1042018:b04cc6c8a72c66c827ae77d7112f0dee", FeedAsynUpdateConstant.OBJECT_INFO_TIMEOUT));
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

		Date date = new Date(1533052800000L);
		TableContainer tableContainer = (TableContainer) context.getBean("tableContainer");

		/*getBymeList(tableContainer);
		updateBymeList(tableContainer);*/

		// getStatusShowList(tableContainer, sinaUserService);
		getCommentTreeList(tableContainer, sinaUserService, true, false, 4268929662208127L, 20, date);
		/*List<Long> ids = Lists.newArrayList(4268139385138083L,4268165930287938L,4268281898486792L,4268139917799138L,4268282829913186L);
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		for (long id : ids) {
			//System.out.println("status_id:" + statusMeta.status_id + "\tcmt_id:" + statusMeta.cmt_id + "\tmflag:" + statusMeta.mflag + "\tuid:" + statusMeta.uid + "\tvflag:" + statusMeta.vflag);
			Comment comment = getComment(tableContainer, id);
			StringBuilder sb = new StringBuilder();
			sb.append(comment.id).append("\t");
			sb.append(comment.postSource.id).append("\t");
			sb.append(comment.text).append("\t");
			SinaUser sinaUser = sinaUserService.getSinaUser(comment.getAuthorId());
			sb.append(sinaUser != null ? sinaUser.screen_name : "frozen_user").append("\t");
			sb.append(sinaUser != null ? sinaUser.id : "0").append("\t");
			sb.append(comment.ip).append("\t");
			sb.append(GetAddressByIp(comment.ip)).append("\t");
			sb.append(dateFormater.format(comment.created_at));
			System.out.println(sb.toString());
		}*/
		/*updateStatusList(tableContainer);

		Comment comment = getComment(tableContainer, 4265759767614279L);
		updateContentState(tableContainer, comment);*/

		System.out.println("\ndone!");
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

			SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
			for (StatusMeta statusMeta : statusMetas) {
				//System.out.println("status_id:" + statusMeta.status_id + "\tcmt_id:" + statusMeta.cmt_id + "\tmflag:" + statusMeta.mflag + "\tuid:" + statusMeta.uid + "\tvflag:" + statusMeta.vflag);
				Comment comment = getComment(tableContainer, statusMeta.cmt_id);
				StringBuilder sb = new StringBuilder();
				sb.append(comment.text).append("\t");
				SinaUser sinaUser = sinaUserService.getSinaUser(comment.getAuthorId());
				sb.append(sinaUser != null ? sinaUser.screen_name : "frozen_user").append("\t");
				sb.append(comment.ip).append("\t");
				sb.append(GetAddressByIp(comment.ip)).append("\t");
				sb.append(dateFormater.format(comment.created_at));
				System.out.println(sb.toString());
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void getCommentTreeList(TableContainer tableContainer, SinaUserService sinaUserService, boolean isMid, boolean isAsc, long id, int count, Date createdDate) {
		String tableName = isMid ? "cmt_tree_mid_rootid" : "cmt_tree_rootid_childid";
		String sqlName = isMid ?
				isAsc ? "GET_ROOT_COMMENT_ASC" : "GET_ROOT_COMMENT_DESC":
				isAsc ? "GET_CHILD_COMMENT_ASC" : "GET_CHILD_COMMENT_DESC";
		TableChannel tableChannel = tableContainer.getTableChannel(tableName, sqlName, id, createdDate);
		String sql = tableChannel.getSql();

		List<CmtTreeBean> list = Lists.newArrayList();
		if (isMid) {
			tableChannel.getJdbcTemplate().query(sql, new Object[] { id, count }, new RowMapper() {
				@Override
				public Object mapRow(ResultSet rs, int i) throws SQLException {
					CmtTreeBean item = new CmtTreeBean();
					item.setRoot_id(rs.getLong("mid"));
					item.setChild_id(rs.getLong("cmt_root_id"));
					item.setVflag(rs.getInt("vflag"));
					item.setMflag(rs.getInt("mflag"));
					list.add(item);
					return null;
				}
			});
		} else {
			tableChannel.getJdbcTemplate().query(sql, new Object[] { id, count }, new RowMapper() {
				@Override
				public Object mapRow(ResultSet rs, int i) throws SQLException {
					CmtTreeBean item = new CmtTreeBean();
					item.setRoot_id(rs.getLong("cmt_root_id"));
					item.setChild_id(rs.getLong("cmt_child_id"));
					item.setVflag(rs.getInt("vflag"));
					item.setMflag(rs.getInt("mflag"));
					list.add(item);
					return null;
				}
			});
		}

		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
		for (CmtTreeBean cmtTreeBean : list) {
			//System.out.println("status_id:" + statusMeta.status_id + "\tcmt_id:" + statusMeta.cmt_id + "\tmflag:" + statusMeta.mflag + "\tuid:" + statusMeta.uid + "\tvflag:" + statusMeta.vflag);
			Comment comment = getComment(tableContainer, cmtTreeBean.getChild_id());
			StringBuilder sb = new StringBuilder();
			sb.append(dateFormater.format(comment.created_at)).append("\t");
			sb.append(getState(comment)).append("\t");
			sb.append(comment.text).append("\t");
			SinaUser sinaUser = sinaUserService.getSinaUser(comment.getAuthorId());
			sb.append(sinaUser != null ? sinaUser.screen_name : "frozen_user").append("\t");
			sb.append(comment.ip);
			System.out.println(sb.toString());
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

	static Comment getComment(TableContainer tableContainer, long id) {
		TableChannel channel = tableContainer.getTableChannel("comment", "GET_CONTENT", id, id);
		String sql = channel.getSql();

		Comment comment = (Comment)channel.getJdbcTemplate().query(sql, new Long[]{id}, new ResultSetExtractor(){
			public Comment extractData(ResultSet rs) throws SQLException, DataAccessException {
				if(rs.next()){
					Comment comment = CommentPBUtil.parseFromPB(rs.getBytes("content"), true);
					if(comment != null){
						comment.id = rs.getLong("id");
						return comment;
					}
				}
				return null;
			}
		});

		return comment;
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

	/**
	 *
	 * @param IP
	 * @return
	 */
	public static String GetAddressByIp(String IP){
		String resout = "未获取到地区";
		try{
			String str = getJsonContent("http://ip.taobao.com/service/getIpInfo.php?ip="+IP);

			// System.out.println(str);

			JSONObject obj = JSONObject.fromObject(str);
			JSONObject obj2 =  (JSONObject) obj.get("data");
			String code = String.valueOf(obj.get("code"));
			if(code.equals("0")){
				resout = obj2.get("city")+ "-" +obj2.get("isp");
			}else{
				resout =  "未获取到地区";
			}
		}catch(Exception e){

			e.printStackTrace();
			resout = "获取IP地址异常："+e.getMessage();
		}
		return resout;

	}

	public static String getJsonContent(String urlStr)
	{
		try
		{// 获取HttpURLConnection连接对象
			URL url = new URL(urlStr);
			HttpURLConnection httpConn = (HttpURLConnection) url
					.openConnection();
			// 设置连接属性
			httpConn.setConnectTimeout(3000);
			httpConn.setDoInput(true);
			httpConn.setRequestMethod("GET");
			// 获取相应码
			int respCode = httpConn.getResponseCode();
			if (respCode == 200)
			{
				return ConvertStream2Json(httpConn.getInputStream());
			}
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return "";
	}


	private static String ConvertStream2Json(InputStream inputStream)
	{
		String jsonStr = "";
		// ByteArrayOutputStream相当于内存输出流
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		// 将输入流转移到内存输出流中
		try
		{
			while ((len = inputStream.read(buffer, 0, buffer.length)) != -1)
			{
				out.write(buffer, 0, len);
			}
			// 将内存流转换为字符串
			jsonStr = new String(out.toByteArray());
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonStr;
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

