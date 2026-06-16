/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Objects;

import filesprocess.CSV;
import static filesprocess.CSV.arruma;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author cristiano.rosa
 */
public class Registro {

    ArrayList<Object> fields;
    public CSV.Cabecalho cabecalhos;

    public Registro(String line, CSV.Cabecalho cabecalhos, String separator) {
        this.fields = new ArrayList<Object>();
        this.cabecalhos = cabecalhos;
        int size = cabecalhos.getAtributos().size();

        if (line.contains("\"")) {
            List<String> asplit = splitWithQuote(line);

            for (int i = 0; i < size; i++) {
                try {
                    this.fields.add(asplit.get(i));
                } catch (Exception e) {
                    this.fields.add("");
                }
            }
        } else {
            String[] _campos = line.split(separator);
            for (int i = 0; i < size; i++) {
                try {
                    this.fields.add(_campos[i]);
                } catch (Exception e) {
                    this.fields.add("");
                }
            }
        }
    }

    private List<String> splitWithQuote(String text) {
        ArrayList<String> source = new ArrayList<>(Arrays.asList(text.split(",")));
        ArrayList<String> dest = new ArrayList<String>();
        String working = "";
        while (!source.isEmpty()) {
            working += source.remove(0);
            long count = working.chars().filter(ch -> ch == '"').count();
            if (count % 2 == 0) {
                dest.add(working);
                working = "";
            } else {
                working += ",";
            }
        }
        return dest;
    }

    public String get(int field) {
        return String.valueOf(this.fields.get(field));
    }

    public boolean set(String field, String value) {
        int index = this.cabecalhos.find(field);
        this.fields.set(index, value);
        return true;
    }

    public String getField(String field) {
        int index = this.cabecalhos.find(field);
        return get(index);
    }

    public String toCSVtxt() {
        String retorno = "";
        String header = "";
        for (int x = 0; x < fields.size(); x++) {
            header += this.cabecalhos.get(x) + ";";
        }
        for (int x = 0; x < fields.size(); x++) {
            retorno += CSV.arruma(String.valueOf(fields.get(x))) + ";";
        }
        return header + "\n" + retorno;
    }

    @Override
    public String toString() {
        String retorno = "[\n";
        for (int x = 0; x < fields.size(); x++) {
            retorno += this.cabecalhos.get(x) + ": \"" + CSV.arruma(String.valueOf(fields.get(x))) + "\"\n";
        }
        return retorno + "]";
    }

    public int getIntField(String field) {
        try {
            String s = getField(field);
            s = s.replaceAll(" ", "");
            s = s.replaceAll(" ", "");
            s = s.replaceAll("/.", "");
            s = s.replaceAll("\\.", "");
            int i = Integer.valueOf(s);
            return i;
        } catch (NumberFormatException numberFormatException) {
            throw numberFormatException;
        }

    }

    public String getValueOf(String field_name) {
        return arruma(getField(field_name));
    }

    public ArrayList<String> getValueOf(ArrayList<String> fields) {
        ArrayList<String> r = new ArrayList<String>();
        for (String field : fields) {
            r.add(getValueOf(field));
        }
        return r;
    }

    public String getValueOf(String... fields) {
        String r = "";
        for (int x = 0; x < fields.length; x++) {
            r += arruma(getValueOf(fields[x]));
            if (x < fields.length - 1) {
                r += " : ";
            };
        }
        return r;
    }

    public ArrayList<Object> getFields() {
        return this.fields;
    }

    public String getLineContent() {
        String retorno = "";
        for (int i = 0; i < this.fields.size(); i++) {
            retorno += this.fields.get(i)+";";
        }
        return retorno;
    }

}
