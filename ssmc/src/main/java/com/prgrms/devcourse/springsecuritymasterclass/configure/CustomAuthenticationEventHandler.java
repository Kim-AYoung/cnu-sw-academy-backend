package com.prgrms.devcourse.springsecuritymasterclass.configure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationEventHandler {

  private final Logger log = LoggerFactory.getLogger(getClass());

  @Async
  @EventListener
  public void handleAuthenticationSuccessEvent(AuthenticationSuccessEvent event) {
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    Authentication authentication = event.getAuthentication();
    log.info("Successful authentication result: {}", authentication.getPrincipal());
  }

  @EventListener
  public void handleAuthenticationFailureEvent(AbstractAuthenticationFailureEvent event) {
    Exception e = event.getException();
    Authentication authentication = event.getAuthentication();
    log.warn("Unsuccessful authentication result: {}", authentication, e);
  }

}