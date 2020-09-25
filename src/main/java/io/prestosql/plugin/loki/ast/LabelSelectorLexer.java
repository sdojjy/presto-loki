// Generated from /Users/jiangjianyuan/work/git/presto-loki/src/main/java/io/prestosql/plugin/loki/ast/LabelSelector.g4 by ANTLR 4.8
package io.prestosql.plugin.loki.ast;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class LabelSelectorLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.8", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		OP=1, LBRACE=2, RBRACE=3, COMMA=4, KEY=5, NUMBER=6, STRING=7, RAWQUERY=8, 
		WS=9;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"OP", "LBRACE", "RBRACE", "COMMA", "KEY", "NUMBER", "STRING", "RAWQUERY", 
			"NUM", "LETTER", "ESC", "UNICODE", "HEX", "SAFECODEPOINT", "WS"
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


	public LabelSelectorLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "LabelSelector.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\13m\b\1\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\3\2\3\2\3\2\3\2\3\2"+
		"\3\2\3\2\5\2)\n\2\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\7\6\63\n\6\f\6\16\6"+
		"\66\13\6\3\7\3\7\7\7:\n\7\f\7\16\7=\13\7\3\b\3\b\3\b\7\bB\n\b\f\b\16\b"+
		"E\13\b\3\b\3\b\3\t\3\t\3\t\3\t\7\tM\n\t\f\t\16\tP\13\t\3\t\3\t\3\n\3\n"+
		"\3\13\3\13\3\f\3\f\3\f\5\f[\n\f\3\r\3\r\3\r\3\r\3\r\3\r\3\16\3\16\3\17"+
		"\3\17\3\20\6\20h\n\20\r\20\16\20i\3\20\3\20\2\2\21\3\3\5\4\7\5\t\6\13"+
		"\7\r\b\17\t\21\n\23\2\25\2\27\2\31\2\33\2\35\2\37\13\3\2\7\5\2C\\aac|"+
		"\n\2$$\61\61^^ddhhppttvv\5\2\62;CHch\5\2\2!$$^^\5\2\13\f\17\17\"\"\2q"+
		"\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2"+
		"\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\37\3\2\2\2\3(\3\2\2\2\5*\3\2\2\2\7,\3"+
		"\2\2\2\t.\3\2\2\2\13\60\3\2\2\2\r\67\3\2\2\2\17>\3\2\2\2\21H\3\2\2\2\23"+
		"S\3\2\2\2\25U\3\2\2\2\27W\3\2\2\2\31\\\3\2\2\2\33b\3\2\2\2\35d\3\2\2\2"+
		"\37g\3\2\2\2!)\7?\2\2\"#\7#\2\2#)\7?\2\2$%\7?\2\2%)\7\u0080\2\2&\'\7#"+
		"\2\2\')\7\u0080\2\2(!\3\2\2\2(\"\3\2\2\2($\3\2\2\2(&\3\2\2\2)\4\3\2\2"+
		"\2*+\7}\2\2+\6\3\2\2\2,-\7\177\2\2-\b\3\2\2\2./\7.\2\2/\n\3\2\2\2\60\64"+
		"\5\25\13\2\61\63\5\25\13\2\62\61\3\2\2\2\63\66\3\2\2\2\64\62\3\2\2\2\64"+
		"\65\3\2\2\2\65\f\3\2\2\2\66\64\3\2\2\2\67;\5\23\n\28:\5\23\n\298\3\2\2"+
		"\2:=\3\2\2\2;9\3\2\2\2;<\3\2\2\2<\16\3\2\2\2=;\3\2\2\2>C\7$\2\2?B\5\27"+
		"\f\2@B\5\35\17\2A?\3\2\2\2A@\3\2\2\2BE\3\2\2\2CA\3\2\2\2CD\3\2\2\2DF\3"+
		"\2\2\2EC\3\2\2\2FG\7$\2\2G\20\3\2\2\2HI\7>\2\2IN\5\35\17\2JM\5\35\17\2"+
		"KM\7$\2\2LJ\3\2\2\2LK\3\2\2\2MP\3\2\2\2NL\3\2\2\2NO\3\2\2\2OQ\3\2\2\2"+
		"PN\3\2\2\2QR\7@\2\2R\22\3\2\2\2ST\4\63;\2T\24\3\2\2\2UV\t\2\2\2V\26\3"+
		"\2\2\2WZ\7^\2\2X[\t\3\2\2Y[\5\31\r\2ZX\3\2\2\2ZY\3\2\2\2[\30\3\2\2\2\\"+
		"]\7w\2\2]^\5\33\16\2^_\5\33\16\2_`\5\33\16\2`a\5\33\16\2a\32\3\2\2\2b"+
		"c\t\4\2\2c\34\3\2\2\2de\n\5\2\2e\36\3\2\2\2fh\t\6\2\2gf\3\2\2\2hi\3\2"+
		"\2\2ig\3\2\2\2ij\3\2\2\2jk\3\2\2\2kl\b\20\2\2l \3\2\2\2\f\2(\64;ACLNZ"+
		"i\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}