/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package filesprocess;

import br.zul.JTxtFile.JTxtFileFastReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author cristiano.rosa
 */
public class TxtList extends ArrayList {

    private final String pathfile;
    private final String content;

    public TxtList(String pathfile) throws IOException {
        super();
        this.pathfile = pathfile;
        this.content = openfile(this.pathfile, "iso-8859-1").replace("\r", "");
        build();
    }

    public TxtList() {
        super();
        this.pathfile = "";
        this.content = "";
    }

    private String openfile(String path_file_complet, String encode)
            throws IOException {
        final File file = new File(path_file_complet);
        String content = new JTxtFileFastReader(file)
                .setCharset(Charset.forName(encode)).readAll();
        return content;
    }

    private void build() {
        String[] lines = this.content.replace("\r", "").split("\n");
        for (String line : lines) {
            try {
                this.add(line);
            } catch (Exception e) {
            }
        }
    }
}
