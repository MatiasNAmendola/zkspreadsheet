/**
 * 
 */
package org.zkoss.zss.model.sys.impl;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.zkoss.poi.ss.formula.ptg.Ptg;
import org.zkoss.poi.hssf.usermodel.HSSFEvaluationWorkbook;
import org.zkoss.poi.hssf.usermodel.HSSFWorkbook;
import org.zkoss.poi.ss.formula.FormulaRenderer;
import org.zkoss.poi.ss.usermodel.Cell;
import org.zkoss.poi.ss.usermodel.CellValue;
import org.zkoss.poi.ss.usermodel.ErrorConstants;
import org.zkoss.poi.ss.usermodel.FormulaEvaluator;
import org.zkoss.poi.ss.usermodel.Hyperlink;
import org.zkoss.poi.ss.usermodel.Row;
import org.zkoss.zss.model.sys.XSheet;
import org.zkoss.poi.ss.usermodel.Workbook;
import org.zkoss.poi.ss.util.CellRangeAddress;
import org.zkoss.util.resource.ClassLocator;
import org.zkoss.zss.engine.Ref;
import org.zkoss.zss.engine.impl.CellRefImpl;
import org.zkoss.zss.model.sys.XBook;
import org.zkoss.zss.model.sys.impl.BookHelper;
import org.zkoss.zss.model.sys.impl.ExcelImporter;
import org.zkoss.zss.model.sys.impl.HSSFBookImpl;
import org.zkoss.zss.ui.impl.Utils;
import org.zkoss.zss.ui.impl.XUtils;

/**
 * Insert a row and check if the formula still work
 * @author henrichen
 */
public class Book10XlsHyperlinkTest {
	private Workbook _workbook;
	private FormulaEvaluator _evaluator;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		final String filename = "Book10.xls";
		final InputStream is = new ClassLocator().getResourceAsStream(filename);
		_workbook = new ExcelImporter().imports(is, filename);
		assertTrue(_workbook instanceof XBook);
		assertTrue(_workbook instanceof HSSFBookImpl);
		assertTrue(_workbook instanceof HSSFWorkbook);
		assertEquals(filename, ((XBook)_workbook).getBookName());
		assertEquals("Sheet1", _workbook.getSheetName(0));
		assertEquals("Sheet2", _workbook.getSheetName(1));
		assertEquals("Sheet3", _workbook.getSheetName(2));
		assertEquals(0, _workbook.getSheetIndex("Sheet1"));
		assertEquals(1, _workbook.getSheetIndex("Sheet2"));
		assertEquals(2, _workbook.getSheetIndex("Sheet3"));

		_evaluator = ((XBook)_workbook).getFormulaEvaluator();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		_workbook = null;
		_evaluator = null;
	}
	
	@Test
	public void testLoadHyperlinks() {
		XSheet sheet1 = (XSheet)_workbook.getSheet("Sheet1");
		Row row1 = sheet1.getRow(0);
		Row row2 = sheet1.getRow(1);
		Row row3 = sheet1.getRow(2);
		Row row4 = sheet1.getRow(3);
		Row row5 = sheet1.getRow(4);
		Row row6 = sheet1.getRow(5);
		
		Cell cellA2 = row2.getCell(0);
		BookHelper.evaluate((XBook)_workbook, cellA2);
		Hyperlink hlinkA1 = XUtils.getHyperlink(row1.getCell(0));
		Hyperlink hlinkA2 = XUtils.getHyperlink(cellA2);
		Hyperlink hlinkA3 = XUtils.getHyperlink(row3.getCell(0));
		Hyperlink hlinkA4 = XUtils.getHyperlink(row4.getCell(0));
		Hyperlink hlinkA5 = XUtils.getHyperlink(row5.getCell(0));
		Hyperlink hlinkA6 = XUtils.getHyperlink(row6.getCell(0));
		
		String stringA1 = XUtils.formatHyperlink(sheet1, hlinkA1, "", true);
		String stringA2 = XUtils.formatHyperlink(sheet1, hlinkA2, "", true);
		String stringA3 = XUtils.formatHyperlink(sheet1, hlinkA3, "", true);
		String stringA4 = XUtils.formatHyperlink(sheet1, hlinkA4, "", true);
		String stringA5 = XUtils.formatHyperlink(sheet1, hlinkA5, "", true);
		String stringA6 = XUtils.formatHyperlink(sheet1, hlinkA6, "", true);
		
		String head = "<a zs.t=\"SHyperlink\" z.t=\"";
		String href = "\" href=\"javascript:\" z.href=\"";
		String mid = "\">";
		String tail = "</a>";
		
		assertEquals(head+hlinkA1.getType()+href+hlinkA1.getAddress()+mid+hlinkA1.getLabel()+tail, stringA1);
		assertEquals(head+hlinkA2.getType()+href+hlinkA2.getAddress()+mid+hlinkA2.getLabel()+tail, stringA2);
		assertEquals(head+hlinkA3.getType()+href+hlinkA3.getAddress()+mid+hlinkA3.getLabel()+tail, stringA3);
		assertEquals(head+hlinkA4.getType()+href+hlinkA4.getAddress()+mid+hlinkA4.getLabel()+tail, stringA4);
		assertEquals(head+hlinkA5.getType()+href+hlinkA5.getAddress()+mid+hlinkA5.getLabel()+tail, stringA5);
		assertEquals(head+hlinkA6.getType()+href+hlinkA6.getAddress()+mid+hlinkA6.getLabel()+tail, stringA6);
	}
	
	private void testToFormulaString(Cell cell, String expect) {
		HSSFEvaluationWorkbook evalbook = HSSFEvaluationWorkbook.create((HSSFWorkbook)_workbook);
		Ptg[] ptgs = BookHelper.getCellPtgs(cell);
		final String formula = FormulaRenderer.toFormulaString(evalbook, ptgs);
		assertEquals(expect, formula);
	}
}
