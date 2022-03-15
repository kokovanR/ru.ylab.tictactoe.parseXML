import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Path;


public class ParseXML {
    static String status[][]= new String[3][3];
    Replay replay = new Replay();
    void prsXml(Path path) throws FileNotFoundException, XMLStreamException {
       int line;
       // <StAX>
        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        XMLStreamReader read = xmlInputFactory.createXMLStreamReader(new FileInputStream(path.toFile()));
         while (read.hasNext()) {
            line = read.next();
            if (line == XMLEvent.START_ELEMENT) {
                switch (read.getName().getLocalPart()) {

                   case "Step":
                        int id = Integer.parseInt(read.getAttributeValue(null, "playerId"));
                        int x = Integer.parseInt(read.getAttributeValue(null, "x"));
                        int y = Integer.parseInt(read.getAttributeValue(null, "y"));
                        status[x][y] = (id == 1 ? "x" : "o");
                        replay.replay(id,x,y);
                        System.out.println();

                        break;

                    case "Playerwin":
                            String symbol = read.getAttributeValue(null, "symbol");
                            String name = read.getAttributeValue(null, "name");
                            String idWin = read.getAttributeValue(null, "id");
                            System.out.printf("Player %s -> %s is winner as '%s'!", idWin,name,symbol);

                        break;

                    case "Draw":
                        line = read.next();
                        if (line == XMLEvent.CHARACTERS) {
                            String draw = read.getText();
                            System.out.printf("%s!!", draw);
                        }
                        break;
                }
            }
        }
    }
}