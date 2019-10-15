package app.logic.minimax;

import app.logic.Board;
import app.ui.Controller;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.parse.Parser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class MiniMaxTest {


    public static void main(String [ ] args)
    {
        generateNextConstellationsSixPossibleTest();
    }

    public static void generateNextConstellationsSixPossibleTest(){
        final String fileName = "src/main/resources/graph/example.dot";
        Board board = new Board(Controller.discColor1,Controller.discColor2);
        board.insertInColumn(5, Controller.discColor1);
        board.insertInColumn(1, Controller.discColor2);

        TreeNode<Board> tree = MiniMax.contructTreeNode(board,2);
        String treeString = TreeNode.treeToString(tree,false);

        Path path = Paths.get(fileName);
        byte[] strToBytes = treeString.getBytes();

        try {
            Files.write(path, strToBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            MutableGraph g = Parser.read(treeString);
            Graphviz.fromGraph(g).render(Format.PNG).toFile(new File("src/main/resources/graph/example.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
