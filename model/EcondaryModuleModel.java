package com.zhzw.model;


/**
 * 二级模块
 */
public class EcondaryModuleModel {

    private String econdaryModuleCode;//二级模块 code
    private String econdaryModuleName;//二级模块name
    private String econdaryModuleType;//二级模块类型  y：可选   n：不可选
    private String moduleDisplay;//是否隐藏该模块  y：显示   n：隐藏

    public String getEcondaryModuleCode() {
        return econdaryModuleCode;
    }

    public void setEcondaryModuleCode(String econdaryModuleCode) {
        this.econdaryModuleCode = econdaryModuleCode;
    }

    public String getEcondaryModuleName() {
        return econdaryModuleName;
    }

    public void setEcondaryModuleName(String econdaryModuleName) {
        this.econdaryModuleName = econdaryModuleName;
    }

    public String getEcondaryModuleType() {
        return econdaryModuleType;
    }

    public void setEcondaryModuleType(String econdaryModuleType) {
        this.econdaryModuleType = econdaryModuleType;
    }

    public String getModuleDisplay() {
        return moduleDisplay;
    }

    public void setModuleDisplay(String moduleDisplay) {
        this.moduleDisplay = moduleDisplay;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (!(o instanceof EcondaryModuleModel)){ return false;}
        EcondaryModuleModel that = (EcondaryModuleModel) o;
        return getEcondaryModuleCode().equals(that.getEcondaryModuleCode()) &&
                getEcondaryModuleName().equals(that.getEcondaryModuleName()) &&
                getEcondaryModuleType().equals(that.getEcondaryModuleType()) &&
                getModuleDisplay().equals(that.getModuleDisplay());
    }

    @Override
    public String toString() {
        return "EcondaryModuleModel{" +
                "econdaryModuleCode='" + econdaryModuleCode + '\'' +
                ", econdaryModuleName='" + econdaryModuleName + '\'' +
                ", econdaryModuleType='" + econdaryModuleType + '\'' +
                ", moduleDisplay='" + moduleDisplay + '\'' +
                '}';
    }
}
