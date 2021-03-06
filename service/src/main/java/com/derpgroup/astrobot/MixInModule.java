package com.derpgroup.astrobot;

import com.derpgroup.derpwizard.voice.model.CommonMetadata;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class MixInModule extends SimpleModule {

  private static final long serialVersionUID = 2391621381550642023L;

  public MixInModule(){
    super("AstroBotModule"); 
  }
  
  @Override
   public void setupModule(SetupContext context)
     {
       context.setMixInAnnotations(CommonMetadata.class, CommonMetadataMixIn.class);
    }
}
