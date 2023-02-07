package edu.ufl.cise.plcsp23;

class NumLitToken extends Token implements INumLitToken {
    NumLitToken(Kind kind, String input, int line, int column){
        super( kind, input, line, column); //look up

    }
    public int getValue(){
        return Integer.parseInt(input);
    }
}
