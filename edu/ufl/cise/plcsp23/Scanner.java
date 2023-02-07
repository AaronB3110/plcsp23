package edu.ufl.cise.plcsp23;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
                    }
                    break;
            }
            nextChar();
        }
    }

    private void nextChar() {
        pos++;
        ch = inputChars[pos];
    }
    
}
