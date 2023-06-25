package de.spricom.zaster.endpoints;

import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.spricom.zaster.dtos.samples.ColumnDto;
import de.spricom.zaster.dtos.samples.ColumnGroupDto;
import dev.hilla.Endpoint;

import java.util.Arrays;

@Endpoint
@AnonymousAllowed
public class SamplesEndpoint {

    public ColumnGroupDto getColumnGroup() {
        return new ColumnGroupDto(
                Arrays.stream("Bla, Blub, Foo".split(", "))
                        .map(ColumnDto::new).toList()
                );
    }
}
