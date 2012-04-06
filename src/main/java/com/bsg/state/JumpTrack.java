package com.bsg.state;

public class JumpTrack {


	public enum JumpTrackStatus {
		START,
		RED1,
		RED2,
		RISK3,
		RISK1,
		AUTOJUMP;
	}
	
	private JumpTrackStatus status;
	
	public JumpTrack() {
		status = JumpTrackStatus.START;
	}
	
	public JumpTrackStatus getStatus() {
		return status;
	}
	
	public void resetJumpTrack() {
		status = JumpTrackStatus.START;
	}
	
	public void incrementJumpTrack() {
		if (status == JumpTrackStatus.AUTOJUMP)
			return;
		
		status = JumpTrackStatus.values()[status.ordinal() + 1];
	}
	
	public void decrementJumpTrack() {
		if (status == JumpTrackStatus.START)
			return;
		
		status = JumpTrackStatus.values()[status.ordinal() - 1];
	}
}
