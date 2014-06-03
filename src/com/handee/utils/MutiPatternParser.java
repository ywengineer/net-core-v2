package com.handee.utils;

import java.util.Vector;

public class MutiPatternParser {

	private static MutiPatternParser _instance; // 管理单例

	private MutiPatternParser() {

	}

	public static MutiPatternParser getInstance() {
		if (_instance == null) {
			_instance = new MutiPatternParser();
		}
		return _instance;
	}

	private boolean initFlag = false;

	// private UnionPatternSet unionPatternSet = new UnionPatternSet();
	private int maxIndex = (int) java.lang.Math.pow(2, 16);
	private int shiftTable[] = new int[maxIndex];
	@SuppressWarnings("unchecked")
	public Vector<AtomicPattern> hashTable[] = new Vector[maxIndex];
	private UnionPatternSet tmpUnionPatternSet = new UnionPatternSet();

	public boolean addFilterKeyWord(String keyWord, int level) {
		if (initFlag == true)
			return false;
		UnionPattern unionPattern = new UnionPattern();
		String[] strArray = keyWord.split(" ");
		for (int i = 0; i < strArray.length; i++) {
			Pattern pattern = new Pattern(strArray[i]);
			AtomicPattern atomicPattern = new AtomicPattern(pattern);
			unionPattern.addNewAtomicPattrn(atomicPattern);
			unionPattern.setLevel(level);
			atomicPattern.setBelongUnionPattern(unionPattern);
		}
		tmpUnionPatternSet.addNewUnionPattrn(unionPattern);
		return true;
	}

	private boolean isValidChar(char ch) {
		if ((ch >= '0' && ch <= '9') || (ch >= 'A' && ch <= 'Z')
				|| (ch >= 'a' && ch <= 'z'))
			return true;
		if ((ch >= 0x4e00 && ch <= 0x7fff) || (ch >= 0x8000 && ch <= 0x952f))
			return true;// 简体中文汉字编码
		return false;
	}

	/**
	 * 返回文本中所包含过滤字符的个数
	 * 
	 * @param content
	 *            被检测的文本
	 * @param levelSet
	 *            含有非法字符的个数
	 * @return
	 */
	public String parse(String content, Vector<Integer> levelSet) {
		if (initFlag == false)
			init();
		Vector<AtomicPattern> aps = new Vector<AtomicPattern>();
		String preContent = preConvert(content);
		for (int i = 0; i < preContent.length();) {
			char checkChar = preContent.charAt(i);
			if (shiftTable[checkChar] == 0) {
				Vector<AtomicPattern> tmpAps = new Vector<AtomicPattern>();
				tmpAps = findMathAps(preContent.substring(0, i + 1),
						hashTable[checkChar]);
				aps.addAll(tmpAps);
				i++;
			} else
				i = i + shiftTable[checkChar];
		}
		parseAtomicPatternSet(aps, levelSet);
		return content;
	}

	/**
	 * 检测是否含有非法字符，有则返回true，否则反回false
	 * 
	 * @param content
	 * @return
	 */
	public boolean parse(String content) {
		if (initFlag == false)
			init();
		Vector<Integer> levelSet = new Vector<Integer>();
		Vector<AtomicPattern> aps = new Vector<AtomicPattern>();
		String preContent = preConvert(content);
		for (int i = 0; i < preContent.length();) {
			char checkChar = preContent.charAt(i);
			if (shiftTable[checkChar] == 0) {
				Vector<AtomicPattern> tmpAps = new Vector<AtomicPattern>();
				tmpAps = findMathAps(preContent.substring(0, i + 1),
						hashTable[checkChar]);
				aps.addAll(tmpAps);
				i++;
			} else
				i = i + shiftTable[checkChar];
		}
		parseAtomicPatternSet(aps, levelSet);
		if (levelSet.size() > 0) {
			return true;
		} else {
			return false;
		}
	}

	private void parseAtomicPatternSet(Vector<AtomicPattern> aps,
			Vector<Integer> levelSet) {
		while (aps.size() > 0) {
			AtomicPattern ap = aps.get(0);
			UnionPattern up = ap.belongUnionPattern;
			if (up.isIncludeAllAp(aps) == true) {
				levelSet.add(new Integer(up.getLevel()));
			}
			aps.remove(0);
		}
	}

	private Vector<AtomicPattern> findMathAps(String src,
			Vector<AtomicPattern> destAps) {
		Vector<AtomicPattern> aps = new Vector<AtomicPattern>();
		for (int i = 0; i < destAps.size(); i++) {
			AtomicPattern ap = destAps.get(i);
			if (ap.findMatchInString(src) == true)

				aps.add(ap);
		}
		return aps;
	}

	private String preConvert(String content) {
		String retStr = new String();
		for (int i = 0; i < content.length(); i++) {
			char ch = content.charAt(i);
			if (this.isValidChar(ch) == true) {
				retStr = retStr + ch;
			}
		}
		return retStr;
	}

	// shift table and hash table of initialize
	private void init() {
		initFlag = true;
		for (int i = 0; i < maxIndex; i++)
			hashTable[i] = new Vector<AtomicPattern>();
		shiftTableInit();
		hashTableInit();
	}

	public void clear() {
		tmpUnionPatternSet.clear();
		initFlag = false;
	}

	private void shiftTableInit() {
		for (int i = 0; i < maxIndex; i++)
			shiftTable[i] = 2;
		Vector<UnionPattern> upSet = tmpUnionPatternSet.getSet();
		for (int i = 0; i < upSet.size(); i++) {
			Vector<AtomicPattern> apSet = upSet.get(i).getSet();
			for (int j = 0; j < apSet.size(); j++) {
				AtomicPattern ap = apSet.get(j);
				Pattern pattern = ap.getPattern();
				if (shiftTable[pattern.charAtEnd(1)] != 0)
					shiftTable[pattern.charAtEnd(1)] = 1;
				if (shiftTable[pattern.charAtEnd(0)] != 0)
					shiftTable[pattern.charAtEnd(0)] = 0;
			}
		}
	}

	private void hashTableInit() {
		Vector<UnionPattern> upSet = tmpUnionPatternSet.getSet();
		for (int i = 0; i < upSet.size(); i++) {
			Vector<AtomicPattern> apSet = upSet.get(i).getSet();
			for (int j = 0; j < apSet.size(); j++) {
				AtomicPattern ap = apSet.get(j);
				Pattern pattern = ap.getPattern();
				if (pattern.charAtEnd(0) != 0) {
					hashTable[pattern.charAtEnd(0)].add(ap);
				}
			}
		}
	}
}

class Pattern { // string
	Pattern(String str) {
		this.str = str;
	}

	public char charAtEnd(int index) {
		if (str.length() > index) {
			return str.charAt(str.length() - index - 1);
		} else
			return 0;
	}

	public String str;

	public String getStr() {
		return str;
	};
}

class AtomicPattern {
	public boolean findMatchInString(String str) {
		if (this.pattern.str.length() > str.length())
			return false;
		int beginIndex = str.length() - this.pattern.str.length();
		String eqaulLengthStr = str.substring(beginIndex);
		if (this.pattern.str.equalsIgnoreCase(eqaulLengthStr))
			return true;
		return false;
	}

	AtomicPattern(Pattern pattern) {
		this.pattern = pattern;
	};

	private Pattern pattern;
	public UnionPattern belongUnionPattern;

	public UnionPattern getBelongUnionPattern() {
		return belongUnionPattern;
	}

	public void setBelongUnionPattern(UnionPattern belongUnionPattern) {
		this.belongUnionPattern = belongUnionPattern;
	}

	public Pattern getPattern() {
		return pattern;
	}

	public void setPattern(Pattern pattern) {
		this.pattern = pattern;
	}
}

class SameAtomicPatternSet {
	SameAtomicPatternSet() {
		SAPS = new Vector<AtomicPattern>();
	};

	public Vector<AtomicPattern> SAPS;
}

class UnionPattern { // union string
	UnionPattern() {
		this.apSet = new Vector<AtomicPattern>();
	}

	public Vector<AtomicPattern> apSet;

	public void addNewAtomicPattrn(AtomicPattern ap) {
		this.apSet.add(ap);
	}

	public Vector<AtomicPattern> getSet() {
		return apSet;
	}

	public boolean isIncludeAllAp(Vector<AtomicPattern> inAps) {
		if (apSet.size() > inAps.size())
			return false;
		for (int i = 0; i < apSet.size(); i++) {
			AtomicPattern ap = apSet.get(i);
			if (isInAps(ap, inAps) == false)
				return false;
		}
		return true;
	}

	private boolean isInAps(AtomicPattern ap, Vector<AtomicPattern> inAps) {
		for (int i = 0; i < inAps.size(); i++) {
			AtomicPattern destAp = inAps.get(i);
			if (ap.getPattern().str.equalsIgnoreCase(destAp.getPattern().str) == true)
				return true;
		}
		return false;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getLevel() {
		return this.level;
	}

	private int level;
}

class UnionPatternSet { // union string set
	UnionPatternSet() {
		this.unionPatternSet = new Vector<UnionPattern>();
	}

	public void addNewUnionPattrn(UnionPattern up) {
		this.unionPatternSet.add(up);
	}

	public Vector<UnionPattern> unionPatternSet;

	public Vector<UnionPattern> getSet() {
		return unionPatternSet;
	}

	public void clear() {
		unionPatternSet.clear();
	}
}
