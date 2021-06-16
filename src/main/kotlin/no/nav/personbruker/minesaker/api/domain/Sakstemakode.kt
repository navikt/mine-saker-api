package no.nav.personbruker.minesaker.api.domain

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
    KOM,
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
    YRK;

    companion object {
        fun teamKoderFraSAF(): List<Sakstemakode> {
            return values().filter { kode -> kode != KOM }
        }
    }

}
