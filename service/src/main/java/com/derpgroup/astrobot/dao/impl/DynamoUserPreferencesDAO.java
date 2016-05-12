package com.derpgroup.astrobot.dao.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.UpdateItemOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;
import com.derpgroup.astrobot.dao.UserPreferencesDAO;
import com.derpgroup.astrobot.model.preferences.UserPreferences;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DynamoUserPreferencesDAO implements UserPreferencesDAO {

  private ObjectMapper objectMapper;
  private DynamoDB dynamoDB;
  private Table table;
  
  public DynamoUserPreferencesDAO() {
    objectMapper = new ObjectMapper();
    
    BasicAWSCredentials creds = new BasicAWSCredentials("AKIAIJ6CEFYHSWPIQRHQ", "rPolRsCo4gH90EOnLEV4AUp/ZfvooEWlkAtOFN6R");
    AmazonDynamoDBClient client = new AmazonDynamoDBClient(creds);
    
    dynamoDB = new DynamoDB(client);

    table = dynamoDB.getTable("UserPreferences_Test");
  }
  
  @Override
  public void setPreferences(UserPreferences userPreferences) {
    String userPreferencesJson = null;
    try {
      userPreferencesJson = objectMapper.writeValueAsString(userPreferences);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    
    Item item = Item.fromJSON(userPreferencesJson);
    table.putItem(item);
  }
  
  public <T> void setSkillPreferences(String userId, String skillName, T skillPreferences) {
    UserPreferences userPreferences = new UserPreferences();
    userPreferences.setUserId(userId);
    
    Map<String, T> preferences = new HashMap<String, T>();
    preferences.put(skillName, skillPreferences);
    userPreferences.setPreferences(preferences);
    
    setPreferences(userPreferences);
  }

  @Override
  public UserPreferences getPreferences(String userId) {
    return queryTable(userId, null);
  }
  
  @Override
  public <T> T getPreferencesBySkillName(String userId, String skillName, TypeReference<T> type){
    StringBuilder skillNameFilter = new StringBuilder("preferences.");
    skillNameFilter.append(skillName);
    UserPreferences userPreferences = queryTable(userId, skillNameFilter.toString());
    T output = objectMapper.convertValue(userPreferences.getPreferences().get(skillName), type);
    return output;
  }
  
  public UserPreferences queryTable(String userId,String filterExpression){
    Item item = table.getItem("userId", userId, filterExpression,null);
    if(item == null){
      return createUserRecord(userId);
    }
    System.out.println(item.toJSONPretty().toString());
    
    try {
      return objectMapper.readValue(item.toJSON(), new TypeReference<UserPreferences>(){});
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }
  
  public UserPreferences createUserRecord(String userId){
    ValueMap vm = new ValueMap().withMap(":val1", new HashMap<String,Object>());
    UpdateItemSpec spec = new UpdateItemSpec().withPrimaryKey("userId", userId).withUpdateExpression("set preferences = :val1").withValueMap(vm).withReturnValues(ReturnValue.ALL_NEW);
    UpdateItemOutcome outcome = table.updateItem(spec);
    try {
      return objectMapper.readValue(outcome.getItem().toJSON(), new TypeReference<UserPreferences>(){});
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }
}
