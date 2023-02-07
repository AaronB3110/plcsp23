package edu.ufl.cise.plcsp23;

public class Token implements IToken {
    final Kind kind;
    final String input;
    final int line;
    final int column;
    SourceLocation loc;

    public Token(Kind kind, String input, int line, int column){
        this.kind = kind;
        this.input = input;
        this.line = line;
        this.column = column;
        this.loc = new SourceLocation(line, column);

        System.out.println("hey i pushed");
    }

    @Override
    public SourceLocation getSourceLocation() {
        return loc;
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
