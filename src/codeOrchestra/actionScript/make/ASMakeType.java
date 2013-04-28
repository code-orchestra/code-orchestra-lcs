package codeOrchestra.actionScript.make;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import codeOrchestra.lcs.flex.config.FlexConfig;

/**
 * @author Alexander Eliseyev
 */
public enum ASMakeType {

  REGULAR(new FlexConfigContributor() {
    public void contribute(FlexConfig flexConfig) {
      // do nothing
    }
    public List<String> getAdditionalCompilerArgs() {
      return Collections.emptyList();
    }
  }),
  
  DEBUG(new FlexConfigContributor() {
    public void contribute(FlexConfig flexConfig) {
      flexConfig.setDebug(true);
    }
    public List<String> getAdditionalCompilerArgs() {
      return Collections.emptyList();
    }
  });

  private FlexConfigContributor flexConfigContributor;

  private ASMakeType(FlexConfigContributor flexConfigContributor) {
    this.flexConfigContributor = flexConfigContributor;
  }

  public void contributeFlexConfiguration(FlexConfig flexConfig) {
    flexConfigContributor.contribute(flexConfig);
  }

  public List<String> getAdditionalCompilerArgs() {
    return flexConfigContributor.getAdditionalCompilerArgs();
  }

  private static interface FlexConfigContributor extends Serializable {
    void contribute(FlexConfig flexConfig);
    List<String> getAdditionalCompilerArgs();
  }

  public static ASMakeType parse(String makeTypeName) {
    for (ASMakeType asModuleMakeType : values()) {
      if (asModuleMakeType.name().equals(makeTypeName)) {
        return asModuleMakeType;
      }
    }
    return null;
  }

}
