package com.derpgroup.astrobot;

import com.derpgroup.derpwizard.voice.model.CommonMetadata;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class MixInModule extends SimpleModule {

  private static final long serialVersionUID = 7233104920981194579L;

  public MixInModule(){
    super("AstroBotModule"); 
  }
  
  @Override
   public void setupModule(SetupContext context)
     {
       context.setMixInAnnotations(CommonMetadata.class, CommonMetadataMixIn.class);
    }
}
