package igrek.todotree.system.files;

import android.app.Activity;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class Files {
    Activity activity;
    private String pathToExtSD;

    public Files(Activity activity) {
        this.activity = activity;
        pathSDInit();
    }

    private void pathSDInit(){
        pathToExtSD = "/storage/extSdCard";
        if(!exists(pathToExtSD)){
            pathToExtSD = Environment.getExternalStorageDirectory().toString();
        }
    }

    public Path pathSD() {
        return new Path(pathToExtSD);
    }

    public String internalAppDirectory() {
        return activity.getFilesDir().toString();
    }

    public List<String> listDir(String path) {
        List<String> lista = new ArrayList<>();
        File f = new File(path);
        File file[] = f.listFiles();
        for (File aFile : file) {
            lista.add(aFile.getName());
        }
        return lista;
    }

    public List<String> listDir(Path path) {
        return listDir(path.toString());
    }

    public byte[] openFile(String filename) throws IOException {
        RandomAccessFile f = null;
        f = new RandomAccessFile(new File(filename), "r");
        int length = (int) f.length();
        byte[] data = new byte[length];
        f.readFully(data);
        f.close();
        return data;
    }

    public String openFileString(String filename) throws IOException {
        byte[] bytes = openFile(filename);
        return new String(bytes, "UTF-8");
    }

    public void saveFile(String filename, byte[] data) throws IOException {
        File file = new File(filename);
        FileOutputStream fos;
        fos = new FileOutputStream(file);
        fos.write(data);
        fos.flush();
        fos.close();
    }

    public void saveFile(String filename, String str) throws IOException {
        saveFile(filename, str.getBytes());
    }

    public boolean isDirectory(String path) {
        File f = new File(path);
        return f.exists() && f.isDirectory();
    }

    public boolean isFile(String path) {
        File f = new File(path);
        return f.exists() && f.isFile();
    }

    public boolean exists(String path) {
        File f = new File(path);
        return f.exists();
    }

    public boolean delete(String path){
        File file = new File(path);
        return file.delete();
    }

    public boolean delete(Path path){
        return delete(path.toString());
    }

}
