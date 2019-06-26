package com.ec.fireman.data.dao;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;

import com.ec.fireman.data.entities.PermissionRequestFiles;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Stateless
public class PermissionRequestFilesDao extends GenericDaoImpl<PermissionRequestFiles> {

  @PostConstruct
  public void init() {
    log.info("LocalDao was successfully created");
    setClazz(PermissionRequestFiles.class);
  }

}
