package edu.ufl.cise.plcsp23;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.ToLongBiFunction;
import java.util.HashMap;

import edu.ufl.cise.plcsp23.IToken.Kind;

public class Scanner implements IScanner {
    final String input;
    final char[] inputChars;
    private final List<Token> tokens = new ArrayList<>();

    int pos;
    int line = 1;
    int col = 1;
    char ch;
    String value = ""; // Used to hold the value of NUMLITS (MAYBE STRING LITS TOO?)

    private enum State{
        START,
        NUM_LIT,
        IDENT,
        HAVE_EQ,
        LESS,
        GREATER,
        EXCHANGE,
        GREATER_EQ,
        LESS_EQ,
        AND,
        OR,
        EXPONENT,
        QUOTE,
        STRING_LIT
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
        col++;
        ch = inputChars[pos];
    }

    private static HashMap<String, Kind> reservedWords;
    static{
        reservedWords = new HashMap<String,Kind>();
        reservedWords.put("image",Kind.RES_image);
        reservedWords.put("if", Kind.RES_if);
        reservedWords.put("pixel", Kind.RES_pixel);
        reservedWords.put("int",Kind.RES_int);
        reservedWords.put("string",Kind.RES_string);
        reservedWords.put("void",Kind.RES_void);
        reservedWords.put("nil",Kind.RES_nil);
        reservedWords.put("load",Kind.RES_load);
        reservedWords.put("display",Kind.RES_display);
        reservedWords.put("write",Kind.RES_write);
        reservedWords.put("x",Kind.RES_x);
        reservedWords.put("y",Kind.RES_y);
        reservedWords.put("a",Kind.RES_a);
        reservedWords.put("r",Kind.RES_r);
        reservedWords.put("X",Kind.RES_X);
        reservedWords.put("Y",Kind.RES_Y);
        reservedWords.put("Z",Kind.RES_Z);
        reservedWords.put("x_cart",Kind.RES_x_cart);
        reservedWords.put("y_cart",Kind.RES_y_cart);
        reservedWords.put("a_polar",Kind.RES_a_polar);
        reservedWords.put("r_polar",Kind.RES_r_polar);
        reservedWords.put("rand",Kind.RES_rand);
        reservedWords.put("sin",Kind.RES_sin);
        reservedWords.put("cos",Kind.RES_cos);
        reservedWords.put("atan",Kind.RES_atan);
        reservedWords.put("while",Kind.RES_while);
        reservedWords.put("red",Kind.RES_red);
        reservedWords.put("grn",Kind.RES_grn);
        reservedWords.put("blu",Kind.RES_blu);
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
                    value = "";
                    tokenStart = pos;
                    switch(ch){
                        case 0 -> {
                            return new Token(IToken.Kind.EOF, tokenStart, 0, inputChars);
                        }
                        case ' ', '\n', '\t', '\r', '\f'-> {
                            if(ch == '\n'){
                                line++;
                                col = 1;
                                pos++;
                                ch = inputChars[pos];
                            }
                            else{
                                nextChar();
                            }
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
                            state = State.AND;
                            nextChar();
                            //return new Token(IToken.Kind.BITAND, tokenStart, 1, inputChars);
                        }
                        case '|' -> {
                            state = State.OR;
                            nextChar();
                            //return new Token(IToken.Kind.BITOR, tokenStart, 1, inputChars);
                        }
                        case '+' -> {
                            nextChar();
                            return new Token(IToken.Kind.PLUS, tokenStart, 1, inputChars);
                        }
                        case '-' -> {
                            nextChar();
                            return new Token(IToken.Kind.MINUS, tokenStart, 1, inputChars);
                        }
                        case '*' -> {
                            state = State.EXPONENT;
                            nextChar();
                            //return new Token(IToken.Kind.TIMES, tokenStart, 1, inputChars);
                        }
                        case '/' -> {
                            nextChar();
                            return new Token(IToken.Kind.DIV, tokenStart, 1, inputChars);
                        }
                        case '%' -> {
                            nextChar();
                            return new Token(IToken.Kind.MOD, tokenStart, 1, inputChars);
                        }
                        case '\"' -> {//"\"
                            state = state.STRING_LIT;
                            nextChar();              
                        }
                        case '1','2','3','4','5','6','7','8','9' -> {
                            state = State.NUM_LIT;
                            value += ch;
                            nextChar();
                        }
                        case '~' -> {
                            while(ch != '\n'){
                                nextChar();
                            }
                            break;
                        }
                        case '0' -> {
                            nextChar();
                            return new NumLitToken(0, tokenStart, pos - tokenStart, inputChars);
                        }
                        default -> {
                            if(ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z' || ch == '_'){
                                state = State.IDENT;
                                value += ch;
                                nextChar();
                            } else {
                                throw new LexicalException("Character not implemented!");
                            }
                            
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
                        throw new LexicalException("Incorrect character");
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
                        case '0','1','2','3','4','5','6','7','8','9' -> {
                            state = State.NUM_LIT;
                            value += ch;
                            nextChar();
                        }
                        default -> {
                            //exception if large number
                            try {
                                Integer.parseInt(value);
                            } catch (NumberFormatException e) {
                                throw new LexicalException("Integer not valid");
                            }
                            return new NumLitToken(Integer.parseInt(value), tokenStart, pos - tokenStart, inputChars);  
                        }
                    }
                }
                case IDENT -> {
                    if(Character.isLetterOrDigit(ch)|| ch == '_'){
                        state = State.IDENT;
                        value += ch;
                        nextChar();
                    } else {
                        state = State.START;
                        if(reservedWords.containsKey(value)){
                            IToken.Kind mapKind = reservedWords.get(value);
                            return new Token(mapKind, tokenStart, value, line, col-(pos-tokenStart), inputChars);
                        }
                        return new Token(Kind.IDENT, tokenStart, value, line, col-(pos-tokenStart), inputChars);
                    }
                }
                case STRING_LIT ->{
                    switch(ch){
                        case '\\' ->{
                        state = State.STRING_LIT;
                        nextChar();
                        if(ch == 'b' ||ch == 'n' || ch =='r' || ch =='"'|| ch == '\\'|| ch == 't'){
                            nextChar();
                        
                        }
                            else{
                                throw new LexicalException("String not valid");
                        
                                //throw error
                            }
                        
                        }// black sslash indicatting escape sequence.
                        case '\"' ->{ // quotes within qoutes  end create a new token 
                        state = State.START;
                        nextChar();

                        /*
                         * FIX:
                         * return inputChars ONLY UP TO THIS POSITION, otherwise it returns the entire input
                         */
        
                        return new StringLitToken(tokenStart, pos-tokenStart, line, col-(pos-tokenStart), inputChars) ; 
                        }
                        case '\n', '\r'  ->{// not allowed thows another wrror // 
                            //could this be a defult? 
                            //throw error
                            throw new LexicalException("String not valid");
                    }
                    default ->{
                        state = State.STRING_LIT;// maybe this is redundant
                        nextChar();
                    }
                }
              }
            }
        }
    }
}
