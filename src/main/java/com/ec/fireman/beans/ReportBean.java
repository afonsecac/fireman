package com.ec.fireman.beans;

import com.ec.fireman.data.dao.InspectionFireExtinguisherDao;
import com.ec.fireman.data.dao.InspectionHeaderDao;
import com.ec.fireman.data.entities.InspectionFireExtinguisher;
import com.ec.fireman.data.entities.InspectionHeader;
import com.ec.fireman.util.MessageUtil;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Log4j2
@Named
@ViewScoped
public class ReportBean implements Serializable {

  private static final long serialVersionUID = 3650632184377124942L;

  @Inject
  private InspectionHeaderDao inspectionHeaderDao;
  @Inject
  private InspectionFireExtinguisherDao inspectionFireExtinguisherDao;

  private List<InspectionHeader> inspectionList;

  @PostConstruct
  public void init() {
  }

  public void buildReport() {
    // buscar info de la inspeccion
    InspectionHeader inspection = inspectionHeaderDao.findById(1);
    // buscar data de extintores por inspeccion
    List<InspectionFireExtinguisher> extinguishers = inspectionFireExtinguisherDao.findAll();

    // if (inspection != null) {
    String nombreArchivo = "INSPECCION";

    InspectionFireExtinguisher item = new InspectionFireExtinguisher();
    item.setCapacity("Capacidad 1");
    item.setLocation("Sala de estar");
    item.setQuantity(1);
    item.setStatus("Lleno");
    item.setType("Rojo");
    extinguishers.add(item);

    InspectionFireExtinguisher item1 = new InspectionFireExtinguisher();
    item1.setCapacity("Capacidad 3");
    item1.setLocation("Comedor");
    item1.setQuantity(3);
    item1.setStatus("Lleno");
    item1.setType("Verde");
    extinguishers.add(item1);

    Map<String, Object> params = new HashMap<String, Object>();
    params.put("concrete", true);
    params.put("metallicStructure", true);
    params.put("mixed", false);
    params.put("block", false);
    params.put("brick", true);
    params.put("adobe", false);
    params.put("installationsGood", true);
    params.put("installationsBad", false);
    params.put("installationsInternal", false);
    params.put("installationsExternal", true);
    params.put("ventilationNatural", true);
    params.put("ventilationMechanic", false);
    params.put("ventilationAdequate", true);
    params.put("ventilationScarce", false);
    params.put("knowledgeExtinction", true);
    params.put("alarms", true);
    params.put("fireDetectors", false);
    params.put("smokeDetectors", false);
    params.put("emergencyLights", true);
    params.put("riskFire", "El cliente tiene conocimientos sobre incendios.");
    params.put("recommendations", "Se recomienda seguir estudiando sobre luces de emergencia.");
    params.put("observations", "Se aprueba la inspecci�n.");

    // params.put("concrete", inspection.isConcrete());
    // params.put("metallicStructure", inspection.isMetallicStructure());
    // params.put("mixed", inspection.isMixed());
    // params.put("block", inspection.isBlock());
    // params.put("brick", inspection.isBrick());
    // params.put("adobe", inspection.isAdobe());
    // params.put("installationsGood", inspection.isInstallationsGood());
    // params.put("installationsBad", inspection.isInstallationsBad());
    // params.put("installationsInternal", inspection.isInstallationsInternal());
    // params.put("installationsExternal", inspection.isInstallationsExternal());
    // params.put("ventilationNatural", inspection.isVentilationNatural());
    // params.put("ventilationMechanic", inspection.isVentilationMechanic());
    // params.put("ventilationAdequate", inspection.isVentilationAdequate());
    // params.put("ventilationScarce", inspection.isVentilationScarce());
    // params.put("knowledgeExtinction", inspection.isKnowledgeExtinction());
    // params.put("alarms", inspection.isAlarms());
    // params.put("fireDetectors", inspection.isFireDetectors());
    // params.put("smokeDetectors", inspection.isSmokeDetectors());
    // params.put("emergencyLights", inspection.isEmergencyLights());
    // params.put("riskFire", inspection.getRiskFire());
    // params.put("recommendations", inspection.getRecommendations());
    // params.put("observations", inspection.getObservations());

    JRBeanCollectionDataSource data = extinguishers != null && !extinguishers.isEmpty()
        ? new JRBeanCollectionDataSource(extinguishers)
        : null;

    try {
      this.generatePDF(nombreArchivo, "inspection.jrxml", data, params);
    } catch (Exception e) {
      log.error(e.getMessage());
      MessageUtil.errorFacesMessage("Reporte", "Error al generar el reporte.");
    }
    // } else {
    // MessageUtil.warningFacesMessage("Reporte", "No existe información");
    // }
  }

  public void generatePDF(String fileName, String templateName, JRBeanCollectionDataSource data,
                          Map<String, Object> params) throws JRException, IOException {
    try {
      FacesContext context = FacesContext.getCurrentInstance();
      params.put("logo", context.getExternalContext().getRealPath(File.separator) + File.separator + "resources"
          + File.separator + "images" + File.separator + "fireman-logo.png");
      String path = context.getExternalContext().getRealPath(File.separator) + File.separator + "resources"
          + File.separator + "reports" + File.separator + templateName;
      HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
      JasperReport jasperReport = JasperCompileManager.compileReport(path);
      JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params,
          data != null ? data : new JREmptyDataSource());
      response.addHeader("Content-disposition", "attachment; filename=" + fileName + ".pdf");

      ServletOutputStream servletOutputStream = response.getOutputStream();
      JasperExportManager.exportReportToPdfStream(jasperPrint, servletOutputStream);

      servletOutputStream.flush();
      servletOutputStream.close();
      context.responseComplete();

    } catch (Exception e) {
      log.error(e.getMessage());
    }
  }

}
