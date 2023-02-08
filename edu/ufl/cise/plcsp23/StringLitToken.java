package edu.ufl.cise.plcsp23;

//class StringLitToken extends Token implements  IStringLitToken {
//    StringLitToken(Kind kind, String input, int line, int column){
//        super(kind,input,line,column);
//    }
//    public String getValue(){
//        return input;
//    }
//}

class StringLitToken extends Token implements IStringLitToken {
    private final String input;

    StringLitToken(Kind kind, int pos, int length, char[] source) {
        super(kind, pos, length, source);
        this.input = new String(source, pos, length);
    }

    public String getValue() {
        return input;
    }
}
