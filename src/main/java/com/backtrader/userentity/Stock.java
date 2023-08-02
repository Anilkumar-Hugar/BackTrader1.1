package com.backtrader.userentity;

import java.util.Date;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Stock {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private int id;
	@JdbcTypeCode(SqlTypes.JSON)
	private Object node;
	private Date date;
	@ManyToOne(fetch = FetchType.EAGER, targetEntity = Users.class)
	private Users userid;
	private String symbol;
}
