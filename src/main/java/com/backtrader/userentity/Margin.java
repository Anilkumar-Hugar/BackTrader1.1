package com.backtrader.userentity;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Margin {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private int id;
	private double openingBalance;
	private double marginAvailable;
	private double marginUsed;
	private Date date;
	@ManyToOne
	private User user;
	

}
