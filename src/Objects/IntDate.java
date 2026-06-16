/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Objects;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author cristiano.rosa
 */
public class IntDate {

    private Date date;
    private int int_date;
    private String s_format;

    public IntDate() {
        this.date = null;
        this.int_date = 0;
        this.s_format = null;
    }

    public IntDate(String s_date, String s_format) throws ParseException {
        SimpleDateFormat f_in = new SimpleDateFormat(s_format);
        this.date = f_in.parse(s_date);
        this.recreate(new IntDate(date, "yyyyMMdd"));
    }

    private void recreate(IntDate original) {
        this.date = original.date;
        this.int_date = original.int_date;
        this.s_format = original.s_format;
    }

    public IntDate(Date date, String format) {
        this.date = date;
        this.s_format = format;
        SimpleDateFormat sdf = new SimpleDateFormat(this.s_format);
        this.int_date = Integer.valueOf(sdf.format(date));
    }

    public int dateToInt(String s_date, String format_in, String format_out) throws ParseException {
        SimpleDateFormat f_in = new SimpleDateFormat(format_in);
        SimpleDateFormat f_out = new SimpleDateFormat(format_out);
        Date d_date = f_in.parse(s_date);
        return Integer.valueOf(f_out.format(d_date));
    }

    public int toInt() {
        return this.int_date;
    }

    public String toString() {
        return "" + this.int_date;
    }

}
