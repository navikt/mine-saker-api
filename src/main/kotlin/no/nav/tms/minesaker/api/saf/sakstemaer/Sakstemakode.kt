package no.nav.tms.minesaker.api.saf.sakstemaer

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
    YRK,
    FIP,
    KLL,
    EYB,
    EYO,
    KOM;

    companion object {
        fun teamKoderFraSAF(): List<Sakstemakode> {
            return values().filter { kode -> kode != KOM }
        }
    }

}

fun String.toInternalSaktemakode() = try {
    Sakstemakode.valueOf(this)
} catch (e: Exception) {
    throw SakstemaException("Ukjent sakstemakode", SakstemaException.ErrorType.UNKNOWN_VALUE, e)
        .addContext("ukjentVerdi", this)
}
