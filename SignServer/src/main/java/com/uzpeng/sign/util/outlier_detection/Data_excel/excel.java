package com.uzpeng.sign.util.outlier_detection.Data_excel;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class excel {



    public static void main(String[] args) throws Exception {
//        POIUtil.MathRandomCastTime();
        long beginTime = System.currentTimeMillis();

        String path = "D:\\software\\IntelliJ IDEA 2018.2.5\\workspace\\Sign-Server\\src\\main\\resources\\test.xlsx";
        excel exc = new excel();
        exc.Excel2007AboveOperate(path);
        long endTime = System.currentTimeMillis();

        System.out.println("Cast time : " + (endTime - beginTime));
    }




    /*public static void Excel2003Operate(String filePath) throws Exception {
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(new FileInputStream(new File(filePath)));
        HSSFSheet sheet = hssfWorkbook.getSheetAt(0);
        for (int i = 0; i < 10000; i++) {
            HSSFRow hssfRow = sheet.createRow(i);
            for (int j = 0; j < 10; j++) {
                HSSFCellUtil.createCell(hssfRow, j, String.valueOf(Math.random()));
            }
        }
        FileOutputStream out = new FileOutputStream("workbook.xlsx");
        hssfWorkbook.write(out);
        out.close();
    }
*/
    public static void ExcelOperate(String filePath) throws Exception {
        Workbook workbook = WorkbookFactory.create(new FileInputStream(new File(filePath)));
        Sheet first = workbook.getSheetAt(0);
        for (int i = 0; i < 100000; i++) {
            Row row = first.createRow(i);
            for (int j = 0; j < 11; j++) {
                if(i == 0) {
                    row.createCell(j).setCellValue("column" + j);
                } else {
                    if (j == 0) {
                        row.createCell(j).setCellValue(i);
                    } else
                        row.createCell(j).setCellValue(Math.random());
                }
            }
        }
        FileOutputStream out = new FileOutputStream("workbook.xlsx");
        workbook.write(out);
        out.close();
    }

    public static void Excel2007AboveOperateOld(String filePath) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(new File(filePath)));
        // 获取第一个表单
        Sheet first = workbook.getSheetAt(0);
        for (int i = 0; i < 100000; i++) {
            Row row = first.createRow(i);
            for (int j = 0; j < 11; j++) {
                if(i == 0) {
                    // 首行
                    row.createCell(j).setCellValue("column" + j);
                } else {
                    // 数据
                    if (j == 0) {
                        CellUtil.createCell(row, j, String.valueOf(i));
                    } else
                        CellUtil.createCell(row, j, String.valueOf(Math.random()));
                }
            }
        }
        // 写入文件
        FileOutputStream out = new FileOutputStream("workbook.xlsx");
        workbook.write(out);
        out.close();
    }

    /**
     * 测试写入百万条数据
     * <br/>Cast time : 87782
     * @param filePath 文件路径
     * @throws IOException
     */
    public static void Excel2007AboveOperate(String filePath) throws IOException {

        XSSFWorkbook workbook1 = new XSSFWorkbook(new FileInputStream(new File(filePath)));
        SXSSFWorkbook sxssfWorkbook = new SXSSFWorkbook(workbook1, 100);
        Sheet first = sxssfWorkbook.getSheetAt(0);
        for (int i = 0; i < 10000; i++) {
            Row row = first.createRow(i);
            for (int j = 0; j < 11; j++) {
                if(i == 0) {
                    // 首行
                    row.createCell(j).setCellValue("column" + j);
                } else {
                    System.out.println("");  //去除重复标记

                    // 数据
                    if (j == 0) {
                        CellUtil.createCell(row, j, String.valueOf(i));
                    } else
                        CellUtil.createCell(row, j, String.valueOf(Math.random()));
                }
            }
        }
        FileOutputStream out = new FileOutputStream(filePath);
        sxssfWorkbook.write(out);
        out.close();
    }

    /*public static void MathRandomCastTime() {
        long beginTime = System.currentTimeMillis();
        for (int i = 0; i < 10000000; i++) {
            Math.random();
        }
        long endTime = System.currentTimeMillis();
        System.out.println(endTime - beginTime);
    }*/


}
