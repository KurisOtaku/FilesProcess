/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
ORIGINAL
 */
package filesprocess;

import JTxtFile.JTxtFileFastReader;
import Objects.IntDate;
import Objects.Registro;
import Objects.Registros;
import java.io.File;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * @author cristiano.rosa
 */
public class CSV {

    private String pathfile;
    private String filecontent;
    private Cabecalho cabecalho; // LINHAS
    private Objects.Registros registros; // LINHAS
    private String separator;
    private boolean isContent;
    private String dateModification;

    //CLONE
    public CSV(CSV csv) {
        this.pathfile = csv.pathfile;
        this.filecontent = csv.filecontent;
        this.cabecalho = csv.cabecalho;
        this.registros = csv.registros;
        this.dateModification = csv.dateModification;
        this.isContent = !this.filecontent.equals(">> Sem Texto <<");
    }

    public CSV() {
        this.pathfile = "";
        this.filecontent = ">> Sem Texto <<";
        this.cabecalho = new Cabecalho("", ";");
        this.registros = new Objects.Registros();
        this.isContent = false;
        this.dateModification = "";
    }

    public CSV(String pathfile, int ignorefirstNllines) {
        this.pathfile = pathfile;
        this.filecontent = openfile(pathfile, "iso-8859-1", ignorefirstNllines).replace("\r", "");
        this.dateModification = getDateModifyFile(pathfile, "yyyy-MM-dd HH:mm");
        this.registros = new Objects.Registros();
        builder();
        this.isContent = !this.filecontent.equals(">> Sem Texto <<");
    }

    public CSV(String pathfile) {
        this.pathfile = pathfile;
        this.filecontent = openfile(pathfile, "iso-8859-1", 0).replace("\r", "");
        this.dateModification = getDateModifyFile(pathfile, "yyyy-MM-dd HH:mm");
        this.registros = new Objects.Registros();
        builder();
        this.isContent = !this.filecontent.equals(">> Sem Texto <<");
    }

    public CSV(String pathfile, boolean otherseparator, String separator) {
        if (otherseparator) {
            this.separator = separator;
        }
        this.pathfile = pathfile;
        this.filecontent = openfile(pathfile, "iso-8859-1", 0).replace("\r", "");
        this.dateModification = getDateModifyFile(pathfile, "yyyy-MM-dd HH:mm");
        this.registros = new Objects.Registros();
        builder();
        this.isContent = !this.filecontent.equals(">> Sem Texto <<");
    }

    public CSV(String pathfile, String encode) {
        this.pathfile = pathfile;
        this.filecontent = openfile(pathfile, encode, 0).replace("\r", "");
        this.dateModification = getDateModifyFile(pathfile, "yyyy-MM-dd HH:mm");
        this.registros = new Objects.Registros();
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
        this.registros = new Objects.Registros(registros);
    }

    private CSV(CSV csv, ArrayList<Registro> rs) {
        this.pathfile = csv.pathfile;
        this.filecontent = csv.filecontent;
        this.cabecalho = csv.cabecalho;
        this.registros = new Objects.Registros(rs);
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

    private String getDateModifyFile(String path_file_complet, String format) {
        String content = "";
        Date dt;
        SimpleDateFormat formatter = new SimpleDateFormat(format);

        try {
            final File file = new File(path_file_complet);
            dt = new Date(file.lastModified());
        } catch (Exception e) {
            dt = new Date();
        }
        content = formatter.format(dt);
        return content;
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
    public String getContentUpdated() {
        String retorno = "";
        for (String atributo : cabecalho.atributos) {
            retorno += atributo + ";";
        }
        retorno += "\n";

        for (Registro registro : registros.registros) {
            retorno += registro.getLineContent() + "\n";
        }
        return retorno;
    }

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

    public boolean setRegistros(Objects.Registros registros) {
        if (this.registros.registros.size() != 0) {
            this.registros = registros;
            return true;
        }
        return false;
    }

    public String getDateModification() {
        return dateModification;
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

    public void printCabecalhos() {
        for (String atributo : this.cabecalho.atributos) {
            System.out.println('"' + atributo + '"');
        }
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
            for (Registro registro : this.registros.getRegistros()) {
                registro.cabecalhos = this.cabecalho;
                String defaultValue = defaultValueProvider.get(registro);
                registro.getFields().add(defaultValue);
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

        public int find(String fieldname) {
            if (this.atributos.indexOf(fieldname) < 0) {
                for (String atributo : atributos) {
                    if (atributo.contains(fieldname)) {
                        return this.atributos.indexOf(atributo);
                    }
                }
            }
            return this.atributos.indexOf(fieldname);
        }

        public String get(int x) {
            return this.atributos.get(x);
        }

        public ArrayList<String> getAtributos() {
            return this.atributos;
        }

    }

    class Cell {

        private Object value;
        private Object extra;
        private String atributename;
    }

    class Registros extends Objects.Registro {

        public Registros(String line, Cabecalho cabecalhos, String separator) {
            super(line, cabecalhos, separator);
        }

    }

}
