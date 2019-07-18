package com.ec.fireman.data.dao;

import com.ec.fireman.data.entities.PermissionIssue;
import lombok.extern.log4j.Log4j2;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;

@Log4j2
@Stateless
public class PermissionIssuedDao extends GenericDaoImpl<PermissionIssue> {

  @PostConstruct
  public void init() {
    log.info("PermissionIssuedDao was successfully created");
    setClazz(PermissionIssue.class);
  }
}
