package edu.ufl.cise.plcsp23;

import edu.ufl.cise.plcsp23.ast.AST;

public class Parser implements IParser{
    private final List<Token> tokens;
    private int current = 0;

    Parser(list<Token> tokens){
        this.tokens = tokens;
    }

    @Override
    public AST parse() throws PLCException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'parse'");
    }
    
}
