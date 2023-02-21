package edu.ufl.cise.plcsp23;

public class Token implements IToken {
    Kind kind;
    int pos;
    int length;
    int column;
    int line;
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

    public Token(Kind kind, int pos, String input, int line, int column, char[] source){
        this.kind = kind;
        this.pos = pos;
        this.input = input;
        this.line = line;
        this.column = column;
    }

    @Override
    public SourceLocation getSourceLocation() {
        return new SourceLocation(line, column);
    }

    @Override
    public Kind getKind() {
        return kind;
    }

    @Override
    public String getTokenString() {
        // TODO Auto-generated method stub
        String tokenStr = String.valueOf(input);
        //tokenStr = tokenStr.substring(pos, (pos+length));
        return tokenStr;
    }

}
