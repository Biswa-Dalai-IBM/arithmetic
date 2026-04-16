// Generated from com/example/arithmetic/antlr/Arithmetic.g4 by ANTLR 4.13.1
package com.example.arithmetic.antlr;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link ArithmeticParser}.
 */
public interface ArithmeticListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link ArithmeticParser#prog}.
	 * @param ctx the parse tree
	 */
	void enterProg(ArithmeticParser.ProgContext ctx);
	/**
	 * Exit a parse tree produced by {@link ArithmeticParser#prog}.
	 * @param ctx the parse tree
	 */
	void exitProg(ArithmeticParser.ProgContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Number}
	 * labeled alternative in {@link ArithmeticParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterNumber(ArithmeticParser.NumberContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Number}
	 * labeled alternative in {@link ArithmeticParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitNumber(ArithmeticParser.NumberContext ctx);
	/**
	 * Enter a parse tree produced by the {@code MulDiv}
	 * labeled alternative in {@link ArithmeticParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterMulDiv(ArithmeticParser.MulDivContext ctx);
	/**
	 * Exit a parse tree produced by the {@code MulDiv}
	 * labeled alternative in {@link ArithmeticParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitMulDiv(ArithmeticParser.MulDivContext ctx);
	/**
	 * Enter a parse tree produced by the {@code AddSub}
	 * labeled alternative in {@link ArithmeticParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterAddSub(ArithmeticParser.AddSubContext ctx);
	/**
	 * Exit a parse tree produced by the {@code AddSub}
	 * labeled alternative in {@link ArithmeticParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitAddSub(ArithmeticParser.AddSubContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Parens}
	 * labeled alternative in {@link ArithmeticParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterParens(ArithmeticParser.ParensContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Parens}
	 * labeled alternative in {@link ArithmeticParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitParens(ArithmeticParser.ParensContext ctx);
}