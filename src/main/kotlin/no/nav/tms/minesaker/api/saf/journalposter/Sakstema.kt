package no.nav.tms.minesaker.api.saf.journalposter

import io.github.oshai.kotlinlogging.KotlinLogging

enum class Sakstema(val navn: String) {
    AAP("Arbeidsavklaringspenger"),
    AAR("Aa-registeret"),
    AGR("Ajourhold – grunnopplysninger"),
    BAR("Barnetrygd"),
    BID("Bidrag"),
    BIL("Bil"),
    DAG("Dagpenger"),
    ENF("Enslig mor eller far"),
    ERS("Erstatning"),
    FEI("Feilutbetaling"),
    FOR("Foreldre- og svangerskapspenger"),
    FOS("Forsikring"),
    FRI("Kompensasjon for selvstendig næringsdrivende/frilansere"),
    FUL("Fullmakt"),
    GEN("Generell"),
    GRA("Gravferdsstønad"),
    GRU("Grunn- og hjelpestønad"),
    HEL("Helsetjenester og ortopediske hjelpemidler"),
    HJE("Hjelpemidler"),
    IAR("Inkluderende arbeidsliv"),
    IND("Tiltakspenger"),
    KOM("Økonomisk sosialhjelp"), //Digisos
    KON("Kontantstøtte"),
    MED("Medlemskap"),
    MOB("Mobilitetsfremmende stønad"),
    OMS("Omsorgspenger, pleiepenger og opplæringspenger"),
    OPA("Oppfølging – arbeidsgiver"),
    OPP("Oppfølging"),
    PEN("Pensjon"),
    PER("Permittering og masseoppsigelser"),
    REH("Rehabiliteringspenger"),
    REK("Rekruttering"),
    RPO("Retting av personopplysninger"),
    RVE("Rettferdsvederlag"),
    SAA("Sanksjon - Arbeidsgiver"),
    SAK("Sakskostnader"),
    SAP("Sanksjon – person"),
    SER("Serviceklager"),
    STO("Regnskap/utbetaling/årsoppgave"),
    SUP("Supplerende stønad"),
    SYK("Sykepenger"),
    SYM("Sykmeldinger"),
    TIL("Tiltak"),
    TRK("Trekkhåndtering"),
    TRY("Trygdeavgift"),
    TSO("Tilleggsstønad"),
    TSR("Tilleggsstønad – arbeidssøkere"),
    UFM("Unntak fra medlemskap"),
    UFO("Uføretrygd"),
    UKJ("Ukjent"),
    VEN("Ventelønn"),
    YRA("Yrkesrettet attføring"),
    YRK("Yrkesskade og menerstatning"),
    FIP("Fiskerpensjon"),
    KLL("Klage – lønnsgaranti"),
    EYB("Barnepensjon"),
    EYO("Omstillingsstønad"),

    // Listen under skal ikke vises til brukere
    FAR("Foreldreskap"),
    KTR("Kontroll"),
    KTA("Kontroll – anmeldelse"),
    ARS("Arbeidsrådgivning – skjermet"),
    ARP("Arbeidsrådgivning – psykologtester"),

    // Brukes der mapping mangler. Indikerer at filen må oppdateres
    Ukjent("Ukjent");

    companion object {
        private val log = KotlinLogging.logger {}

        fun fromExternal(temakode: String): Sakstema {
            return entries.firstOrNull { it.name == temakode } ?: run {
                log.warn { "Mangler mapping for sakstemakode $temakode" }
                Ukjent
            }
        }
    }
}
