package com.prgrms.devcourse.springsecuritymasterclass.configure;

import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.math.NumberUtils.toInt;

public class OddAdminVoter implements AccessDecisionVoter<FilterInvocation> {

  static final Pattern PATTERN = Pattern.compile("[0-9]+$");

  private final RequestMatcher requiresAuthorizationRequestMatcher;

  public OddAdminVoter(RequestMatcher requiresAuthorizationRequestMatcher) {
    this.requiresAuthorizationRequestMatcher = requiresAuthorizationRequestMatcher;
  }

  @Override
  public int vote(Authentication authentication, FilterInvocation fi, Collection<ConfigAttribute> attributes) {
    HttpServletRequest request = fi.getRequest();
    if (!requiresAuthorization(request)) {
      return ACCESS_GRANTED;
    }
    User user = (User) authentication.getPrincipal();
    String name = user.getUsername();
    Matcher matcher = PATTERN.matcher(name);
    if (matcher.find()) {
      int number = toInt(matcher.group(), 0);
      if (number % 2 == 1) {
        return ACCESS_GRANTED;
      }
    }
    return ACCESS_DENIED;
  }

  private boolean requiresAuthorization(HttpServletRequest request) {
    return requiresAuthorizationRequestMatcher.matches(request);
  }

  @Override
  public boolean supports(ConfigAttribute attribute) {
    return true;
  }

  @Override
  public boolean supports(Class<?> clazz) {
    return FilterInvocation.class.isAssignableFrom(clazz);
  }

}