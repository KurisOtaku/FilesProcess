/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package filesprocess;

import com.aspose.cells.Cells;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 *
 * @author cristiano.rosa
 */
public class Excel {

    public static void m2ain(String[] args) {
        Double teste = 45882d;
        System.out.println(
                LocalDate // Represent a date-only vaule, without time-of-day and without time zone.
                        .of(1899, Month.DECEMBER, 30) // Specify epoch reference date used by *some* versions of Excel. Beware: Some versions use a 1904 epoch reference. Returns a `LocalDate` object.
                        .plusDays( // Add a number of days. 
                                (long) Double.parseDouble(teste + "") // Get a number of whole days from your input string.
                        ) // Returns another instance of a `LocalDate`, per Immutable Objects pattern, rather than altering the original.        
        );
        System.out.println(
                excelSerialParaData(teste)
        );
    }

    public static void main(String[] args) throws Exception {
        String path = "K:\\Relatórios de Uso comum\\Relatórios SPO 2025\\Tasks\\2025-08\\Compra.xlsx";
        Excel ex = new Excel(path);
        ex = ex.filtrarPorColuna("Mês/ Ano", "Ago/25")
                .filtrarPorColuna("Pdv", "10179") //   .filtrarPorColuna("Data Visita", "13/08/2025")
                ;
        ex.convert("Data Visita", val -> {
            if (val instanceof Double) {
                return excelSerialParaData((Double) val);
            }
            return val;
        });
        ex = ex.filtrarPorColuna("Data Visita", "13/08/2025")
                .filtrarPorColuna("Mensal/ Diária", "PRODUCT_COVERAGE");
        ex.convert("Mensal/ Diária", val -> {
            return "Diária";
        });
        String resp = "";

        resp += "Visita: " + ex.getLinha(0).get("Data Visita") + "\n";
        for (int i = 0; i < ex.rows - 1; i++) {
            Map<String, Object> linhe = ex.getLinha(i);
            String task_text = (String) linhe.get("Texto da Tarefa");
            String task_type = (String) linhe.get("Mensal/ Diária");;
            resp += "Tarefa: \"" + task_text + "\"\n";
        }
        System.out.println(resp);

    }

    private String textCSV;
    private Cells cells;
    private final int rows;
    private final int cols;
    private Workbook workbook;

    // Construtor público a partir de caminho de arquivo
    public Excel(String pathfile) throws Exception {
        this(new Workbook(pathfile));
    }

    // Construtor privado para criar Excel a partir de um Workbook já existente
    private Excel(Workbook wb) throws Exception {
        this.workbook = wb;
        Worksheet worksheet = wb.getWorksheets().get(0);
        this.cells = worksheet.getCells();
        this.rows = this.cells.getMaxDataRow() + 1;
        this.cols = this.cells.getMaxDataColumn() + 1;

        // Gerar textCSV
/*
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Object valor = cells.get(i, j).getValue();
                sb.append(valor != null ? valor.toString() : "").append(";");
            }
            sb.append("\n");
        }
*/        
        //this.textCSV = sb.toString();
    }

    // Método de filtro que retorna um novo Excel
    public Excel filtrarPorColuna(String coluna, Object valorFiltro) throws Exception {
        // Lê os nomes das colunas
        List<String> nomesColunas = new ArrayList<>();
        for (int i = 0; i < cols; i++) {
            Object valor = cells.get(0, i).getValue();
            nomesColunas.add(valor != null ? valor.toString() : "");
        }

        // Encontra índice da coluna
        int colunaIndex = nomesColunas.indexOf(coluna);
        if (colunaIndex == -1) {
            throw new IllegalArgumentException("Coluna não encontrada: " + coluna);
        }

        // Criar novo workbook para armazenar as linhas filtradas
        Workbook novoWorkbook = new Workbook();
        Worksheet novaSheet = novoWorkbook.getWorksheets().get(0);
        Cells novasCells = novaSheet.getCells();

        int novaLinha = 0;

        // Copia cabeçalho
        for (int j = 0; j < cols; j++) {
            novasCells.get(novaLinha, j).putValue(nomesColunas.get(j));
        }
        novaLinha++;

        // Percorre e copia linhas filtradas
        for (int i = 1; i < rows; i++) {
            Object valorCelula = cells.get(i, colunaIndex).getValue();
            if ((valorCelula == null && valorFiltro == null)
                    || (valorCelula != null && valorCelula.equals(valorFiltro))) {

                for (int j = 0; j < cols; j++) {
                    Object val = cells.get(i, j).getValue();
                    novasCells.get(novaLinha, j).putValue(val);
                }
                novaLinha++;
            }
        }

        // Retorna novo Excel baseado no workbook filtrado
        return new Excel(novoWorkbook);
    }

    public Excel filtrarPorColunaLambda(String coluna, Predicate<Object> filtro) throws Exception {
        // Lê os nomes das colunas
        List<String> nomesColunas = new ArrayList<>();
        for (int i = 0; i < cols; i++) {
            Object valor = cells.get(0, i).getValue();
            nomesColunas.add(valor != null ? valor.toString() : "");
        }

        // Encontra índice da coluna
        int colunaIndex = nomesColunas.indexOf(coluna);
        if (colunaIndex == -1) {
            throw new IllegalArgumentException("Coluna não encontrada: " + coluna);
        }

        // Criar novo workbook para armazenar as linhas filtradas
        Workbook novoWorkbook = new Workbook();
        Worksheet novaSheet = novoWorkbook.getWorksheets().get(0);
        Cells novasCells = novaSheet.getCells();

        int novaLinha = 0;

        // Copia cabeçalho
        for (int j = 0; j < cols; j++) {
            novasCells.get(novaLinha, j).putValue(nomesColunas.get(j));
        }
        novaLinha++;

        // Percorre e copia linhas filtradas
        for (int i = 1; i < rows; i++) {
            Object valorCelula = cells.get(i, colunaIndex).getValue();

            if (filtro.test(valorCelula)) {
                for (int j = 0; j < cols; j++) {
                    Object val = cells.get(i, j).getValue();
                    novasCells.get(novaLinha, j).putValue(val);
                }
                novaLinha++;
            }
        }

        // Retorna novo Excel baseado no workbook filtrado
        return new Excel(novoWorkbook);
    }
        
    

    public Map<String, Object> buscarPrimeiraLinha() {
        // Lê os nomes das colunas
        List<String> nomesColunas = new ArrayList<>();
        for (int i = 0; i < cols; i++) {
            Object valor = cells.get(0, i).getValue();
            nomesColunas.add(valor != null ? valor.toString() : "");
        }
        // Monta o mapa com os valores dessa linha
        Map<String, Object> linha = new LinkedHashMap<>();
        for (int j = 1; j < cols; j++) {
            Object val = cells.get(1, j).getValue();
            linha.put(nomesColunas.get(j), val);
        }
        return linha;

    }

    public Map<String, Object> buscarPrimeiroPorColuna(String coluna, Object valorFiltro) {
        // Lê os nomes das colunas
        List<String> nomesColunas = new ArrayList<>();
        for (int i = 0; i < cols; i++) {
            Object valor = cells.get(0, i).getValue();
            nomesColunas.add(valor != null ? valor.toString() : "");
        }

        // Encontra índice da coluna
        int colunaIndex = nomesColunas.indexOf(coluna);
        if (colunaIndex == -1) {
            throw new IllegalArgumentException("Coluna não encontrada: " + coluna);
        }

        // Percorre linhas e retorna a primeira que casar
        for (int i = 1; i < rows; i++) {
            Object valorCelula = cells.get(i, colunaIndex).getValue();

            if ((valorCelula == null && valorFiltro == null)
                    || (valorCelula != null && valorCelula.equals(valorFiltro))) {

                // Monta o mapa com os valores dessa linha
                Map<String, Object> linha = new LinkedHashMap<>();
                for (int j = 0; j < cols; j++) {
                    Object val = cells.get(i, j).getValue();
                    linha.put(nomesColunas.get(j), val);
                }
                return linha;
            }
        }

        // Se nada encontrado
        return null;
    }

    public List<Map<String, Object>> getTodasLinhas() {
        List<Map<String, Object>> todasLinhas = new ArrayList<>();

        // Lê cabeçalhos
        List<String> nomesColunas = new ArrayList<>();
        for (int i = 0; i < cols; i++) {
            Object valor = cells.get(0, i).getValue();
            nomesColunas.add(valor != null ? valor.toString() : "");
        }

        // Percorre todas as linhas (começando da 1, já que 0 é cabeçalho)
        for (int i = 1; i < rows; i++) {
            Map<String, Object> linha = new LinkedHashMap<>();
            for (int j = 0; j < cols; j++) {
                Object val = cells.get(i, j).getValue();
                linha.put(nomesColunas.get(j), val);
            }
            todasLinhas.add(linha);
        }

        return todasLinhas;
    }

    public Map<String, Object> getLinha(int index) {
        if (index < 0 || index >= (rows - 1)) {
            throw new IndexOutOfBoundsException("Índice fora do intervalo: " + index);
        }

        // Lê cabeçalhos
        List<String> nomesColunas = new ArrayList<>();
        for (int i = 0; i < cols; i++) {
            Object valor = cells.get(0, i).getValue();
            nomesColunas.add(valor != null ? valor.toString() : "");
        }

        // Monta o mapa da linha
        Map<String, Object> linha = new LinkedHashMap<>();
        int excelRowIndex = index + 1; // +1 porque a linha 0 é o cabeçalho
        for (int j = 0; j < cols; j++) {
            Object val = cells.get(excelRowIndex, j).getValue();
            linha.put(nomesColunas.get(j), val);
        }

        return linha;
    }

    public void convert(String coluna, Function<Object, Object> conversor) {
        // Ler cabeçalho
        List<String> nomesColunas = new ArrayList<>();
        for (int i = 0; i < cols; i++) {
            Object valor = cells.get(0, i).getValue();
            nomesColunas.add(valor != null ? valor.toString() : "");
        }

        // Descobrir índice da coluna
        int colunaIndex = nomesColunas.indexOf(coluna);
        if (colunaIndex == -1) {
            throw new IllegalArgumentException("Coluna não encontrada: " + coluna);
        }

        // Aplicar conversão em cada célula da coluna (sem mexer no cabeçalho)
        for (int i = 1; i < rows; i++) {
            Object valorOriginal = cells.get(i, colunaIndex).getValue();
            Object valorConvertido = conversor.apply(valorOriginal);
            cells.get(i, colunaIndex).putValue(valorConvertido);
        }
    }

    public static String excelSerialParaData(double numeroExcel) {
        /*
      LocalDate lcldt =  LocalDate // Represent a date-only vaule, without time-of-day and without time zone.
                .of(1899, Month.DECEMBER, 30) // Specify epoch reference date used by *some* versions of Excel. Beware: Some versions use a 1904 epoch reference. Returns a `LocalDate` object.
                .plusDays( // Add a number of days. 
                        (long) Double.parseDouble(numeroExcel+"") // Get a number of whole days from your input string.
                ); // Returns another instance of a `LocalDate`, per Immutable Objects pattern, rather than altering the original.        
         */
// Excel considera 1 = 1900-01-01
        int dias = (int) numeroExcel;
        double frac = numeroExcel - dias; // parte fracionária do dia

        // Ajuste do bug do 29/02/1900
        if (dias < 60) {
            dias -= 1; // datas antes de 1900-03-01 não precisam de ajuste
        } else {
            dias -= 2; // corrige o bug (adiciona 2 dias)
        }

        Calendar cal = Calendar.getInstance();
        cal.set(1900, Calendar.JANUARY, 1, 0, 0, 0); // 1899-12-31 é base
        cal.add(Calendar.DATE, dias);

        // adiciona a parte fracionária (horas, minutos, segundos)
        int totalSegundos = (int) Math.round(frac * 24 * 60 * 60);
        cal.add(Calendar.SECOND, totalSegundos);

        Date data = cal.getTime();
        return new SimpleDateFormat("dd/MM/yyyy").format(data);
    }

    public int getRows() {
        return this.rows;
    }

    
    public Excel ordenarPorColuna(String coluna, Comparator<Object> comparador) throws Exception {
    // Lê os nomes das colunas
    List<String> nomesColunas = new ArrayList<>();
    for (int i = 0; i < cols; i++) {
        Object valor = cells.get(0, i).getValue();
        nomesColunas.add(valor != null ? valor.toString() : "");
    }

    // Encontra índice da coluna
    int colunaIndex = nomesColunas.indexOf(coluna);
    if (colunaIndex == -1) {
        throw new IllegalArgumentException("Coluna não encontrada: " + coluna);
    }

    // Lista para armazenar linhas (exceto cabeçalho)
    List<List<Object>> linhas = new ArrayList<>();

    for (int i = 1; i < rows; i++) {
        List<Object> linha = new ArrayList<>();
        for (int j = 0; j < cols; j++) {
            linha.add(cells.get(i, j).getValue());
        }
        linhas.add(linha);
    }

    // Ordena usando o comparador fornecido
    linhas.sort((l1, l2) -> {
        Object v1 = l1.get(colunaIndex);
        Object v2 = l2.get(colunaIndex);
        return comparador.compare(v1, v2);
    });

    // Criar novo workbook
    Workbook novoWorkbook = new Workbook();
    Worksheet novaSheet = novoWorkbook.getWorksheets().get(0);
    Cells novasCells = novaSheet.getCells();

    int linhaNova = 0;

    // Copia cabeçalho
    for (int j = 0; j < cols; j++) {
        novasCells.get(linhaNova, j).putValue(nomesColunas.get(j));
    }
    linhaNova++;

    // Copia linhas ordenadas
    for (List<Object> linha : linhas) {
        for (int j = 0; j < cols; j++) {
            novasCells.get(linhaNova, j).putValue(linha.get(j));
        }
        linhaNova++;
    }

    return new Excel(novoWorkbook);
}
}
