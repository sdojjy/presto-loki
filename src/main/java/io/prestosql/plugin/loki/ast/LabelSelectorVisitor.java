// Generated from /Users/jiangjianyuan/work/git/presto-loki/src/main/java/io/prestosql/plugin/loki/ast/LabelSelector.g4 by ANTLR 4.8
package io.prestosql.plugin.loki.ast;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link LabelSelectorParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface LabelSelectorVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link LabelSelectorParser#basequery}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBasequery(LabelSelectorParser.BasequeryContext ctx);
	/**
	 * Visit a parse tree produced by {@link LabelSelectorParser#pair}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPair(LabelSelectorParser.PairContext ctx);
}