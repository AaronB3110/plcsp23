package edu.ufl.cise.plcsp23;

public class NumLitToken implements INumLitToken {
    private int value;

    @Override
    public int getValue(){
        return this.value;
    }

    @Override
    public SourceLocation getSourceLocation() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Kind getKind() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getTokenString() {
        // TODO Auto-generated method stub
        return null;
    }
}
