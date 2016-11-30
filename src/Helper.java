/**
 * Created by sborisov on 29.11.16.
 */
public class Helper {
    void printHelp()
    {
        System.out.println("Usage of parser:" );
        System.out.println("java -jar parser.jar -h \"print this help\"" );
        System.out.println("java -jar parser.jar -root c:\\root -out f:\\output" );
        System.out.println("\tc:\\root   \"start point of scanning\"" );
        System.out.println("\tf:\\output \"output directory for script\"" );
    }

}
