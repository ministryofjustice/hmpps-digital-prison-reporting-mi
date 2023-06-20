package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.security

import org.springframework.security.config.annotation.web.invoke
// @Configuration
// class TestResourceServerConfiguration {
//
//  @Bean
//  @Throws(Exception::class)
//  fun filterChain(http: HttpSecurity): SecurityFilterChain? {
//    http {
//      headers { frameOptions { sameOrigin = true } }
//      sessionManagement { sessionCreationPolicy = SessionCreationPolicy.STATELESS }
//      // Can't have CSRF protection as requires session
//      csrf { disable() }
//      authorizeHttpRequests {
//        authorize("/**", permitAll)
//      }
//    }
//    return http.build()
//  }
// }
