package edu.ufl.cise.plcsp23;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.ufl.cise.plcsp23.IToken.Kind;

public class Scanner implements IScanner {
    final String input;
    final char[] inputChars;
    private final List<Token> tokens = new ArrayList<>();

    int pos;
    char ch;

    private enum State{
        START,
        NUM_LIT,
        LET_IDENT,
        HAVE_EQ,
    }

    // Constructor
    public Scanner(String input){
        this.input = input;
        this.inputChars = Arrays.copyOf(input.toCharArray(), input.length()+1);
        this.pos = 0;
        this.ch = inputChars[0];

    }

    @Override
    public IToken next() throws LexicalException {
        Token token = scanToken();
        tokens.add(token);
        return token;
    }

    private Token scanToken() throws LexicalException {
        State state = State.START;
        int tokenStart = -1;

        while (true) {
            switch (state) {
                case START:
                    tokenStart = pos;
                    switch(ch){
                        case 0 -> {
                            return new Token(IToken.Kind.EOF, tokenStart, 0, inputChars);
                        }
                        case ' ', '\t','\r','\n' -> nextChar();

                        case '.' -> {
                            return new Token(IToken.Kind.DOT, tokenStart, 1, inputChars);
                        }
                        case ',' -> {
                            return new Token(IToken.Kind.COMMA, tokenStart, 1, inputChars);
                        }
                        case '?' -> {
                            return new Token(IToken.Kind.QUESTION, tokenStart, 1, inputChars);
                        }
                        case ':' -> {
                            return new Token(IToken.Kind.COLON, tokenStart, 1, inputChars);
                        }
                        case '(' -> {
                            return new Token(IToken.Kind.LPAREN, tokenStart, 1, inputChars);
                        }
                        case ')' -> {
                            return new Token(IToken.Kind.RPAREN, tokenStart, 1, inputChars);
                        }
                        case '<' -> {
                            return new Token(IToken.Kind.LT, tokenStart, 1, inputChars);
                        }
                        case '>' -> {
                            return new Token(IToken.Kind.GT, tokenStart, 1, inputChars);
                        }
                        case '[' -> {
                            return new Token(IToken.Kind.LSQUARE, tokenStart, 1, inputChars);
                        }
                        case ']' -> {
                            return new Token(IToken.Kind.RSQUARE, tokenStart, 1, inputChars);
                        }
                        case '{' -> {
                            return new Token(IToken.Kind.LCURLY, tokenStart, 1, inputChars);
                        }
                        case '}' -> {
                            return new Token(IToken.Kind.RCURLY, tokenStart, 1, inputChars);
                        }
                        case '=' -> {
                            return new Token(IToken.Kind.ASSIGN, tokenStart, 1, inputChars);
                        }
                        case '!' -> {
                            return new Token(IToken.Kind.BANG, tokenStart, 1, inputChars);
                        }
                        case '&' -> {
                            return new Token(IToken.Kind.BITAND, tokenStart, 1, inputChars);
                        }
                        case '|' -> {
                            return new Token(IToken.Kind.BITOR, tokenStart, 1, inputChars);
                        }
                        case '+' -> {
                            return new Token(IToken.Kind.PLUS, tokenStart, 1, inputChars);
                        }
                        case '-' -> {
                            return new Token(IToken.Kind.QUESTION, tokenStart, 1, inputChars);
                        }
                        case '*' -> {
                            return new Token(IToken.Kind.TIMES, tokenStart, 1, inputChars);
                        }
                        case '/' -> {
                            return new Token(IToken.Kind.DIV, tokenStart, 1, inputChars);
                        }
                        case '%' -> {
                            return new Token(IToken.Kind.MOD, tokenStart, 1, inputChars);
                        }
                    }
                    break;
            }
            nextChar();
        }
    }

    /*  EQ, // ==
		EXCHANGE, // <->
		LE, // <=
		GE, // >=
		AND, // &&
		OR, // ||
		TIMES, // *
		EXP, // ** 
    */

    private void nextChar() {
        pos++;
        ch = inputChars[pos];
    }
    
}
