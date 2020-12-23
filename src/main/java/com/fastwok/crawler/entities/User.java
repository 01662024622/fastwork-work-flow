package com.fastwok.crawler.entities;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name="users")
public class User {
    @Id
    private Long id;
    private String name;
    @Column(name="email_htauto",nullable = true)
    private String emailFastwork;
    private String authentication;
}
