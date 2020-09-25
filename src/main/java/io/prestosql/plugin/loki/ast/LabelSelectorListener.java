// Generated from /Users/jiangjianyuan/work/git/presto-loki/src/main/java/io/prestosql/plugin/loki/ast/LabelSelector.g4 by ANTLR 4.8
package io.prestosql.plugin.loki.ast;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link LabelSelectorParser}.
 */
public interface LabelSelectorListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link LabelSelectorParser#basequery}.
	 * @param ctx the parse tree
	 */
	void enterBasequery(LabelSelectorParser.BasequeryContext ctx);
	/**
	 * Exit a parse tree produced by {@link LabelSelectorParser#basequery}.
	 * @param ctx the parse tree
	 */
	void exitBasequery(LabelSelectorParser.BasequeryContext ctx);
	/**
	 * Enter a parse tree produced by {@link LabelSelectorParser#pair}.
	 * @param ctx the parse tree
	 */
	void enterPair(LabelSelectorParser.PairContext ctx);
	/**
	 * Exit a parse tree produced by {@link LabelSelectorParser#pair}.
	 * @param ctx the parse tree
	 */
	void exitPair(LabelSelectorParser.PairContext ctx);
}