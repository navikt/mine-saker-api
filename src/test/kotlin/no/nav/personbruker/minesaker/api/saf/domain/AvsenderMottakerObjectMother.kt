package no.nav.personbruker.minesaker.api.saf.domain

object AvsenderMottakerObjectMother {

    fun giveMeAvsenderPerson(): AvsenderMottaker {
        val actualId = ID("123456")
        val expectedType = AvsenderMottakerType.PERSON
        return AvsenderMottaker(actualId, expectedType)
    }

}
