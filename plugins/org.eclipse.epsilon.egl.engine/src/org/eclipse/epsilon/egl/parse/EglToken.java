/*******************************************************************************
 * Copyright (c) 2008 The University of York.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * Contributors:
 *     Louis Rose - initial API and implementation
 ******************************************************************************/
package org.eclipse.epsilon.egl.parse;

import org.antlr.runtime.CommonToken;

public class EglToken extends CommonToken {

	// Generated by Eclipse
	private static final long serialVersionUID = -1317153822962960518L;

	public enum TokenType {
		PROGRAM,
		PLAIN_TEXT,
		NEW_LINE,
		START_TAG,
		START_OUTPUT_TAG,
		END_TAG,
		END_OUTDENT_TAG,
		START_COMMENT_TAG,
		START_MARKER_TAG,
		END_COMMENT_TAG,
		EOF;
		
		private final int identifier;
		
		private TokenType() {
			// Increase the ID to avoid overlap with existing tokens (for example, see bug #558543)
			this.identifier = ordinal() + (MIN_TOKEN_TYPE << 8);
		}
		
		public int getIdentifier() {
			return identifier;
		}
		
		public static TokenType typeOf(int identifier) {
			for (TokenType type : values()) {
				if (type.identifier == identifier)
					return type;
			}
			
			throw new IllegalArgumentException(identifier+" is not the identifier of any TokenType");
		}
	}
	
	private TokenType type;
	private String text;
	private int line;

	public EglToken(TokenType type, String text, int line, int col) {
		super(type.getIdentifier(), text);
		
		if (text==null)
			throw new NullPointerException("text cannot be null");
		
		this.type = type;
		this.text = text;
		this.line = line;
		this.charPositionInLine = col;
	}

	public int getColumn() {
		return super.charPositionInLine;
	}

	@Override
	public int getLine() {
		return line;
	}


	@Override
	public String getText() {
		return text;
	}
	
	@Override
	public int getType() {
		return type.getIdentifier();
	}
	
	public TokenType getTokenType() {
		return type;
	}

	public void setColumn(int column) {
		this.charPositionInLine = column;
	}


	@Override
	public void setLine(int line) {
		this.line = line;
	}


	@Override
	public void setText(String text) {
		this.text = text;
	}
	
	@Override
	public void setType(int type) {
		this.type = TokenType.typeOf(type);
	}

	
	public void setTokenType(TokenType type) {
		this.type = type;
	}

	
	@Override
	public boolean equals(Object o) {
		if (o==null) return false;
		if (!(o instanceof EglToken)) return false;
		
		EglToken that = (EglToken)o;
		
		return type.equals(that.type) &&
		       getText().equals(that.getText()) &&
		       getLine()   == that.getLine() &&
		       getColumn() == that.getColumn();
	}
	
	@Override
	public int hashCode() {
		int result = 17;
		
		result = 37*result + type.hashCode();
		result = 37*result + getText().hashCode();
		result = 37*result + getLine();
		result = 37*result + getColumn();
		
		return result;
	}
	
	@Override
	public String toString() {
		return type + " " + getText() + ", line " + getLine() + " col " + getColumn(); 
	}
}
