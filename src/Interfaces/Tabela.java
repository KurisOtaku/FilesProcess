/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package Interfaces;

import Objects.Registro;
import Objects.Registros;
import filesprocess.CSV.Cabecalho;
import java.util.ArrayList;
import java.util.Comparator;

/**
 *
 * @author cristiano.rosa
 */
public interface Tabela {

    public ArrayList<String> getCabecalho();

    public Cabecalho getCabecalhoObj();

    /*
    ======================================================================
        METODOS PUBLICOS
    ======================================================================    
     */
    public boolean getIsEmpty();

    public String getPathfile();

    public boolean setPathfile(String pathfile);

    public boolean setFilecontent(String filecontent);

    public boolean setCabecalho(Cabecalho cabecalho);

    public boolean setRegistros(Registros registros);

    public String getDateModification();
    
    

    /**
     * Ordena os dados com base no campo especificado.
     *
     * @param field o nome do campo pelo qual os dados devem ser ordenados
     * @param order o tipo de ordenação: 0 para ordem crescente, 1 para ordem
     * decrescente
     *
     */
    public void sortBy(String field, int order) ;

    public void sort(Comparator<Registro> comparator) ;
    public void printCabecalhos() ;
    
    
}
