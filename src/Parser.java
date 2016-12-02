import java.io.*;
import java.util.*;
import org.ini4j.*;

public class Parser {

    private final String PICASA_CONFIG_FILE = ".picasa.ini";
    private final String COMMAND_STRING = "echo f | xcopy /s /e /h /y ";
    private Map<String, String> params;
    private Map<String, String> albums;
    private List<Item> filesListForCopyScript;

    private String root_path;
    private String out_path;

    private long folders_count = 0;
    private long files_count = 0;


    public static void main(String[] args) {
        if (args.length != 4 ){
            new Helper().printHelp();
            System.exit(1);
        }

        new Parser(args);

   }

    public Parser(String[] args) {
        params = new HashMap();
        albums = new HashMap();
        filesListForCopyScript = new ArrayList();
//        filesListForCopyScript.add(new AbstractMap.SimpleEntry("foo", "bar"));

        for (int i=0; i<args.length; i+=2){
            params.put(args[i], args[i+1]);
        }

        scanFolder( params.get("-root") );
        generateScript();
    }



    private void scanFolder(String path){
        File root = new File( path );

        if ( !root.exists() ){
            System.out.println("Folder: "+ path + " doesn't exist!");
            System.exit(1);
        }

        File[] list = root.listFiles();

        if (list == null) return;

        for ( File f : list ) {
            if ( f.isDirectory() ) {
                scanFolder( f.getAbsolutePath() );
//                System.out.println( "Dir:" + f.getAbsoluteFile() );
            }
            else {
                  if( f.getName().matches(PICASA_CONFIG_FILE)){
                        parseIni(f);
                        System.out.println( "The file is parsed: " + f.getAbsoluteFile() );
                  }
            }
        }

    }

    void parseIni(File f){
        try{
            Wini ini = new Wini(f);
            List<Map.Entry<String, String>> curFiles = new ArrayList();
//output names of all sections
//            String target = params.get("-out") + File.separator + curAlbum + File.separator+ curSection;
            Collection<Profile.Section> list = ini.values();

            for(Profile.Section section : list){

                String curSection = section.getName();

                if( curSection.contains(".album:") ){
                    curSection = curSection.replace(".album:", "");
                    albums.put(curSection, Translit.toTranslit(section.get("name")));
                } else if ( curSection.contains(".jpg") || curSection.contains(".png") || curSection.contains(".JPG") || curSection.contains(".PNG")) {
                    String curAlbum = section.get("albums");
                    if (!curAlbum.isEmpty()) {
                        if (curAlbum.contains(",")) {

                            String[] result = curAlbum.split(",");

                            for (int i = 0; i < result.length; i++) {
                                curFiles.add(new AbstractMap.SimpleEntry(result[i], curSection));
                            }
                        } else {
                            curFiles.add(new AbstractMap.SimpleEntry(curAlbum, curSection));
                        }

                    }
                }
            }

            for (Map.Entry<String, String> entry : curFiles) {
                String source = f.getParent() + File.separator + entry.getValue();
                String target = params.get("-out") + File.separator +  albums.get(entry.getKey()) + File.separator + entry.getValue();
                filesListForCopyScript.add(new Item( source , target ));

            }

        }
        catch (Exception e) {
            System.out.println(e);
        }
    }


    private void generateScript(){
//        try {
//            File outFile = new File ("script.cmd");
//
//            FileWriter fWriter = new FileWriter(outFile, false);
//            PrintWriter pWriter = new PrintWriter (fWriter);
//
//            for (Map.Entry<String, String> entry : filesListForCopyScript) {
////                System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
//                pWriter.println(COMMAND_STRING + " \"" + entry.getKey() + "\" \"" + entry.getValue() +"\" ");
//            }
//
//            pWriter.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

     try {
            String script = "script.cmd";
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(script), "Cp866"));
            Collections.sort(filesListForCopyScript);
            for (Item entry : filesListForCopyScript) {
//                System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
                out.append(COMMAND_STRING + " \"" + entry.getFirst() + "\" \"" + entry.getSecond() +"\" ");
                out.newLine();
            }
            out.close();
            System.out.println("\"" +script + "\" is generated.");
    } catch (FileNotFoundException ex) {
        ex.printStackTrace();
    } catch (UnsupportedEncodingException ex) {
        ex.printStackTrace();
    } catch (IOException ex) {
        ex.printStackTrace();
    }
}

}
