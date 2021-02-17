/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package filesprocess;

import br.zul.JTxtFile.JTxtFileFastReader;
import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author cristiano.rosa
 */
public class CSV {

    private String pathfile;
    private String filecontent;
    private Cabecalho cabecalho; // LINHAS
    private Registros registros; // LINHAS

    public CSV(String pathfile) {
        this.pathfile = pathfile;
        this.filecontent = openfile(pathfile, "iso-8859-1").replace("\r", "");
        this.registros = new Registros();
        builder();
    }

    public CSV(String pathfile, String filecontent, Cabecalho cabecalho,
            ArrayList<Registro> registros) {
        this.pathfile = pathfile;
        this.filecontent = filecontent;
        this.cabecalho = cabecalho;
        this.registros = new Registros(registros);
    }

    private CSV(CSV csv, ArrayList<Registro> rs) {
        this.pathfile = csv.pathfile;
        this.filecontent = csv.filecontent;
        this.cabecalho = csv.cabecalho;
        this.registros = new Registros(rs);
    }

    private String openfile(String path_file_complet, String encode) {
        try {
            final File file = new File(path_file_complet);
            String content = new JTxtFileFastReader(file).setCharset(Charset.forName(encode)).readAll();
            return content;
        } catch (Exception e) {
            return ">> Sem Texto <<";
        }
    }

    /*
    ======================================================================
        METODOS PUBLICOS
    ======================================================================    
     */
    public ArrayList<Registro> getAll() throws Exception {
        ArrayList<Registro> rs;
        rs = this.registros.getAll();
        if (rs != null) {
            return rs;
        } else {
            throw new Exception("Não encontrado");
        }
    }

    public CSV filter(int value, String fieldname) throws Exception {
        ArrayList<Registro> rs;
        int index = cabecalho.find(fieldname);
        rs = this.registros.findByIntValue(value, index);
        CSV resp = new CSV(this, rs);
        if (rs != null) {
            return resp;
        } else {
            throw new Exception("Não encontrado");
        }
    }

    public CSV filter(String value, String fieldname) throws Exception {
        ArrayList<Registro> rs;
        int index = cabecalho.find(fieldname);
        rs = this.registros.find(value, index);
        CSV resp = new CSV(this, rs);
        if (rs != null) {
            return resp;
        } else {
            throw new Exception("Não encontrado");
        }

    }

    public ArrayList<Registro> findRegisterBy(int value, String fieldname) throws Exception {
        ArrayList<Registro> rs;
        int index = cabecalho.find(fieldname);
        rs = this.registros.findByIntValue(value, index);
        if (rs != null) {
            return rs;
        } else {
            throw new Exception("Não encontrado");
        }

    }

    public CSV findRegisterBy(String value, String fieldname) throws Exception {
        ArrayList<Registro> rs;
        int index = cabecalho.find(fieldname);
        rs = this.registros.find(value, index);
        if (rs != null) {
            return new CSV(pathfile, filecontent, cabecalho, rs);
        } else {
            throw new Exception("Não encontrado");
        }
    }

    /*
    ======================================================================
        PRIVATE
    ======================================================================    
     */
    private static String removeNonDigits(final String str) {
        if (str == null || str.length() == 0) {
            return "";
        }
        return str.replaceAll(" ", "").replaceAll("[^0-9]+", "");
    }

    private void builder() {
        String[] lines = filecontent.replace("\r", "").split("\n");
        this.cabecalho = new Cabecalho(lines[0]);
        for (int i = 1; i < lines.length; i++) {
            try {
                Registro r = new Registro(lines[i], this.cabecalho);
                registros.add(r);
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
    }

    public ArrayList<Registro> toResult() {
        try {
            return this.getAll();
        } catch (Exception ex) {
            return null;
        }
    }

    public CSV findRegisterContains(String value, String fieldname)
            throws Exception {
        ArrayList<Registro> rs;
        int index = cabecalho.find(fieldname);
        rs = this.registros.findContains(value, index);
        if (rs != null) {
            return new CSV(pathfile, filecontent, cabecalho, rs);
        } else {
            throw new Exception("Não encontrado");
        }
    }

    /*
    ======================================================================
        CLASSES INTERNAS
    ======================================================================    
     */
    private static class Registros {

        private ArrayList<Registro> registros;

        public Registros() {
            this.registros = new ArrayList<Registro>();
        }

        private Registros(ArrayList<Registro> registros) {
            this.registros = registros;
        }

        private void add(Registro r) {
            registros.add(r);
        }

        private ArrayList<Registro> find(String value, int field) {
            ArrayList<Registro> rs = new ArrayList<Registro>();
            for (Registro registro : registros) {
                if (registro.get(field).equals(value)) {
                    rs.add(registro);
                }
            }
            return rs.isEmpty() ? null : rs;
        }

        private ArrayList<Registro> findContains(String value, int field) {
            ArrayList<Registro> rs = new ArrayList<Registro>();
            for (Registro registro : registros) {
                if (registro.get(field).contains(value)) {
                    rs.add(registro);
                }
            }
            return rs.isEmpty() ? null : rs;
        }

        private ArrayList<Registro> findByIntValue(int value, int field) {
            ArrayList<Registro> rs = new ArrayList<Registro>();
            for (Registro registro : registros) {
                if (Integer.valueOf(registro.get(field)).equals(value)) {
                    rs.add(registro);
                }
            }
            return rs.isEmpty() ? null : rs;
        }

        private Registro find(int value, int field) {
            for (Registro registro : registros) {
                if (registro.get(field).equals(value)) {
                    return registro;
                }
            }
            return null;
        }

        private ArrayList<Registro> getAll() {
            return this.registros;
        }

    }

    public class Registro {

        private ArrayList<Object> fields;
        private Cabecalho cabecalhos;

        public Registro(String line, Cabecalho cabecalhos) {
            this.fields = new ArrayList<Object>();
            this.cabecalhos = cabecalhos;
            String[] campos = line.split(";");
            for (String field : campos) {
                this.fields.add(field);
            }
        }

        private String get(int field) {
            return String.valueOf(this.fields.get(field));
        }

        public String getField(String field) {
            int index = this.cabecalhos.find(field);
            return get(index);
        }

        @Override
        public String toString() {
            String retorno = "[\n";
            for (int x = 0; x < fields.size(); x++) {
                retorno += this.cabecalhos.get(x) + ": \"" + arrumar((String) fields.get(x)) + "\"\n";
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

    }

    public static String arruma(String s) {
        boolean repete = true;
        while (repete) {
            s = s.replace("  ", "");
            repete = s.contains("  ");
        }
        try {
            s = String.valueOf(Long.valueOf(s));
        } catch (Exception e) {
        }
        return s.replace("  ", "");
    }

    public String arrumar(String string) {
        return string.replaceAll("  ", "");
    }

    public class Cabecalho {

        private ArrayList<String> atributos;

        public Cabecalho(String firstline) {
            this.atributos = new ArrayList<String>();
            String[] fields = firstline.split(";");
            for (String field : fields) {
                this.atributos.add(arrumar(field));
            }
        }

        @Override
        public String toString() {
            String retorno = "[";
            for (String atributo : atributos) {
                retorno += "\"" + atributo + "\",";
            }
            return retorno + "]";
        }

        private int find(String fieldname) {
            return this.atributos.indexOf(fieldname);
        }

        private String get(int x) {
            return this.atributos.get(x);
        }

    }
}
