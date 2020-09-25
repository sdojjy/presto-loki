// Generated from /Users/jiangjianyuan/work/git/presto-loki/src/main/java/io/prestosql/plugin/loki/ast/LabelSelector.g4 by ANTLR 4.8
package io.prestosql.plugin.loki.ast;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class LabelSelectorParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.8", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		OP=1, LBRACE=2, RBRACE=3, COMMA=4, KEY=5, NUMBER=6, STRING=7, RAWQUERY=8, 
		WS=9;
	public static final int
		RULE_basequery = 0, RULE_pair = 1;
	private static String[] makeRuleNames() {
		return new String[] {
			"basequery", "pair"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, null, "'{'", "'}'", "','"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "OP", "LBRACE", "RBRACE", "COMMA", "KEY", "NUMBER", "STRING", "RAWQUERY", 
			"WS"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "LabelSelector.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public LabelSelectorParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	public static class BasequeryContext extends ParserRuleContext {
		public TerminalNode LBRACE() { return getToken(LabelSelectorParser.LBRACE, 0); }
		public List<PairContext> pair() {
			return getRuleContexts(PairContext.class);
		}
		public PairContext pair(int i) {
			return getRuleContext(PairContext.class,i);
		}
		public TerminalNode RBRACE() { return getToken(LabelSelectorParser.RBRACE, 0); }
		public List<TerminalNode> COMMA() { return getTokens(LabelSelectorParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(LabelSelectorParser.COMMA, i);
		}
		public BasequeryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_basequery; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LabelSelectorListener ) ((LabelSelectorListener)listener).enterBasequery(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LabelSelectorListener ) ((LabelSelectorListener)listener).exitBasequery(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LabelSelectorVisitor ) return ((LabelSelectorVisitor<? extends T>)visitor).visitBasequery(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BasequeryContext basequery() throws RecognitionException {
		BasequeryContext _localctx = new BasequeryContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_basequery);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(4);
			match(LBRACE);
			setState(5);
			pair();
			setState(10);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(6);
				match(COMMA);
				setState(7);
				pair();
				}
				}
				setState(12);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(13);
			match(RBRACE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PairContext extends ParserRuleContext {
		public TerminalNode KEY() { return getToken(LabelSelectorParser.KEY, 0); }
		public TerminalNode OP() { return getToken(LabelSelectorParser.OP, 0); }
		public TerminalNode STRING() { return getToken(LabelSelectorParser.STRING, 0); }
		public PairContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pair; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LabelSelectorListener ) ((LabelSelectorListener)listener).enterPair(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LabelSelectorListener ) ((LabelSelectorListener)listener).exitPair(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LabelSelectorVisitor ) return ((LabelSelectorVisitor<? extends T>)visitor).visitPair(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PairContext pair() throws RecognitionException {
		PairContext _localctx = new PairContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_pair);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(15);
			match(KEY);
			setState(16);
			match(OP);
			setState(17);
			match(STRING);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\13\26\4\2\t\2\4\3"+
		"\t\3\3\2\3\2\3\2\3\2\7\2\13\n\2\f\2\16\2\16\13\2\3\2\3\2\3\3\3\3\3\3\3"+
		"\3\3\3\2\2\4\2\4\2\2\2\24\2\6\3\2\2\2\4\21\3\2\2\2\6\7\7\4\2\2\7\f\5\4"+
		"\3\2\b\t\7\6\2\2\t\13\5\4\3\2\n\b\3\2\2\2\13\16\3\2\2\2\f\n\3\2\2\2\f"+
		"\r\3\2\2\2\r\17\3\2\2\2\16\f\3\2\2\2\17\20\7\5\2\2\20\3\3\2\2\2\21\22"+
		"\7\7\2\2\22\23\7\3\2\2\23\24\7\t\2\2\24\5\3\2\2\2\3\f";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}