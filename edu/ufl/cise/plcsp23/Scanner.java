package edu.ufl.cise.plcsp23;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.HashMap;

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
        LESS,
        GREATER,
        EXCHANGE,
        GREATER_EQ,
        LESS_EQ,
        AND,
        OR,
        EXPONENT
    }

    

    // Constructor
    public Scanner(String input){
        this.input = input;
        this.inputChars = Arrays.copyOf(input.toCharArray(), input.length()+1);
        this.pos = 0;
        this.ch = inputChars[0];

    }

    // Helper function for next char
    void nextChar() {
        pos++;
        ch = inputChars[pos];
    }

    private static HashMap<String, Kind> reservedWords;
    static{
        reservedWords = new HashMap<String,Kind>();
        reservedWords.put("if", Kind.RES_if);
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
                case START -> {
                    tokenStart = pos;
                    switch(ch){
                        case 0 -> {
                            return new Token(IToken.Kind.EOF, tokenStart, 0, inputChars);
                        }
                        case ' ', '\n', '\t', '\r' -> {
                            nextChar();
                        }
                        case '.' -> {
                            nextChar();
                            return new Token(IToken.Kind.DOT, tokenStart, 1, inputChars);
                        }
                        case ',' -> {
                            nextChar();
                            return new Token(IToken.Kind.COMMA, tokenStart, 1, inputChars);
                        }
                        case '?' -> {
                            nextChar();
                            return new Token(IToken.Kind.QUESTION, tokenStart, 1, inputChars);
                        }
                        case ':' -> {
                            nextChar();
                            return new Token(IToken.Kind.COLON, tokenStart, 1, inputChars);
                        }
                        case '(' -> {
                            nextChar();
                            return new Token(IToken.Kind.LPAREN, tokenStart, 1, inputChars);
                        }
                        case ')' -> {
                            nextChar();
                            return new Token(IToken.Kind.RPAREN, tokenStart, 1, inputChars);
                        }
                        case '<' -> {
                            state = state.LESS;
                            nextChar();
                            //return new Token(IToken.Kind.LT, tokenStart, 1, inputChars);
                        }
                        case '>' -> {
                            state = state.GREATER;
                            nextChar();
                            //return new Token(IToken.Kind.GT, tokenStart, 1, inputChars);
                        }
                        case '[' -> {
                            nextChar();
                            return new Token(IToken.Kind.LSQUARE, tokenStart, 1, inputChars);
                        }
                        case ']' -> {
                            nextChar();
                            return new Token(IToken.Kind.RSQUARE, tokenStart, 1, inputChars);
                        }
                        case '{' -> {
                            nextChar();
                            return new Token(IToken.Kind.LCURLY, tokenStart, 1, inputChars);
                        }
                        case '}' -> {
                            nextChar();
                            return new Token(IToken.Kind.RCURLY, tokenStart, 1, inputChars);
                        }
                        case '=' -> {
                            // Sending to HAVE EQ branch
                            state = State.HAVE_EQ;
                            nextChar();
                            //return new Token(IToken.Kind.ASSIGN, tokenStart, 1, inputChars);
                        }
                        case '!' -> {
                            nextChar();
                            return new Token(IToken.Kind.BANG, tokenStart, 1, inputChars);
                        }
                        case '&' -> {
                            nextChar();
                            return new Token(IToken.Kind.BITAND, tokenStart, 1, inputChars);
                        }
                        case '|' -> {
                            nextChar();
                            return new Token(IToken.Kind.BITOR, tokenStart, 1, inputChars);
                        }
                        case '+' -> {
                            nextChar();
                            return new Token(IToken.Kind.PLUS, tokenStart, 1, inputChars);
                        }
                        case '-' -> {
                            nextChar();
                            return new Token(IToken.Kind.QUESTION, tokenStart, 1, inputChars);
                        }
                        case '*' -> {
                            nextChar();
                            return new Token(IToken.Kind.TIMES, tokenStart, 1, inputChars);
                        }
                        case '/' -> {
                            nextChar();
                            return new Token(IToken.Kind.DIV, tokenStart, 1, inputChars);
                        }
                        case '%' -> {
                            nextChar();
                            return new Token(IToken.Kind.MOD, tokenStart, 1, inputChars);
                        }
                        default -> {
                            throw new UnsupportedOperationException("Not implemented yet");
                        }
                    }  
                }
                case HAVE_EQ -> {
                    if(ch == '='){
                        state = state.START;
                        nextChar();
                        return new Token(Kind.EQ, tokenStart, 2, inputChars);
                    } else {
                        state = state.START;
                        return new Token(Kind.ASSIGN, tokenStart, 1, inputChars);
                    }
                }
                case LESS -> {
                    if(ch == '='){
                        state = state.LESS_EQ;
                        nextChar();
                        return new Token(Kind.LE, tokenStart, 2, inputChars);
                    } else if (ch == '-'){
                        state = state.EXCHANGE;
                        nextChar();
                    } else {
                        state = state.START;
                        return new Token(Kind.LT, tokenStart, 2, inputChars);
                    }
                }
                case GREATER -> {
                    if(ch == '='){
                        state = state.GREATER_EQ;
                        nextChar();
                        return new Token(Kind.GE, tokenStart, 2, inputChars);
                    } else {
                        state = state.START;
                        return new Token(Kind.GT, tokenStart, 1, inputChars);
                    }
                }
                case EXCHANGE -> {
                    if(ch == '>'){
                        state = state.START;
                        nextChar();
                        return new Token(Kind.EXCHANGE, tokenStart, 3, inputChars);
                    }else {
                        // throw exception
                    }
                }
                case AND -> {
                    if(ch == '&'){
                        state = state.START;
                        nextChar();
                        return new Token(Kind.AND, tokenStart, 2, inputChars);
                    } else {
                        state = state.START;
                        return new Token(Kind.BITAND, tokenStart, 2, inputChars);
                    }
                }
                case OR -> {
                    if(ch == '|'){
                        state = state.START;
                        nextChar();
                        return new Token(Kind.OR, tokenStart, 2, inputChars);
                    } else {
                        state = state.START;
                        return new Token(Kind.BITOR, tokenStart, 1, inputChars);
                    }
                }
                case EXPONENT -> {
                    if(ch == '*'){
                        state = state.EXPONENT;
                        nextChar();
                        return new Token(Kind.EXP, tokenStart, 2, inputChars);
                    } else {
                        state = state.START;
                        return new Token(Kind.TIMES, tokenStart, 1, inputChars);
                    }
                }
                case NUM_LIT -> {
                    switch(ch){

                    }
                }
                case LET_IDENT -> {
                    switch(ch){

                    }
                }    
            }
        }
    }
    
}
