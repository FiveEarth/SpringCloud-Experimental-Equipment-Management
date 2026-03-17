package edu.graduation.reserve.bean;


public class Maintain {


  private long id;
  private long equipmentId;
  private long maintainType;
  private long applyUserId;
  private long assignUserId;
  private String maintainContent;
  private java.sql.Timestamp maintainTime;
  private double cost;
  private long progressStatus;
  private long remindCycle;
  private java.sql.Timestamp nextRemindTime;
  private java.sql.Timestamp createTime;
  private java.sql.Timestamp updateTime;


  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }


  public long getEquipmentId() {
    return equipmentId;
  }

  public void setEquipmentId(long equipmentId) {
    this.equipmentId = equipmentId;
  }


  public long getMaintainType() {
    return maintainType;
  }

  public void setMaintainType(long maintainType) {
    this.maintainType = maintainType;
  }


  public long getApplyUserId() {
    return applyUserId;
  }

  public void setApplyUserId(long applyUserId) {
    this.applyUserId = applyUserId;
  }


  public long getAssignUserId() {
    return assignUserId;
  }

  public void setAssignUserId(long assignUserId) {
    this.assignUserId = assignUserId;
  }


  public String getMaintainContent() {
    return maintainContent;
  }

  public void setMaintainContent(String maintainContent) {
    this.maintainContent = maintainContent;
  }


  public java.sql.Timestamp getMaintainTime() {
    return maintainTime;
  }

  public void setMaintainTime(java.sql.Timestamp maintainTime) {
    this.maintainTime = maintainTime;
  }


  public double getCost() {
    return cost;
  }

  public void setCost(double cost) {
    this.cost = cost;
  }


  public long getProgressStatus() {
    return progressStatus;
  }

  public void setProgressStatus(long progressStatus) {
    this.progressStatus = progressStatus;
  }


  public long getRemindCycle() {
    return remindCycle;
  }

  public void setRemindCycle(long remindCycle) {
    this.remindCycle = remindCycle;
  }


  public java.sql.Timestamp getNextRemindTime() {
    return nextRemindTime;
  }

  public void setNextRemindTime(java.sql.Timestamp nextRemindTime) {
    this.nextRemindTime = nextRemindTime;
  }


  public java.sql.Timestamp getCreateTime() {
    return createTime;
  }

  public void setCreateTime(java.sql.Timestamp createTime) {
    this.createTime = createTime;
  }


  public java.sql.Timestamp getUpdateTime() {
    return updateTime;
  }

  public void setUpdateTime(java.sql.Timestamp updateTime) {
    this.updateTime = updateTime;
  }

}
