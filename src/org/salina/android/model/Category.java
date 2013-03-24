package org.salina.android.model;

/**
 * ī�װ? ������ <br/>
 * Post(�ԽõǴ� ������ ��)�� 6���� �з� ���� ī�װ?�� ����<br/>
 * <ul>
 * <li> QUESTION : �� </li>
 * <li> SUGGESTION : ���� </li>
 * <li> PROBLEM : ���� ���� </li>
 * <li> PRAISE : Ī�� </li>
 * <li> ANSWER : ��� </li>
 * <li> COMMENT : ��� </li>
 * </ul>
 * @author ���ؿ�
 *
 */
public enum Category {
	/**
	 * �� ī�װ?
	 */
	QUESTION(1, "question"),
	
	/**
	 * ����/���� ī�װ?
	 */
	SUGGESTION(2, "suggestion"),
	
	/**
	 * �������� ī�װ?
	 */
	PROBLEM(3, "problem"),
	
	/**
	 * Ī�� ī�װ?
	 */
	PRAISE(4, "praise"),
	
	/**
	 * ��� ī�װ?<br>
	 * ����� �ۿ� ������ ���� ���̰�, ����� �ǰ��� ���� ���̴�.
	 */
	ANSWER(5, "answer"),
	
	/**
	 * ��� ī�װ?
	 */
	COMMENT(6, "comment");
	
	private int codeNumber;
	private String urlIdentifier;
	
	Category(int codeNumber, String urlIdentifier){
		this.codeNumber = codeNumber;
		this.urlIdentifier = urlIdentifier;
	}
	
	public int getCodeNumber(){
		return codeNumber;
	}
	
	public String getUrlIdentifier() {
		return urlIdentifier;
	}
}
