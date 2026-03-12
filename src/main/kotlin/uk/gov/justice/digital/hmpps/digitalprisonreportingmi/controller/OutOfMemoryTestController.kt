package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.controller

import org.slf4j.LoggerFactory
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@Validated
@RestController
class OutOfMemoryTestController {
  companion object {
    private val log = LoggerFactory.getLogger(this::class.java)
  }
  private val infiniteList = mutableListOf<ByteArray>()

  @GetMapping("/testing/oom")
  fun testOutOfMemory(): String {
    while (true) {
      infiniteList.add(ByteArray(20_000_000))
      log.debug("Stored 20MB * ${infiniteList.size}")
    }
    return ""
  }
}
