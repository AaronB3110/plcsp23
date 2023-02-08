package edu.ufl.cise.plcsp23;

public class Token implements IToken {
    Kind kind;
    int pos;
    int length;
    int column;
    String input;
    char[] source;
    SourceLocation location;

    public Token(Kind kind, int pos, int length, char[] source){
        //super();
        this.kind = kind;
        this.pos = pos;
        this.length = length;
        this.source = source;
    }

    public Token(Kind kind, String input, int line, int column){
        super();
        this.kind = kind;
        this.input = input;
        this.length = line;
        this.column = column;
    }

    @Override
    public SourceLocation getSourceLocation() {
        return null;
    }

    @Override
    public Kind getKind() {
        return kind;
    }

    @Override
    public String getTokenString() {
        // TODO Auto-generated method stub
        return null;
    }
    
}
