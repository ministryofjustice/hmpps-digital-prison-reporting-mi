package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "establishment_establishment", schema = "domain")
class Establishment(
  @Id
  val id: String,

  val name: String,
)
