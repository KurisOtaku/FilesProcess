/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package filesprocess;

import JTxtFile.JTxtFileFastReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import sun.jvm.hotspot.debugger.Debugger;

public class Param {

    Map<String, String> params;
    private boolean isOk;
    private String message;
    private String pathfile;
    private String content;
    private boolean creating;

    public Param() {
        params = new HashMap<String, String>();
        this.isOk = false;
        this.message = "Falta pathfile";
        this.content = ">> Sem Texto <<";
        this.creating = true;
        verify();
    }

    public Param(String pathfile) throws IOException {
        this.creating = true;
        params = new HashMap<String, String>();
        this.pathfile = pathfile;
        verify();
        build();
    }

    private boolean isReady() {
        if (pathfile == null || pathfile.equals("")) {
            return false;
        } else {
            if (pathfile.equals("")) {
                return false;
            }
        }
        if (content == null) {
            return false;
        } else {
            if (content.equals("")) {
                return false;
            } else {
                if (content.equals(">> Sem Texto <<")) {
                    return false;
                }
            }
        }
        verify(); // DOUBLE CHECK :P
        return true;
    }

    public boolean verify() {
        this.message = "";
        if (pathfile == null || pathfile.equals("")) {
            this.message += "Falta pathfile\n";
        } else {
            if (pathfile.equals("")) {
                this.message += "Falta content\n";
            }
        }
        if (content == null) {
            this.message += "Falta content\n";
        } else {
            if (content.equals("")) {
                this.message += "Falta content\n";
            } else {
                if (content.equals(">> Sem Texto <<")) {
                    this.message += "Falta content\n";
                }
            }
        }

        if (params.isEmpty()) {
            this.message += "Falta rodar build()";
        }
        if (this.message.equals("")) {
            // Tudo Ok
            this.isOk = true;
            this.message = "OK";
            return true;
        } else {
            System.out.println("Param diz: \n" + this.message);
        }
        return false;
    }

    public void build() throws IOException {
        if (isReady()) {
            String[] f_pararm = getParameters(pathfile);
            if (f_pararm != null) {
                for (String param : f_pararm) {
                    String[] p = param.split(";");
                    params.put(p[0], p[1]);
                }
            }
        } else {
            System.out.println("Nao esta pronto");
            if (!trataMessages()) {
               this.verify();
                build();
               this.verify();
            }
        }
        if (creating) {
            savefile(this.pathfile);
        }
    }

    private boolean trataMessages() throws IOException {
        if (message.contains("Falta content")) {
            this.content = openfile(pathfile, "iso-8859-1");
            verify();
        }
        if (message.contains("Falta rodar build()")) {
            return false;
        }
        return true;
    }

    public void setPathfile(String pathfile) {
        this.pathfile = pathfile;
        verify();
    }

    public void setContent(String content) {
        this.content = content;
        verify();
    }

    public boolean getisOK() {
        if (!this.isOk) {
            System.out.println(this.message);
        }
        return this.isOk;
    }

    private String[] getParameters(String pathfile) {
        verify();
        String[] parameters_lines = null;
        try {
            String content = "";
            if (creating) {
                content = this.content
                        .replace("\r", "");
            } else {
                content = openfile(
                        pathfile,
                        "iso-8859-1")
                        .replace("\r", "");
            }
            this.isOk = !content.equals(">> Sem Texto <<");
            parameters_lines = content.split("\n");
        } catch (Exception e) {
            parameters_lines = null;
        }
        if (parameters_lines != null) {
            String pf = "";
            for (String parameters_line : parameters_lines) {
                if (!parameters_line.contains("#")) {
                    pf += parameters_line + "\n";
                }
            }
            return pf.split("\n");
        } else {
            return null;
        }
    }

    public String getParam(String key) {
        verify();
        return this.params.get(key);
    }

    private String openfile(String path_file_complet, String encode) {
        try {
            final File file = new File(path_file_complet);
            String content = new JTxtFileFastReader(file).setCharset(
                    Charset.forName(encode)).readAll();
            return content;
        } catch (Exception e) {
            return ">> Sem Texto <<";
        }
    }

    public boolean savefile(String pathfile) throws IOException {
        File f = new File(pathfile);
        if (!f.exists()) {
            f.createNewFile();
        }
        String[] linhas = content.split("\n");
        boolean resultado = false;
        try {
            PrintWriter writer = new PrintWriter(pathfile, "UTF-8");
            for (String linha : linhas) {
                writer.println(linha);
            }
            writer.close();
            resultado = true;
        } catch (Exception d) {
            d.printStackTrace();
        }
        return resultado;
    }

    public Map<String, String> getMap() {
        return this.params;
    }

    public String getPathfile() {
        return this.pathfile;
    }

    public void uptdateContent() {
        String content = "#formato;CSV\n"
                + "#ignorar se linha começa com #\n";
        for (Map.Entry<String, String> entry : params.entrySet()) {
            Object key = entry.getKey();
            Object val = entry.getValue();
            content += key + ";" + val + ";\n";
        }
        this.content = content;
    }

}
