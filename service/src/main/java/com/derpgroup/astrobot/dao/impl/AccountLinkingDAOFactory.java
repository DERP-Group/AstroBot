package com.derpgroup.astrobot.dao.impl;

import com.derpgroup.astrobot.configuration.AccountLinkingDAOConfig;
import com.derpgroup.astrobot.dao.AccountLinkingDAO;

public class AccountLinkingDAOFactory {

  public static AccountLinkingDAO getDAO(AccountLinkingDAOConfig config){
    AccountLinkingDAO dao = null;
    switch(config.getType().toUpperCase()){
    case "H2": 
      dao = new H2EmbeddedAccountLinkingDAO(config);
      break;
      default:
        throw new RuntimeException("Unsupported AccounTLinkingDAO type.");
    }
    return dao;
  }
}
