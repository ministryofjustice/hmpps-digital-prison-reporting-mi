package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name ="establishment_establishment", schema = "domain")
class Establishment(
  val name: String,
  @Id
  val id: String,
)