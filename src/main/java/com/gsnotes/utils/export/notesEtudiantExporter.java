package com.gsnotes.utils.export;

import com.gsnotes.bo.Element;
import com.gsnotes.bo.InscriptionModule;
import com.gsnotes.bo.Module;
import com.gsnotes.dao.InsciptionModuleDao;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class notesEtudiantExporter {

    private XSSFWorkbook workbook;

    private XSSFSheet sheet;
    private List<Row> rows=new ArrayList<>();
    private List<List<CellAddress>> cellNotesAdd=new ArrayList<>();
    private List<CellAddress> AvgAddresse=new ArrayList<>();
    private List<String> ModulesDesc=new ArrayList<>();
    private int ColomNbre;
    boolean CylcleIng=true;

    public void setColomNbre(int colomNbre) {
        ColomNbre = colomNbre;
    }

    final int ColoumWidth=12;
    public final int StartBodyDataAtCol=4;
    final int satart_Row_Module_Desc=2;
    final int Col_Start_Index_Desc=0;
    int ColoumCursor;
    String niveau;

    public int getColoumCursor() {
        return ColoumCursor;
    }

    private CellStyle cellStyle;


    public notesEtudiantExporter(String sheet){

        workbook=new XSSFWorkbook();
        this.sheet=workbook.createSheet(sheet);

        cellStyle= workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        Rows(60);
        ChangeColoumWidt();
        AdjustHeightRow();
        InfoEtudiant();

    }

    public void InfoEtudiant(){
       Row row=rows.get(satart_Row_Module_Desc);
       Cell cell;
       String[] info ={"ID Etudiant","CNE","NOM","Prenom"};
       int col=0;
       for (String s:info){
           cell=row.createCell(col++);
           cell.setCellValue(s);
           cell.setCellStyle(styleCellWithBoldFont());
       }
       ColoumCursor=col;
    }

    public void SubHeader(){
        Row row= rows.get(1);
        Cell cell=row.createCell(Col_Start_Index_Desc);
        cell.setCellStyle(styleCellWithBoldFont());
        cell.setCellValue("Class");

        cell=row.createCell(Col_Start_Index_Desc+1);
        cell.setCellStyle(styleCellWithBoldFont());
        cell.setCellValue(this.niveau);
    }

    public void ModulesDetails(){
        Cell cell;
        Row row=rows.get(satart_Row_Module_Desc+1);
        int col=Col_Start_Index_Desc+4;
        for (String s:ModulesDesc){
            cell=row.createCell(col++);
            cell.setCellStyle(styleCellWithBoldFont());
            cell.setCellValue(s);
        }
    }

    public void AdjustHeightRow(){
        rows.get(0).setHeightInPoints(35);
        rows.get(1).setHeightInPoints(20);
        rows.get(2).setHeightInPoints(35);
        rows.get(3).setHeightInPoints(26);
    }

    public void listModulesDesc(List<Module> modules){
        Row row= rows.get(satart_Row_Module_Desc);

        for (Module m:modules){

            int k=1;
            Cell cell=row.createCell(ColoumCursor);
            cell.setCellStyle(styleCellWithBoldFont());
            cell.setCellValue(m.getTitre());

            if(!m.getElements().isEmpty()){
               k+=m.getElements().size();
               for (Element e: m.getElements()){
                   ModulesDesc.add(e.getNom());
               }
            }

            MergeCell(row.getRowNum(),cell.getColumnIndex(), cell.getColumnIndex()+k);
            ColoumCursor+=k+1;

            ModulesDesc.add("Moyenne");
            ModulesDesc.add("Validation");
        }
    }

    public void MergeCell(int row,int firstCol,int lastCol){
        this.sheet.addMergedRegion(new CellRangeAddress(row,row, firstCol, lastCol));
    }

   public CellStyle styleCellWithBoldFont(){
       Font font=workbook.createFont();
       font.setBold(true);

       CellStyle cellStyle=workbook.createCellStyle();
       cellStyle.setAlignment(HorizontalAlignment.CENTER);
       cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
       cellStyle.setFont(font);

       return cellStyle;
   }

    public void insertInFile( int colIndex , List<List<Object>> Data) {
        int j=StartBodyDataAtCol;
        for (List<Object> datatype : Data) {
           Row row=rows.get(j++);
            int colNum = colIndex;
            for (Object field : datatype) {
                Cell cell = row.createCell(colNum++);
                cell.setCellStyle(cellStyle);
                if (field instanceof String) {
                    cell.setCellValue((String) field);
                } else if (field instanceof Integer) {
                    cell.setCellValue((Integer) field);
                } else if (field instanceof Double) {
                    cell.setCellValue((Double) field);
                } else if (field instanceof Long) {
                    cell.setCellValue((Long) field);
                }
            }
        }
    }

    private CellAddress createCell(Row row, int columnCount, Object value) {

        Cell cell = row.createCell(columnCount);
        cell.setCellStyle(cellStyle);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Double) {
            cell.setCellValue((Double) value);
        } else {
            cell.setCellValue((String) value);
        }
        return cell.getAddress();
    }

    public void Rows(int size){
        for (int i=0;i<size;i++){
            Row row=sheet.createRow(i);
            row.setHeightInPoints(16);
            rows.add(row);
        }
    }

    public List<Row> getRows() {
        return rows;
    }

    public void CellwithFormule( int colIndex , List<Object> pData) {
        int seuil=12;
        if(!CylcleIng){
            seuil=8;
        }

        int i=StartBodyDataAtCol;
        List<CellAddress> addresses=new ArrayList<>();
        for (Object data : pData) {
            Row row = rows.get(i++);
            CellAddress addr=createCell(row,colIndex,data);
            Cell cell=row.createCell(colIndex+1);
            cell.setCellStyle(cellStyle);
            cell.setCellFormula("IF(AND("+addr+">="+seuil+",+"+ addr+"<="+20+"),\"V\",\"NV\")");
            addresses.add(addr);
        }
        cellNotesAdd.add(addresses);
    }

    public void ColoumMoyenneGeneral(int col){
        Row row=rows.get(satart_Row_Module_Desc);
        Cell cell=row.createCell(col);
        cell.setCellStyle(styleCellWithBoldFont());
        cell.setCellValue("Moyenne");

        int j=StartBodyDataAtCol;
        for (int i=0;i<ColomNbre;i++) {
            String formul="AVERAGE(";
            for (int k=0;k<cellNotesAdd.size();k++) {
                List<CellAddress> cellAddresses=cellNotesAdd.get(k);
                if(k==cellNotesAdd.size()-1){
                    formul+=cellAddresses.get(i)+")";
                }else {
                    formul+=cellAddresses.get(i)+",";
                }
            }
            row= rows.get(j++);
            cell=row.createCell(col);
            cell.setCellStyle(cellStyle);
            cell.setCellFormula(formul);
            AvgAddresse.add(cell.getAddress());
        }
    }

    public void RankColoum(int col){
        Row row=rows.get(satart_Row_Module_Desc);
        Cell cell=row.createCell(col);
        cell.setCellStyle(styleCellWithBoldFont());
        cell.setCellValue("Rank");

        int j=StartBodyDataAtCol;
        for (CellAddress add:AvgAddresse){
            row=rows.get(j++);
            cell=row.createCell(col);
            cell.setCellStyle(cellStyle);
            String formul="RANK.EQ("+add+","+AvgAddresse.get(0)+":"+AvgAddresse.get(AvgAddresse.size()-1)+")";
            cell.setCellFormula(formul);
        }
    }
    public void ChangeColoumWidt(){

        for (int i=0;i<30;i++){
            this.sheet.setColumnWidth(i,ColoumWidth*256);
        }
    }


    public void exportInbureau() throws IOException {

        FileOutputStream out = new FileOutputStream("C:/Users/User/IdeaProjects/notes.xlsx");
        workbook.write(out);
        out.close();
    }

    public void export(HttpServletResponse response) throws IOException {

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();

        outputStream.close();

    }





}
