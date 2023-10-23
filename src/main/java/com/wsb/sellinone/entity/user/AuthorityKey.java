package com.wsb.sellinone.entity.user;

import jakarta.persistence.EmbeddedId;

import java.io.Serializable;

public class AuthorityKey implements Serializable {

    private String name;

    private UserEntity username;
}
