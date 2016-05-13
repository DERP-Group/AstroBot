package com.derpgroup.astrobot;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@JsonTypeInfo(use = Id.NAME, include = JsonTypeInfo.As.PROPERTY, property="type", defaultImpl = AstroBotMetadata.class)
@JsonSubTypes({
  @Type(value = AstroBotMetadata.class)
})
public abstract class CommonMetadataMixIn {

}
