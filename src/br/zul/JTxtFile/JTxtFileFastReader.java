package br.zul.JTxtFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author luizh
 */
public class JTxtFileFastReader {

    //==========================================================================
    //VARIÁVEIS//
    //=========//
    private final InputStream inputStream;
    private Charset charset;
    
    //==========================================================================
    //CONSTRUTORES//
    //============//
    public JTxtFileFastReader(InputStream inputStream) {
        this.inputStream = inputStream;
        this.charset = StandardCharsets.UTF_8;
    }
    
    public JTxtFileFastReader(String path) throws FileNotFoundException{
        this(new File(path));
    }
    
    public JTxtFileFastReader(File file) throws FileNotFoundException{
        this(new FileInputStream(file));
    }
    
    //==========================================================================
    //MÉTODOS PÚBLICOS//
    //================//
    public String readAll() throws IOException{
        try (InputStreamReader reader = new InputStreamReader(inputStream, charset)) {
            char[] buffer = new char[9128];
            int len;
            StringBuilder builder = new StringBuilder();
            while ((len=reader.read(buffer))!=-1){
                builder.append(buffer, 0, len);
            }
            return builder.toString();
        } finally {
            close();
        }
    }
    
    public void close(){
        try {
            inputStream.close();
        } catch (IOException ex) {}
    }
    
    //==========================================================================
    //GETTERS E SETTERS//
    //=================//
    public Charset getCharset() {
        return charset;
    }
    public JTxtFileFastReader setCharset(Charset charset) {
        this.charset = charset;
        return this;
    }
    
}
