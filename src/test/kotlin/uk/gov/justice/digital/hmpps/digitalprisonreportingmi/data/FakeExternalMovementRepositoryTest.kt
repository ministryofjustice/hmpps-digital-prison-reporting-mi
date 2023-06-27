package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.model.ExternalMovement
import java.time.LocalDate
import java.time.LocalTime

class FakeExternalMovementRepositoryTest {

  @Test
  fun `the repository returns all the external movements`() {
    val expected = listOf(
      ExternalMovement(
        "N9980PJ",
        LocalDate.of(2023, 1, 31),
        LocalTime.of(3, 1),
        "Cardiff",
        "Kirkham",
        "In",
        "Admission",
        "Unconvicted Remand",
      ),
      ExternalMovement(
        "Q9660WX",
        LocalDate.of(2023, 4, 25),
        LocalTime.of(12, 19),
        "Elmley",
        "Pentonville",
        "In",
        "Transfer",
        "Transfer In from Other Establishment",
      ),
    )
    val externalMovements = FakeExternalMovementRepository().externalMovements()
    assertEquals(500, externalMovements.size)
    assertEquals(expected, externalMovements.subList(0, 2))
  }
}
