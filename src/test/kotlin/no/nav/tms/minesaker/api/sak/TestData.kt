package no.nav.tms.minesaker.api.sak

import no.nav.tms.minesaker.api.saf.journalposter.Sakstema
import org.intellij.lang.annotations.Language
import java.time.ZoneId
import java.time.ZonedDateTime

const val defaultInnsynsLenke = "http://default.innsyn"
val innsynsTestLenker: Map<Sakstema, String> = mapOf(
    Sakstema.DAG to "http://dag.innsyn",
    Sakstema.HJE to "http://hje.innsyn",
    Sakstema.KOM to "http://kom.innsyn",
    Sakstema.AAP to "http://aap.innsyn",
    Sakstema.SYK to "http://syk.innsyn",
    Sakstema.SYM to "http://syk.innsyn",
)


val aapSak = ForventetSakstemaInnhold(
    navn = "Arbeidsavklaring",
    kode = "AAP",
    sistEndret = nowAtUtc().minusDays(2),
    detaljvisningUrl = innsynsTestLenker.getOrDefault(Sakstema.AAP, defaultInnsynsLenke)
)
val dagSak =
    ForventetSakstemaInnhold(
        navn = "Dagpenger",
        kode = "DAG",
        sistEndret = nowAtUtc().minusDays(8),
        detaljvisningUrl = innsynsTestLenker.getOrDefault(Sakstema.DAG, defaultInnsynsLenke)
    )
val hjeSak = ForventetSakstemaInnhold(
    navn = "Sak uten kjent kode",
    kode = "HJE",
    sistEndret = nowAtUtc().minusDays(28),
    detaljvisningUrl = defaultInnsynsLenke
)
val sykSak =
    ForventetSakstemaInnhold(
        navn = "Annen sak",
        kode = "SYK",
        sistEndret = nowAtUtc().minusDays(1),
        detaljvisningUrl = innsynsTestLenker.getOrDefault(Sakstema.SYK, defaultInnsynsLenke)
    )
val komSak =
    ForventetSakstemaInnhold(
        navn = "Enda en sak",
        kode = "KOM",
        sistEndret = nowAtUtc().minusDays(302),
        detaljvisningUrl = innsynsTestLenker.getOrDefault(Sakstema.KOM, defaultInnsynsLenke)
    )

/**
 *
 * forventet response til FE
data class SakerDTO(
val sakstemaer: List<ForventetSakstemaInnhold>,
val sakerURL: URL,
val dagpengerSistEndret: ZonedDateTime?
)
 */


data class ForventetSakstemaInnhold(
    val navn: String,
    val kode: String,
    val sistEndret: ZonedDateTime,
    val detaljvisningUrl: String
) {
    companion object {
        fun List<ForventetSakstemaInnhold>.toDigisosResponse() =
            joinToString(prefix = "[", postfix = "]", separator = ",") { it.toDigisosResponse }
    }

    @Language("JSON")
    val toDigisosResponse = """{
      |"navn": "$navn",
      | "kode": "$kode",
      | "sistEndret": "${sistEndret.toLocalDateTime()}"
      |}""".trimMargin()
}

fun nowAtUtc(): ZonedDateTime = ZonedDateTime.now(ZoneId.of("UTC")) ?: throw IllegalArgumentException()
