package uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.data.ExternalMovementRepositoryTest.AllMovements.allExternalMovements
import uk.gov.justice.digital.hmpps.digitalprisonreportingmi.model.ExternalMovement
import java.time.LocalDate
import java.time.LocalTime

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ExternalMovementRepositoryTest {

  @Autowired
  lateinit var externalMovementRepository: ExternalMovementRepository

  @BeforeEach
  fun setup() {
    allExternalMovements.forEach {
      externalMovementRepository.save(it)
    }
  }

  @Test
  fun `should return 2 external movements for the selected page 2 and pageSize 2 sorted by date in ascending order`() {
    val actual = externalMovementRepository.list(2, 2, "date", true, emptyMap())
    Assertions.assertEquals(listOf(FakeExternalMovementRepositoryTest.AllMovements.externalMovement3, FakeExternalMovementRepositoryTest.AllMovements.externalMovement4), actual)
    Assertions.assertEquals(2, actual.size)
  }

  object AllMovements {
    val externalMovement1 = ExternalMovement(
      1,
      "N9980PJ",
      LocalDate.of(2023, 1, 31),
      LocalTime.of(3, 1),
      "Ranby",
      "Kirkham",
      "In",
      "Admission",
      "Unconvicted Remand",
    )
    val externalMovement2 = ExternalMovement(
      2,
      "Q9660WX",
      LocalDate.of(2023, 4, 25),
      LocalTime.of(12, 19),
      "Elmley",
      "Pentonville",
      "In",
      "Transfer",
      "Transfer In from Other Establishment",
    )
    val externalMovement3 = ExternalMovement(
      3,
      "A966ZZZ",
      LocalDate.of(2023, 4, 30),
      LocalTime.of(13, 19),
      "Wakefield",
      "Dartmoor",
      "In",
      "Transfer",
      "Transfer In from Other Establishment",
    )
    val externalMovement4 = ExternalMovement(
      4,
      "Z966YYY",
      LocalDate.of(2023, 5, 1),
      LocalTime.of(15, 19),
      "Cardiff",
      "Maidstone",
      "Out",
      "Transfer",
      "Transfer Out to Other Establishment",
    )
    val externalMovement5 = ExternalMovement(
      5,
      "Q966ABC",
      LocalDate.of(2023, 5, 20),
      LocalTime.of(14, 0),
      "Isle of Wight",
      "Northumberland",
      "In",
      "Transfer",
      "Transfer In from Other Establishment",
    )
    val allExternalMovements = listOf(
      externalMovement1,
      externalMovement2,
      externalMovement3,
      externalMovement4,
      externalMovement5,
    )
  }
}
