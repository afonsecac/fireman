package com.ec.fireman.data.entities;

import lombok.Data;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Audited
@Data
@Entity
public class InspectionHeader implements BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id;

  @Enumerated(EnumType.STRING)
  private State state;

  @ManyToOne
  private PermissionRequest permissionRequest;

}