package edu.ufl.cise.plcsp23;
import static org.junit.jupiter.api.Assertions.*;

import org.hamcrest.core.Is;
import org.junit.jupiter.api.Test;
import edu.ufl.cise.plcsp23.IToken.Kind;

public class BasicTester {
    void checkEOF(IToken t){
        assertEquals(Kind.EOF, t.getKind());
    }

    void checkDot(IToken t) {
        assertEquals(Kind.DOT, t.getKind());
    }

    void checkEquals(IToken t){
        assertEquals(Kind.ASSIGN, t.getKind());
    }

    void checkToken(Kind kind, IToken t){
        assertEquals(kind, t.getKind());
    }

    @Test
    void emptyProg() throws LexicalException{
        String input = "";
        final IScanner scanner = CompilerComponentFactory.makeScanner(input);
        checkEOF(scanner.next());
    }

    @Test
    void whiteSpace() throws LexicalException{
        String input = " \t \r\n  \n";
        final IScanner scanner = CompilerComponentFactory.makeScanner(input);
        checkEOF(scanner.next());
    }
    
    @Test
    void dot() throws LexicalException{
        String input = ".";
        final IScanner scanner = CompilerComponentFactory.makeScanner(input);
        checkDot(scanner.next());
    }
    
    @Test
    void eq() throws LexicalException{
        String input = "=";
        final IScanner scanner = CompilerComponentFactory.makeScanner(input);
        checkEquals(scanner.next());
    }

    @Test
    void singleCharToken() throws LexicalException {
        String input = """
        +  *
        ,
        ,
        """;
        final IScanner scanner = CompilerComponentFactory.makeScanner(input);
        checkToken(Kind.PLUS, scanner.next());
        checkToken(Kind.TIMES, scanner.next());
        checkToken(Kind.COMMA, scanner.next());
        checkToken(Kind.COMMA, scanner.next());
        checkEOF(scanner.next());

    }

    @Test
    void checkEquals() throws LexicalException {
        String input = """
        ==
        == ==
        ==*==
        *==+
        """;
        final IScanner scanner = CompilerComponentFactory.makeScanner(input);
        checkToken(Kind.EQ,scanner.next());
        checkToken(Kind.EQ,scanner.next());
        checkToken(Kind.EQ,scanner.next());
        checkToken(Kind.EQ,scanner.next());
        checkToken(Kind.TIMES,scanner.next());
        checkToken(Kind.EQ,scanner.next());
        checkToken(Kind.TIMES,scanner.next());
        checkToken(Kind.EQ,scanner.next());
        checkToken(Kind.PLUS,scanner.next());
    }

    @Test
    void checkGreaterLess() throws LexicalException {
        String input = """
                <=
                <
                =
                ==
                >=
                >
                <->
                """;
        final IScanner scanner = CompilerComponentFactory.makeScanner(input);
        checkToken(Kind.LE, scanner.next());
        checkToken(Kind.LT, scanner.next());
        checkToken(Kind.ASSIGN, scanner.next());
        checkToken(Kind.EQ, scanner.next());
        checkToken(Kind.GE, scanner.next());
        checkToken(Kind.GT, scanner.next());
        checkToken(Kind.EXCHANGE, scanner.next());
        checkEOF(scanner.next());
        
    }
}
