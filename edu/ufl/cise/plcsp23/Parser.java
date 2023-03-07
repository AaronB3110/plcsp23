package edu.ufl.cise.plcsp23;
import java.rmi.server.RemoteRef;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.experimental.theories.Theories;
import org.junit.jupiter.params.shadow.com.univocity.parsers.csv.CsvFormat;

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
   
    private void match(Kind ... kind) throws PLCException{ // passing in a list of kinds
        for(Kind k : kind){
            if(currentToken.getKind() == k){
                currentToken = scan.next();
                return;
            }
        }
        throw new SyntaxException("Expected one of " + Arrays.toString(kind) + " but found " + currentToken.getKind());
    }
    private void typeMatch(Type...types) throws PLCException{ // passing in a list of kinds
        for(Type t : types){
            if(Type.getType(currentToken) ==t){
                currentToken = scan.next();
                return;
            }
        }
        throw new SyntaxException("Expected one of " + Arrays.toString(types) + " but found " + currentToken.getKind());
    }


    private boolean match1(Kind ... kind) throws PLCException{ // passing in a list of kinds
        for(Kind k : kind){
            if(currentToken.getKind() == k){
                currentToken = scan.next();
                return true;
            }
        }
       return false;
    }
    private boolean typeMatch1(Type...types) throws PLCException{ // passing in a list of kinds
        for(Type t : types){
            if(Type.getType(currentToken) ==t){
                currentToken = scan.next();
                return true;
            }
        }
        return false;
    }

    Parser(IScanner scan) throws LexicalException{
        this.scan = scan;
        currentToken = scan.next();
}
private Program program() throws PLCException{
    IToken start = currentToken;
    // if(typeMatch1(Type.IMAGE, Type.PIXEL, Type.INT, Type.STRING, Type.VOID)){
        if(Type.getType(currentToken) == Type.IMAGE || Type.getType(currentToken) == Type.PIXEL || Type.getType(currentToken) == Type.INT || Type.getType(currentToken) == Type.STRING || Type.getType(currentToken) == Type.VOID){
            Type type = Type.getType(start);
            currentToken = scan.next();
        AST ident = new Ident(currentToken);
        if( currentToken.getKind() == Kind.IDENT){
            match(Kind.IDENT);
        }
        else{
            throw new SyntaxException("Expected IDENT but found " + currentToken.getKind());
        }
         if(currentToken.getKind() == Kind.LPAREN){
        match(Kind.LPAREN);
       }
       else{
           throw new SyntaxException("Expected LPAREN but found " + currentToken.getKind());
         }

        ArrayList<NameDef> params = paramList();
        match(Kind.RPAREN);
        Block block = block();
        return new Program(start, type, (Ident)ident, params, block);
    }
    else{
        throw new SyntaxException("program() Expected one of [A Type]  but found " + currentToken.getKind());
    }

}

private ArrayList<Declaration> decList() throws PLCException{
    IToken start = currentToken;
    ArrayList<Declaration> decs = new ArrayList<Declaration>();

    while(currentToken.getKind() != Kind.RCURLY && currentToken.getKind() != Kind.RES_write  && currentToken.getKind() != Kind.RES_while && currentToken.getKind() != Kind.IDENT){
        Declaration dec = declaration();
        currentToken = scan.next();
        decs.add(dec);
        if(currentToken.getKind() == Kind.DOT){
            match(Kind.DOT);
        }
    }
    return decs;
}

private Declaration declaration() throws PLCException{
    IToken start = currentToken;
    NameDef name = nameDef();
    AST expr = null;
    if(currentToken.getKind() == Kind.ASSIGN){
        match(Kind.ASSIGN);
        expr = expression();
    }
        return new Declaration(start, name, (Expr) expr);
    }

private ArrayList<Statement> statementList() throws PLCException{
    IToken start = currentToken;
    ArrayList<Statement> statements = new ArrayList<Statement>();

    while(currentToken.getKind() != Kind.RCURLY && currentToken.getKind() != Kind.RPAREN){
        Statement statement = statement();
        statements.add(statement);
        if(currentToken.getKind() == Kind.DOT){
            match(Kind.DOT);
        }
    }
    return statements;
}

private ArrayList<NameDef> paramList() throws PLCException{
    IToken start = currentToken;
    ArrayList<NameDef> params = new ArrayList<NameDef>();
    NameDef name = null;
   
   if(currentToken.getKind() != Kind.RPAREN){
        name = nameDef();
        params.add(name);
   
   while(currentToken.getKind() != Kind.RCURLY && currentToken.getKind() != Kind.RES_while && currentToken.getKind() != Kind.IDENT && currentToken.getKind() != Kind.RPAREN){
        if( currentToken.getKind() == Kind.COMMA){
         match(Kind.COMMA);
        name = nameDef();
        params.add(name);
    }
}
 }
    return params;
}


private NameDef nameDef() throws PLCException{
    IToken start = currentToken;
    Dimension dim = null;
    Ident id = null;
    typeMatch(Type.IMAGE, Type.PIXEL, Type.INT, Type.STRING, Type.VOID);
    if(currentToken.getKind() == Kind.LSQUARE){
        dim = dimension();
    }
    id = new Ident(currentToken);
    match(Kind.IDENT);

    return new NameDef(start, Type.getType(start), dim,id);
}

private Type type() throws PLCException{
    IToken start = currentToken;
    if(match1(Kind.RES_image, Kind.RES_pixel, Kind.RES_int,Kind.RES_string, Kind.RES_void)){
        return Type.getType(start);
    } 
    else{
        throw new SyntaxException("Expected one of [RES_image, RES_pixel, RES_int, RES_string, RES_void] but found " + currentToken.getKind());
    }
    
}

private Dimension dimension() throws PLCException{
    IToken start = currentToken;
    match(Kind.LSQUARE);
    AST e = expression();
    match(Kind.COMMA);
    AST e1 = expression();
    match(Kind.RSQUARE);
    return new Dimension(start, (Expr)e, (Expr)e1);
}


private AST expression() throws PLCException{
    IToken start = currentToken;
   AST ast;
    if(currentToken.getKind() == Kind.RES_if){
        ast = condititonal_exxpression();
    }
    else{
        ast = or_expression();
    }
    return ast;
}

// private AST condititonal_exxpression() throws LexicalException, SyntaxException{
//     IToken start = currentToken;
//     AST ast = null;
//     if(currentToken.getKind() == Kind.RES_if){
//         AST guard = null;
//         AST trueCase = null;
//         AST falseCase = null;
//         currentToken = scan.next();
//         guard = expression();
//          if(currentToken.getKind() == Kind.QUESTION){
//              currentToken = scan.next(); 
//               trueCase = expression();
//               if(currentToken.getKind() == Kind.QUESTION ){
//                   currentToken = scan.next();
//                  falseCase = expression();
//                 //   ast = new ConditionalExpr(start, (Expr)guard, (Expr)trueCase,(Expr) falseCase);
//               }
//          }
    
//          else{
//                 throw new SyntaxException("Expected a '?'");
//          }
//          ast = new ConditionalExpr(start,(Expr)ast,(Expr)trueCase, (Expr)falseCase);
//     }
//     return ast;
// }

private AST condititonal_exxpression() throws PLCException{
    IToken start = currentToken;
    AST ast = null;
    if(currentToken.getKind() == Kind.RES_if){
        currentToken = scan.next();
        AST guard = expression();
        if(currentToken.getKind() == Kind.QUESTION){
            currentToken = scan.next();
            AST trueCase = expression();
            if(currentToken.getKind() == Kind.QUESTION){
                currentToken = scan.next();
                AST falseCase = expression();
                return new ConditionalExpr(start, (Expr)guard, (Expr)trueCase, (Expr)falseCase);
            }
        }
            else{
                throw new SyntaxException("Expected a '?'");
            }
        }
        return ast;
}

private AST or_expression() throws PLCException{
    IToken start = currentToken;
    AST ast = and_Expression();
    while(currentToken.getKind() == Kind.OR || currentToken.getKind() == Kind.BITOR){
        currentToken = scan.next();
        AST rightExpression = and_Expression();
        ast = new BinaryExpr(start, (Expr)ast, Kind.OR, (Expr)rightExpression);
    }
    return ast;
}
private AST and_Expression() throws PLCException{
    IToken start = currentToken;
    AST ast = comparison_Expression();
    while(currentToken.getKind() == Kind.AND|| currentToken.getKind() == Kind.BITAND){
        currentToken = scan.next(); 
        AST rightExpression = comparison_Expression();
        ast = new BinaryExpr(start,(Expr) ast, Kind.AND, (Expr)rightExpression);
    }
    return ast;
}



private AST comparison_Expression() throws PLCException{
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

private AST power_expression() throws PLCException{
    IToken start = currentToken;
    AST ast = additive_expression();

    if(currentToken.getKind() == Kind.EXP){
        currentToken = scan.next();
        AST rightExpression = power_expression();
        ast = new BinaryExpr(start,(Expr) ast, Kind.EXP,(Expr) rightExpression);
    }
    return ast;
}

private AST additive_expression() throws PLCException{
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


private AST multiplicative_expression() throws PLCException{
    IToken start = currentToken;
    AST ast = UnaryExpr();
    while(currentToken.getKind() == Kind.TIMES || currentToken.getKind() == Kind.DIV||currentToken.getKind() == Kind.MOD){
        Kind op = currentToken.getKind();
        currentToken = scan.next();
        AST rightExpression = UnaryExpr();
        ast = new BinaryExpr(start,(Expr) ast, op, (Expr)rightExpression);
    }
    return ast;
}
private AST unaryExprPostFix() throws PLCException{
    IToken start = currentToken;
    AST ast = primaryExpression();
    PixelSelector pix = null;
    ColorChannel color = null;
    if(currentToken.getKind() == Kind.LSQUARE){
         pix = pixs();
        if(currentToken.getKind() == Kind.COLON){
        color = channel();
        }
    }
    if(pix == null){
        return ast;
    }
    return new UnaryExprPostfix(start, (Expr)ast, pix, color);
}

private AST UnaryExpr() throws PLCException{
    IToken start = currentToken;
    AST ast;
    if(currentToken.getKind() == Kind.BANG|| currentToken.getKind() == Kind.MINUS|| currentToken.getKind() == Kind.RES_sin|| currentToken.getKind() == Kind.RES_cos|| currentToken.getKind() == Kind.RES_atan){
        currentToken = scan.next();
        AST rightExpression = UnaryExpr();
        ast = new UnaryExpr(start, start.getKind(), (Expr)rightExpression);
    }
    else{
        ast = unaryExprPostFix();
    }

    return ast;

}
private AST primaryExpression() throws PLCException{
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
    //currentToken = scan.next();
        ast= new RandomExpr(start);
    }
    else if ( currentToken.getKind() == Kind.RES_Z){
    currentToken = scan.next();
    ast = new ZExpr(start);
}
else if(currentToken.getKind() == Kind.RES_x){
    currentToken = scan.next();
    ast = new PredeclaredVarExpr(start);
}
else if(currentToken.getKind() == Kind.RES_y){
    currentToken = scan.next();
    ast = new PredeclaredVarExpr(start);
}
else if(currentToken.getKind() == Kind.RES_a){
    currentToken = scan.next();
    ast = new PredeclaredVarExpr(start);
}
else if(currentToken.getKind() == Kind.RES_r){
    currentToken = scan.next();
    ast = new PredeclaredVarExpr(start);
}
 else if(currentToken.getKind() == Kind.LSQUARE){
    ast =  expandePixel();
    }

    else if(currentToken.getKind() == Kind.RES_x_cart || currentToken.getKind() == Kind.RES_y_cart || currentToken.getKind() == Kind.RES_a_polar || currentToken.getKind() == Kind.RES_r_polar){
 ast = pixelFuncExpr();
 }
    else{
        throw(new SyntaxException("invalid token in primary expression. character is: " + currentToken.getKind() + " " ));
    }
    return ast;
}   



private ExpandedPixelExpr expandePixel() throws PLCException{
    IToken start = currentToken;
    match(Kind.LSQUARE);
    AST expr = expression();
    match(Kind.COMMA);
    AST expr2 = expression();
    match(Kind.COMMA);
    AST expr3 = expression();
    match(Kind.RSQUARE);
    return new ExpandedPixelExpr(start, (Expr)expr, (Expr)expr2, (Expr)expr3);

}

private PixelFuncExpr pixelFuncExpr() throws PLCException{
    IToken start = currentToken;

    //add exception 
    match(Kind.RES_x_cart, Kind.RES_y_cart, Kind.RES_a_polar, Kind.RES_r_polar);
    PixelSelector pix = pixs();
    return new PixelFuncExpr(start, start.getKind(), pix);
}

private Statement statement() throws PLCException{
    IToken start = currentToken;
    switch(currentToken.getKind()){
        case IDENT ->{
        LValue val = lvalue();
        match(Kind.ASSIGN);
        AST expr = expression();
        return new AssignmentStatement(currentToken, val, (Expr)expr);
        }
    case RES_write ->{
        currentToken = scan.next();
        AST expr = expression();
        return new WriteStatement(currentToken, (Expr)expr);
    }
    case RES_while ->{
        currentToken = scan.next();
        AST expr = expression();
        Block block = block();
        return new WhileStatement(currentToken, (Expr)expr, block);
    }
    default -> throw new SyntaxException("invalid token in statement");
}

}
public LValue lvalue() throws PLCException{
    IToken start = currentToken;
   if(match1(Kind.IDENT)){
    Ident ident = new Ident(start);
    PixelSelector pix = null;
    ColorChannel channel = null;
    if(currentToken.getKind() == Kind.LSQUARE){
        pix = pixs();
    }
        if(currentToken.getKind() == Kind.COLON){
        channel = channel();
        }
    return new LValue(start, ident, pix, channel);
}
else{
    throw(new SyntaxException("invalid token in lvalue"));

}
}


 public PixelSelector pixs() throws PLCException{
    IToken start = currentToken;
    match(Kind.LSQUARE);
    AST expr = expression();
    match(Kind.COMMA);
    AST expr2 = expression();
    match(Kind.RSQUARE);
    return new PixelSelector(start, (Expr)expr,(Expr) expr2);
}

public Block block() throws PLCException{
    IToken start = currentToken;
    match(Kind.LCURLY);
    ArrayList<Declaration> decls = new ArrayList<Declaration>();
    ArrayList<Statement> stmts = new ArrayList<Statement>();
    decls = decList();
    stmts = statementList();
    match(Kind.RCURLY);
    return new Block(start, decls, stmts);
}   


public ColorChannel channel() throws PLCException{
    IToken start = currentToken;
    match(Kind.COLON);
    if(currentToken.getKind() == Kind.RES_red){
        match(Kind.RES_red);
       return ColorChannel.red;
    }

    if(currentToken.getKind() == Kind.RES_grn){
        match(Kind.RES_grn);
       return ColorChannel.grn;
    }
    if(currentToken.getKind() == Kind.RES_blu){
        match(Kind.RES_blu);
        return ColorChannel.blu;
    }
    
    else{
        throw new PLCException("invalid token in channel" + currentToken.getKind());
    }
}

    @Override
    public AST parse() throws PLCException {
       return program();
    }

}