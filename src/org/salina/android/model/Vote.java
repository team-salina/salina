package org.salina.android.model;


/**
 * Post(Feedback, Answer)의 투표 점수 정보 클래스
 * @author 이준영
 *
 */
public class Vote {
	private long pk;
	private String user_id;
	private Category post_category;
	private long post_pk;
	private int vote_score;
}
