package br.zul.JTxtFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author luizh
 */
public class JTxtFileFastWriter {

    //==========================================================================
    //VARIÁVEIS//
    //=========//
    private final OutputStream outputStream;
    private Charset charset;
    
    //==========================================================================
    //CONSTRUTORES//
    //============//
    public JTxtFileFastWriter(OutputStream outputStream) {
        this.outputStream = outputStream;
        this.charset = StandardCharsets.UTF_8;
    }
    
    public JTxtFileFastWriter(String path, boolean append) throws FileNotFoundException{
        this(new File(path), append);
    }
    
    public JTxtFileFastWriter(File file, boolean append) throws FileNotFoundException{
        this(new FileOutputStream(file));
    }
    
    //==========================================================================
    //MÉTODOS PÚBLICOS//
    //================//
    public void writeAll(String content) throws IOException{
        try (OutputStreamWriter writer = new OutputStreamWriter(outputStream, charset)){
            writer.write(content);
        } finally {
            close();
        }
    }
    
    public void close(){
        try {
            outputStream.close();
        } catch (IOException ex) {}
    }
    
    //==========================================================================
    //GETTERS E SETTERS//
    //=================//
    public Charset getCharset() {
        return charset;
    }
    public JTxtFileFastWriter setCharset(Charset charset) {
        this.charset = charset;
        return this;
    }
    
}
