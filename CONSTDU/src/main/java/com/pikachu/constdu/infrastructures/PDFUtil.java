package com.pikachu.constdu.infrastructures;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.xfa.XfaForm;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.StampingProperties;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class PDFUtil {

    public PdfDocument getStampingPdfFile(String srcPath, String destPath) {
        PdfDocument doc = null;
        try{
            PdfReader reader = new PdfReader(srcPath);
            PdfWriter writer = new PdfWriter(destPath);
            reader.setUnethicalReading(true);
            doc = new PdfDocument(reader, writer, new StampingProperties().useAppendMode());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return doc;
    }

    public PdfDocument writeDataToField(String fieldName, String value, XfaForm xfaForm, PdfDocument doc) {
        xfaForm.setXfaFieldValue(fieldName, value);
        try {
            xfaForm.write(doc);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return doc;
    }

    public XfaForm getXfaFormFromPdfDocument(PdfDocument document) {
        PdfAcroForm acroForm = PdfAcroForm.getAcroForm(document, true);
        return acroForm.getXfaForm();
    }

    public Calendar getDateFromString(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Date date = null;
        try {
            date = formatter.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar dateCalendar = Calendar.getInstance();
        if (date != null) {
            dateCalendar = Calendar.getInstance();
            dateCalendar.setTime(date);
        }else{
            dateCalendar.set(1970, Calendar.JANUARY, 1);
        }

        return dateCalendar;
    }

}
