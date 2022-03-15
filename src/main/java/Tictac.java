import javax.xml.stream.*;
import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class Tictac {

    final String _x = "x", _o = "o", _dot = ".";
    static int stepCount;
    String[][] table;
    String playerName;
    Random random;
    Scanner scanner;
    ParseXML prs = new ParseXML();
    //SimpleDateFormat formater = new SimpleDateFormat("dd-MM-yyyy__hh_mm_ss__");
    //  List<>

    public static void main(String[] args) throws IOException {

        new Tictac().runParse();
    }

    Tictac() {
        random = new Random();
        scanner = new Scanner(System.in);
        table = new String[3][3];
    }

    //parse xml
   void runParse() throws IOException {
        System.out.println("Воспроизвести предыдущую игру? Да - 0, Нет. Сыграть новую и записать - 1");
        if (scanner.nextInt() == 0) {
            try {
                prs.prsXml(Paths.get("RecordGame.xml"));
            } catch (FileNotFoundException e) {

            System.out.println("Нет файла.");

        } catch (XMLStreamException e) {
                System.out.println("Не верный формат файла.");
        }
        }else{scanner.nextLine(); game();}
    }

    // logic
    void game() throws IOException {
        System.out.println("Ваше имя :");
        playerName = scanner.nextLine();
        field(); // инициализация таблицы

        try(FileOutputStream out = new FileOutputStream( "RecordGame.xml")){


            //do {
                // <StAX>
                XMLOutputFactory output = XMLOutputFactory.newInstance();
                XMLStreamWriter writer = output.createXMLStreamWriter(out);

                writer.writeStartDocument("utf-8", "1.0");
                    writer.writeStartElement("Gameplay");
                        writer.writeStartElement("Player");
                            writer.writeAttribute("id", "1");
                            writer.writeAttribute("name",playerName);
                            writer.writeAttribute("symbol", "x");
                        writer.writeEndElement();
                        writer.writeStartElement("Player");
                            writer.writeAttribute("id", "2");
                            writer.writeAttribute("name","Бот");
                            writer.writeAttribute("symbol", "o");
                        writer.writeEndElement();

                        writer.writeStartElement("Game");  // <Game>

                while(true) {
                    stepCount++;
                    move(writer,stepCount);
                    if (win(_x)) {
                        System.out.println(playerName + " выиграл");
                        String text = "\n" + playerName + "+";
                        Files.write(Paths.get("Scores.txt"), text.getBytes(), StandardOpenOption.APPEND);

                        writer.writeEndElement(); // <Game>
                        writer.writeStartElement("GameResult");
                            writer.writeStartElement("Playerwin");
                                writer.writeAttribute("id", "1");
                                writer.writeAttribute("name",playerName);
                                writer.writeAttribute("symbol", "х");
                            writer.writeEndElement();
                        writer.writeEndElement();
                        break;
                    }
                    if (fieldFull()) {
                        System.out.println("Ничья");

                        writer.writeEndElement(); // <Game>
                        writer.writeStartElement("GameResult");
                            writer.writeStartElement("draw");
                                writer.writeCharacters("DRAW!");
                            writer.writeEndElement();
                        writer.writeEndElement();
                        break;
                    }
                    stepCount++;
                    bot(writer,stepCount);
                    printField();
                    if (win(_o)) {
                        System.out.println("Бот выиграл");
                        String text = "\nБот+";
                        Files.write(Paths.get("Scores.txt"), text.getBytes(), StandardOpenOption.APPEND);

                        writer.writeEndElement(); // <Game>
                        writer.writeStartElement("GameResult");
                            writer.writeStartElement("Playerwin");
                                writer.writeAttribute("id", "2");
                                writer.writeAttribute("name","Бот");
                                writer.writeAttribute("symbol", "о");
                            writer.writeEndElement();
                        writer.writeEndElement();
                        break;
                    }
                    if (fieldFull()) {
                        System.out.println("Ничья");

                        writer.writeEndElement(); // <Game>
                        writer.writeStartElement("GameResult");
                            writer.writeStartElement("Draw");
                                writer.writeCharacters("DRAW!");
                            writer.writeEndElement();
                        writer.writeEndElement();
                        break;
                        }
                    }


                System.out.println("Всё");

                printField();
                field();

                writer.writeEndElement();
                writer.writeEndDocument();
                writer.flush();
                writer.close();
            //} while (scanner.nextInt() == 0);
            System.out.println("Ещё раз ? Да - 0, Нет - 1");
            if(scanner.nextInt() == 0) {scanner.nextLine(); game();}


        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

    // feat.
    void field() {
        for (int row = 0; row < 3; row++)
            for (int col = 0; col < 3; col++)
                table[row][col] = _dot;
    }

    void printField() {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++)
                System.out.print(table[row][col] + " ");
            System.out.println();
        }
    }

    void move(XMLStreamWriter wr, int i) throws XMLStreamException {
        int x, y;
        do {
            System.out.println("Enter Y and X (1..3):");
            x = scanner.nextInt() - 1;
            y = scanner.nextInt() - 1;
        } while (!empty(x, y));

        table[x][y] = _x;

        wr.writeStartElement("Step");
            wr.writeAttribute("num",Integer.toString(i));
            wr.writeAttribute("playerId", "1");
            wr.writeAttribute("x",Integer.toString(x));
            wr.writeAttribute("y",Integer.toString(y));
        wr.writeEndElement();
    }


    boolean empty(int x, int y) {
        if (x < 0 || y < 0 || x >= 3|| y >= 3)
            return false;
        return table[x][y] == _dot;
    }

    void bot(XMLStreamWriter wr, int i) throws XMLStreamException {
        int x, y;
        do {
            x = random.nextInt(3);
            y = random.nextInt(3);
        } while (!empty(x, y));
        table[x][y] = _o;

        wr.writeStartElement("Step");
            wr.writeAttribute("num",Integer.toString(i));
            wr.writeAttribute("playerId", "2");
            wr.writeAttribute("x",Integer.toString(x));
            wr.writeAttribute("y",Integer.toString(y));
        wr.writeEndElement();
    }

    boolean win(String dot) {
        for (int i = 0; i < 3; i++)
            if ((table[i][0] == dot && table[i][1] == dot &&
                    table[i][2] == dot) ||
                    (table[0][i] == dot && table[1][i] == dot &&
                            table[2][i] == dot))
                return true;
        if ((table[0][0] == dot && table[1][1] == dot &&
                table[2][2] == dot) ||
                (table[2][0] == dot && table[1][1] == dot &&
                        table[0][2] == dot))
            return true;
        return false;
    }

    boolean fieldFull() {
        for (int row = 0; row < 3; row++)
            for (int col = 0; col < 3; col++)
                if (table[row][col] == _dot)
                    return false;
        return true;
    }
}