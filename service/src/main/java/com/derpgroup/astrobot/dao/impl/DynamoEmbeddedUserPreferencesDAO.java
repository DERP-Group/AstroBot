package com.derpgroup.astrobot.dao.impl;

import java.io.IOException;
/*import java.util.ArrayList;
import java.util.List;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;*/
import com.derpgroup.astrobot.dao.UserPreferencesDAO;
import com.derpgroup.astrobot.model.preferences.UserPreferences;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DynamoEmbeddedUserPreferencesDAO implements UserPreferencesDAO {

  private ObjectMapper objectMapper;
//  private AmazonDynamoDB dynamodb;
  
//  private String tableName = "userPreferences";
  
  public DynamoEmbeddedUserPreferencesDAO() {
    objectMapper = new ObjectMapper();

    //TODO: Get the in-memory version to work for testing - has problems with missing libsqlite4 at runtime
//    dynamodb = DynamoDBEmbedded.create();
    
    init();
  }
  
  private void init() {
   /* List<AttributeDefinition> attributeDefinitions = new ArrayList<AttributeDefinition>();
    List<KeySchemaElement> keySchemaElements = new ArrayList<KeySchemaElement>();
    ProvisionedThroughput provisionedThroughput = new ProvisionedThroughput(25L,25L);
    dynamodb.createTable(attributeDefinitions, tableName, keySchemaElements, provisionedThroughput);*/
  }

  @Override
  public void setPreferences(UserPreferences userPreferences) {
    
  }

  @Override
  public UserPreferences getPreferences(String userId) {

    try {
      return objectMapper.readValue(userId, new TypeReference<UserPreferences>(){});
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public <T> T getPreferencesBySkillName(String userId, String skillName,
      TypeReference<T> type) {
    // TODO Auto-generated method stub
    return null;
  }

}
