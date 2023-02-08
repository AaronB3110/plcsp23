package edu.ufl.cise.plcsp23;

public class NumLitToken extends Token implements INumLitToken {
    final int val;

    public NumLitToken(int val, int pos, int length, char[] inputChars){
        super(Kind.NUM_LIT, pos, length, inputChars);
        this.val = val;
    }

    public int getValue(){
        return val;
    }
}
