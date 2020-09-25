// Generated from /Users/jiangjianyuan/work/git/presto-loki/src/main/java/io/prestosql/plugin/loki/ast/LabelSelector.g4 by ANTLR 4.8
package io.prestosql.plugin.loki.ast;
import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;

/**
 * This class provides an empty implementation of {@link LabelSelectorVisitor},
 * which can be extended to create a visitor which only needs to handle a subset
 * of the available methods.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public class LabelSelectorBaseVisitor<T> extends AbstractParseTreeVisitor<T> implements LabelSelectorVisitor<T> {
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public T visitBasequery(LabelSelectorParser.BasequeryContext ctx) { return visitChildren(ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public T visitPair(LabelSelectorParser.PairContext ctx) { return visitChildren(ctx); }
}