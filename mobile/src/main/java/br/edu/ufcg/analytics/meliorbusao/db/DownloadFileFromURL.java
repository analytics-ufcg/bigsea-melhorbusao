package br.edu.ufcg.analytics.meliorbusao.db;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import br.edu.ufcg.analytics.meliorbusao.Cities;
import br.edu.ufcg.analytics.meliorbusao.Constants;
import br.edu.ufcg.analytics.meliorbusao.listeners.OnFinishedParseListener;
import br.edu.ufcg.analytics.meliorbusao.utils.SharedPreferencesUtils;


/**
 * Background Async Task to download file
 */
class DownloadFileFromURL extends AsyncTask<String, String, String> {
    private Context mContext;
    private OnFinishedParseListener mListener;

    /**
     * Before starting background thread
     * Show Progress Bar Dialog
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    public DownloadFileFromURL(Context context,OnFinishedParseListener listener) {
        mContext = context;
        mListener = listener;
    }

    /**
     * Downloading file in background thread
     */
    @Override
    protected String doInBackground(String... f_url) {
        int count;
        try {
            Log.d("DownloadFileFromURL", "Starting to download");
            File externalStorage = Environment.getExternalStorageDirectory();
            File bdPath = new File(externalStorage, Constants.BD_FOLDER_PATH);
            if (!bdPath.exists()) {
                bdPath.mkdirs();
            } else {
//                File f = new File(Environment.getExternalStorageDirectory() + Constants.BD_FOLDER_PATH);
//                System.out.println(f.getAbsolutePath());
//                System.out.println("file? " + f.isFile());
//                System.out.println("directory? " + f.isDirectory());
//                System.out.println("Exists?!?! " + f.exists());
//                System.out.println(f.length());
//                File[] files = f.listFiles();
//                for (File inFile : files) {
////                if (inFile.isDirectory()) {
//                    Log.d("Files", "FileName:" + inFile.getName());
//                    Log.d("Files", "FileLength:" + inFile.length());
//                    Log.d("Files", "FileName:" + inFile.getTotalSpace());
////                if(inFile.getName().equals("arquivo.tar.gz")) inFile.delete();
//                    if (inFile.getName().equals("adump_melhor_busao_bd.zip")) {
//                        Log.d("Files", "File Found");
//                    }
//
////                }
//                }
            }

            File arquivo = new File(bdPath, "dump_melhor_busao_bd.zip");


            URL url = new URL(f_url[0]);
            URLConnection conection = url.openConnection();
            conection.connect();
            // getting file length
            int lenghtOfFile = conection.getContentLength();
            // input stream to read file - with 8k buffer
            InputStream input = new BufferedInputStream(url.openStream(), 8192);
            // Output stream to write file


            OutputStream output = new FileOutputStream(arquivo);

            byte data[] = new byte[1024];
            long total = 0;
            while ((count = input.read(data)) != -1) {
                total += count;
                // publishing the progress....
                // After this onProgressUpdate will be called
                publishProgress("" + (int) ((total * 100) / lenghtOfFile));
                // writing data to file
                output.write(data, 0, count);
            }
            Log.d("DownloadFileFromURL", "Successfully Downloaded");
            // flushing output
            output.flush();
            // closing streams
            output.close();
            input.close();

            String path = Environment.getExternalStorageDirectory() + Constants.BD_FOLDER_PATH;
            File f = new File(Environment.getExternalStorageDirectory() + Constants.BD_FOLDER_PATH);
//            System.out.println(f.getAbsolutePath());
//            System.out.println("file? " + f.isFile());
//            System.out.println("directory? " + f.isDirectory());
//            System.out.println("Exists?!?! " + f.exists());
//            System.out.println(f.length());

            File[] files = f.listFiles();
            for (File inFile : files) {
//                if (inFile.isDirectory()) {
//                Log.d("Files", "FileName:" + inFile.getName());
//                Log.d("Files", "FileLength:" + inFile.length());
//                Log.d("Files", "FileName:" + inFile.getTotalSpace());
//                if(inFile.getName().equals("arquivo.tar.gz")) inFile.delete();
                if (inFile.getName().equals("dump_melhor_busao_bd.zip")) {
                    Log.d("Files", "File Found @ " + f.getAbsolutePath() + "/");
                    unzip(inFile.getName(), f.getAbsolutePath() + "/");
                }
//                }
            }

        } catch (Exception e) {
            Log.e("Error: ", e.getMessage());
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        //TODO rever esse finished parte e talvez alterar.
        //o que ele faz eh detectar e informar ao listener que terminou a operacao,
        //daih da dismiss no dialog e o app fica usavel!
        mListener.finishedParse(Constants.KIND_ROUTESTOP);
    }

    public void unzip(String zipFile, String location) throws IOException {
        try {
            File f = new File(location);

            if (!f.isDirectory()) {
                f.mkdirs();
            }
            ZipInputStream zin = new ZipInputStream(new FileInputStream(location + zipFile));
            try {
                Log.d("Zip Found", "Decompressing");
                ZipEntry ze = null;
                while ((ze = zin.getNextEntry()) != null) {
                    String path = location + ze.getName();

                    if (ze.isDirectory()) {
                        File unzipFile = new File(path);
                        if (!unzipFile.isDirectory()) {
                            unzipFile.mkdirs();
                        }
                    } else {
                        FileOutputStream fout = new FileOutputStream(path, false);
                        try {
                            byte b[] = new byte[1024];
                            int n;
                            while ((n = zin.read(b, 0, 1024)) >= 0) {
                                fout.write(b, 0, n);
                            }
                            zin.closeEntry();
                        } finally {
                            fout.close();
                        }
                    }
                }
                Log.d("Zip Found", "Decompressed");

            } finally {
                zin.close();
                //Deleta o .zip após descompactar com sucesso!
                File[] files = f.listFiles();
                for (File inFile : files) {
                    if (inFile.getName().equals("dump_melhor_busao_bd.zip")) {
                        inFile.delete();
                        Log.d("Zip found", "File deleted");
                    }
                }

                //Arquivo baixado com sucesso, descompactado e removido. Chamar quem quer que seja
                //que vai ler o .BD :)
            }

        } catch (Exception e) {
            Log.e("UNZIP", "Unzip exception", e);
        }
    }



}