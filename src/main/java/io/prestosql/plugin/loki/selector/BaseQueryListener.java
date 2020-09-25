package io.prestosql.plugin.loki.selector;

import io.prestosql.plugin.loki.ast.LabelSelectorListener;
import io.prestosql.plugin.loki.ast.LabelSelectorParser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

public class BaseQueryListener implements LabelSelectorListener {
    private final Filters.AndFilter tagFilter = new Filters.AndFilter();

    @Override
    public void enterBasequery(LabelSelectorParser.BasequeryContext ctx) {
    }

    @Override
    public void exitBasequery(LabelSelectorParser.BasequeryContext ctx) {

    }

    @Override
    public void enterPair(LabelSelectorParser.PairContext ctx) {
        String op = ctx.OP().getText();
        String key = ctx.KEY().getText();
        String value = ctx.STRING().getSymbol().getText();
        value = value.substring(1, value.length() - 1);
        if ("=".equals(op)) {
            tagFilter.AddFilter(new Filters.EqualsFilter(key, value));
        } else if ("!=".equals(op)) {
            tagFilter.AddFilter(new Filters.NotFilter(new Filters.EqualsFilter(key, value)));
        } else if ("=~".equals(op)) {
            tagFilter.AddFilter(new Filters.RegexMatchFilter(key, value));
        } else {//!~
            tagFilter.AddFilter(new Filters.NotFilter(new Filters.RegexMatchFilter(key, value)));
        }
    }

    @Override
    public void exitPair(LabelSelectorParser.PairContext ctx) {

    }


    @Override
    public void visitTerminal(TerminalNode terminalNode) {

    }

    @Override
    public void visitErrorNode(ErrorNode errorNode) {

    }

    @Override
    public void enterEveryRule(ParserRuleContext parserRuleContext) {

    }

    @Override
    public void exitEveryRule(ParserRuleContext parserRuleContext) {

    }

    public TagFilter getTagFilter() {
        return tagFilter;
    }
}
