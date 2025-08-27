/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
ORIGINAL
 */
package filesprocess;

import JTxtFile.JTxtFileFastReader;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author cristiano.rosa
 */
public class CSV {

    private String pathfile;
    private String filecontent;
    private Cabecalho cabecalho; // LINHAS
    private Registros registros; // LINHAS
    private String separator;
    private boolean isContent;

    //CLONE
    public CSV(CSV csv) {
        this.pathfile = csv.pathfile;
        this.filecontent = csv.filecontent;
        this.cabecalho = csv.cabecalho;
        this.registros = csv.registros;
        this.isContent = !this.filecontent.equals(">> Sem Texto <<");
    }

    public CSV() {
        this.pathfile = "";
        this.filecontent = ">> Sem Texto <<";
        this.cabecalho = new Cabecalho("", ";");
        this.registros = new Registros();
        this.isContent = false;
    }

    public CSV(String pathfile, int ignorefirstNllines) {
        this.pathfile = pathfile;
        this.filecontent = openfile(pathfile, "iso-8859-1", ignorefirstNllines).replace("\r", "");
        this.registros = new Registros();
        builder();
        this.isContent = !this.filecontent.equals(">> Sem Texto <<");
    }

    public CSV(String pathfile) {
        this.pathfile = pathfile;
        this.filecontent = openfile(pathfile, "iso-8859-1", 0).replace("\r", "");
        this.registros = new Registros();
        builder();
        this.isContent = !this.filecontent.equals(">> Sem Texto <<");
    }

    public CSV(String pathfile, boolean otherseparator, String separator) {
        if (otherseparator) {
            this.separator = separator;
        }
        this.pathfile = pathfile;
        this.filecontent = openfile(pathfile, "iso-8859-1", 0).replace("\r", "");
        this.registros = new Registros();
        builder();
        this.isContent = !this.filecontent.equals(">> Sem Texto <<");
    }

    public CSV(String pathfile, String encode) {
        this.pathfile = pathfile;
        this.filecontent = openfile(pathfile, encode, 0).replace("\r", "");
        this.registros = new Registros();
        builder();
        this.isContent = !this.filecontent.equals(">> Sem Texto <<");
    }

    public ArrayList<String> getCabecalho() {
        return (ArrayList<String>) cabecalho.atributos.clone();
    }

    public Cabecalho getCabecalhoObj() {
        return this.cabecalho;
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

    private String openfile(String path_file_complet, String encode, int ignoreFirstNLines) {
        String content = "";
        try {
            final File file = new File(path_file_complet);
            content = new JTxtFileFastReader(file).setCharset(
                    Charset.forName(encode)).readAll();
        } catch (Exception e) {
            content = ">> Sem Texto <<";
        }
        if (ignoreFirstNLines == 0) {
            return content;
        } else {
            return removeFirstLines(content, ignoreFirstNLines);
        }

    }

    private String removeFirstLines(String content, int ignoreFirstNLines) {
        String newcontent = "";
        String[] lines = content.split("\n");
        for (int i = ignoreFirstNLines; i < lines.length; i++) {
            newcontent += lines[i];
            if (i < lines.length - 1) {
                newcontent += "\n";
            }
        }
        return newcontent;
    }

    /*
    ======================================================================
        METODOS PUBLICOS
    ======================================================================    
     */
    public boolean getIsEmpty() {
        return !this.isContent;
    }

    public String getPathfile() {
        return pathfile;
    }

    public boolean setPathfile(String pathfile) {
        if (this.pathfile != null) {
            this.pathfile = pathfile;
            return true;
        }
        return false;
    }

    public boolean setFilecontent(String filecontent) {
        if (this.filecontent != null) {
            this.filecontent = filecontent;
            return true;
        }
        return false;
    }

    public boolean setCabecalho(Cabecalho cabecalho) {
        if (this.cabecalho.atributos.size() != 0) {
            this.cabecalho = cabecalho;
            return true;
        }
        return false;
    }

    public boolean setRegistros(Registros registros) {
        if (this.registros.registros.size() != 0) {
            this.registros = registros;
            return true;
        }
        return false;
    }

    /**
     * Ordena os dados com base no campo especificado.
     *
     * @param field o nome do campo pelo qual os dados devem ser ordenados
     * @param order o tipo de ordenação: 0 para ordem crescente, 1 para ordem
     * decrescente
     *
     */
    public void sortBy(String field, int order) {
        Comparator<Registro> comparator = Comparator.comparingInt(p -> p.getIntField(field));

        if (order == 1) {
            comparator = comparator.reversed();
        } else if (order != 0) {
            throw new IllegalArgumentException("Ordem deve ser 0 (crescente) ou 1 (decrescente)");
        }
        this.registros.registros.sort(comparator);
    }

    public void sort(Comparator<Registro> comparator) {
        this.registros.registros.sort(comparator);
    }

    /*
    -----------------------------------------------------
       INSERT COLUMN
    inicio
    -----------------------------------------------------    
     */
    @FunctionalInterface
    public interface DefaultValueProvider {

        String get(Registro registro);
    }

    public boolean insertColumn(String column_name, DefaultValueProvider defaultValueProvider) {
        try {
            this.cabecalho.atributos.add(column_name);
            for (Registro registro : this.registros.registros) {
                registro.cabecalhos = this.cabecalho;
                String defaultValue = defaultValueProvider.get(registro);
                registro.fields.add(defaultValue);
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /*
    -----------------------------------------------------
       INSERT COLUMN
    fim
    -----------------------------------------------------    
     */
    public boolean setSeparator(String separator) {
        if (this.separator != null) {
            this.separator = separator;
            return true;
        }
        return false;
    }

    public String getFirstOf(String field) {
        return this.registros.get(0).getValueOf(field);
    }

    public ArrayList<Registro> getAll() throws Exception {
        ArrayList<Registro> rs;
        rs = this.registros.getAll();
        if (rs != null) {
            return rs;
        } else {
            throw new Exception("Não encontrado");
        }
    }

    /**
     *
     * @param fieldname
     * @param buffer >fieldname< is in buffer @retur n
     */
    public CSV filterIn(String fieldname, TxtList list) throws Exception {
        ArrayList<Registro> rs;
        int index = cabecalho.find(fieldname);

        rs = this.registros.findAllIn(list, index);
        CSV resp = new CSV(this, rs);
        if (rs != null) {
            return resp;
        } else {
            System.out.println("fieldname: \"" + fieldname + "\"\n");
            throw new Exception("Não encontrado");
        }
    }

    /**
     *
     * @param fieldname
     * @param buffer >fieldname< is in buffer @return @param rebuild default
     * false
     */
    public CSV filterIn(String fieldname, TxtList list, boolean rebuild) throws Exception {
        CSV resp = filterIn(fieldname, list);
        if (resp.registros != null) {
            if (rebuild) {
                this.redoit(resp);
            }
            return resp;
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
            throw new Exception("Valor \"" + fieldname + "\" Não encontrado");
        }

    }

    public void filter(String value, String fieldname, boolean rebuild) throws Exception {
        CSV result = filter(value, fieldname);
        if (rebuild) {
            redoit(result);
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

    public void constuctThis() throws Exception {
        if (this.registros.registros.size() > 0
                && this.cabecalho.atributos.size() > 0) {
            throw new Exception("CSV já foi contruído a primeira vez!");
        }
        if (this.filecontent == null) {
            throw new Exception("CSV não pôde ser gerado. FileContent vazio");
        }
        if (this.pathfile == null) {
            throw new Exception("CSV não pôde ser gerado. pathfile vazio");
        }
        this.builder();
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
        if (this.separator == null) {
            this.separator = ";";
        }
        String[] lines = filecontent.replace("\r", "").split("\n");
        this.cabecalho = new Cabecalho(lines[0], this.separator);
        for (int i = 1; i < lines.length; i++) {
            try {
                Registro r = new Registro(lines[i], this.cabecalho, this.separator);
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

    public ArrayList<Registro> findRegisterByDateAndAfter(Date today, String fieldname, String format)
            throws Exception {
        ArrayList<Registro> rs;
        int index = cabecalho.find(fieldname);
        IntDate idata = new IntDate(today, "yyyyMMdd");
        rs = this.registros.findByDateValueAndLower(idata, index);
        if (rs != null) {
            return rs;
        } else {
            throw new Exception("Não encontrado");
        }
    }

    /**
     * Interna Left = this; Right = Another CSV; fields = Cabecalhos;
     * https://www.alura.com.br/artigos/assets/power-bi-consultas/imagem-3.png
     *
     * @param fieldleft
     * @param fieldright
     * @param right
     * @throws java.lang.Exception
     */
    public void innerjoin(String fieldleft, String fieldright, CSV right) throws Exception {
        // REQUISITOS
        ArrayList<Registro> rRight = right.getAll();
        Cabecalho cright = right.cabecalho;

        // CABEÇALHOS 
        int indexLeft = this.cabecalho.find(fieldleft);
        int indexRight = cright.find(fieldright);

        ArrayList<Registro> result = new ArrayList<Registro>();

        this.registros.registros.stream().anyMatch(itemL -> {
            return rRight.contains(itemL);
        });
        result.addAll(this.registros.registros.stream().filter(itemL -> {

            boolean resposta = false;
            for (Registro registro : rRight) {
                if (itemL.equals(registro.getField(fieldright))) {
                    resposta = true;
                }
            }
            return resposta;
        }).collect(Collectors.toList()));

        redoit(new CSV(this, result));

    }

    public int size() {
        return this.registros.size();
    }

    private void redoit(CSV resp) {
        this.cabecalho = resp.cabecalho;
        this.registros = resp.registros;
    }

    /**
     * Converte um campo específico para o tipo desejado.
     *
     * @param field O nome do campo a ser convertido.
     * @param type O tipo para o qual o campo será convertido. Pode ser "int",
     * "float", ou "UTF_8".
     */
    public void convert(String field, String type) {
        int index = this.cabecalho.find(field);
        if (index > -1) {
            switch (type.toLowerCase()) {
                case "int":
                    this.registros.convertToInt(index);
                    break;
                case "float":
                    this.registros.convertToFloat(index);
                    break;
                case "utf_8":
                    this.registros.converterToUTF_8(index);
                default:
                // STRING
            }
        } else {
            System.out.println("Nada convertido");

        }
    }

//    public void convertFieldTo(String field, Class class_to) {
//        Object converted = class_to.cast(field);
//        for (Registro registro : this.getAll()) {
//            
//        }
//    }

    /*
    ======================================================================
        CLASSES INTERNAS
    ======================================================================    
     */
    private class Registros {

        private ArrayList<Registro> registros;

        public Registros() {
            this.registros = new ArrayList<Registro>();
        }

        public Registro get(int index) {
            return this.registros.get(index);
        }

        private ArrayList<Registro> findAllIn(TxtList list, int field) {
            ArrayList<Registro> rs = new ArrayList<Registro>();
            rs.addAll(this.getAll().stream().filter(linha -> {
                try {
                    for (Object object : list) {
                        if (String.valueOf(object)
                                .equals(String.valueOf(linha.get(field)))) {
                            return true;
                        };
                    }
                    //return list.contains(linha.get(field));
                    return false;
                } catch (Exception ex) {
                    return list.contains("");
                }
            })
                    .collect(Collectors.toList()));
            return rs.isEmpty() ? null : rs;
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

        private ArrayList<Registro> findByDateValueAndLower(IntDate idate, int field) {
            ArrayList<Registro> rs = new ArrayList<Registro>();
            for (Registro registro : registros) {
                int i;
                try {
                    IntDate itdate = new IntDate(
                            registro.get(field),
                            "dd/MM/yyyy");
                    i = itdate.toInt();
                    if (i <= idate.toInt()) {
                        rs.add(registro);
                    }
                } catch (ParseException ex) {

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

        private int size() {
            return this.registros.size();
        }

        private void convertToInt(int index) {
            int value = 0;
            for (Registro registro : registros) {
                try {
                    String s_value = registro.get(index);
                    if (!s_value.equals("")) {
                        value = Integer.valueOf(s_value.replace(" ", "").replace(".", ""));
                    } else {
                        value = 0;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                registro.fields.set(index, value);
            }
        }

        private void convertToFloat(int index) {
            float value = 0f;
            for (Registro registro : registros) {
                try {
                    value = Float.valueOf(registro.get(index).replace(".", "").replace(",", "."));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                registro.fields.set(index, value);
            }
        }

        private void converterToUTF_8(int index) {
            String value = "";
            for (Registro registro : registros) {
                try {
                    byte[] bytes = registro
                            .get(index)
                            .getBytes(
                                    StandardCharsets.ISO_8859_1);
                    value = new String(bytes, StandardCharsets.UTF_8);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                registro.fields.set(index, value);
            }

        }

    }

    public class Registro {

        private ArrayList<Object> fields;
        private Cabecalho cabecalhos;

        public Registro(String line, Cabecalho cabecalhos, String separator) {
            this.fields = new ArrayList<Object>();
            this.cabecalhos = cabecalhos;
            int size = cabecalhos.atributos.size();

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

        private String get(int field) {
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
                retorno += arrumar(String.valueOf(fields.get(x))) + ";";
            }
            return header + "\n" + retorno;
        }

        @Override
        public String toString() {
            String retorno = "[\n";
            for (int x = 0; x < fields.size(); x++) {
                retorno += this.cabecalhos.get(x) + ": \"" + arrumar(String.valueOf(fields.get(x))) + "\"\n";
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

        public Cabecalho(String firstline, String separator) {
            this.atributos = new ArrayList<String>();
            String[] fields = firstline.split(separator);
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
            if (this.atributos.indexOf(fieldname) < 0) {
                for (String atributo : atributos) {
                    if (atributo.contains(fieldname)) {
                        return this.atributos.indexOf(atributo);
                    }
                }
            }
            return this.atributos.indexOf(fieldname);
        }

        private String get(int x) {
            return this.atributos.get(x);
        }

    }

    class Cell {

        private Object value;
        private Object extra;
        private String atributename;
    }

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
}
