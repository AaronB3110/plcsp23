package edu.ufl.cise.plcsp23.ast;

import edu.ufl.cise.plcsp23.IToken;
import edu.ufl.cise.plcsp23.PLCException;
import edu.ufl.cise.plcsp23.SyntaxException;

public enum Type{
	IMAGE,
	PIXEL,
	INT,
	STRING,
	VOID;

	public static Type getType(IToken token) throws SyntaxException{
		return switch(token.getKind()) {
		case RES_image -> IMAGE;
		case RES_pixel -> PIXEL;
		case RES_int -> INT;
		case RES_string -> STRING;
		case RES_void -> VOID;
		default -> throw new SyntaxException("error in Type.getType, unexpected token kind " + token.getKind());
		};
	}
}
