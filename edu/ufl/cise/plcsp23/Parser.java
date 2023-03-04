package edu.ufl.cise.plcsp23;

import java.beans.Expression;

import edu.ufl.cise.plcsp23.IToken.Kind;
import edu.ufl.cise.plcsp23.ast.*;



// public class Parser implements IParser{
//     private final List<Token> tokens;
//     private int current = 0;

//     Parser(list<Token> tokens){
//         this.tokens = tokens;
//     }

//     @Override
//     public AST parse() throws PLCException {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'parse'");
//     }
    
// }

public class Parser implements IParser{
    IScanner scan;
    IToken currentToken;
   
    // private boolean match(Kind ... kind) throws LexicalException{ // passing in a list of kinds
    //     for(Kind k : kind){
    //         if(currentToken.getKind() == k){
    //             currentToken = scan.next();
    //             return true;
    //         }
    //     }
    //     return false;
    // }

    Parser(IScanner scan) throws LexicalException{
        this.scan = scan;
        currentToken = scan.next();
}
private AST expression() throws LexicalException, SyntaxException{
    IToken start = currentToken;
    AST ast = null;
    ast = condititonal_exxpression();
    if(ast == null){
        ast = or_expression();
    }
    return ast;
}

private AST condititonal_exxpression() throws LexicalException, SyntaxException{
    IToken start = currentToken;
    AST ast = null;
    if(currentToken.getKind() == Kind.RES_if){
        AST guard = null;
        AST trueCase = null;
        AST falseCase = null;
        currentToken = scan.next();
        guard = expression();
         if(currentToken.getKind() == Kind.QUESTION){
             currentToken = scan.next(); 
              trueCase = expression();
              if(currentToken.getKind() == Kind.QUESTION ){
                  currentToken = scan.next();
                 falseCase = expression();
                  ast = new ConditionalExpr(start, (Expr)guard, (Expr)trueCase,(Expr) falseCase);
              }
         }
         else{
                throw new SyntaxException("Expected a '?'");
         }
         ast = new ConditionalExpr(start,(Expr)ast,(Expr)trueCase, (Expr)falseCase);
    }
    return ast;
}

private AST or_expression() throws LexicalException, SyntaxException{
    IToken start = currentToken;
    AST ast = and_Expression();
    while(currentToken.getKind() == Kind.OR || currentToken.getKind() == Kind.BITOR){
        currentToken = scan.next();
        AST rightExpression = and_Expression();
        ast = new BinaryExpr(start, (Expr)ast, Kind.OR, (Expr)rightExpression);
    }
    return ast;
}
private AST and_Expression() throws LexicalException, SyntaxException{
    IToken start = currentToken;
    AST ast = comparison_Expression();
    while(currentToken.getKind() == Kind.AND|| currentToken.getKind() == Kind.BITAND){
        currentToken = scan.next(); 
        AST rightExpression = comparison_Expression();
        ast = new BinaryExpr(start,(Expr) ast, Kind.AND, (Expr)rightExpression);
    }
    return ast;
}



private AST comparison_Expression() throws LexicalException, SyntaxException{
    IToken start = currentToken;
    AST ast = power_expression();
    while(currentToken.getKind() == Kind.LT || currentToken.getKind() == Kind.LE || currentToken.getKind() == Kind.GT || currentToken.getKind() == Kind.GE || currentToken.getKind() == Kind.EQ){
        Kind op = currentToken.getKind();
        currentToken = scan.next();
        AST rightExpression = power_expression();
        ast = new BinaryExpr(start, (Expr)ast, op, (Expr)rightExpression);
    }
    return ast;
}

private AST power_expression() throws LexicalException, SyntaxException{
    IToken start = currentToken;
    AST ast = additive_expression();

    if( currentToken.getKind() == Kind.EXP){
        currentToken = scan.next();
        AST rightExpression = power_expression();
        ast = new BinaryExpr(start,(Expr) ast, Kind.EXP,(Expr) rightExpression);
    }
    return ast;
}

private AST additive_expression() throws LexicalException, SyntaxException{
    IToken start = currentToken;
    AST ast = multiplicative_expression();
    while(currentToken.getKind() == Kind.PLUS || currentToken.getKind() == Kind.MINUS){
        Kind op = currentToken.getKind();
        currentToken = scan.next();
        AST rightExpression = multiplicative_expression();
        ast = new BinaryExpr(start,(Expr) ast, op, (Expr)rightExpression);
    }
    return ast;
}


private AST multiplicative_expression() throws LexicalException, SyntaxException{
    IToken start = currentToken;
    AST ast = UnaryExpr();
    while(currentToken.getKind() == Kind.EXP || currentToken.getKind() == Kind.DIV||currentToken.getKind() == Kind.MOD){
        Kind op = currentToken.getKind();
        currentToken = scan.next();
        AST rightExpression = UnaryExpr();
        ast = new BinaryExpr(start,(Expr) ast, op, (Expr)rightExpression);
    }
    return ast;
}

private AST UnaryExpr() throws SyntaxException, LexicalException{
    IToken start = currentToken;
    AST ast;
    if(currentToken.getKind() == Kind.BANG|| currentToken.getKind() == Kind.MINUS|| currentToken.getKind() == Kind.RES_sin|| currentToken.getKind() == Kind.RES_cos|| currentToken.getKind() == Kind.RES_atan){
        AST rightExpression = UnaryExpr();
        ast = new UnaryExpr(start, start.getKind(), (Expr)rightExpression);
    }
    else{
        ast = primaryExpression();
    }

    return ast;

}
private AST primaryExpression() throws SyntaxException, LexicalException{
    AST ast = null;
    IToken start = currentToken;

    if(currentToken.getKind() == Kind.STRING_LIT){
        currentToken = scan.next();
         ast = new StringLitExpr(start);
    }
    else if(currentToken.getKind() == Kind.NUM_LIT){
        currentToken = scan.next();
       ast = new NumLitExpr(start);
    }

   else if(currentToken.getKind() == Kind.IDENT){
    currentToken = scan.next();
       ast =  new IdentExpr(start);
    }
    else if(currentToken.getKind() == Kind.LPAREN){
        currentToken = scan.next();
        ast = expression();
        if(currentToken.getKind() == Kind.RPAREN){
            currentToken = scan.next();
        }
        else{
            throw(new SyntaxException("invalid token in primary expression"));
        }
    }
   else if(currentToken.getKind() == Kind.RES_rand){
    currentToken = scan.next();
        ast= new RandomExpr(start);
    }
    else if ( currentToken.getKind() == Kind.RES_Z){
    currentToken = scan.next();
    ast = new ZExpr(start);
}
    else{
        throw(new SyntaxException("invalid token in primary expression"));
    }
    return ast;
}   

    @Override
    public AST parse() throws PLCException {
       return expression();
    }

}