<%@ page contentType="text/json; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>

<%@ page import="cn.sina.api.commons.util.ApiLogger"%>
<%@ page import="cn.sina.api.commons.util.Base62Parse"%>
<%@ page import="cn.sina.api.user.model.UserAttr"%>

<%@ page import="cn.sina.api.user.service.UserService"%>
<%@ page import="com.google.common.collect.Lists"%>

<%@ page import="com.google.common.collect.Maps"%>
<%@ page import="com.weibo.api.core.ApplicationContextHolder"%>
<%@ page import="org.apache.commons.collections.CollectionUtils"%>
<%@ page import="org.apache.commons.collections.MapUtils" %>
<%@ page import="org.apache.poi.hssf.usermodel.HSSFCell" %>
<%@ page import="org.apache.poi.hssf.usermodel.HSSFCellStyle" %>
<%@ page import="org.apache.poi.hssf.usermodel.HSSFFont" %>
<%@ page import="org.apache.poi.hssf.usermodel.HSSFRow" %>
<%@ page import="org.apache.poi.hssf.usermodel.HSSFSheet" %>
<%@ page import="org.apache.poi.hssf.usermodel.HSSFWorkbook" %>
<%@ page import="org.apache.poi.hssf.util.HSSFColor" %>
<%@ page import="org.apache.poi.ss.util.CellRangeAddress" %>
<%@ page import="java.io.BufferedReader" %>
<%@ page import="java.io.File" %>
<%@ page import="java.io.FileOutputStream" %>
<%@ page import="java.io.FileReader" %>
<%@ page import="java.text.ParseException" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Collections" %>
<%@ page import="java.util.Comparator" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>

<%!
    // init
    UserService userService = (UserService) ApplicationContextHolder.getBean("userService");
%>

<%!
    public void getXlsx() {
        BufferedReader reader = null;
        try {
            Map<Long, Map<String, List<String>>> midInteraction = Maps.newHashMap();

            List<File> fileList = Lists.newArrayList();
            fileList = getFileList("/data1/erming/interaction/", fileList);
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
            // headstyle.setWrapText(true);// 自动换行

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
            reader = new BufferedReader(new FileReader(new File("/data1/erming/mids.txt")));
            while ((line = reader.readLine()) != null) {
                ApiLogger.info("[Excel export]" + lineNum++);
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

            String filename = "/data1/erming/interaction.xls";//设置下载时客户端Excel的名称
            workbook.write(new FileOutputStream(new File(filename)));
        } catch (Exception ex) {

        }
    }
%>

<%!
    public void setRowText(UserService userService, HSSFRow row, HSSFCellStyle style, String oplog) {
        String[] oplogArray = oplog.split("\t");
        long uid = Long.valueOf(oplogArray[1]);
        Map<Long, UserAttr> userAttrMap = userService.getAllUserAttr(new long[]{uid});

        HSSFCell cell = row.createCell(0);
        cell.setCellValue(oplogArray[0]);
        cell.setCellStyle(style);
        cell = row.createCell(1);
        cell.setCellValue(oplogArray[1]);
        cell.setCellStyle(style);
        cell = row.createCell(2);
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
%>

<%!
    public List<File> getFileList(String strPath, List<File> fileList) {
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
%>

<%!
    static class InnerLine {
        long timeMillis;
        String line;

        InnerLine(long timeMillis, String line) {
            this.timeMillis = timeMillis;
            this.line = line;
        }

        public static List<InnerLine> getSortList(List<String> stringList) {
            List<InnerLine> sorInnerLine = Lists.newArrayList();
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
                    return (o1.timeMillis < o2.timeMillis) ? 1 : (o1.timeMillis == o2.timeMillis) ? 0 : -1;
                }
            });

            return sorInnerLine;
        }
    }
%>

<%
    try {
        getXlsx();
    } catch (Exception e) {
        ApiLogger.error("repairCount failed", e);
    }
%>
