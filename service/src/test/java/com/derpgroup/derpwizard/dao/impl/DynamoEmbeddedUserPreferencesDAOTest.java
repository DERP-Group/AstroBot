package com.derpgroup.astrobot.dao.impl;
/*
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;*/

import org.junit.Before;
import org.junit.Test;
/*
import com.amazonaws.services.dynamodbv2.model.TableDescription;
import com.derpgroup.astrobot.model.preferences.UserPreferences;
import com.fasterxml.jackson.core.type.TypeReference;*/

public class DynamoEmbeddedUserPreferencesDAOTest {

 // private DynamoEmbeddedUserPreferencesDAO userPreferencesDAO;
  
  @Before
  public void setup(){
    //TODO: Get the in-memory version to work for testing - has problems with missing libsqlite4 at runtime
//    userPreferencesDAO = new DynamoEmbeddedUserPreferencesDAO();
  }
  
  @Test
  public void testInit(){
    
  }
  
}
