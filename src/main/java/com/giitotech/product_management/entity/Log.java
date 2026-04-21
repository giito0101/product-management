package com.giitotech.product_management.entity;

import java.sql.Timestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="log")
public class Log {

	// columnの生成
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
	private int id;

	@Enumerated(EnumType.STRING)
    @Column(nullable = false, name="action")
	private Action action;

    @Column(name="timestamp")
	private Timestamp timestamp;
    
	@ManyToOne(cascade = {CascadeType.MERGE, 
            CascadeType.DETACH, CascadeType.REFRESH})
	@JoinColumn(name="user_id")
	private User user;

	// コンストラクターの生成
	public Log() {
	}

	public Log(User user, Action action, Timestamp timestamp) {
		this.user = user;
		this.action = action;
		this.timestamp = timestamp;
	}

	// setter/getterの生成
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public User getUser() {
		return user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}

	// toStringの生成
	@Override
	public String toString() {
		return "Log [id=" + id + ", action=" + action + ", timestamp=" + timestamp + "]";
	}
}
