package com.ec.fireman.beans;

import com.ec.fireman.data.dao.LocalDao;
import com.ec.fireman.data.dao.PermissionRequestDao;
import com.ec.fireman.data.dao.PermissionRequestFilesDao;
import com.ec.fireman.data.dao.RequirementDao;
import com.ec.fireman.data.entities.*;
import com.ec.fireman.data.representation.RequirementFileUpload;
import com.ec.fireman.util.MessageUtil;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Log4j2
@Named
@SessionScoped
public class CounterBean implements Serializable {

  private static final long serialVersionUID = 8632563163040959753L;

  @Inject
  private LocalDao localDao;
  @Inject
  private RequirementDao requirementDao;
  @Inject
  private PermissionRequestDao permissionRequestDao;
  @Inject
  private PermissionRequestFilesDao permissionRequestFilesDao;

  private List<PermissionRequest> requests;
  private List<RequirementFileUpload> files;
  private PermissionRequest selectedRequest;

  @PostConstruct
  public void init() {
    this.refreshRequests();
    selectedRequest = new PermissionRequest();
  }

  public void refreshRequests() {
    requests = permissionRequestDao.findPermissionRequestByPermissionRequestStatus(PermissionRequestStatus.INSPECTED);
    log.info("Permissions length: " + (requests != null ? requests.size() : 0));
  }

  public void clearData() {
    selectedRequest = new PermissionRequest();
  }

  @Transactional
  public void editRequest() {
    selectedRequest.setPermissionRequestStatus(PermissionRequestStatus.PERMISSION_ISSUED);
    permissionRequestDao.update(selectedRequest);
    log.info(selectedRequest.toString());
    MessageUtil.sendFacesMessage("Solicitud", "Permiso de funcionamiento emitido");
    this.refreshRequests();
    this.clearData();
  }

  public void upload() {
    for (RequirementFileUpload requirementFileUpload : files) {
      log.info(requirementFileUpload.toString());
      if (requirementFileUpload.getFile() != null) {
        byte[] bytes = null;
        try {
          bytes = IOUtils.toByteArray(requirementFileUpload.getFile().getInputstream());
        } catch (IOException e) {
          log.error(e.getMessage());
        }
//        PermissionRequest permissionRequest = permissionRequestDao.findPermissionRequestByLocal(selectedLocal.getId());
        PermissionRequestFiles prf = new PermissionRequestFiles();
//        prf.setPermissionRequest(permissionRequest);
        prf.setRequirement(requirementDao.findById(requirementFileUpload.getRequirementId()));
        prf.setState(State.ACTIVE);
        prf.setData(bytes);
        prf.setFileName(requirementFileUpload.getFile().getFileName());
        permissionRequestFilesDao.save(prf);

        MessageUtil.sendFacesMessage("Succesful", requirementFileUpload.getFile().getFileName() + " is uploaded.");
      }
    }
  }

    @Transactional
    public void cancelRequest() {
        selectedRequest.setPermissionRequestStatus(PermissionRequestStatus.CLOSED);
        permissionRequestDao.update(selectedRequest);
        MessageUtil.sendFacesMessage("Cancelaci�n", "Permiso de funcionamiento cancelado correctaente");
        this.refreshRequests();
        this.clearData();
    }

  public List<RequirementFileUpload> listRequirements() {
    // TODO: LIST ACTIVE REQUIREMENTS BY ROLE (DAO)
    List<Requirement> requirements = requirementDao.findAll();
    files = new ArrayList<RequirementFileUpload>();
    if (requirements != null && !requirements.isEmpty()) {
      for (Requirement req : requirements) {
        files.add(new RequirementFileUpload(req.getId(), req.getName()));
      }
    }
    return files;
  }

}
