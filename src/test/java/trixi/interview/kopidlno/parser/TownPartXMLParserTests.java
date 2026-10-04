package trixi.interview.kopidlno.parser;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import trixi.interview.kopidlno.domain.Town;
import trixi.interview.kopidlno.domain.TownPart;
import trixi.interview.kopidlno.persistent.JPATownRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TownPartXMLParserTests extends XMLParserHelpers {
    @Mock
    private JPATownRepository townRepository;

    @Test
    void parse_townPartAndRelatedTown() throws Exception {
        Town town = town(573060L, "Kopidlno");
        when(townRepository.findByCode(573060L)).thenReturn(town);
        TownPartXMLParser parser = new TownPartXMLParser(townRepository);

        List<TownPart> townParts = parser.parse(parseXml("""
                <vf:Data>
                  <vf:CastiObci>
                    <vf:CastObce gml:id="CO.69302">
                      <coi:Kod>69302</coi:Kod>
                      <coi:Nazev>Ledkov</coi:Nazev>
                      <coi:Obec>
                        <obi:Kod>573060</obi:Kod>
                      </coi:Obec>
                      <coi:PlatiOd>2014-01-03T00:00:00</coi:PlatiOd>
                      <coi:IdTransakce>449647</coi:IdTransakce>
                      <coi:GlobalniIdNavrhuZmeny>473551</coi:GlobalniIdNavrhuZmeny>
                      <coi:MluvnickeCharakteristiky>
                        <com:Pad2>Ledkova</com:Pad2>
                        <com:Pad3>Ledkovu</com:Pad3>
                        <com:Pad4>Ledkov</com:Pad4>
                        <com:Pad6>Ledkově</com:Pad6>
                        <com:Pad7>Ledkovem</com:Pad7>
                      </coi:MluvnickeCharakteristiky>
                      <coi:Geometrie>
                        <coi:DefinicniBod>
                          <gml:Point gml:id="DCO.69302" srsName="urn:ogc:def:crs:EPSG::5514" srsDimension="2">
                            <gml:pos>-681339.00 -1022382.00</gml:pos>
                          </gml:Point>
                        </coi:DefinicniBod>
                      </coi:Geometrie>
                    </vf:CastObce>
                  </vf:CastiObci>
                </vf:Data>
                """));

        assertThat(townParts).hasSize(1);
        assertThat(townParts.getFirst().getCode()).isEqualTo(69302L);
        assertThat(townParts.getFirst().getName()).isEqualTo("Ledkov");
        assertThat(townParts.getFirst().getTown()).isSameAs(town);
        verify(townRepository).findByCode(573060L);
    }

    @Test
    void parse_multipleTownParts_reuseRelatedTown() throws Exception {
        Town town = town(123L, "Praha");
        when(townRepository.findByCode(123L)).thenReturn(town);
        TownPartXMLParser parser = new TownPartXMLParser(townRepository);

        List<TownPart> townParts = parser.parse(parseXml("""
                <vf:CastObceList>
                    <vf:CastObce>
                        <coi:Kod>456</coi:Kod>
                        <coi:Nazev>Praha 1</coi:Nazev>
                        <coi:Obec><obi:Kod>123</obi:Kod></coi:Obec>
                    </vf:CastObce>
                    <vf:CastObce>
                        <coi:Kod>457</coi:Kod>
                        <coi:Nazev>Praha 2</coi:Nazev>
                        <coi:Obec><obi:Kod>123</obi:Kod></coi:Obec>
                    </vf:CastObce>
                </vf:CastObceList>
                """));

        assertThat(townParts).hasSize(2);
        assertThat(townParts.stream().map(TownPart::getCode).toList()).containsExactly(456L, 457L);
        assertThat(townParts).allSatisfy(part -> assertThat(part.getTown()).isSameAs(town));
        verify(townRepository, times(1)).findByCode(123L);
    }

    @Test
    void parse_multipleTownParts_differentTown() throws Exception {
        Town town1 = town(123L, "Praha");
        when(townRepository.findByCode(123L)).thenReturn(town1);
        Town town2 = town(456L, "Brno");
        when(townRepository.findByCode(456L)).thenReturn(town2);
        TownPartXMLParser parser = new TownPartXMLParser(townRepository);

        List<TownPart> townParts = parser.parse(parseXml("""
                <vf:CastObceList>
                    <vf:CastObce>
                        <coi:Kod>232</coi:Kod>
                        <coi:Nazev>Brno 1</coi:Nazev>
                        <coi:Obec><obi:Kod>456</obi:Kod></coi:Obec>
                    </vf:CastObce>
                    <vf:CastObce>
                        <coi:Kod>179</coi:Kod>
                        <coi:Nazev>Praha 2</coi:Nazev>
                        <coi:Obec><obi:Kod>123</obi:Kod></coi:Obec>
                    </vf:CastObce>
                </vf:CastObceList>
                """));

        assertThat(townParts).hasSize(2);
        assertThat(townParts.stream().map(TownPart::getCode).toList()).containsExactly(232L, 179L);
        assertThat(townParts.get(0).getTown()).isSameAs(town2);
        assertThat(townParts.get(1).getTown()).isSameAs(town1);
    }

    @Test
    void parse_noTownParts() throws Exception {
        TownPartXMLParser parser = new TownPartXMLParser(townRepository);

        List<TownPart> townParts = parser.parse(parseXml("""
                <vf:Kraje>
                    <vf:Kraj gml:id="ST.1">
                        <kra:Kod>1</kra:Kod>
                        <kra:Nazev>Stredocesky</kra:Nazev>
                    </vf:Kraj>
                    <vf:Kraj gml:id="ST.2">
                        <kra:Kod>2</kra:Kod>
                        <kra:Nazev>Moravskoslezsky</kra:Nazev>
                    </vf:Kraj>
                    <vf:Kraj gml:id="ST.3">
                        <kra:Kod>3</kra:Kod>
                        <kra:Nazev>Liberecky</kra:Nazev>
                    </vf:Kraj>
                </vf:Kraje>
                """));

        assertThat(townParts).isEmpty();
        verify(townRepository, never()).findByCode(anyLong());
    }

    @Test
    void parse_relatedTownNotExist() {
        when(townRepository.findByCode(999L)).thenReturn(null);
        TownPartXMLParser parser = new TownPartXMLParser(townRepository);

        assertThatThrownBy(() -> parser.parse(parseXml("""
                <vf:CastObce>
                    <coi:Kod>456</coi:Kod>
                    <coi:Nazev>Praha 1</coi:Nazev>
                    <coi:Obec><obi:Kod>999</obi:Kod></coi:Obec>
                </vf:CastObce>
                """)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Related town with code 999 not found in the database.");
    }

    private Town town(Long code, String name) {
        Town town = new Town();
        town.setCode(code);
        town.setName(name);
        return town;
    }
}
