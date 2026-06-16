/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Objects;

import filesprocess.CSV;
import filesprocess.TxtList;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 *
 * @author cristiano.rosa
 */
public class Registros {

    public ArrayList<Registro> registros;

    public Registros() {
        this.registros = new ArrayList<Registro>();
    }

    public Registros(ArrayList<Registro> registros) {
        this.registros = registros;
    }

    public Registro get(int index) {
        return this.registros.get(index);
    }

    public ArrayList<Registro> findAllIn(TxtList list, int field) {
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


    public void add(Registro r) {
        registros.add(r);
    }

    public ArrayList<Registro> find(String value, int field) {
        ArrayList<Registro> rs = new ArrayList<Registro>();
        for (Registro registro : registros) {
            if (registro.get(field).equals(value)) {
                rs.add(registro);
            }
        }
        return rs.isEmpty() ? null : rs;
    }

    public ArrayList<Registro> findContains(String value, int field) {
        ArrayList<Registro> rs = new ArrayList<Registro>();
        for (Registro registro : registros) {
            if (registro.get(field).contains(value)) {
                rs.add(registro);
            }
        }
        return rs.isEmpty() ? null : rs;
    }

    public ArrayList<Registro> findByIntValue(int value, int field) {
        ArrayList<Registro> rs = new ArrayList<Registro>();
        for (Registro registro : registros) {
            if (Integer.valueOf(registro.get(field)).equals(value)) {
                rs.add(registro);
            }
        }
        return rs.isEmpty() ? null : rs;
    }

    public ArrayList<Registro> findByDateValueAndLower(IntDate idate, int field) {
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

    
    public Registro find(int value, int field) {
        //@todo
        for (Registro registro : registros) {
            if (registro.get(field).equals(value)) {
                return registro;
            }
        }
        return null;
    }

    public ArrayList<Registro> getAll() {
        return this.registros;
    }

    public int size() {
        return this.registros.size();
    }

    public void convertToInt(int index) {
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

    public void convertToFloat(int index) {
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

    public void converterToUTF_8(int index) {
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

    public ArrayList<Registro> getRegistros() {
        return this.registros;
    }

}
