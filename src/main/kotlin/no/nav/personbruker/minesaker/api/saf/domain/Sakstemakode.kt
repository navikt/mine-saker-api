package no.nav.personbruker.minesaker.api.saf.domain

import no.nav.personbruker.minesaker.api.common.exception.UgyldigVerdiException

enum class Sakstemakode {
  
    AAP,
    AAR,
    AGR,
    BAR,
    BID,
    BIL,
    DAG,
    ENF,
    ERS,
    FAR,
    FEI,
    FOR,
    FOS,
    FRI,
    FUL,
    GEN,
    GRA,
    GRU,
    HEL,
    HJE,
    IAR,
    IND,
    KON,
    KTR,
    MED,
    MOB,
    OMS,
    OPA,
    OPP,
    PEN,
    PER,
    REH,
    REK,
    RPO,
    RVE,
    SAA,
    SAK,
    SAP,
    SER,
    SIK,
    STO,
    SUP,
    SYK,
    SYM,
    TIL,
    TRK,
    TRY,
    TSO,
    TSR,
    UFM,
    UFO,
    UKJ,
    VEN,
    YRA,
    YRK

}

fun String.toInternalSaktemakode(): Sakstemakode {
    val gyldigSakstemakode = runCatching {
        Sakstemakode.valueOf(this)

    }.onFailure { cause ->
        throw UgyldigVerdiException("Ukjent sakstemakode", cause).addContext("ukjentVerdi", this)
    }

    return gyldigSakstemakode.getOrThrow()
}
