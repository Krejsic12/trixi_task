package trixi.interview.kopidlno.parser;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import trixi.interview.kopidlno.domain.Town;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TownXMLParserTests extends XMLParserHelpers {
    private final TownXMLParser parser = new TownXMLParser();

    @Test
    void parse_townCodeAndName() throws Exception {
        Document xml = parseXml("""
                <vf:Data>
                  <vf:Obce>
                    <vf:Obec gml:id="OB.12345">
                      <obi:Kod>12345</obi:Kod>
                      <obi:Nazev>Praha</obi:Nazev>
                      <obi:StatusKod>3</obi:StatusKod>
                      <obi:Okres>
                        <oki:Kod>3604</oki:Kod>
                      </obi:Okres>
                      <obi:Pou>
                        <pui:Kod>2186</pui:Kod>
                      </obi:Pou>
                      <obi:PlatiOd>2019-07-10T00:00:00</obi:PlatiOd>
                      <obi:IdTransakce>2937100</obi:IdTransakce>
                      <obi:GlobalniIdNavrhuZmeny>2042164</obi:GlobalniIdNavrhuZmeny>
                      <obi:MluvnickeCharakteristiky>
                        <com:Pad2>Prahy</com:Pad2>
                        <com:Pad3>Praze</com:Pad3>
                        <com:Pad4>Preho</com:Pad4>
                        <com:Pad6>Praze</com:Pad6>
                        <com:Pad7>Prahou</com:Pad7>
                      </obi:MluvnickeCharakteristiky>
                      <obi:NutsLau>CZ052212345</obi:NutsLau>
                      <obi:Geometrie>
                        <obi:DefinicniBod>
                          <gml:MultiPoint gml:id="DOB.12345" srsName="urn:ogc:def:crs:EPSG::5514" srsDimension="2">
                            <gml:pointMembers>
                              <gml:Point gml:id="DOB.12345.1">
                                <gml:pos>-679188.00 -1024096.00</gml:pos>
                              </gml:Point>
                            </gml:pointMembers>
                          </gml:MultiPoint>
                        </obi:DefinicniBod>
                      </obi:Geometrie>
                    </vf:Obec>
                  </vf:Obce>
                </vf:Data>
                """);

        List<Town> towns = parser.parse(xml);

        assertThat(towns).hasSize(1);
        assertThat(towns.getFirst().getCode()).isEqualTo(12345L);
        assertThat(towns.getFirst().getName()).isEqualTo("Praha");
    }

    @Test
    void parse_multipleTowns() throws Exception {
        Document xml = parseXml("""
                <vf:Obce>
                    <vf:Obec gml:id="OB.246">
                        <obi:Kod>246</obi:Kod>
                        <obi:Nazev>Brno</obi:Nazev>
                    </vf:Obec>
                    <vf:Obec gml:id="OB.369">
                        <obi:Kod>369</obi:Kod>
                        <obi:Nazev>Ostrava</obi:Nazev>
                    </vf:Obec>
                    <vf:Obec gml:id="OB.444">
                        <obi:Kod>444</obi:Kod>
                        <obi:Nazev>Pribram</obi:Nazev>
                    </vf:Obec>
                </vf:Obce>
                """);

        List<Town> towns = parser.parse(xml);

        assertThat(towns).hasSize(3);
        assertThat(towns.stream().map(Town::getCode).toList()).containsExactly(246L, 369L, 444L);
    }

    @Test
    void parse_noTowns() throws Exception {
        Document xml = parseXml("""
                <vf:Staty>
                    <vf:Stat gml:id="ST.1">
                        <sta:Kod>1</sta:Kod>
                        <sta:Nazev>Ceska republika</sta:Nazev>
                    </vf:Stat>
                    <vf:Stat gml:id="ST.2">
                        <sta:Kod>2</sta:Kod>
                        <sta:Nazev>Slovensko</sta:Nazev>
                    </vf:Stat>
                    <vf:Stat gml:id="ST.3">
                        <sta:Kod>3</sta:Kod>
                        <sta:Nazev>USA</sta:Nazev>
                    </vf:Stat>
                </vf:Staty>
                """);

        List<Town> towns = parser.parse(xml);

        assertThat(towns).isEmpty();
    }
}
